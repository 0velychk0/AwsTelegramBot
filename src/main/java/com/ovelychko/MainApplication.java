package com.ovelychko;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class MainApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TelegramWebhookBot SENDER = new SimpleTelegramWebhookBot();

    public MainApplication() {
        log.info("MainApplication created");
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        LambdaLogger logger = context.getLogger();
        // log execution details
        logger.log("EVENT: " + event);
        logger.log("ENVIRONMENT VARIABLES: " + System.getenv());
        logger.log("CONTEXT: " + context);

        Update update;
        try {
            log.info("event.getBody(): " + event.getBody());
            update = MAPPER.readValue(event.getBody(), Update.class);

            AwsLambdaCallUtil.saveUserData(update);

            AwsLambdaCallUtil.saveUserRequestData(update);

            log.info("Update: " + update);
            SENDER.onWebhookUpdateReceived(update);
        } catch (Exception e) {
            log.error("Failed to parse update: " + e);
            throw new RuntimeException("Failed to parse update!", e);
        }
        log.info("Starting handling update " + update.getUpdateId());

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody("code: 200 Done")
                .withIsBase64Encoded(false);
    }
}
