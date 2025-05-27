package com.bottrade.trade.model.moomoo;

import com.bottrade.model.Constant;
import com.bottrade.model.OrderDTO;
import com.bottrade.model.bot.TradeInfo;
import com.bottrade.model.utils.Utils;
import com.google.protobuf.GeneratedMessageV3;
import com.moomoo.openapi.*;
import com.moomoo.openapi.pb.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
public class MoomooClient implements MMSPI_Conn, MMSPI_Trd {

    private MMAPI_Conn_Trd trd;
    private MMAPI_Conn_Qot qot;

    private boolean connected = false;

    private ConcurrentHashMap<Integer,CompletableFuture> futureMap;

    private CompletableFuture<Boolean> startFuture;

    @Autowired
    private MoomooAttributes moomooAttributes;

    private boolean unlocked = false;


    public MoomooClient(){
        //交易连接
        trd = new MMAPI_Conn_Trd();
        trd.setClientInfo("javaclient", 1);  //设置客户端信息
        trd.setConnSpi(this);  //设置连接回调
        trd.setTrdSpi(this);   //设置交易回调

        //行情订阅
        qot = new MMAPI_Conn_Qot();
        qot.setClientInfo("javaclient", 1);
        qot.setConnSpi(this);
        trd.setTrdSpi(this);
        this.futureMap = new ConcurrentHashMap<>();
    }

    @Override
    public void onInitConnect(MMAPI_Conn client, long errCode, String desc) {
        MMSPI_Conn.super.onInitConnect(client, errCode, desc);
        log.info("[MoomooClient] - onInitConnect- errCode="+errCode+",desc =" + desc);
        connected = errCode == 0L;
        if(this.startFuture != null){
            this.startFuture.complete(connected);
        }
    }

    @Override
    public void onDisconnect(MMAPI_Conn client, long errCode) {
        MMSPI_Conn.super.onDisconnect(client, errCode);
        log.info("[MoomooClient] - onDisconnect , error="+errCode);
        connected = false;
        startFuture = null;
        unlocked = false;
    }

    private CompletableFuture<Boolean> start(){
        log.info("[MoomooClient] - start - " + moomooAttributes);
        startFuture = new CompletableFuture<>();
        final short port = (short) moomooAttributes.getOpenDPort();
        trd.initConnect("127.0.0.1", port, false);
        return startFuture;
    }

    public void close() {
        if (connected) {
            trd.close();
            qot.close();
        }
    }

    private void executeInternal(Function function){
        if(!connected){
            CompletableFuture<Boolean> future = startFuture == null ? start() : startFuture;
            future.whenComplete((aBoolean, throwable) -> {
                if(aBoolean != null && aBoolean){
                    function.apply(1);
                }else{
                    for (Integer key : futureMap.keySet()){
                        futureMap.get(key).completeExceptionally(new RuntimeException("init connection fail"));
                    }
                    futureMap.clear();
                    startFuture = null;
                    connected = false;
                }
            });
        }else {
            function.apply(1);
        }
    }

    public CompletableFuture<TrdPlaceOrder.Response> orderReal(OrderDTO dto){
        if(unlocked){
            return sendOrder(dto);
        }
        final CompletableFuture<TrdPlaceOrder.Response> orderFuture = new CompletableFuture<>();
        final CompletableFuture<TrdUnlockTrade.Response> unlockFuture = unlock();

        unlockFuture.whenComplete((response, throwable) -> {
            if (throwable != null || response.getRetType() != 0) {
                orderFuture.completeExceptionally(throwable == null ? new RuntimeException(Utils.decodeRetMsg(response.getRetMsg())) : throwable);
            }else {
                sendOrder(dto).whenComplete((or, ot) -> {
                    if(ot != null){
                        orderFuture.completeExceptionally(ot);
                    }else {
                        orderFuture.complete(or);
                    }
                });
            }
        });
        return orderFuture;
    }

    public CompletableFuture<TrdPlaceOrder.Response> order(OrderDTO dto){
        if(moomooAttributes.getTrdEnv() == 1){
            return orderReal(dto);
        }else {
            return sendOrder(dto);
        }
    }

