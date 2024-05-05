package latte.netty_server.demo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Test;

public class EchoClientTest {
  static class EchoClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s)
        throws Exception {
      System.out.println("Received: " + s);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      super.channelActive(ctx);
      ctx.writeAndFlush("ping\n");
    }
  }
  static class EchoClient {
    String host;
    int port;
    public EchoClient(String host, int port) {
      this.host = host;
      this.port = port;
    }
    public void start() throws Exception {
      NioEventLoopGroup group = new NioEventLoopGroup();
      try {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .handler(new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new EchoClientHandler());
              }
            });

        ChannelFuture future = bootstrap.connect(host, port).sync();
        future.channel().closeFuture().sync();
      } finally {
        group.shutdownGracefully();
      }
    }
  }
  @Test
  public void start() throws Exception {
    String host = "localhost";
    int port = 8080;
    EchoClient client = new EchoClient(host, port);
    client.start();
  }
}
