package com.bottrade.trade;

import com.alibaba.fastjson.JSON;
import com.bottrade.model.bot.BotCmdResult;
import com.bottrade.model.bot.OrderResult;
import com.bottrade.model.utils.Utils;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.common.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class Test {
    public static void main(String[] args) throws NoSuchMethodException, IOException {
        String aa = "A. 30 coins/minute\n" +
                "B. 40 coins/minute\n" +
                "C. 50 coins/minute\n" +
                "D. No costs in 7 days";


//        // 创建 HttpURLConnection 对象
//        URL url = new URL("http://127.0.0.1:6006/online-ocr?url=https://api.telegram.org/file/bot6967736949:AAHaejwgBa4dlsGEUnzixyaF01CY7HaziDg/photos/file_0.jpg");
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//        // 设置请求方法
//        connection.setRequestMethod("GET");
//
//        // 发送请求并获取响应
//        int responseCode = connection.getResponseCode();
//        String responseMessage = connection.getResponseMessage();
//        InputStream inputStream = connection.getInputStream();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            sb.append(line);
//        }
//
//        System.out.println(sb);
//
//        // 关闭连接
//        reader.close();
//        inputStream.close();
//        connection.disconnect();
//        new Test().test();
//        String s = "/info dsfhdhfk vfvsv";
//
//        System.out.println(Arrays.asList(s.split(" ")));
//        new TypeToken<List<String>>(){}.getType();
//        Type type = TypeToken.of(List<String>.class).getType();
//        Type nn = new TypeToken<List<String>>(){}.getType();

//        System.out.println(nn);
//        new TestA().handleResult("");
//
//        MyList list = new MyList();
//
//        list.bb();
//
//        list.<String>test();
//
//        Method method = list.getClass().getDeclaredMethod("get");
//
//        Type returnType = method.getGenericReturnType();
//
//        System.out.println(returnType); // 输出: java.lang.String

//        String octalString = new String("\347\276\216\350\202\241\346\250\241\346\213\237\344\272\244\346\230\223\344\270\215\346\224\257\346\214\201\346\214\207\345\256\232\342\200\234\345\205\201\350\256\270\347\233\230\345\211\215\347\233\230\345\220\216\342\200\235");
//        System.out.println(a);

//        String octalString = "\344\272\244\346\230\223\345\257\206\347\240\201\350\276\223\345\205\245\351\224\231\350\257\257\357\274\214\346\202\250\350\277\230\346\234\2115\346\254\241\346\234\272\344\274\232";
//        String utf8String = decodeOctalString(octalString);
//        System.out.println(utf8String);
//        String retMsg = "\\347\\276\\216\\350\\202\\241\\347\\233\\230\\345\\211\\215\\347\\233\\230\\345\\220\\216\\346\\227\\266\\346\\256\\265\\344\\270\\215\\346\\224\\257\\346\\214\\201\\345\\270\\202\\344\\273\\267\\345\\215\\225";
//
//        System.out.println(Utils.decodeRetMsg(retMsg));
//
//        retMsg = retMsg.substring(1);
//
//        // 按反斜杠分割字符串
//        String[] octalValues = retMsg.split("\\\\");
//        System.out.println(Arrays.toString(octalValues));
//
//        StringBuilder decodedBuilder = new StringBuilder();
//
//        // 遍历每个八进制值，转换并拼接
//        for (String octalValue : octalValues) {
//            // 将八进制字符串转换为整数，然后转换为字符
//            int charCode = Integer.parseInt(octalValue, 8);
//            decodedBuilder.append((char) charCode);
//        }
//
//        // 输出解码后的字符串
//        System.out.println("Decoded string: " + decodedBuilder.toString());
    }

    private static String decodeOctalString(String octalString) {
        byte[] bytes = octalString.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
        String decodedString = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        return decodedString;
    }

    // 获取 default 方法返回值类型
    public interface MyInterface<T> {

        void test();

        TypeToken<List<T>> typeToken();

        default  List<T> get()  {

//            Class<E> eClass = Class.forName("com.bottrade.model.bot.BotCmdResult");
            System.out.println("get--"+typeToken().getType());
            return new ArrayList<>();
        }
    }

    public static class MyList implements MyInterface<String> {

        @Override
        public void test() {
            get();
//            System.out.println(typeToken().getType());
        }

        @Override
        public TypeToken<List<String>> typeToken() {
            return new TypeToken<List<String>>() {};
        }

        public <E> List<E> bb(){
            System.out.println(new TypeToken<List<E>>(){}.getType());
            return new ArrayList<>();
        }

    }

    public static class TestA implements IBotCmdResultHandler<String>{

        @Override
        public String cmd() {
            return "null";
        }

        @Override
        public void handleResult(String body) {
            convert(body);
        }

        @Override
        public BotCmdResult<String> convert(String body) {
            return IBotCmdResultHandler.super.convert(body);
        }
    }

    public interface IBotCmdResultHandler<T> {

        String cmd();

        void handleResult(String body);

        default BotCmdResult<T> convert(String body){
            Class<String> stringClass = String.class;

            Type type = new TypeToken<BotCmdResult<T>>(){}.getType();
            System.out.println(type);

            Type type1 = new TypeToken<BotCmdResult<String>>(){}.getType();
            System.out.println(type1);

            return JSON.parseObject(body, type);
        }
    }

    private String tt;

    private CompletableFuture<Boolean> startFuture;

    public void test(){

        CompletableFuture<Boolean> aFuture = a();
        CompletableFuture<Boolean> bFuture = b();

        CompletableFuture.allOf(aFuture,bFuture).whenComplete(new BiConsumer<Void, Throwable>() {
            @Override
            public void accept(Void unused, Throwable throwable) {
                System.out.println(Thread.currentThread() + "--allOf");
            }
        });

    }

    public CompletableFuture<Boolean> a(){
        System.out.println(Thread.currentThread() + "--A");
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                future.complete(true);
            }
        }).start();
        if(tt == null){
            extracted("--faa");
        }

        return future;
    }

    public CompletableFuture<Boolean> b(){
        System.out.println(Thread.currentThread() + "--B");
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                future.complete(true);
            }
        }).start();
        if(tt == null){
            extracted("--fbb");
        }
        return future;
    }

    private CompletableFuture<Boolean> extracted(String x) {
        if(startFuture == null){
            CompletableFuture<Boolean> startFuture = ff();
            startFuture.whenComplete((aBoolean, throwable) -> {
                if (aBoolean != null && aBoolean) {
                    System.out.println(Thread.currentThread() + x);
                }
            });
        }

        return startFuture;
    }

    public  CompletableFuture<Boolean> ff(){
        System.out.println(Thread.currentThread() + "--ff");
        startFuture = new CompletableFuture<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                tt = "ddd";
                startFuture.complete(true);
            }
        }).start();
        return startFuture;
    }
}
