-- 原始订单、券商信息建表sql

-- at_original_order definition
CREATE TABLE `at_original_order` (
                                     `id` int NOT NULL AUTO_INCREMENT,
                                     `order_batch_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '订单批次ID：日期+_+秒值',
                                     `order_batch_index` int DEFAULT NULL COMMENT '订单批次-index',
                                     `order_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '订单ID：symbol+秒值',
                                     `order_id_index` int DEFAULT NULL COMMENT '订单ID-Index symbol单量计数递增',
                                     `order_index` int DEFAULT NULL COMMENT '订单ID-Index 单内操作量递增（考虑废弃 无意义统计），订单维持则保持不变',
                                     `order_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开仓/清仓 open/close',
                                     `order_update_flag` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '加仓/减仓/维持 add/reduce/hold',
                                     `symbol` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标的',
                                     `symbol_flag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标的类型：symbol,symbol_opt（待实现）',
                                     `symbol_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'stock,etf,index 以及对应的 _opt（待实现）',
                                     `mkt_value` decimal(10,2) DEFAULT NULL COMMENT '市场价值',
                                     `qty` int DEFAULT NULL COMMENT '持仓数量',
                                     `arith_flag` int DEFAULT NULL COMMENT '算数符号(正负) arithmetic',
                                     `open_value` decimal(10,2) DEFAULT NULL COMMENT '浮动盈亏',
                                     `opv_percent` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '浮动盈亏比例',
                                     `avg_price` decimal(10,2) DEFAULT NULL COMMENT '持仓均价',
                                     `last_price` decimal(10,2) DEFAULT NULL COMMENT '最新价格',
                                     `create_time` datetime DEFAULT NULL COMMENT '操作时间',
                                     `order_time` datetime DEFAULT NULL COMMENT '订单接收时间',
                                     `opt_flag` int DEFAULT NULL COMMENT '是否为期权操作 0/1',
                                     `trade_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '交易类型，buy/sell',
                                     `trade_desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '交易描述 xx做多/做空, 建仓、加仓、减仓（考虑废弃）',
                                     `order_percent` decimal(10,4) DEFAULT NULL COMMENT '持仓百分比',
                                     `update_percent` decimal(10,4) DEFAULT NULL COMMENT '变化百分比',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=703 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='原始订单记录';