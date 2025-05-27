package com.bottrade.trade.model.moomoo;

import com.moomoo.openapi.pb.TrdPlaceOrder;

public interface PlaceOrderCallback {

    void onResponse(int req,TrdPlaceOrder.Response rsp);

}
