package com.bottrade.bot.dao.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bottrade.bot.dao.OrderConvertUtil;
import com.bottrade.bot.dao.entity.AtOriginalOrder;
import com.bottrade.bot.dao.mapper.AtOriginalOrderMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bottrade.bot.dao.service.OriginalOrderService;
import com.bottrade.bot.model.mq.MQHelper;
import com.bottrade.model.OrderDTO;
import com.bottrade.model.OriginalOrderDTO;
import com.bottrade.model.utils.MathUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dayun
 * @since 2024-03-27
 */
@Service
public class AtOriginalOrderService
        extends ServiceImpl<AtOriginalOrderMapper, AtOriginalOrder>
        implements OriginalOrderService {

    @Autowired
    private MQHelper mqHelper;

    public Integer getNextBatchIndex() {
        Integer nextIndex = this.getBatchIndex() + 1;
        return nextIndex;
    }

    public Integer getBatchIndex() {//缓存或distinct batch id
        Integer lastIndex;
        AtOriginalOrder lastOrder = this.lambdaQuery().orderByDesc(AtOriginalOrder::getId).last("limit 1").one();
        if (null != lastOrder) {
            lastIndex = lastOrder.getOrderBatchIndex();
        } else {
            lastIndex = 0;
        }
        return lastIndex;
    }

    public Boolean saveBatchOrders(List<OriginalOrderDTO> orderDTOS) {
        List<AtOriginalOrder> list = OrderConvertUtil.convertDTOs(orderDTOS);
        long count = this.count(Wrappers.<AtOriginalOrder>query().lambda().eq(AtOriginalOrder::getOrderBatchIndex, orderDTOS.get(0).getOrderBatchIndex()));
        if (count == 0) {
            return this.saveBatch(list);
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean save(OriginalOrderDTO dto) {
        return null;
    }

    public Boolean compareLastBatch(Integer batchIndex, BigDecimal netVal) { //try catch : 如果字母识别错误。如：YCL -> YEL （查询标的操作库，如没有匹配 跳过?模糊匹配?）
        Map<String, List<OrderDTO>> orderMap = new HashMap<>();

        // todo 修改为-> 持有数量加减仓
        List<OrderDTO> ordersAdd = new ArrayList<>();
        List<OrderDTO> ordersReduce = new ArrayList<>();
        List<OrderDTO> ordersClose = new ArrayList<>();
        List<OrderDTO> ordersNew = new ArrayList<>();

        if (batchIndex > 1) {
            //操作顺序：1.清仓 2.新开仓 3.加减仓
            //清仓
            Integer lastBatchIndex = batchIndex - 1;
            List<AtOriginalOrder> newOrders = this.list(Wrappers.<AtOriginalOrder>lambdaQuery().eq(AtOriginalOrder::getOrderBatchIndex, batchIndex));
            List<AtOriginalOrder> lastOrders = this.list(Wrappers.<AtOriginalOrder>lambdaQuery().eq(AtOriginalOrder::getOrderBatchIndex, lastBatchIndex).eq(AtOriginalOrder::getOrderFlag,"open"));
            Map<String, AtOriginalOrder> newOrderMap = newOrders.stream().collect(Collectors.toMap(AtOriginalOrder::getSymbolFlag, Collectors -> Collectors));
            Map<String, AtOriginalOrder> lastOrderMap = lastOrders.stream().collect(Collectors.toMap(AtOriginalOrder::getSymbolFlag, Collectors -> Collectors));

            List<String> newOrderSymbols = newOrders.stream().map(AtOriginalOrder::getSymbolFlag).collect(Collectors.toList());
            List<String> lastOrderSymbols = lastOrders.stream().map(AtOriginalOrder::getSymbolFlag).collect(Collectors.toList());

            //新 -> 旧 差集（新开仓的订单）
            List<String> newSymbols = CollectionUtil.subtractToList(newOrderSymbols, lastOrderSymbols);
            for (int i = 0; i < newSymbols.size(); i++) {
                if (null == lastOrderMap.get(newSymbols.get(i))) {
                    String symbol = newSymbols.get(i);
                    AtOriginalOrder newOrder = newOrderMap.get(symbol);
                    int qty = Math.abs(newOrder.getQty());

                    BigDecimal lastPrice = newOrder.getLastPrice();
                    BigDecimal orderPercent = MathUtil.calculateOrderPercent(lastPrice, new BigDecimal(Math.abs(qty)), netVal);
                    newOrder.setOrderPercent(orderPercent);
                    AtOriginalOrder lastOrder = lastOrderMap.get(symbol);
                    newOrder.setUpdatePercent(orderPercent.subtract(lastOrder.getOrderPercent()).abs());

                    newOrder.setOrderBatchIndex(batchIndex);
                    newOrder.setOrderFlag("open");
                    newOrder.setOrderUpdateFlag("add");

                    OrderDTO orderDTO = new OrderDTO();
                    BeanUtils.copyProperties(newOrder, orderDTO);
                    ordersNew.add(orderDTO);
                    this.saveOrUpdate(newOrder);
                }
            }

            //旧 -> 新 差集（关闭头寸的订单）
            List<String> closeSymbols = CollectionUtil.subtractToList(lastOrderSymbols, newOrderSymbols);
            for (int i = 0; i < closeSymbols.size(); i++) {
                AtOriginalOrder closeOrder;
                if (null != lastOrderMap.get(closeSymbols.get(i))) {

                    BigDecimal orderPercent = BigDecimal.ONE;
                    closeOrder = lastOrderMap.get(closeSymbols.get(i));
                    closeOrder.setId(null);
                    closeOrder.setTradeType("sell");
                    closeOrder.setOrderUpdateFlag("reduce");
                    closeOrder.setOrderFlag("close");
                    closeOrder.setOrderBatchId(newOrders.get(0).getOrderBatchId());
                    closeOrder.setOrderBatchIndex(batchIndex);
                    closeOrder.setOrderPercent(orderPercent);
                    closeOrder.setUpdatePercent(orderPercent);

                    OrderDTO orderDTO = new OrderDTO();
                    BeanUtils.copyProperties(closeOrder, orderDTO);
                    orderDTO.setPriceTag(0);//策略获取 默认0
                    ordersClose.add(orderDTO);
                    this.save(closeOrder);
                }
            }

            //交集 -> 已有订单维持/变化
            List<String> keepSymbols = new ArrayList<>();
            keepSymbols.addAll(CollectionUtil.intersectionDistinct(newOrderSymbols, lastOrderSymbols));
            for (int i = 0; i < keepSymbols.size(); i++) {
                String symbol = keepSymbols.get(i);
                AtOriginalOrder newOrder = newOrderMap.get(symbol);
                AtOriginalOrder lastOrder = lastOrderMap.get(symbol);
                newOrder.setOrderFlag("open");
                int orderCompare = NumberUtils.compare(Math.abs(newOrder.getQty()), Math.abs(lastOrder.getQty()));

                if (orderCompare > 0) {//1 加仓
                    int qty = Math.abs(newOrder.getQty()) - Math.abs(lastOrder.getQty());
                    BigDecimal orderPercent = MathUtil.calculate(new BigDecimal(qty),new BigDecimal(Math.abs(newOrder.getQty())), '/');

                    newOrder.setOrderBatchIndex(batchIndex);
                    newOrder.setOrderPercent(lastOrder.getOrderPercent().add(orderPercent));
                    newOrder.setUpdatePercent(orderPercent.subtract(lastOrder.getOrderPercent()).abs());

                    newOrder.setOrderUpdateFlag("add");
                    OrderDTO orderDTO = new OrderDTO();
                    BeanUtils.copyProperties(newOrder, orderDTO);
                    ordersAdd.add(orderDTO);
                    this.updateById(newOrder);
                } else if (orderCompare < 0) {//-1 减仓
                    int qty = Math.abs(lastOrder.getQty()) - Math.abs(newOrder.getQty());
                    BigDecimal orderPercent = MathUtil.calculate(new BigDecimal(qty),new BigDecimal(Math.abs(lastOrder.getQty())), '/');

                    newOrder.setOrderBatchIndex(batchIndex);
                    newOrder.setUpdatePercent(orderPercent);
                    newOrder.setOrderPercent(lastOrder.getOrderPercent().subtract(orderPercent));

                    newOrder.setOrderUpdateFlag("reduce");
                    OrderDTO orderDTO = new OrderDTO();
                    BeanUtils.copyProperties(newOrder, orderDTO);
                    ordersReduce.add(orderDTO);
                    this.updateById(newOrder);
                } else {
                    //订单维持
                    newOrder.setOrderUpdateFlag("hold");
                    this.updateById(newOrder);

                }
            }

            //发送订单更新MQ通知
            orderMap.put("add", ordersAdd); //加仓 做多buy 做空sell
            orderMap.put("reduce", ordersReduce);//减仓
            orderMap.put("close", ordersClose);//平仓（减仓）
            orderMap.put("new", ordersNew);//建仓（加仓）

        } else {//新开单操作
            List<AtOriginalOrder> newOrders = this.list(Wrappers.<AtOriginalOrder>lambdaQuery().eq(AtOriginalOrder::getOrderBatchIndex, batchIndex));
            for (int i = 0; i < newOrders.size(); i++) {
                AtOriginalOrder newOrder = newOrders.get(i);
                int qty = newOrder.getQty();
                BigDecimal lastPrice = newOrder.getLastPrice();
                BigDecimal orderPercent = MathUtil.calculateOrderPercent(lastPrice, new BigDecimal(Math.abs(qty)), netVal);
                newOrder.setOrderPercent(orderPercent);
                newOrder.setUpdatePercent(orderPercent);
                newOrder.setOrderUpdateFlag("add");
                newOrder.setOrderFlag("open");

                OrderDTO orderDTO = new OrderDTO();
                BeanUtils.copyProperties(newOrder, orderDTO);
                ordersNew.add(orderDTO);
                this.saveOrUpdate(newOrder);//for sql优化
                orderMap.put("new", ordersNew);
            }
        }

        this.sendOrderUpdate(orderMap);

        return Boolean.TRUE;
    }

    public void sendOrderUpdate(Map<String, List<OrderDTO>> orderMap){
        mqHelper.publishUpdateOrder(orderMap);
    }


}
