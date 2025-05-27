package com.bottrade.bot.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ai-ocr")
@Data
public class AIOcrProperties {
    private String  onlineUrl;
    private String  fileUrl;
}