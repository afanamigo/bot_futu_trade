package com.bottrade.model.utils;

import cn.hutool.core.util.NumberUtil;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class MathUtil {

    public static final char ADD='+';
    public static final char SUB='-';
    public static final char MUL='*';
    public static final char DIV='/';

//    NumberUtil.mul(orderDTO.getLastPrice(),new BigDecimal(qty))

    public static BigDecimal calculateOrderPercent(BigDecimal qty, BigDecimal price,BigDecimal netVal){
        return qty.multiply(price).divide(netVal,4, RoundingMode.UP);
    }

    public static BigDecimal calculate(BigDecimal firstValue, BigDecimal secondValue, char currentOp) {
        BigDecimal result;
        switch (currentOp) {
            case '+':
                result = NumberUtil.add(firstValue, secondValue);
                break;
            case '-':
                result = NumberUtil.sub(firstValue, secondValue);
                break;
            case '*':
                result = NumberUtil.mul(firstValue, secondValue);
                break;
            case '/':
                result = NumberUtil.div(firstValue, secondValue);
                break;
            case '%':
                result = NumberUtil.toBigDecimal(firstValue).remainder(NumberUtil.toBigDecimal(secondValue));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + currentOp);
        }
        return result.setScale(4);
    }
}
