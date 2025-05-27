package com.bottrade.bot.ocr;

import com.alibaba.fastjson.JSONObject;
import com.bottrade.bot.props.AIOcrProperties;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@AllArgsConstructor
@Slf4j
public class OcrService {

    private static final Gson gson = new Gson();


    private final Tesseract tesseract;

    private final AIOcrProperties aiOcrProperties;

    /**
     * 识别图片中的文字
     * @param imageFile 图片文件
     * @return 文字信息
     */
    public String recognizeText(MultipartFile imageFile) throws TesseractException, IOException {
        // 转换
        InputStream sbs = new ByteArrayInputStream(imageFile.getBytes());

        //thumbnailator -> 变更尺寸
        BufferedImage bufferedImage = ImageIO.read(sbs);
        // 对图片进行文字识别
        return tesseract.doOCR(bufferedImage);
    }

    /**
     * 识别图片中的文字 from InputStream
     * @return 文字信息
     */
    public String recognizeText(BufferedImage bufferedImage) throws TesseractException {
        // 转换
//        BufferedImage bufferedImage = ImageIO.read(sbs);
        // 对图片进行文字识别
        log.info("++===== recognizeText: doOCR");
        return tesseract.doOCR(bufferedImage);
    }

    public OcrOrderInfo aiRecognizeText(String imgUrl) {
        try{
            URL url = new URL(String.format( "%s?url=%s",aiOcrProperties.getOnlineUrl(),imgUrl));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法
            connection.setRequestMethod("GET");
            // 发送请求并获取响应
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            // 关闭连接
            reader.close();
            inputStream.close();
            connection.disconnect();

            return JSONObject.parseObject(sb.toString(), OcrOrderInfo.class);
        }catch (Exception e){
            log.error("aiRecognizeText--error--",e);
        }

        return null;
    }

}
