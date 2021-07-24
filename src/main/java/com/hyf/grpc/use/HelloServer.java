package com.hyf.grpc.use;

import com.hyf.grpc.hello.Request;
import com.hyf.grpc.hello.Response;
import com.hyf.grpc.hello.RestGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author baB_hyf
 * @date 2021/07/23
 */
public class HelloServer {

    public static void main(String[] args) {
        int port = 11100;

        new RestServer(port).start();


        // 授权传入的 RPC
        // ServerCall call = new ServerCall() {
        //     @Override
        //     public void request(int i) {
        //
        //     }
        //
        //     @Override
        //     public void sendHeaders(Metadata metadata) {
        //
        //     }
        //
        //     @Override
        //     public void sendMessage(Object o) {
        //
        //     }
        //
        //     @Override
        //     public void close(Status status, Metadata metadata) {
        //
        //     }
        //
        //     @Override
        //     public boolean isCancelled() {
        //         return false;
        //     }
        //
        //     @Override
        //     public MethodDescriptor getMethodDescriptor() {
        //         return null;
        //     }
        // };
        // Status status = AuthorizationUtil.clientAuthorizationCheck(call, Lists.newArrayList("1577975140@qq.com"));
        // System.out.println(status);


        // Server server = AltsServerBuilder
        //         .forPort(port)
        //         .enableUntrustedAltsForTesting()
        //         .addService(new RestService())
        //         .build();
        // new RestServer(server).start();
    }

    /**
     * rpc服务端，添加rpc服务并启动
     */
    public static class RestServer {

        private Server server;

        public RestServer(int port) {
            this(ServerBuilder.forPort(port)
                    .addService(new RestService())
                    .build());
        }

        public RestServer(Server server) {
            this.server = server;
        }

        public void start() {
            try {
                server.start();
                System.out.println("start server on port: " + server.getPort());
                Thread.currentThread().join();
            } catch (IOException e) {
                System.out.println("server fail on start.");
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * rpc服务接口
     */
    public static class RestService extends RestGrpc.RestImplBase {

        @Override
        public void simplex(Request request, StreamObserver<Response> responseObserver) {
            responseObserver.onNext(convert(request));
            responseObserver.onCompleted();
        }

        @Override
        public void responseStream(Request request, StreamObserver<Response> responseObserver) {
            for (int i = 0; i < 10; i++) {
                Response response = convert(request, i);
                responseObserver.onNext(response);
            }
            responseObserver.onCompleted();
        }

        @Override
        public StreamObserver<Request> requestStream(StreamObserver<Response> responseObserver) {
            return new StreamObserver<Request>() {

                final long startTime = System.nanoTime();
                int next = 0;
                Request previous;

                @Override
                public void onNext(Request request) {
                    next++;
                    // server calc
                    previous = request;
                }

                @Override
                public void onError(Throwable throwable) {
                    throwable.printStackTrace();
                }

                @Override
                public void onCompleted() {
                    long seconds = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime);

                    Response response = Response.newBuilder()
                            .setCode(200)
                            .setText("请求时间: " + seconds + "s")
                            .putValues("request_count", String.valueOf(next))
                            .putValues("last_request", previous.toString())
                            .build();

                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }
            };
        }

        @Override
        public StreamObserver<Request> fullDuplex(StreamObserver<Response> responseObserver) {
            return new StreamObserver<Request>() {

                @Override
                public void onNext(Request request) {
                    for (int i = 0; i < 10; i++) {
                        responseObserver.onNext(convert(request, i));
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

        private Response convert(Request request) {
            String uri = request.getUri();
            System.out.println(uri);
            return Response.newBuilder().setCode(200).build();
        }

        private Response convert(Request request, int i) {
            String uri = request.getUri();
            System.out.println(uri);
            return Response.newBuilder().setCode(i).build();
        }
    }
}
