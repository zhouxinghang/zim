package team.zhou.zim.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;
import team.zhou.zim.common.protocol.CommandType;
import team.zhou.zim.common.protocol.ZIMRequestProto;

/**
 * @author zhouxinghang
 * @date 2019-10-19
 */
@Slf4j
@SpringBootApplication
public class ZimClientApplication implements CommandLineRunner {
    @Autowired
    private ImClient imClient;

    public static void main(String[] args) {
        SpringApplication.run(ZimClientApplication.class, args);
        log.info("启动 Client 服务成功");
    }

    @Override
    public void run(String... args) throws Exception {
        imClient.sendStringMsg("测试消息" + "\r\n");
        ZIMRequestProto.ZIMReqProto reqProto = ZIMRequestProto.ZIMReqProto.newBuilder()
            .setRequestId(111)
            .setMsg("testMsg")
            .setType(CommandType.MSG)
            .build();
        imClient.sendMsg(reqProto);
    }
}
