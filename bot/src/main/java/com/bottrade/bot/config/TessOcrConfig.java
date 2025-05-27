package com.bottrade.bot.config;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.LoadLibs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

import static net.sourceforge.tess4j.util.LoadLibs.TESS4J_TEMP_DIR;

/**
 * tesseract ocr(optical character recognition)文字识别配置类
 */
@Configuration
@Slf4j
public class TessOcrConfig {

    @Bean
    public Tesseract tesseract() {
        Tesseract tesseract = new Tesseract();
        File tessDataFolder = new File(TESS4J_TEMP_DIR, "tessdata");
        if(!tessDataFolder.exists()){
            tessDataFolder = LoadLibs.extractTessResources("tessdata");
        }
        String classpathUrl = tessDataFolder.getAbsolutePath();
        log.info("++===== classpathUrl: {}",classpathUrl);
        tesseract.setDatapath(classpathUrl);
        // 设置为英文
        tesseract.setLanguage("eng");
        return tesseract;
    }
}
