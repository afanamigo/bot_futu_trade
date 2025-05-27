package com.bottrade.model.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static Map<String,String> fromCmdExtra(String extra){
        Map<String,String> map = new HashMap<>();
        if(StringUtils.isNotEmpty(extra)){
            String[] strs = extra.split(",");
            for(String str : strs){
                String[] items = str.split("=");
                if(items.length != 2) continue;
                map.put(items[0],items[1]);
            }
        }
        return map;
    }

    public static String decodeRetMsg(String octalString) {
        byte[] bytes = octalString.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
        String decodedString = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        return decodedString;
    }
}
