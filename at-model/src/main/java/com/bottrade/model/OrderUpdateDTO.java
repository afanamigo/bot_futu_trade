package com.bottrade.model;

import com.bottrade.model.bot.BotInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单更新消息对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrderUpdateDTO extends BotInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    List<OriginalOrderDTO> orders;
    List<OrderDTO> simpleOrders;
    BigDecimal accountVal;
}
