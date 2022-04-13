package com.ovelychko;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class Configures {

    Properties props = new Properties();

    @Getter
    private String webHookPath;
    @Getter
    private String userName;
    @Getter
    private String botToken;
    @Getter
    private String omdbapiKey;
    @Getter
    private String imbdLink;

    public Configures() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties");
            props.load(is);
            is.close();
            webHookPath = props.getProperty("telegram.webHookPath");
            userName = props.getProperty("telegram.userName");
            botToken = props.getProperty("telegram.botToken");
            omdbapiKey = props.getProperty("telegram.omdbapiKey");
            imbdLink = props.getProperty("telegram.imbdLink");
        } catch (Exception ex) {
            log.error("Properties load failed: {}", ex.toString());
        }
    }
}
