package com.bottrade.bot.dao;

import cn.hutool.core.text.StrPool;
import com.bottrade.bot.dao.entity.AtOriginalOrder;
import com.bottrade.model.OriginalOrderDTO;
import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class OrderConvertUtil {
    public static final String CACHE_AUTO_TRADE_GLOBAL="auto_trade";
    public static final String CACHE_BATCH_INDEX_KEY="auto_trade:cache_batch_index:";
    public static final String SYMBOL="symbol";
    public static final String MKT_VALUE="mktValue";
    public static final String OPEN_VALUE="openValue";
    public static final String LAST_PRICE="lastPrice";
    public static final String QTY="qty";
    public static final String OPV_PERCENT="opvPercent";
    public static final String AVG_PRICE="avgPrice";


    public static Map<String,String> convertOrderMapFromList(List<String> orderInfos){
        Map<String,String> orderInfoMap = new HashMap<>();
        orderInfoMap.put(SYMBOL,orderInfos.get(0));
        orderInfoMap.put(QTY,orderInfos.get(1));
        orderInfoMap.put(AVG_PRICE,orderInfos.get(2));
        orderInfoMap.put(LAST_PRICE,orderInfos.get(3));

//        orderInfoMap.put(AVG_PRICE,orderInfos.get(4));
//        orderInfoMap.put(MKT_VALUE,orderInfos.get(1));

//        orderInfoMap.put(OPEN_VALUE,orderInfos.get());
//        orderInfoMap.put(OPV_PERCENT,orderInfos.get());
        return orderInfoMap;
    }

    public static List<AtOriginalOrder> convertDTOs(List<OriginalOrderDTO> orderDTOS){
        return orderDTOS.stream().map(OrderConvertUtil::getOriginalOrder).collect(Collectors.toList());
    }

    public static Integer getArithFlag(Integer num){
        Integer flag = 0;
        if(num>0){
            flag = 1;
        }else if(num<0){
            flag = -1;
        }
        return flag;
    }

    public static String getTradeType(Integer num){
        String flag = "";
        if(num>0){
            flag = "buy";
        }else if(num<0){
            flag = "sell";
        }
        return flag;
    }

    @NotNull
    public static AtOriginalOrder getOriginalOrder(OriginalOrderDTO dto) {
        AtOriginalOrder orderEntity = new AtOriginalOrder();
        orderEntity.setSymbol(dto.getSymbol());
        orderEntity.setSymbolFlag(dto.getSymbolFlag());
        orderEntity.setMktValue(dto.getMktValue());
        orderEntity.setOpenValue(dto.getOpenValue());
        orderEntity.setLastPrice(dto.getLastPrice());
        orderEntity.setQty(dto.getQty());
        orderEntity.setOrderPercent(dto.getOrderPercent());
        orderEntity.setOpvPercent(dto.getOpvPercent());
        orderEntity.setAvgPrice(dto.getAvgPrice());
        orderEntity.setOrderBatchIndex(dto.getOrderBatchIndex());
        orderEntity.setOrderBatchId(dto.getOrderBatchId());//订单批次id


        //+- 符号与做多做空
        orderEntity.setArithFlag(getArithFlag(dto.getQty()));
        orderEntity.setTradeType(getTradeType(dto.getQty()));

//        orderEntity.setOrderId();//订单id
//        orderEntity.setOrderId();//订单id-index
//        orderEntity.setOrderIndex();//批次订单操作index
//        orderEntity.setOrderFlag();//开仓/清仓状态
//        orderEntity.setOrderUpdateFlag();//加仓/减仓/维持

//        orderEntity.setTradeFlag();
//        orderEntity.setTradeType();
//        orderEntity.setTradeDesc();

        orderEntity.setOrderTime(dto.getOrderTime());
        return orderEntity;
    }

    public static String getOrderId(String symbol, String batchId){
        String[] s = batchId.split(StrPool.UNDERLINE);
        return s[0] + StrPool.UNDERLINE + s[1];//yyyy-MM-dd_symbol_sec
    }
}
