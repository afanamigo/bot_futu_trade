package com.bottrade.trade.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import com.bottrade.model.Constant;
import com.bottrade.model.OrderUpdateDTO;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RocketMQMessageListener(topic = Constant.Topics.ORDER, consumerGroup = Constant.Groups.TRADE_REAL,messageModel = MessageModel.BROADCASTING)
public class OrderMQListener implements RocketMQListener<OrderUpdateDTO> {

    /**
     * 订单分析
     * 操作流程：
     * 1. 线性订单首笔进行资金百分比下单 -> 2.后续加减仓按照原始订单数量变动比例操作
     *
     * [初版简化版本]：每笔订单计算资金百分比  标的*市价/总资金 计算本次订单持仓比例 -> 查询账户持仓比例 -> 差值
     * 1. 计算本次订单比例 -> 查询操作账户是否持仓[计算比例] -> 差值操作持仓买卖
     * @param dto
     */
    @Override
    public void onMessage(OrderUpdateDTO dto) {
        log.info("[OrderMQListener]receive message: " + dto);
    }
}
