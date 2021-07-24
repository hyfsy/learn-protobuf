package com.hyf.grpc.service;

import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

/**
 * gRPC服务实现
 *
 * @author baB_hyf
 * @date 2021/07/24
 */
public class ServiceStubGrpcService extends ServiceStubGrpc.ServiceStubImplBase {

    @Override
    public void unary(Request request, StreamObserver<Response> responseObserver) {
        Response response = Response.newBuilder().setCode(200).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void serverStreaming(Request request, StreamObserver<Response> responseObserver) {
        for (int i = 0; i < 10; i++) {
            Response response = Response.newBuilder().setCode(i).build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Request> clientStreaming(StreamObserver<Response> responseObserver) {
        return new StreamObserver<Request>() {

            final long startTime = System.nanoTime();
            Request previous;

            @Override
            public void onNext(Request request) {
                // server calc
                previous = request;
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                Response response = Response.newBuilder().setCode(200).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();

                long seconds = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime);
                System.out.println("server time: " + seconds + "s");
            }
        };
    }

    @Override
    public StreamObserver<Request> biDirectionalStreaming(StreamObserver<Response> responseObserver) {
        return new StreamObserver<Request>() {

            @Override
            public void onNext(Request request) {
                for (int i = 0; i < 10; i++) {
                    Response response = Response.newBuilder().setCode(i).build();
                    responseObserver.onNext(response);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}