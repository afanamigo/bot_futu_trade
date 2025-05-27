package com.bottrade.bot;

import com.bottrade.bot.props.AIOcrProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@MapperScan(value = "com.bottrade.bot.dao.mapper")
@SpringBootApplication
@EnableConfigurationProperties({
        AIOcrProperties.class
})
public class AtBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtBotApplication.class, args);
    }

}
