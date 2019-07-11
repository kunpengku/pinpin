package com.kpk.pinpin.demo.utils.http;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * 将各种返回的类型放在一起
 * @author youletter
 */
@Data
@AllArgsConstructor
public class Response {
    @Data
    @AllArgsConstructor
    public static class Template<T>{
        private boolean ret;
        private List<T> data;
        private Integer code;
        private String msg;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TemplateA<T>{
        private boolean ret;
        private T data;
        private Integer errCode;
        private String errMsg;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemplateB<T>{
        private boolean success;
        private T data;
        private Integer code;
        private String msg;
    }

    @Data
    @AllArgsConstructor
    public static class TemplateC<T>{
        private String info;
        private T data;
        private Integer code;
    }

    @Data
    @AllArgsConstructor
    public static class TemplateD<T>{
        private Integer pageNo;
        private Integer pageSize;
        private Integer count;
        private boolean ret;
        private List<T> list;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TemplateE<T>{
        private List<T> data;
        private Integer code;
        private String message;
        private String success;
    }

    @Data
    @AllArgsConstructor
    public static class TemplateF<T>{
        private T data;
        private Integer code;
        private String message;
        private String success;
    }
}
