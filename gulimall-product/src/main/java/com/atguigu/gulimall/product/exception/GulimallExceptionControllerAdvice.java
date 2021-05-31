package com.atguigu.gulimall.product.exception;

import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.atguigu.gulimall.product.app")
public class GulimallExceptionControllerAdvice {

    /**
     * 统一处理异常，可以使用Exception.class先打印一下异常类型来确定具体异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        log.error("数据校验出现问题{}, 异常类型:{}", e.getMessage(), e.getClass());
        BindingResult result = e.getBindingResult();
        Map<String, String> errorMap = new HashMap<>();
        // 获取校验的错误结果
        result.getFieldErrors().forEach((item) -> {
            // 获取错误的属性名字 + 获取到错误提示FieldError
            errorMap.put(item.getField(), item.getDefaultMessage());
        });
        return R.ok().error(BizCodeEnume.VALID_EXCEPTION.getCode(),BizCodeEnume.VALID_EXCEPTION.getMsg()).put("data", errorMap);
    }

//    @ExceptionHandler(value = Throwable.class)
//    public R handleValidException(Throwable throwable) {
//        log.error("Throwable错误，未处理：" + throwable);
//        return R.ok().error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(), BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
//    }
}
