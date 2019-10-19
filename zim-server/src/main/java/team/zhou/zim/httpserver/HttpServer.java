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
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            //BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。
            //Option是为了NioServerSocketChannel设置的，用来接收传入连接的
            .option(ChannelOption.SO_BACKLOG, 128)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    // server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
                    ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                    //将多个消息转换为单一的FullHttpRequest或FullHttpResponse对象
                    ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65535));
                    // server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
                    ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                    //解决大数据包传输问题，用于支持异步写大量数据流并且不需要消耗大量内存也不会导致内存溢出错误( OutOfMemoryError )。
                    //仅支持ChunkedInput类型的消息。也就是说，仅当消息类型是ChunkedInput时才能实现ChunkedWriteHandler提供的大数据包传输功能
                    ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());//解决大码流的问题
                    ch.pipeline().addLast("http-server", new HttpServerHandler());
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
