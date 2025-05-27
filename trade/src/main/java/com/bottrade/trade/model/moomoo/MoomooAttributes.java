package com.bottrade.trade.model.moomoo;

import com.moomoo.openapi.pb.TrdCommon;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;

@Data
public class MoomooAttributes {

    private long accId;

    private int trdEnv;

    private int trdMarket;

    private int openDPort;

    private String pwdMD5;

    private int securityFirm;

    public MoomooAttributes() {
        accId = 974142L;
        trdEnv = TrdCommon.TrdEnv.TrdEnv_Simulate_VALUE;
        trdMarket = TrdCommon.TrdMarket.TrdMarket_US_VALUE;
        openDPort = 8818;
        pwdMD5 = "6024f5beb1dfff13a674730e2e182c43";
        securityFirm = 2;
    }

    public void copyFromMap(Map<String, String> extraMap) {
        this.accId = NumberUtils.toLong(extraMap.get("accId"),this.accId);
        this.trdEnv = NumberUtils.toInt(extraMap.get("trdEnv"),this.trdEnv);
        this.trdMarket = NumberUtils.toInt(extraMap.get("trdMarket"),this.trdMarket);
        this.openDPort = NumberUtils.toInt(extraMap.get("openDPort"),this.openDPort);
        this.pwdMD5 = extraMap.getOrDefault("pwdMD5",pwdMD5);
        this.securityFirm = NumberUtils.toInt(extraMap.get("securityFirm"),this.securityFirm);
    }

}
