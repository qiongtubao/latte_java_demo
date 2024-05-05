package latte.netty_server.demo;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {
  static Logger logger = LoggerFactory.getLogger(EchoServerHandler.class);

  int num = 0;
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
   try {
     ByteBuf in = (ByteBuf) msg;
     ByteBuf buf = in.alloc().directBuffer(1024);

     buf.writeBytes(in);
     ctx.write(buf.copy());
     ctx.flush();

     num++;
     if (num == 100) {
       System.gc();
       logger.warn("gc");
     }
   } catch (Exception e) {
     logger.warn("num:" + num);
     throw  e;
   }

    // 故意不释放 ByteBuf 资源
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
