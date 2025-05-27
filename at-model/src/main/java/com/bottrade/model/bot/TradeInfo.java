package com.bottrade.model.bot;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TradeInfo implements Serializable {

    private Double power; //最大购买力（此字段是按照 50% 的融资初始保证金率计算得到的 近似值。但事实上，每个标的的融资初始保证金率并不相同。我们建议您使用 查询最大可买可卖 接口返回的 最大可买 字段，来判断实际可买入的最大数量）
    private Double totalAssets; //资产净值
    private Double cash; //现金
    private Double marketVal; //证券市值, 仅证券账户适用
    private Double frozenCash; //冻结资金
    private Double debtCash; //计息金额
    private Double avlWithdrawalCash; //现金可提，仅证券账户适用
    private Double dtbpAmount; // 日内限额

    private List<Position> positions; // 持仓列表

    @Data
    public static class Position{
        private String code;           //代码
        private String name;           //名称
        private Double qty;            //持有数量，2位精度，期权单位是"张"，下同
        private Double canSellQty;     //可用数量，是指持有的可平仓的数量。可用数量=持有数量-冻结数量。期权和期货的单位是“张”。
        private Double price;          //市价，3位精度，期货为2位精度
        private Double costPrice;      //摊薄成本价（证券账户），平均开仓价（期货账户）。证券无精度限制，期货为2位精度，如果没传，代表此时此值无效
        private Double val;            //市值，3位精度, 期货此字段值为0
        private Double marketVal;            //持仓/证券市值，3位精度, 期货此字段值为0
    }
}
