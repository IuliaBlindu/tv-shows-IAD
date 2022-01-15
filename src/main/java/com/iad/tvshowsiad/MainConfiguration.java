package com.iad.tvshowsiad;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainConfiguration {

    @Value("${port}")
    @Getter
    @Setter
    private int port;

}
