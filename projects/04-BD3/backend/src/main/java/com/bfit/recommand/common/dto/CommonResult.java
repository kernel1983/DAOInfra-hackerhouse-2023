package com.bfit.recommand.common.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
@Builder
public class CommonResult<T> implements Serializable {

    private Integer code;
    private String errMsg;
    private T data;

    public static CommonResult ok(){
        return CommonResult.builder().code(HttpStatus.OK.value()).build();
    }

    public static <T>CommonResult ok(T t){
        return CommonResult.builder().code(HttpStatus.OK.value()).data(t).build();
    }

}
