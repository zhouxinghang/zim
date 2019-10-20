package team.zhou.zim.client.service;

import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import team.zhou.zim.client.model.ImResult;
import team.zhou.zim.common.enums.ErrorCode;
import team.zhou.zim.common.model.LoginResult;


/**
 * @author zhouxinghang
 * @date 2019-10-20
 */
@Service
public class AccountService {
    @Autowired
    private HttpRequstService httpRequstService;

    public LoginResult login(String account, String pwd) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("account", account);
        jsonObject.put("pwd", pwd);
        ImResult<LoginResult> imResult = httpRequstService.post("http://localhost:8080/login", jsonObject.toJSONString(), LoginResult.class);
        if (imResult.getCode() == ErrorCode.SUCCESS.getCode()) {
            return imResult.getResult();
        } else {
            return null;
        }
    }
}
