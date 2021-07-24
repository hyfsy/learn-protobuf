package com.hyf.grpc.service;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * gRPC服务端
 *
 * @author baB_hyf
 * @date 2021/07/24
 */
public class GrpcServer {

    private final Server server;

    public GrpcServer(int port) {
        this(ServerBuilder.forPort(port)
                .addService(new ServiceStubGrpcService())
                .build());
    }

    public GrpcServer(Server server) {
        this.server = server;
    }

    public static void main(String[] args) {
        new GrpcServer(12800).start();
    }

    public void start() {
        try {
            server.start();
            System.out.println("server started on port: " + server.getPort());
            Thread.currentThread().join(); // grpc的所有线程都是守护线程
        } catch (IOException e) {
            System.out.println("server fail on starting");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("server stopped.");
        }
    }
}
