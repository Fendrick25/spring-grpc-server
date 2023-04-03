package com.java.javagrpc;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Grpc;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@GrpcGlobalServerInterceptor
public class GrpcClientInterceptor implements ServerInterceptor {
    public static final Context.Key<String> CLIENT_IP_KEY = Context.key("client-ip");

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String clientIp = extractIpAddress(serverCall.getAttributes().get(Grpc.TRANSPORT_ATTR_LOCAL_ADDR).toString());
        log.info("Received request from client with IP address {}", clientIp);

        Context context = Context.current().withValue(CLIENT_IP_KEY, clientIp);
        return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
       // return serverCallHandler.startCall(serverCall, metadata);
    }

    private String extractIpAddress(String fullAddress) {
        Pattern pattern = Pattern.compile("/(\\d+\\.\\d+\\.\\d+\\.\\d+):\\d+");
        Matcher matcher = pattern.matcher(fullAddress);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }
}
