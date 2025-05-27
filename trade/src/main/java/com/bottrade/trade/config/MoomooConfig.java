package com.bottrade.trade.config;

import com.bottrade.trade.model.moomoo.MoomooAttributes;
import com.bottrade.trade.model.moomoo.MoomooClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * tesseract ocr(optical character recognition)文字识别配置类
 */
@Configuration
@Slf4j
public class MoomooConfig {
//    @Value("${moomoo.accid}")
//    private String accid;
//
//    @Bean
//    public MoomooClient moomooClient() {
//        MoomooClient moomooClient = new MoomooClient(Long.valueOf(accid));
//        return moomooClient;
//    }

    @Bean
    public MoomooAttributes moomooAttributes(){
        return new MoomooAttributes();
    }
}
