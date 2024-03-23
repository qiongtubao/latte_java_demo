package latte.java.demo.grpc.helloworld;

import io.grpc.*;
import latte.java.demo.grpc.helloworld.proto.GreeterGrpc;
import latte.java.demo.grpc.helloworld.proto.HelloReply;
import latte.java.demo.grpc.helloworld.proto.HelloRequest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientTest {
    private static final Logger logger = Logger.getLogger(ClientTest.class.getName());
    class HelloWorldClient {
        
        private final GreeterGrpc.GreeterBlockingStub blockingStub;

        /**
         * Construct client for accessing HelloWorld server using the existing channel.
         */
        public HelloWorldClient(Channel channel) {
            // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
            // shut it down.

            // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
            blockingStub = GreeterGrpc.newBlockingStub(channel);
        }

        /**
         * Say hello to server.
         */
        public void greet(String name) {
            logger.info("Will try to greet " + name + " ...");
            HelloRequest request = HelloRequest.newBuilder().setName(name).build();
            HelloReply response;
            try {
                response = blockingStub.sayHello(request);
            } catch (StatusRuntimeException e) {
                logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
                return;
            }
            logger.info("Greeting: " + response.getMessage());
        }
    }

    @Test
    public void startGrpcClient() throws InterruptedException {
        String user = "world";
        // Access a service running on the local machine on port 50051
        String target = "localhost:50051";
        

        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        //
        // For the example we use plaintext insecure credentials to avoid needing TLS certificates. To
        // use TLS, use TlsChannelCredentials instead.
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
                .build();
        try {
            HelloWorldClient client = new HelloWorldClient(channel);
            client.greet(user);
        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
