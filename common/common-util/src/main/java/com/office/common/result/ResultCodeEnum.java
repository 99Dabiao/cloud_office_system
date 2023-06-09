package com.office.common.result;

import lombok.Getter;

/**
 * @ClassName Result
 * @Description TODO 枚举信息
 * @Author Dabiao
 * @Date 2023/3/20 20:35
 * @Version 1.0
 */
@Getter
public enum ResultCodeEnum {
        SUCCESS(200,"成功"),
        FAIL(201, "失败"),
        SERVICE_ERROR(2012, "服务异常"),

        DATA_ERROR(204, "数据异常"),
        LOGIN_AUTH(208, "未登陆"),
        PERMISSION(209, "没有权限"),
        LOGIN_ERROR(210,"认证失败"),
        ACCOUNT_STOP(211,"账户已被停用"),
        ;
        private final Integer code;
        private final String message;
        ResultCodeEnum(Integer code, String message) {
            this.code = code;
            this.message = message;
        }
}
