package com.hyf.grpc.service;

import io.grpc.*;
import io.grpc.stub.StreamObserver;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * gRPC客户端
 *
 * @author baB_hyf
 * @date 2021/07/24
 */
public class GrpcClient {

    // blocking stub support unary and streaming types
    private final ServiceStubGrpc.ServiceStubBlockingStub blockingStub;
    // async stub support all call types
    private final ServiceStubGrpc.ServiceStubStub         stub;
    // return ListenableFuture style, only support unary type
    private final ServiceStubGrpc.ServiceStubFutureStub   futureStub;

    public GrpcClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
    }

    public GrpcClient(ManagedChannel channel) {
        blockingStub = ServiceStubGrpc.newBlockingStub(channel);
        stub = ServiceStubGrpc.newStub(channel);
        futureStub = ServiceStubGrpc.newFutureStub(channel);
    }

    public static void main(String[] args) {
        GrpcClient client = new GrpcClient("localhost", 12800);

        Request request = Request.newBuilder().setUri("/grpc/hello").build();

        client.unary(request);
        client.serverStreaming(request);
        client.clientStreaming(request);
        client.biDirectionalStreaming(request);
    }

    public void unary(Request request) {
        try {
            // 返回单个响应
            Response response = blockingStub.unary(request);
            System.out.println("Server return code: " + response.getCode());
        }
        // 运行时发生错误会抛出
        catch (StatusRuntimeException e) {
            Status status = e.getStatus();
            Metadata trailers = e.getTrailers();
            System.out.println("RPC error, status: " + status + ", metadata: " + trailers);
            e.printStackTrace();
        }
    }

    public void serverStreaming(Request request) {
        // 返回响应迭代器
        Iterator<Response> responseIterator = blockingStub.serverStreaming(request);
        while (responseIterator.hasNext()) {
            Response next = responseIterator.next();
            System.out.println(next);
        }
    }

    public void clientStreaming(Request request) {

        // 标记服务端处理完成
        CountDownLatch finish = new CountDownLatch(1);

        // 处理返回的响应流
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

        // 获取请求响应流处理器 生成请求
        StreamObserver<Request> requestStreamObserver = stub.clientStreaming(responseStreamObserver);

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

    public void biDirectionalStreaming(Request request) {

        // 标记服务端处理完成
        CountDownLatch finish = new CountDownLatch(1);

        // 处理返回的响应流
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

        // 获取请求响应流处理器 生成请求
        StreamObserver<Request> requestStreamObserver = stub.biDirectionalStreaming(responseStreamObserver);

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
