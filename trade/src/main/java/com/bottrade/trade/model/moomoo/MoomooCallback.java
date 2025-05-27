package com.bottrade.trade.model.moomoo;

import com.moomoo.openapi.pb.TrdGetAccList;
import com.moomoo.openapi.pb.TrdGetFunds;
import com.moomoo.openapi.pb.TrdPlaceOrder;

public abstract class MoomooCallback {

    public void onGetAccList(int nSerialNo, TrdGetAccList.Response rsp) {}

    public void onGetFunds(int req, TrdGetFunds.Response rsp){}

    public void onPlaceOrder(int req, TrdPlaceOrder.Response rsp){}
}
