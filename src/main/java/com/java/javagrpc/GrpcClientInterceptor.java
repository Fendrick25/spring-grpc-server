package com.java.javagrpc;

import io.grpc.*;
import io.grpc.internal.GrpcAttributes;
import io.grpc.internal.GrpcUtil;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@GrpcGlobalServerInterceptor
public class GrpcClientInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String clientIp = serverCall.getAttributes().get(Grpc.TRANSPORT_ATTR_LOCAL_ADDR).toString();
        log.info("Received request from client with IP address {}", clientIp);

        return serverCallHandler.startCall(serverCall, metadata);
    }
}
