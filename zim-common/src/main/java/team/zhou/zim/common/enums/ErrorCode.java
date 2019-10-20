package team.zhou.zim.common.enums;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author zhouxinghang
 * @date 2019-10-20
 */
public enum  ErrorCode {
    /**
     * 成功
     */
    SUCCESS(0, "success"),
    SYS_ERROR(999, "system error");

    public int code;
    public String msg;

    private static Map<Integer, ErrorCode> codeEnumMap;

    static {
        codeEnumMap = Maps.newHashMapWithExpectedSize(ErrorCode.values().length);
        for (ErrorCode errorCode : ErrorCode.values()) {
            codeEnumMap.put(errorCode.getCode(), errorCode);
        }
    }

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ErrorCode fromCode(int code) {
        return codeEnumMap.getOrDefault(code, SYS_ERROR);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
