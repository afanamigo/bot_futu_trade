package com.bottrade.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单变动传输对象
 */
@Data
public class OrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String symbol;
    private String symbolFlag;
    private Integer qty;
    private Integer orderBatchIndex;//批次
    private BigDecimal lastPrice;//订单执行价格 或 市价单
    private BigDecimal orderPercent;//操作百分比
    private Integer priceTag;//是否市价单 市价/限价单 0/1
    private Integer arithFlag;//算数符号 +-, +做多 -做空

    @NotBlank(message = "开仓中、清仓 open/close")
    private String orderFlag;
    @NotBlank(message = "加仓、减仓、维持 add/reduce/hold")
    private String orderUpdateFlag;

    @NotBlank(message = "订单执行状态")
    private Integer orderStatus;

    @NotBlank(message = "订单变更时间")
    private LocalDateTime updateTime;
}
