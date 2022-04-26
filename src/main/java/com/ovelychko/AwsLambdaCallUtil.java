package com.ovelychko;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
public class AwsLambdaCallUtil {

    private static final String REQUEST_QUEUE_NAME = "UserRequestDataQueue";
    private static final String USER_QUEUE_NAME = "TelegramUserDataQueue";
    private static final Gson gson = new GsonBuilder().create();

    public static void saveUserData(Update update) {
        log.info("saveUserData SQS started");

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

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        String queueUrl = sqs.getQueueUrl(USER_QUEUE_NAME).getQueueUrl();

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(gson.toJson(telegramUserData));
        sqs.sendMessage(send_msg_request);

        log.info("saveUserData SQS finished");
    }

    public static void saveUserRequestData(Update update) {
        log.info("saveUserRequestData SQS started");

        String searchText = update.getMessage().getText();
        long teleUser = update.getMessage().getFrom().getId();

        LocalDateTime ldt = LocalDateTime.now();
        ldt.toEpochSecond(ZoneOffset.UTC);
        UserRequestData userRequestData = new UserRequestData(
                ldt.toEpochSecond(ZoneOffset.UTC),
                teleUser,
                null,
                searchText);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        String queueUrl = sqs.getQueueUrl(REQUEST_QUEUE_NAME).getQueueUrl();

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(gson.toJson(userRequestData));
        sqs.sendMessage(send_msg_request);

        log.info("saveUserRequestData SQS finished");
    }
}