    /**
     * 下单
     */
    public CompletableFuture<TrdPlaceOrder.Response> sendOrder(OrderDTO dto){
        final CompletableFuture<TrdPlaceOrder.Response> future = new CompletableFuture<>();

//        final CompletableFuture<QotGetBasicQot.Response> quoteFuture = getQuote(dto.getSymbol());
//        QotGetBasicQot.Response qotResp = quoteFuture.get();
//        QotCommon.BasicQot basicQotList = qotResp.getS2C().getBasicQotList();
//        double orderPrice = basicQotList.getCurPrice() * basicQotList.getBasicQotList();
//        double dtbpAmount = getInfo().get().getDtbpAmount();//日内限额 (ratio系数)
//
//        if(orderPrice * 1.2 > dtbpAmount){
//            return null; //不执行订单
//        }

        Function func = o -> {
            String symbolFlag = dto.getSymbolFlag().split("_")[1];
            String symbol = dto.getSymbol().toUpperCase();
            TrdCommon.TrdHeader header = TrdCommon.TrdHeader.newBuilder()
                    .setAccID(moomooAttributes.getAccId())
                    .setTrdEnv(moomooAttributes.getTrdEnv())
                    .setTrdMarket(moomooAttributes.getTrdMarket())
                    .build();
            TrdPlaceOrder.C2S.Builder c2sBuilder = TrdPlaceOrder.C2S.newBuilder()
                    .setPacketID(trd.nextPacketID())
                    .setHeader(header)
                    .setSecMarket(TrdCommon.TrdSecMarket.TrdSecMarket_US_VALUE)
                    .setCode(symbol)
                    .setQty(Math.abs(dto.getQty()));

            int orderType = TrdCommon.OrderType.OrderType_Market_VALUE;
            if(dto.getLastPrice() != null){
                double price = dto.getLastPrice().doubleValue();
                if(price > 0){
                    c2sBuilder.setPrice(price);
                    c2sBuilder.setFillOutsideRTH(true);
                    orderType = TrdCommon.OrderType.OrderType_Normal_VALUE;
                }
            }
            c2sBuilder.setOrderType(orderType);

            if (Constant.ORDER_SIDE_SELL.equalsIgnoreCase(symbolFlag)) {
                c2sBuilder.setTrdSide(TrdCommon.TrdSide.TrdSide_Sell_VALUE);
            } else if (Constant.ORDER_SIDE_BUY.equalsIgnoreCase(symbolFlag)) {
                c2sBuilder.setTrdSide(TrdCommon.TrdSide.TrdSide_Buy_VALUE);
            }
            TrdPlaceOrder.Request req = TrdPlaceOrder.Request.newBuilder().setC2S(c2sBuilder.build()).build();
            int seqNo = trd.placeOrder(req);
            futureMap.put(seqNo,future);
            return null;
        };
        executeInternal(func);

        return future;
    }

    /**
     * 获取账户金额
     */
    public CompletableFuture<TrdGetFunds.Response> getFunds(){
        final CompletableFuture<TrdGetFunds.Response> future = new CompletableFuture<>();
        Function func = o -> {
            TrdCommon.TrdHeader header = TrdCommon.TrdHeader.newBuilder()
                    .setAccID(moomooAttributes.getAccId())
                    .setTrdEnv(moomooAttributes.getTrdEnv())
                    .setTrdMarket(moomooAttributes.getTrdMarket())
                    .build();
            TrdGetFunds.C2S c2s = TrdGetFunds.C2S.newBuilder()
                    .setHeader(header)
                    .build();
            TrdGetFunds.Request req = TrdGetFunds.Request.newBuilder().setC2S(c2s).build();
            int seqNo = trd.getFunds(req);
            futureMap.put(seqNo,future);
            return null;
        };
        executeInternal(func);
        return future;
    }

