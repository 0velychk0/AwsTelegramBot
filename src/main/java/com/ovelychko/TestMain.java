package com.ovelychko;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

@Slf4j
public class TestMain {
    private static final Configures config = new Configures();
    private static final String GET_DETAILS_COMMAND = "/getDetails_";

    public static void main(String[] argc) {
        log.info(config.getBotToken());
        handleGetDetailsCommand();
//        ObjectMapper mapper = new ObjectMapper();
//
////        Gson gson = new Gson();
////        NameList nameList = gson.fromJson(data, NameList.class);
//
//
//        try (CloseableHttpClient client = HttpClients.createDefault()) {
//            URIBuilder builder = new URIBuilder();
//            builder.setScheme("https");
//            builder.setHost("www.omdbapi.com");
//            builder.addParameter("apikey", "apikey");
//            builder.addParameter("s", "Die hard");
//            HttpGet request = new HttpGet(builder.build().toString());
//
//            MovieSearchModelCollection response = client.execute(request,
//                    httpResponse -> mapper.readValue(httpResponse.getEntity().getContent(), MovieSearchModelCollection.class)
//            );
//
//            for (MovieSearchModel node : response.getSearch()) {
//                System.out.printf("Title: %s (%s), ImdbID: %s \n", node.getTitle(), node.getYear(), node.getImdbID());
//            }
//
//        } catch (Exception ex) {
//            System.out.println("Error: " + ex);
//        }
    }

    private static void handleGetDetailsCommand() {

        ObjectMapper mapper = new ObjectMapper();

        String searchText = "/getDetails_tt0082971".substring(GET_DETAILS_COMMAND.length()).trim();

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
                log.info("Result: {}", response);
            }
        } catch (Exception ex) {
            log.error("Error during GET request: {}", ex.toString());
        }
    }
}
