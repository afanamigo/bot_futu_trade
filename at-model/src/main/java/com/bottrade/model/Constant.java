package com.bottrade.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Constant {

    public static final String MARKET_US = ".US";
    public static final String ORDER_SIDE_BUY = "buy";
    public static final String ORDER_SIDE_SELL = "sell";
    public static final String ORDER_NEW = "new";
    public static final String ORDER_ADD = "add";
    public static final String ORDER_CLOSE = "close";
    public static final String ORDER_REDUCE = "reduce";
    public static final String[] ORDER_SORT = {"new", "add", "close", "reduce"};

    public static final List<String> BOT_CMDS = Arrays.asList("/info");

    public interface Topics{
        String ORDER = "order-notify";
        String SIMPLE_ORDER = "order-notify-simple";

        String CMD = "at-cmd";

        String ORDER_RESULT = "order-result";

        String CMD_RESULT = "at-cmd-result";

        String ORDER_UPDATE = "order-update";
    }

    public interface Groups{
        String TRADE = "at-trade";
        String TRADE_REAL = "at-trade-real";

        String RESULT = "order-result";

        String CMD = "order-cmd";

        String CMD_RESULT = "order-cmd-result";

        String ORDER_UPDATE = "order-update";
    }

    public static enum BotCmd{
        info("/info"),
        accList("/acclist"),
        setup("/setup")
        ;

        private String cmd;

        BotCmd(String cmd) {
            this.cmd = cmd;
        }

        public String getCmd() {
            return cmd;
        }

        public static BotCmd of(String cmd){
            for (BotCmd botCmd : BotCmd.values()){
                if(Objects.equals(botCmd.cmd, cmd)){
                    return botCmd;
                }
            }
            return null;
        }
    }
}
