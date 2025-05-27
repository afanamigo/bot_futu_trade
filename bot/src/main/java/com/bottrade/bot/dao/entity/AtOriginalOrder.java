package com.bottrade.bot.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author dayun
 * @since 2024-03-27
 */
@Getter
@Setter
  @TableName("at_original_order")
public class AtOriginalOrder implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 订单批次ID：日期+_+秒值
     */
      private String orderBatchId;

      /**
     * 订单ID：symbol+秒值

     */
      private String orderId;

      /**
     * 订单ID-Index symbol单量计数递增

     */
      private String orderIdIndex;

      /**
     * 订单ID-Index symbol单量计数递增
     */
      private Integer orderIndex;

      /**
     * 订单批次-index

     */
      private Integer orderBatchIndex;

      /**
     * 开仓、清仓 open->close

     */
      private String orderFlag;

      /**
     * 加仓、减仓、维持 add/reduce/hold

     */
      private String orderUpdateFlag;

      /**
     * 操作标的

     */
      private String symbol;

      /**
     * 标的类型：symbol,symbol_opt

     */
      private String symbolFlag;

      /**
     * stock,etf,index 以及对应的 _opt

     */
      private String symbolType;

      /**
     * 市场价值, 订单总时值

     */
      private BigDecimal mktValue;

      /**
     * 订单数量

     */
      private Integer qty;

      /**
     * 算数符号 +-, +做多 -做空

     */
      private Integer arithFlag;

      /**
     * 浮动盈亏

     */
      private BigDecimal openValue;

      /**
     * 浮动盈亏百分比
     */
      private String opvPercent;

      /**
     * 持仓均价

     */
      private BigDecimal avgPrice;

      /**
     * 最新价格

     */
      private BigDecimal lastPrice;

      /**
     * 订单金额百分比(占总金额), 如数量小于10&金额小于500 不取用百分比
     */
      private BigDecimal orderPercent;

      /**
     * 订单更新百分比
     */
      private BigDecimal updatePercent;

    private LocalDateTime createTime;

      /**
     * 订单接收时间

     */
      private LocalDateTime orderTime;

      /**
     * 是否为期权：0/1
     */
      private Integer optFlag;

      /**
     * 交易类型 buy/sell
     */
      private String tradeType;

      /**
     * 交易描述 xx做多/做空, 建仓、加仓、减仓
     */
      private String tradeDesc;
}
