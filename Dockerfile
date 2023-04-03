FROM openjdk:17-jdk
WORKDIR /app
COPY target/java-grpc-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 50052
ENTRYPOINT ["java","-jar","app.jar"]