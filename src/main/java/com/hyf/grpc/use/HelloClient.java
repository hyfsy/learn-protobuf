package com.hyf.grpc.use;

import com.hyf.grpc.hello.Request;
import com.hyf.grpc.hello.Response;
import com.hyf.grpc.hello.RestGrpc;
import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author baB_hyf
 * @date 2021/07/24
 */
public class HelloClient {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 11100;

        new RestClient(host, port).request();

        // ManagedChannel channel = AltsChannelBuilder
        //         .forTarget("localhost:11100")
        //         .addTargetServiceAccount("expected_server_service_account")
        //         .addTargetServiceAccount("1577975140@qq.com")
        //         .build();
        // new RestClient(host, port, channel).request();
    }

    /**
     * rpc客户端
     */
    public static class RestClient {

        private String host;
        private int    port;

        private RestGrpc.RestBlockingStub blockingStub; // blocking stub support unary and streaming types
        private RestGrpc.RestStub         stub; // async stub support all call types
        private RestGrpc.RestFutureStub   futureStub; // return ListenableFuture style, only support unary type

        public RestClient(String host, int port) {
            this(host, port, ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
        }

        public RestClient(String host, int port, ManagedChannel channel) {
            this.host = host;
            this.port = port;

            blockingStub = RestGrpc.newBlockingStub(channel);
            stub = RestGrpc.newStub(channel);
            futureStub = RestGrpc.newFutureStub(channel);
        }


        public void request() {
            Request request = Request.newBuilder()
                    .setHost(host)
                    .setPort(port)
                    .setMethod("POST")
                    .setUri("/hello/rpc")
                    .build();

            simplex(request);
            responseStream(request);
            requestStream(request);
            fullDuplex(request);

        }

        public void simplex(Request request) {
            System.out.println("============== simplex ==================");

            try {
                Response simplex = blockingStub.simplex(request);
                System.out.println(simplex);
            } catch (StatusRuntimeException e) {
                Status status = e.getStatus();
                Metadata trailers = e.getTrailers();
                System.out.println("RPC error, status: " + status + ", metadata: " + trailers);
                e.printStackTrace();
            }
        }

        public void responseStream(Request request) {
            System.out.println("============== responseStream ==================");

            Iterator<Response> responseIterator = blockingStub.responseStream(request);
            while (responseIterator.hasNext()) {
                Response next = responseIterator.next();
                System.out.println(next);
            }
        }

        public void requestStream(Request request) {
            System.out.println("============== requestStream ==================");

            CountDownLatch finish = new CountDownLatch(1);

            StreamObserver<Response> responseStreamObserver = new StreamObserver<Response>() {

                @Override
                public void onNext(Response response) {
                    System.out.println("client get one response: " + response);
                }

                @Override
                public void onError(Throwable throwable) {
                    Status status = Status.fromThrowable(throwable);
                    System.out.println("request fail: " + status);
                    finish.countDown();
                }

                @Override
                public void onCompleted() {
                    System.out.println("request finished.");
                    finish.countDown();
                }
            };

            StreamObserver<Request> requestStreamObserver = stub.requestStream(responseStreamObserver);
            try {
                for (int i = 0; i < 5; i++) {
                    requestStreamObserver.onNext(request);
                    Thread.sleep(1000);
                    // 服务器端完成或抛异常
                    if (finish.getCount() == 0) {
                        // 无需再发送请求，发了也会被丢弃
                        return;
                    }
                }
                requestStreamObserver.onCompleted();
            } catch (Exception e) {
                requestStreamObserver.onError(e);
            }

            try {
                finish.await(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void fullDuplex(Request request) {

            System.out.println("============== fullDuplex ==================");

            CountDownLatch finish = new CountDownLatch(1);

            StreamObserver<Response> responseStreamObserver = new StreamObserver<Response>() {

                @Override
                public void onNext(Response response) {
                    System.out.println("client get one response: " + response);
                }

                @Override
                public void onError(Throwable throwable) {
                    Status status = Status.fromThrowable(throwable);
                    System.out.println("request fail: " + status);
                    finish.countDown();
                }

                @Override
                public void onCompleted() {
                    System.out.println("request finished.");
                    finish.countDown();
                }
            };

            StreamObserver<Request> requestStreamObserver = stub.requestStream(responseStreamObserver);
            try {
                for (int i = 0; i < 5; i++) {
                    requestStreamObserver.onNext(request);
                }
                requestStreamObserver.onCompleted();
            } catch (Exception e) {
                requestStreamObserver.onError(e);
            }

            try {
                finish.await(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
