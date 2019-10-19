package team.zhou.zim.service;

import org.springframework.stereotype.Service;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhouxinghang
 * @date 2019-10-19
 */
@Service
public class ServerHeartBeatHandlerImpl implements IHeartBeatHandler {

    @Override
    public void process(ChannelHandlerContext ctx) throws Exception {
        // todo 心跳
    }
}
