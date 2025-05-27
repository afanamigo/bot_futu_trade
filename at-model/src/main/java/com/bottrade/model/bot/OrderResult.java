package com.bottrade.model.bot;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class OrderResult extends BaseBotResult {

    private Integer noSeril;

    private boolean suc;

    private String orderId;

}
