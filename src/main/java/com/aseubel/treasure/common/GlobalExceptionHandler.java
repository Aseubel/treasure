package com.aseubel.treasure.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice // 声明这是一个全局异常处理组件，结合了 @ControllerAdvice 和 @ResponseBody
public class GlobalExceptionHandler {

    /**
     * 处理所有未被特定处理方法捕获的异常
     * 
     * @param e 异常对象
     * @return 统一错误结果
     */
    @ExceptionHandler(Exception.class) // 指定处理 Exception 及其子类异常
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 定义HTTP响应状态码为500
    public Result<String> handleException(Exception e) {
        log.error("发生未处理异常: {}", e.getMessage(), e); // 记录详细错误日志
        // 可以根据不同的异常类型返回更具体的错误信息和状态码
        // 例如： if (e instanceof YourCustomException) { ... }
        return Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误，请联系管理员");
    }

    /**
     * 处理参数校验异常 (如果后续引入 Validation)
     * 例如处理 @Valid 注解校验失败抛出的 BindException 或 MethodArgumentNotValidException
     */
    /*
     * @ExceptionHandler({BindException.class,
     * MethodArgumentNotValidException.class})
     * 
     * @ResponseStatus(HttpStatus.BAD_REQUEST)
     * public Result<String> handleValidationExceptions(Exception e) {
     * String errorMessage = "参数校验失败";
     * if (e instanceof BindException) {
     * errorMessage = ((BindException)
     * e).getBindingResult().getAllErrors().get(0).getDefaultMessage();
     * } else if (e instanceof MethodArgumentNotValidException) {
     * errorMessage = ((MethodArgumentNotValidException)
     * e).getBindingResult().getAllErrors().get(0).getDefaultMessage();
     * }
     * log.warn("参数校验失败: {}", errorMessage);
     * return Result.error(HttpStatus.BAD_REQUEST.value(), errorMessage);
     * }
     */

    // 可以根据需要添加更多特定异常的处理方法
    // 例如处理数据库访问异常、权限不足异常等
    /*
     * @ExceptionHandler(DataAccessException.class)
     * 
     * @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
     * public Result<String> handleDataAccessException(DataAccessException e) {
     * log.error("数据库访问异常: {}", e.getMessage(), e);
     * return Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "数据库操作失败");
     * }
     */
}