    /**
     * 查询行情
     * @param symbol
     * @return
     */
    public CompletableFuture<QotGetBasicQot.Response> getQuote(String symbol){
        final CompletableFuture<QotGetBasicQot.Response> future = new CompletableFuture<>();
        Function func = o -> {
            QotCommon.Security sec = QotCommon.Security.newBuilder()
                    .setMarket(TrdCommon.TrdSecMarket.TrdSecMarket_US_VALUE)
                    .setCode(symbol)
                    .build();

            QotSub.C2S c2s = QotSub.C2S.newBuilder()
                    .addSecurityList(sec)
                    .addSubTypeList(QotCommon.SubType.SubType_Basic_VALUE)
                    .setIsSubOrUnSub(true)
                    .build();
            QotSub.Request req = QotSub.Request.newBuilder().setC2S(c2s).build();
            int seqNo = qot.sub(req);

            QotGetBasicQot.C2S c2sQot = QotGetBasicQot.C2S.newBuilder()
                    .addSecurityList(sec)
                    .build();
            QotGetBasicQot.Request reqQot = QotGetBasicQot.Request.newBuilder().setC2S(c2sQot).build();
            int seqNoQot = qot.getBasicQot(reqQot);
            futureMap.put(seqNoQot, future);
            return null;
        };
        executeInternal(func);
        return future;
    }


    /**
     * 获取基本信息
     * @return
     */
    public CompletableFuture<TradeInfo> getInfo(){
        final CompletableFuture<TrdGetFunds.Response> fundsFuture = getFunds();
        final CompletableFuture<TrdGetPositionList.Response> positionListFuture = getPositionList();
        CompletableFuture<TradeInfo> infoFuture = new CompletableFuture<>();
        CompletableFuture.allOf(fundsFuture,positionListFuture)
                .whenComplete((unused, throwable) -> {
                    if(throwable != null){
                        infoFuture.completeExceptionally(throwable);
                    }else {
                        try {
                            TrdGetFunds.Response fundsRsp = fundsFuture.get();
                            TrdGetPositionList.Response positionRsp = positionListFuture.get();
                            if(fundsRsp.getRetType() != 0 || positionRsp.getRetType() != 0){
                                infoFuture.completeExceptionally(new RuntimeException(
                                        Utils.decodeRetMsg(fundsRsp.getRetMsg()) + " -- " + Utils.decodeRetMsg(positionRsp.getRetMsg())
                                ));
                            }else {
                                infoFuture.complete(convert2TradeInfo(fundsRsp,positionRsp));
                            }
                        } catch (Exception e) {
                            infoFuture.completeExceptionally(new RuntimeException(e));
                        }
                    }
                });
        return infoFuture;
    }

    private CompletableFuture<TrdGetPositionList.Response> getPositionList(){
        final CompletableFuture<TrdGetPositionList.Response> future = new CompletableFuture<>();
        Function func = o -> {
            TrdCommon.TrdHeader header = TrdCommon.TrdHeader.newBuilder()
                    .setAccID(moomooAttributes.getAccId())
                    .setTrdEnv(moomooAttributes.getTrdEnv())
                    .setTrdMarket(moomooAttributes.getTrdMarket())
                    .build();
            TrdGetPositionList.C2S c2s = TrdGetPositionList.C2S.newBuilder()
                    .setHeader(header)
                    .build();
            TrdGetPositionList.Request req = TrdGetPositionList.Request.newBuilder().setC2S(c2s).build();
            int seqNo = trd.getPositionList(req);
            futureMap.put(seqNo,future);
            return null;
        };
        executeInternal(func);
        return future;
    }

    public CompletableFuture<TrdGetAccList.Response> getAccList(){
        final CompletableFuture<TrdGetAccList.Response> future = new CompletableFuture<>();
        Function func = o -> {
            TrdGetAccList.C2S c2s = TrdGetAccList.C2S.newBuilder().setUserID(0)
                    .build();
            TrdGetAccList.Request req = TrdGetAccList.Request.newBuilder().setC2S(c2s).build();
            int seqNo = trd.getAccList(req);
            futureMap.put(seqNo,future);
            return null;
        };
        executeInternal(func);
        return future;
    }

    public CompletableFuture<TrdUnlockTrade.Response> unlock(){
        CompletableFuture<TrdUnlockTrade.Response> future = new CompletableFuture<>();
        Function func = o -> {
            TrdUnlockTrade.C2S c2s = TrdUnlockTrade.C2S.newBuilder()
                    .setPwdMD5(moomooAttributes.getPwdMD5())
                    .setUnlock(true)
                    .setSecurityFirm(moomooAttributes.getSecurityFirm())
                    .build();
            TrdUnlockTrade.Request req = TrdUnlockTrade.Request.newBuilder().setC2S(c2s).build();
            int seqNo = trd.unlockTrade(req);
            futureMap.put(seqNo,future);
            return null;
        };
        executeInternal(func);
        return future;
    }

