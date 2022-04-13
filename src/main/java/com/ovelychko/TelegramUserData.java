package com.ovelychko;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TelegramUserData {
    private long id;
    private String firstName;
    private Boolean isBot;
    private String lastName;
    private String userName;
    private String languageCode;
    private Boolean canJoinGroups;
    private Boolean canReadAllGroupMessages;
    private Boolean supportInlineQueries;
}
