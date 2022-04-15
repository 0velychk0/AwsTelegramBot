package com.ovelychko;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

@Slf4j
public class AwsLambdaCallUtil {

    private static void invokeFunction(Object data, String functionName) {
        log.info("{} started", functionName);
        ObjectMapper mapper = new ObjectMapper();

        LambdaClient awsLambda = LambdaClient.builder()
                .region(Region.EU_WEST_3)
                .build();

        try {
            String json = mapper.writeValueAsString(data);
            log.info("{} JSON: {}", functionName, json);
            SdkBytes payload = SdkBytes.fromUtf8String(json);

            InvokeRequest request = InvokeRequest.builder()
                    .functionName(functionName)
                    .payload(payload)
                    .build();

            InvokeResponse res = awsLambda.invoke(request);
            String value = res.payload().asUtf8String();
            log.info(value);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        awsLambda.close();
        log.info("{} finished", functionName);
    }

    public static void saveUserData(Update update) {
//        new Thread(new Runnable() {
//            public void run() {
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
                invokeFunction(telegramUserData, "AwsTelegramUserDataAdd");
//            }
//        }).start();
    }

    public static void saveUserRequestData(Update update) {
//        new Thread(new Runnable() {
//            public void run() {
                String searchText = update.getMessage().getText().trim();
                long teleUser = update.getMessage().getFrom().getId();
                LocalDateTime ldt = LocalDateTime.now();
                ldt.toEpochSecond(ZoneOffset.UTC);
                UserRequestData userRequestData = new UserRequestData(
                        ldt.toEpochSecond(ZoneOffset.UTC),
                        teleUser,
                        null,
                        searchText);
                invokeFunction(userRequestData, "AwsUserRequestDataAdd");
//            }
//        }).start();
    }
}
