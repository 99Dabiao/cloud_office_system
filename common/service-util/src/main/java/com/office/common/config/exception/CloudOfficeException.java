package com.office.common.config.exception;

import com.office.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * @ClassName CloudOfficeException
 * @Description TODO 自定义异常
 * @Author Dabiao
 * @Date 2023/3/21 18:36
 * @Version 1.0
 */
@Data
public class CloudOfficeException extends RuntimeException{
    private Integer code;
    private String msg;
    /**
     * 通过状态码和错误消息创建异常对象
     * @param code
     * @param msg
     */
    public CloudOfficeException(Integer code,String msg){
        super(msg);
        this.code = code;
        this.msg = msg;

    }
    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public CloudOfficeException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMessage();
    }

    @Override
    public String toString() {
        return "CloudOfficeException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
