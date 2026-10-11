package com.se2030.cinema.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.booking")
public class AppProperties {

    private int holdMinutes = 15;

    public int getHoldMinutes() {
        return holdMinutes;
    }

    public void setHoldMinutes(int holdMinutes) {
        this.holdMinutes = holdMinutes;
    }
}