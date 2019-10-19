package team.zhou.zim.client.handle.im;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhouxinghang
 * @date 2019-10-19
 */

@Slf4j
@ChannelHandler.Sharable
public class ImClientHandle extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        log.info("收到服务端消息:{}", msg);
    }
}
