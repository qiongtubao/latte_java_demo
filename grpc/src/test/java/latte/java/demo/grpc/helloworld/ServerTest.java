package latte.java.demo.grpc.helloworld;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import latte.java.demo.grpc.helloworld.proto.GreeterGrpc;
import latte.java.demo.grpc.helloworld.proto.HelloReply;
import latte.java.demo.grpc.helloworld.proto.HelloRequest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ServerTest {
    private static final Logger logger = Logger.getLogger(ServerTest.class.getName());
    static public class HelloWorldServer {
        private Server server;
        public void start() throws IOException {
            /* The port on which the server should run */
            int port = 50051;
            server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                    .addService(new GreeterImpl())
                    .build()
                    .start();
            logger.info("Server started, listening on " + port);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                    System.err.println("*** shutting down gRPC server since JVM is shutting down");
                    try {
                        HelloWorldServer.this.stop();
                    } catch (InterruptedException e) {
                        e.printStackTrace(System.err);
                    }
                    System.err.println("*** server shut down");
                }
            });
        }

        public void stop() throws InterruptedException {
            if (server != null) {
                server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
            }
        }

        private void blockUntilShutdown() throws InterruptedException {
            if (server != null) {
                server.awaitTermination();
            }
        }
    }
    @Test
    public void startGrpcServer() throws Exception {
        final HelloWorldServer server = new HelloWorldServer();
        server.start();
        server.blockUntilShutdown();
    }
    static class GreeterImpl extends GreeterGrpc.GreeterImplBase  {

        @Override
        public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
