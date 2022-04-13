package com.ovelychko;

import java.util.StringJoiner;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.TextUtils;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;

@Slf4j
public class SimpleTelegramWebhookBot extends TelegramWebhookBot {

    private final ObjectMapper mapper = new ObjectMapper();

    private final String webHookPath = "";
    private final String userName = "";
    private final String botToken = "";
    private final String omdbapiKey = "";
    private final String imbdLink = "";

    public SimpleTelegramWebhookBot() {
        log.info("WebhookTelegramController created");
    }

    public void invokeFunction(TelegramUserData telegramUserData) {
        log.info("invokeFunction started");

        String functionName = "AwsTelegramUserDataAdd";
        LambdaClient awsLambda = LambdaClient.builder()
                .region(Region.EU_WEST_3)
                .build();

        InvokeResponse res = null;
        try {
            String json = mapper.writeValueAsString(telegramUserData);
            SdkBytes payload = SdkBytes.fromUtf8String(json);

            InvokeRequest request = InvokeRequest.builder()
                    .functionName(functionName)
                    .payload(payload)
                    .build();

            res = awsLambda.invoke(request);
            String value = res.payload().asUtf8String();
            log.info(value);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        awsLambda.close();
        log.info("invokeFunction finished");
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        log.info("onUpdateReceived");

        String searchText = update.getMessage().getText().trim();
        log.info("Search Text:{}", searchText);

        User user = update.getMessage().getFrom();
        TelegramUserData telegramUserData = new TelegramUserData(
                user.getId(),
                user.getFirstName(),
                user.getIsBot(),
                user.getLastName(),
                user.getUserName(),
                user.getLanguageCode(),
                user.getCanJoinGroups(),
                user.getCanReadAllGroupMessages(),
                user.getSupportInlineQueries());
        invokeFunction(telegramUserData);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            URIBuilder builder = new URIBuilder();
            builder.setScheme("https");
            builder.setHost("www.omdbapi.com");
            builder.addParameter("apikey", omdbapiKey);
            builder.addParameter("s", searchText);
            HttpGet request = new HttpGet(builder.build().toString());

            MovieSearchModelCollection response = client.execute(request,
                    httpResponse -> mapper.readValue(httpResponse.getEntity().getContent(),
                            MovieSearchModelCollection.class));

            log.info("Parsed result: {}", response);

            if (response != null && response.response && response.getSearch() != null) {

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(update.getMessage().getChatId().toString());
                StringJoiner joiner = new StringJoiner("\n");
                joiner.add("Number of results: " + response.getTotalResults());
                if (response.getTotalResults() > 10)
                    joiner.add("Displayed 10 results only");
                sendMessage.setText(joiner.toString());
                this.execute(sendMessage);

                for (MovieSearchModel model : response.search) {
                    if (!TextUtils.isBlank(model.getPoster()) && model.getPoster().startsWith("http")) {
                        SendPhoto sendPhoto = new SendPhoto();
                        sendPhoto.setChatId(update.getMessage().getChatId().toString());
                        sendPhoto.setCaption(String.format("%s - %s (%s),\n https://www.imdb.com/title/%s",
                                model.getType(), model.getTitle(), model.getYear(), model.getImdbID()));
                        sendPhoto.setPhoto(new InputFile(model.getPoster()));
                        this.execute(sendPhoto);
                    } else {
                        SendMessage textMessage = new SendMessage();
                        textMessage.setChatId(update.getMessage().getChatId().toString());
                        textMessage.setText(String.format("%s - %s (%s),\n https://www.imdb.com/title/%s",
                                model.getType(), model.getTitle(), model.getYear(), model.getImdbID()));
                        this.execute(textMessage);
                    }
                }
            } else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(update.getMessage().getChatId().toString());
                sendMessage.setText("Result not found for the keyword: " + searchText);
                this.execute(sendMessage);
            }
        } catch (Exception ex) {
            log.error("Error during GET request: {}", ex.toString());
        }
        return null;
    }

    public String getBotUsername() {
        return userName;
    }

    public String getBotToken() {
        return botToken;
    }

    public String getBotPath() {
        return webHookPath;
    }
}