    @Override
    public void onReply_UnlockTrade(MMAPI_Conn client, int nSerialNo, TrdUnlockTrade.Response rsp) {
        MMSPI_Trd.super.onReply_UnlockTrade(client, nSerialNo, rsp);
        log.info("[MoomooClient] - onReply_UnlockTrade" + rsp);
        unlocked = rsp.getRetType() == 0;
        completeFuture(nSerialNo, rsp);
    }

    @Override
    public void onReply_GetFunds(MMAPI_Conn client, int nSerialNo, TrdGetFunds.Response rsp) {
        MMSPI_Trd.super.onReply_GetFunds(client, nSerialNo, rsp);
        log.info("[MoomooClient] - onReply_GetFunds" + rsp);
        completeFuture(nSerialNo, rsp);

    }

    @Override
    public void onReply_PlaceOrder(MMAPI_Conn client, int nSerialNo, TrdPlaceOrder.Response rsp) {
        MMSPI_Trd.super.onReply_PlaceOrder(client, nSerialNo, rsp);
        log.info("[MoomooClient] - onReply_PlaceOrder" + rsp);
        completeFuture(nSerialNo, rsp);
    }

    @Override
    public void onReply_GetAccList(MMAPI_Conn client, int nSerialNo, TrdGetAccList.Response rsp) {
        MMSPI_Trd.super.onReply_GetAccList(client, nSerialNo, rsp);
        log.info("[MoomooClient] - onReply_GetAccList" + rsp);
        completeFuture(nSerialNo,rsp);
    }

    @Override
    public void onReply_GetPositionList(MMAPI_Conn client, int nSerialNo, TrdGetPositionList.Response rsp) {
        MMSPI_Trd.super.onReply_GetPositionList(client, nSerialNo, rsp);
        log.info("[MoomooClient] - onReply_GetPositionList" + rsp);
        completeFuture(nSerialNo, rsp);
    }

    @Override
    public void onReply_Sub(MMAPI_Conn client, int nSerialNo, QotSub.Response rsp) {
        MMSPI_Qot.super.onReply_Sub(client, nSerialNo, rsp);
        log.info("[MoomooClient] - onReply_Sub" + rsp);
        completeFuture(nSerialNo, rsp);
    }

    @Override
    public void onReply_GetBasicQot(MMAPI_Conn client, int nSerialNo, QotGetBasicQot.Response rsp) {
        MMSPI_Qot.super.onReply_GetBasicQot(client, nSerialNo, rsp);
        log.info("[MoomooClient] - onReply_GetBasicQot" + rsp);
        completeFuture(nSerialNo, rsp);
    }

    private void completeFuture(int nSerialNo, GeneratedMessageV3 rsp) {
        CompletableFuture future = futureMap.remove(nSerialNo);
        if(future != null ){
            future.complete(rsp);
        }
    }

    private TradeInfo convert2TradeInfo(TrdGetFunds.Response fundsRsp,TrdGetPositionList.Response positionRsp){
        TradeInfo tradeInfo = new TradeInfo();
        TrdCommon.Funds funds = fundsRsp.getS2C().getFunds();
        tradeInfo.setDtbpAmount(funds.getRemainingDTBP());
        tradeInfo.setCash(funds.getCash());
        tradeInfo.setDebtCash(funds.getDebtCash());
        tradeInfo.setPower(funds.getPower());
        tradeInfo.setMarketVal(funds.getMarketVal());
        tradeInfo.setFrozenCash(funds.getFrozenCash());
        tradeInfo.setTotalAssets(funds.getTotalAssets());
        tradeInfo.setAvlWithdrawalCash(funds.getAvlWithdrawalCash());

        tradeInfo.setPositions(positionRsp.getS2C().getPositionListList().stream().map(position -> {
            TradeInfo.Position tradePosition = new TradeInfo.Position();
            tradePosition.setName(position.getName());
            tradePosition.setPrice(position.getPrice());
            tradePosition.setCode(position.getCode());
            tradePosition.setVal(position.getVal());
            tradePosition.setCostPrice(position.getCostPrice());
            tradePosition.setCanSellQty(position.getCanSellQty());
            tradePosition.setQty(position.getQty());
            return tradePosition;
        }).toList());

        return tradeInfo;
    }
}
