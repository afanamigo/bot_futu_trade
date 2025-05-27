package com.bottrade.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 原始订单记录DTO
 * 队列使用
 */
@Data
@Schema(description = "原始订单记录DTO")
public class OriginalOrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "订单ID：日期+ _symbol+ _秒值")
    private String orderId;
    @NotBlank(message = "订单批次ID：日期+ _秒值")
    private String orderBatchId;
    @NotBlank(message = "订单批次Index")
    private Integer orderBatchIndex;
    @NotBlank(message = "标的")
    private String symbol;
    @NotBlank(message = "标的类型：symbol,symbol_opt")
    private String symbolFlag;
    @NotBlank(message = "市场价值")
    private BigDecimal mktValue;
    @NotBlank(message = "浮动盈亏")
    private BigDecimal openValue;
    @NotBlank(message = "最新价格")
    private BigDecimal lastPrice;
    @NotBlank(message = "持有数量")
    private Integer qty;
    @NotBlank(message = "浮动盈亏比例")
    private String opvPercent;
    @NotBlank(message = "持仓均价")
    private BigDecimal avgPrice;
    @NotBlank(message = "订单金额百分比(占总金额), 如数量小于10&金额小于500 不取用百分比")
    private BigDecimal orderPercent;
    @NotBlank(message = "订单接收时间")
    private LocalDateTime orderTime;

    @NotBlank(message = "交易类型 buy/sell 0/1")
    private Integer tradeType;
    @NotBlank(message = "开仓、清仓 open/close")
    private String orderFlag;
    @NotBlank(message = "加仓、减仓、维持 hold/add/reduce/off") // 维持/加仓/减仓/清仓/ 0/1/2/3
    private String orderUpdateFlag;
}
