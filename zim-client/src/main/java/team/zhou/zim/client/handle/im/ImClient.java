package team.zhou.zim.client.handle.im;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import team.zhou.zim.client.service.AccountService;
import team.zhou.zim.common.model.LoginResult;
import team.zhou.zim.common.protocol.ZIMRequestProto;

/**
 * @author zhouxinghang
 * @date 2019-10-19
 */
@Slf4j
@Service
public class ImClient {
    private static final EventLoopGroup GROUP = new NioEventLoopGroup(0, new DefaultThreadFactory("zim-work"));

    private int nettyPort = 11211;

    private SocketChannel channel;

    @Autowired
    private AccountService accountService;

    @PostConstruct
    private void init() {
        // 登录
        LoginResult loginResult = accountService.login("zhouxinghang", "123456");
        if (loginResult == null) {
            log.error("启动 zim client 失败,原因:登录失败");
            return;
        }

        Bootstrap bootstrap = new Bootstrap()
            .group(GROUP)
            .channel(NioSocketChannel.class)
            .handler(new ImClientHandleInitializer());

        try {
            // todo 需要先登录，然后根据登录返回的连接，进行长连接
            ChannelFuture future = bootstrap.connect(loginResult.getServerIp(), loginResult.getServerPort()).sync();
            if (future.isSuccess()) {
                log.info("启动 zim client 成功");
                channel = (SocketChannel) future.channel();
            }
        } catch (InterruptedException e) {
            log.error("启动 zim client 失败", e);
        }
    }

    public void sendStringMsg(String msg) {
        ByteBuf message = Unpooled.buffer(msg.getBytes().length);
        message.writeBytes(msg.getBytes());
        ChannelFuture future = channel.writeAndFlush(message);
        future.addListener(future1 -> log.info("客户端发送消息成功,msg:{}", msg));
    }

    public void sendMsg(ZIMRequestProto.ZIMReqProto reqProto) {
        ChannelFuture future = channel.writeAndFlush(reqProto);
        future.addListener(future1 -> log.info("客户端发送消息成功,msg:\n{}", reqProto));
    }
}
