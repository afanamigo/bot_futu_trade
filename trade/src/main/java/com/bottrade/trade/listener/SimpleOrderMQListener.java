package com.bottrade.trade.listener;

import com.bottrade.model.Constant;
import com.bottrade.model.OrderDTO;
import com.bottrade.model.OrderUpdateDTO;
import com.bottrade.model.bot.OrderResult;
import com.bottrade.model.utils.Utils;
import com.bottrade.trade.model.moomoo.MoomooClient;
import com.bottrade.trade.model.mq.MQHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RocketMQMessageListener(topic = Constant.Topics.SIMPLE_ORDER, consumerGroup = Constant.Groups.TRADE,messageModel = MessageModel.BROADCASTING)
public class SimpleOrderMQListener implements RocketMQListener<OrderUpdateDTO> {

    @Autowired
    private MoomooClient moomooClient;

    @Autowired
    private MQHelper mqHelper;

    @Override
    public void onMessage(OrderUpdateDTO dto) {
        if(dto != null && dto.getSimpleOrders() != null){
            log.info("[SimpleOrderMQListener] dto="+dto);
            for (OrderDTO orderDTO : dto.getSimpleOrders()){
                try {
                    //check DTBP
                     moomooClient.order(orderDTO).whenComplete((rsp, throwable) -> {
                         OrderResult orderResult = new OrderResult();
                         orderResult.setSourceChatId(dto.getSourceChatId());
                         orderResult.setBotUsername(dto.getBotUsername());
                         if(throwable != null){
                             orderResult.setSuc(false);
                             orderResult.setErrorMsg(throwable.toString());
                         }else {
                             log.info("rsp=" + rsp);
                             orderResult.setSuc(rsp.getRetType() == 0);
                             if(orderResult.isSuc()){
                                 orderResult.setOrderId(String.valueOf(rsp.getS2C().getOrderID()));
                             }else {
                                 orderResult.setErrorMsg(rsp.getRetMsg());
                             }
                         }
                         log.info("[SimpleOrderMQListener] orderResult=" + orderResult);
                         mqHelper.notifyResult(orderResult);
                     });
                }catch (Exception e){
                    log.info("[SimpleOrderMQListener] error="+e);
                }
            }
        }
    }
}
