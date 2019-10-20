package team.zhou.zim.client.service;

import java.util.Map;

import team.zhou.zim.client.model.ImResult;

/**
 * @author zhouxinghang
 * @date 2019-10-20
 * http 协议请求 server
 */
public interface IRequestService<T> {

    /**
     * post 请求
     * @param url
     * @param json 请求参数
     * @param <T> 返回具体结果
     * @return
     */
    <T> ImResult<T> post(String url, String  json);

    /**
     * get 请求
     * @param url
     * @param reqParam
     * @param <T>
     * @return
     */
    <T> ImResult<T> get(String url, Map<String, Object> reqParam);
}
