package latte.netty_server.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {
  int num = 0;
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ByteBuf in = (ByteBuf) msg;
    ByteBuf buf = in.alloc().directBuffer(1024);

    buf.writeBytes(in);
    ctx.write(buf.copy());
    ctx.flush();
    num++;
    System.out.println("num:" + num);
    // 故意不释放 ByteBuf 资源
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
