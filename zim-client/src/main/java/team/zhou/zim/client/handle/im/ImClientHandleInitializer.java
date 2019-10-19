package team.zhou.zim.client.handle.im;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import team.zhou.zim.common.protocol.ZIMRequestProto;

/**
 * @author zhouxinghang
 * @date 2019-10-19
 */
public class ImClientHandleInitializer extends ChannelInitializer<SocketChannel>  {

    private final ImClientHandle clientHandle = new ImClientHandle();

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        this.initForProtobuf(channel);
    }

    private void initForProtobuf(SocketChannel channel) {
        channel.pipeline()
            //10 秒没发送消息 将IdleStateHandler 添加到 ChannelPipeline 中
            .addLast(new IdleStateHandler(0, 10, 0))
            // 超时设置
            .addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS))
            // 拆包解码
            .addLast(new ProtobufVarint32FrameDecoder())
            .addLast(new ProtobufDecoder(ZIMRequestProto.ZIMReqProto.getDefaultInstance()))
            // 拆包编码
            .addLast(new ProtobufVarint32LengthFieldPrepender())
            .addLast(new ProtobufEncoder())
            // 服务端业务逻辑
            .addLast(clientHandle);
    }

    private void initForString(SocketChannel channel) {
        channel.pipeline()
            .addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
            // 解码和编码，应和客户端一致
            .addLast("decoder", new StringDecoder())
            .addLast("encoder", new StringEncoder())
            // 服务端业务逻辑
            .addLast("handler", clientHandle);
    }
}
