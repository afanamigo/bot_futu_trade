package com.bottrade.bot.dao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bottrade.bot.dao.entity.AtOriginalOrder;
import com.bottrade.model.OrderDTO;
import com.bottrade.model.OriginalOrderDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Johnny.L
 * @date 2023/12/17
 */
public interface OriginalOrderService extends IService<AtOriginalOrder> {

	/**
	 * 保存订单 -> 分析订单变化
	 * @param dto
	 * @return
	 */
	Boolean save(OriginalOrderDTO dto);
	Boolean compareLastBatch(Integer batchIndex, BigDecimal netVal);

	void sendOrderUpdate(Map<String, List<OrderDTO>> orderMap);

	/**
	 * 批量保存订单并分析订单变化
	 * @param orderDTOS 订单更新
	 * @return true/false
	 */
	Boolean saveBatchOrders(List<OriginalOrderDTO> orderDTOS);

	/**
	 * 缓存 -> 订单批次
	 * @return
	 */
	Integer getNextBatchIndex();
	Integer getBatchIndex();
}
