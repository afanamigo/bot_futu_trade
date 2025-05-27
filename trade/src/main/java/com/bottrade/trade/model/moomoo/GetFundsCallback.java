package com.bottrade.trade.model.moomoo;

import com.moomoo.openapi.pb.TrdGetFunds;

public interface GetFundsCallback {
    void onResponse(int req,TrdGetFunds.Response rsp);
}
