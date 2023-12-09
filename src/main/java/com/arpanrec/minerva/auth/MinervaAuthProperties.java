package com.arpanrec.minerva.auth;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minerva.auth")
@Data
public class MinervaAuthProperties {

    private String headerKey = "Authorization";
}
