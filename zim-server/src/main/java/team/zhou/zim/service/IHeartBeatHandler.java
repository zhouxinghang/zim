package team.zhou.zim.service;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhouxinghang
 * @date 2019-10-19
 */
public interface IHeartBeatHandler {

    /**
     * 处理心跳
     * @param ctx
     * @throws Exception
     */
    void process(ChannelHandlerContext ctx) throws Exception;
}
