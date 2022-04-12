package com.ovelychko;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

@Slf4j
public class TestMain {

    public static void main(String[] argc) {
        ObjectMapper mapper = new ObjectMapper();

//        Gson gson = new Gson();
//        NameList nameList = gson.fromJson(data, NameList.class);


        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://www.omdbapi.com/?apikey=1ac1214b&s=" + "Free");

            MovieSearchModelCollection response = client.execute(request,
                    httpResponse -> mapper.readValue(httpResponse.getEntity().getContent(), MovieSearchModelCollection.class)
            );

//            HttpGet request = new HttpGet("http://www.omdbapi.com/?apikey=1ac1214b&s=" + "Free");
//
//            HttpResponse response = client.execute(request);
//            String json = EntityUtils.toString(response.getEntity());
//
//            MovieSearchModelCollection response = mapper.readValue(json,
//                    new TypeReference<MovieSearchModelCollection>() {
//                    }
//            );

            for (MovieSearchModel node : response.getSearch()) {
                System.out.printf("Title: %s (%s), ImdbID: %s \n", node.getTitle(), node.getYear(), node.getImdbID());
            }

        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
    }
}
