package team.zhou.zim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhouxinghang
 * @date 2019-10-19
 */
@Slf4j
@SpringBootApplication
public class ZimServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZimServerApplication.class, args);
        log.info("启动 Server 服务成功");
    }
}
