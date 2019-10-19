package team.zhou.zim.imserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import team.zhou.zim.imserver.handle.ImServerHanleInitializer;

/**
 * @author zhouxinghang
 * @date 2019-10-19
 */
@Slf4j
@Service
public class ImServer {

    private static final EventLoopGroup BOOS = new NioEventLoopGroup();
    private static final EventLoopGroup WORK = new NioEventLoopGroup();

    @Value("${zim.server.port}")
    private int nettyPort;

    @PostConstruct
    private void init() {
        ServerBootstrap bootstrap = new ServerBootstrap()
            .group(BOOS, WORK)
            .channel(NioServerSocketChannel.class)
            .localAddress(new InetSocketAddress(nettyPort))
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childHandler(new ImServerHanleInitializer());

        try {
            ChannelFuture future = bootstrap.bind().sync();
            if (future.isSuccess()) {
                log.info("启动 im server 成功");
            }
        } catch (InterruptedException e) {
            log.error("启动 im server 失败", e);
        }

    }
}
