package com.bottrade.trade.controller;

import com.bottrade.trade.model.moomoo.GetFundsCallback;
import com.bottrade.trade.model.moomoo.MoomooAttributes;
import com.bottrade.trade.model.moomoo.MoomooCallback;
import com.bottrade.trade.model.moomoo.MoomooClient;
import com.moomoo.openapi.pb.TrdGetAccList;
import com.moomoo.openapi.pb.TrdGetFunds;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/main")
@Slf4j
public class MainController {

    @Autowired
    private MoomooClient client;

    @Autowired
    private MoomooAttributes moomooAttributes;

    @GetMapping("/get-funds")
    public String getFunds(){
//        client.getFunds(new GetFundsCallback() {
//            @Override
//            public void onResponse(int req, TrdGetFunds.Response rsp) {
//                log.info(req+"---"+rsp);
//            }
//        });
        return "";
    }

    @GetMapping("/get-acc")
    public String getAccList(){
        CompletableFuture<TrdGetAccList.Response> future = client.getAccList();
        try {
            TrdGetAccList.Response response = future.get();
            return response.toString();
        } catch (Exception e) {
            return e.toString();
        }

    }

    @PostMapping("/setup")
    public String setup(@RequestBody MoomooAttributes attributes){
        log.info(attributes.toString() + "----" + moomooAttributes);
        BeanUtils.copyProperties(attributes,moomooAttributes);
        client.close();
        return moomooAttributes.toString();
    }
}
