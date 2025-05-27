package com.bottrade.bot.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
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
  @TableName("at_market_broker")
public class AtMarketBroker implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      /**
     * 券商名字

     */
      private String name;

      /**
     * 券商代号,缩写

     */
      private String brokerSymbol;

    private String logo;

      /**
     * 是否有效 0/1
     */
      private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
