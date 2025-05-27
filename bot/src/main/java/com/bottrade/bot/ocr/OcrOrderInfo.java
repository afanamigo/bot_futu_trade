package com.bottrade.bot.ocr;

import com.bottrade.bot.dao.OrderConvertUtil;
import com.bottrade.model.OrderUpdateDTO;
import com.bottrade.model.OriginalOrderDTO;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class OcrOrderInfo {


    @SerializedName(value = "net_account_value",alternate = {"netAccountValue"})
    private String netAccountValue;
    @SerializedName(value = "open_pnl",alternate = {"openPnl"})
    private String openPnl;
    @SerializedName(value = "open_pnl_percent",alternate = {"openPnlPercent","openPnlPercentage"})
    private String openPnlPercent;
    @SerializedName(value = "market_value",alternate = {"marketValue"})
    private String marketValue;
    @SerializedName(value = "cash",alternate = {"buyingPower"})
    private String cash;
    @SerializedName(value = "day_pnl",alternate = {"dayPnl","daysPnl"})
    private String dayPnl;
    @SerializedName(value = "positions",alternate = {"stocks"})
    private List<Positions> positions;

    @NoArgsConstructor
    @Data
    public static class Positions {
        @SerializedName("symbol")
        private String symbol;
        @SerializedName(value = "market_value",alternate = {"marketValue"})
        private String marketValue;
        @SerializedName(value = "quantity",alternate = {"qty"})
        private Integer quantity;
        @SerializedName(value = "open_pnl",alternate = {"openPnl"})
        private String openPnl;
        @SerializedName(value = "open_pnl_percent",alternate = {"openPnlPercent","openPnlPercentage"})
        private String openPnlPercent;
        @SerializedName(value = "last_price",alternate = {"lastPrice"})
        private String lastPrice;
    }

    public OrderUpdateDTO convertToDTO(String batchId, Integer nextBatchIndex){
        OrderUpdateDTO dto = new OrderUpdateDTO();
        dto.setAccountVal(new BigDecimal(numStr(netAccountValue)));
        if(positions != null && positions.size() > 0){
            List<OriginalOrderDTO> orders = new ArrayList<>();
            for (Positions item : positions){
                OriginalOrderDTO orderDTO = new OriginalOrderDTO();
                orderDTO.setSymbol(item.symbol.toLowerCase());
                orderDTO.setSymbolFlag(item.symbol.toLowerCase() + "_" + OrderConvertUtil.getTradeType(item.getQuantity()));
                orderDTO.setLastPrice(new BigDecimal(numStr(item.lastPrice)));
                orderDTO.setQty(item.quantity);
                orderDTO.setOrderBatchId(batchId);
                orderDTO.setOrderBatchIndex(nextBatchIndex);

                orderDTO.setOrderTime(LocalDateTime.now());
                orders.add(orderDTO);
            }
            dto.setOrders(orders);
        }

        return dto;
    }

    private String numStr(String str){
        return str.replace("$", "").replace(",", "");
    }
}
