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
  @TableName("at_trade_account")
public class AtTradeAccount implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

    private String accountName;

      /**
     * 登录密码（md5）
     */
      private String password;

    private String brokerSymbol;

      /**
     * 业务账号
     */
      private String accAccount;

      /**
     * 交易密码（md5）

     */
      private String tradePwd;

    private Integer userId;

    private Integer tacticsId;

      /**
     * 是否开启跟单 0/1

     */
      private Integer followStatus;

    private LocalDateTime updateTime;

    private String sdkKey;

    private String sdkSecret;

    private String sdkToken;

    private LocalDateTime createTime;

    private String nickName;

    private Integer opendPort;
}
