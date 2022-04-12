package com.ovelychko;

import java.util.StringJoiner;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class SimpleTelegramWebhookBot extends TelegramWebhookBot {

    private final String webHookPath = "dd";
    private final String userName = "dd";
    private final String botToken = "dd";
    private final String omdbapiKey = "dd";
    private final String imbdLink = "dd";

    public SimpleTelegramWebhookBot() {
        log.info("WebhookTelegramController created");
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        log.info("onUpdateReceived");

        String searchText = update.getMessage().getText().trim();
        log.info("Search Text:{}", searchText);

        ObjectMapper mapper = new ObjectMapper();

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
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setChatId(update.getMessage().getChatId().toString());
                    sendPhoto.setCaption(String.format("%s - %s (%s), https://www.imdb.com/title/%s \n",
                            model.getType(), model.getTitle(), model.getYear(), model.getImdbID(), model.getImdbID()));
                    sendPhoto.setPhoto(new InputFile(model.getPoster()));
                    this.execute(sendPhoto);
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
