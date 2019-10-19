package team.zhou.zim.httpserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import team.zhou.zim.httpserver.handle.HttpServerHandler;
import team.zhou.zim.imserver.handle.ImServerHanleInitializer;

/**
 * @author zhouxinghang
 * @date 2019-10-19
 */
@Slf4j
@Service
public class HttpServer {
    private static final EventLoopGroup BOOS = new NioEventLoopGroup();
    private static final EventLoopGroup WORK = new NioEventLoopGroup();

    @Value("${zim.server.http.port}")
    private int httpPort;

    @PostConstruct
    private void init() {
        ServerBootstrap bootstrap = new ServerBootstrap()
            .group(BOOS, WORK)
            .channel(NioServerSocketChannel.class)
            .localAddress(new InetSocketAddress(httpPort))
            .option(ChannelOption.SO_BACKLOG, 10240)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.SO_REUSEADDR, true)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_SNDBUF, 1024*64)
            .childOption(ChannelOption.SO_RCVBUF, 1024*64)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new HttpRequestDecoder());
                    socketChannel.pipeline().addLast(new HttpResponseEncoder());
                    socketChannel.pipeline().addLast(new ChunkedWriteHandler());
                    socketChannel.pipeline().addLast(new HttpObjectAggregator(100 * 1024 * 1024));
                    socketChannel.pipeline().addLast(new HttpServerHandler());
                }
            });

        try {
            ChannelFuture future = bootstrap.bind().sync();
            if (future.isSuccess()) {
                log.info("启动 http server 成功");
            }
        } catch (InterruptedException e) {
            log.error("启动 http server 失败", e);
        }
    }
}
