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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleTelegramWebhookBot extends TelegramWebhookBot {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Configures config = new Configures();

    private static String GET_DETAILS_COMMAND = "/getDetails_";

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        String searchText = update.getMessage().getText().trim();
        log.info("Search Text:{}", searchText);

        if (searchText.startsWith(GET_DETAILS_COMMAND)) {
            this.handleGetDetailsCommand(update);
        } else {
            this.handleSearchByTitleCommand(update);
        }

        return null;
    }

    public String getBotUsername() {
        return config.getUserName();
    }

    public String getBotToken() {
        return config.getBotToken();
    }

    public String getBotPath() {
        return config.getWebHookPath();
    }

    private void handleSearchByTitleCommand(Update update) {

        String searchText = update.getMessage().getText().trim();

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            URIBuilder builder = new URIBuilder();
            builder.setScheme("https");
            builder.setHost("www.omdbapi.com");
            builder.addParameter("apikey", config.getOmdbapiKey());
            builder.addParameter("s", searchText);
            HttpGet request = new HttpGet(builder.build().toString());

            MovieSearchModelCollection response = client.execute(request,
                    httpResponse -> mapper.readValue(httpResponse.getEntity().getContent(),
                            MovieSearchModelCollection.class));

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

                        StringJoiner caption = new StringJoiner("\n");
                        caption.add(String.format("%s - %s (%s),", model.getType(), model.getTitle(), model.getYear()));
                        caption.add(String.format("imdb: %s,", config.getImbdLink() + model.getImdbID()));
                        caption.add("More details: " + GET_DETAILS_COMMAND + model.getImdbID());
                        sendPhoto.setCaption(caption.toString());

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
    }

    private void handleGetDetailsCommand(Update update) {

        String searchText = update.getMessage().getText().substring(GET_DETAILS_COMMAND.length()).trim();

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            URIBuilder builder = new URIBuilder();
            builder.setScheme("https");
            builder.setHost("www.omdbapi.com");
            builder.addParameter("apikey", config.getOmdbapiKey());
            builder.addParameter("i", searchText);
            HttpGet request = new HttpGet(builder.build().toString());

            MovieDetailsDataModel response = client.execute(request,
                    httpResponse -> mapper.readValue(httpResponse.getEntity().getContent(),
                            MovieDetailsDataModel.class));

            if (response != null) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(update.getMessage().getChatId().toString());
                sendMessage.setText(response.toString());
                this.execute(sendMessage);
            }
        } catch (Exception ex) {
            log.error("Error during GET request: {}", ex.toString());
        }
    }
}
