package com.company;

import java.util.HashMap;
import java.util.concurrent.Callable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class Main {
    private static String TEST_HEADER = "0.1234";

    // d 解析出来的数据
    static class SocketResponseData<T> {
        T extra;
        Model.GameUser gameUser;

        public T getExtra() {
            return extra;
        }

        public void setExtra(T extra) {
            this.extra = extra;
        }

        public Model.GameUser getGameUser() {
            return gameUser;
        }

        public void setGameUser(Model.GameUser gameUser) {
            this.gameUser = gameUser;
        }
    }

    public static class SocketResponse {
        private String a;
        private String d;  // 加入这个d为string
        private String h;  // 每次请求的唯一id

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getD() {
            return d;
        }

        public void setD(String d) {
            this.d = d;
        }
    }

    static class Dispatcher {
        private HashMap<String, Protocol> requestMap = new HashMap<String, Protocol>();

        void onData() {
            // 模拟收到数据
            System.out.println("start dispatcher on data!");
            SocketResponseData<Model.ExtraTest> data = new SocketResponseData<Model.ExtraTest>();
            data.setExtra(new Model.ExtraTest(999));

            SocketResponse socketResponse = new SocketResponse();
            socketResponse.setA("auth_login");
            socketResponse.setD(JSON.toJSONString(data));


            Protocol protocol = requestMap.get(Main.TEST_HEADER);
            protocol.onResponse(JSON.toJSONString(socketResponse));
        }

        void send(Protocol protocol, String d) {
            System.out.println("dispatcher send. " + d);
            this.requestMap.put(Main.TEST_HEADER, protocol);
        }
    }

    static class Protocol {
        Dispatcher dispatcher;

        Protocol(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public void onResponse(String d) {
            SocketResponse socketResponse = JSON.parseObject(d, SocketResponse.class);
            _onResponse(socketResponse);
        }

        public void _onResponse(SocketResponse socketResponse) {
            throw new NotImplementedException();
        }
    }

    static class ResponseCallable<T> implements Callable {
        T resp;

        public Object call() {
            return null;
        }

        public T getResp() {
            return resp;
        }

        public void setResp(T resp) {
            this.resp = resp;
        }
    }

    public static class Request<K, E> extends Protocol {
        // K, T 请求参数  和响应数据
        TypeReference<SocketResponseData<E>> typeReference;
        ResponseCallable<SocketResponseData<E>> responseCallable;

        public void setTypeReference(TypeReference<SocketResponseData<E>> typeReference) {
            this.typeReference = typeReference;
        }

        public void setResponseCallable(ResponseCallable<SocketResponseData<E>> responseCallable) {
            this.responseCallable = responseCallable;
        }

        Request(Dispatcher dispatcher) {
            super(dispatcher);
        }

        public void sendData(K params) {
            String s = JSON.toJSONString(params);
            dispatcher.send(this, s);
        }

        @Override
        public void _onResponse(SocketResponse socketResponse) {
            SocketResponseData<E> obj = JSON.parseObject(socketResponse.d, typeReference);
            this.responseCallable.setResp(obj);
            this.responseCallable.call();
        }
    }

    public static void main(String[] args) {
        // write your code here
        Model.RequestParams requestParams = new Model.RequestParams("用户id123...");
        Dispatcher dispatcher = new Dispatcher();

        Request<Model.RequestParams, Model.ExtraTest> req = new Request<Model.RequestParams, Model.ExtraTest>(dispatcher);
        req.setTypeReference(new TypeReference<SocketResponseData<Model.ExtraTest>>(){});
        req.setResponseCallable(new ResponseCallable<SocketResponseData<Model.ExtraTest>>(){
            @Override
            public Object call() {
                System.out.println("callback" + resp);
                return super.call();
            }
        });
        req.sendData(requestParams);

        dispatcher.onData();
    }
}
