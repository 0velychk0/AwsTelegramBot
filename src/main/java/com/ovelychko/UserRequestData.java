package com.ovelychko;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRequestData {
    private long id;
    private long telegramUserId;
    private String viberUserId;
    private String request;
}
