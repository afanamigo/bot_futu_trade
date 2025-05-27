package com.bottrade.bot.utils;

/**
 * AutoTrade
 * 缓存的key 常量
 */
public interface AtCacheConstants {
    String AUTO_TRADE = "at:";
    String ACCOUNT_VAL = AUTO_TRADE + "account_val:";

    String MAIN_ACCOUNT_VAL = ACCOUNT_VAL + "main:";

    static String formatNum(String str) {
        return str.replace("$", "").replace(",", "");
    }
}
