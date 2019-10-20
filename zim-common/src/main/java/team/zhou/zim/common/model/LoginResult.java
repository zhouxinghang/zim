package team.zhou.zim.common.model;

import lombok.Data;

/**
 * @author zhouxinghang
 * @date 2019-10-20
 */
@Data
public class LoginResult {
    private int code;
    private String serverIp;
    private int serverPort;
}
