package team.zhou.zim.client.model;

import lombok.Data;
import team.zhou.zim.common.enums.ErrorCode;

/**
 * @author zhouxinghang
 * @date 2019-10-20
 */
@Data
public class ImResult<T> {
    public int code;
    public String msg;
    public T result;

    public ImResult(ErrorCode errorCode, T result) {
        this.result = result;
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public ImResult(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }
}
