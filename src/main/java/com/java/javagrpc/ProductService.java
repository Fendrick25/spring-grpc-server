package com.java.javagrpc;

import com.javarpc.spring.grpc.proto.Identity;
import com.javarpc.spring.grpc.proto.Product;
import com.javarpc.spring.grpc.proto.ProductRequest;
import com.javarpc.spring.grpc.proto.ProductServiceGrpc;;
import io.grpc.Context;
import io.grpc.Metadata;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@GrpcService
@Slf4j
public class ProductService extends ProductServiceGrpc.ProductServiceImplBase {
    private List<StreamObserver<Product>> activeObservers = new CopyOnWriteArrayList<>();
    private static ConcurrentHashMap<Identity, String> clientIp = new ConcurrentHashMap<>();
    private Random random = new Random();
    @Override
    public void getProduct(ProductRequest request, StreamObserver<Product> responseObserver) {
        System.out.println("Request: " + request);
        Product product = Product.newBuilder()
                .setId(request.getId())
                .setName("Product A")
                .setPrice(50000.00)
                .build();

        responseObserver.onNext(product);
        responseObserver.onCompleted();
    }

    @Override
    public void streamProduct(Identity identity, StreamObserver<Product> responseObserver) {

    }

    @Override
    public void listenProductStream(Identity identity, StreamObserver<Product> responseObserver){
        try{
            String clientIp = GrpcClientInterceptor.CLIENT_IP_KEY.get();
            log.info("identity: {}, with ip: {}", identity.getId(), clientIp);
            if(Integer.parseInt(identity.getId()) > 5){
                activeObservers.add(responseObserver);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    @Scheduled(fixedRate = 1000)
    public void sendData(){
        for(int i = 0; i < 1000; i++){
            Product product = Product.newBuilder()
                    .setId(String.valueOf(random.nextInt(999999)))
                    .setName("Product " + random.nextInt(9999))
                    .setPrice(random.nextDouble(9999999))
                    .build();
            activeObservers.forEach(observer -> {
                try{
                    observer.onNext(product);
                }catch (Exception e){
                    activeObservers.remove(observer);
                    log.info("observer error");
                }
            });
        }
    }
}
