package io.github.thebesteric.framework.versioner.test;



import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;


public class R {

    private int code = 200;
    private Object data;
    private Long timestamp;
    private String message = "SUCCEED";
    private static final String ZONE_OFFSET = "+8";
    private String trackId;

    /* if need track_id you can use reflect */
    public R setTrackId(String trackId) {
        this.trackId = trackId;
        return this;
    }

    public synchronized static R newInstance() {
        R r = new R();
        r.data = new HashMap<String, Object>();
        return r;
    }

    public synchronized static R initInstance(int code, String message, Object data) {
        R instance = new R();
        instance.code = code;
        instance.timestamp = LocalDateTime.now().toInstant(ZoneOffset.of(ZONE_OFFSET)).toEpochMilli();
        instance.message = message;
        instance.data = data;
        return instance;
    }

    public Object getData() {
        return this.data;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean checkCode(int code) {
        return this.code == code;
    }

    @SuppressWarnings({"unchecked"})
    public R set(String key, Object value) {
        Object object = this.data;
        if (object == null) {
            this.data = new HashMap<>();
        }
        if (this.data instanceof Map) {
            ((Map<String, Object>) this.data).put(key, value);
        } else {
            throw new RuntimeException("cannot put the key in the object");
        }
        return this;
    }

    public R put(String key, Object data) {
        return this.set(key, data);
    }

    public R setCode(int code) {
        this.code = code;
        this.message = HttpStatusCode.SUCCESS.message;
        return this;
    }

    public R setCode(HttpStatusCode httpStatusCode) {
        return setCode(httpStatusCode.code);
    }

    public R setMessage(String message) {
        this.message = message;
        return this;
    }

    public R setData(Object data) {
        this.data = data;
        return this;
    }

    public static R success() {
        return success(HttpStatusCode.SUCCESS);
    }

    public static R success(String message) {
        return success(HttpStatusCode.SUCCESS, message);
    }

    public static R success(Object data) {
        return success(null, data);
    }

    public static R success(HttpStatusCode httpStatusCode) {
        return success(httpStatusCode, null);
    }

    public static R success(HttpStatusCode httpStatusCode, String message) {
        return success(httpStatusCode, message, null);
    }

    public static R success(String message, Object data) {
        return success(HttpStatusCode.SUCCESS, message, data);
    }

    public static R success(HttpStatusCode httpStatusCode, String message, Object data) {
        return success(httpStatusCode.code, null != message ? message : httpStatusCode.message, data);
    }

    public static R success(int code, String message, Object data) {
        return initInstance(code, message, data);
    }

    public static R error() {
        return error(HttpStatusCode.ERROR);
    }

    public static R error(String message) {
        return error(HttpStatusCode.ERROR, message);
    }

    public static R error(Object data) {
        return error(HttpStatusCode.ERROR, null, data);
    }

    public static R error(String message, Object data) {
        return error(HttpStatusCode.ERROR, message, data);
    }

    public static R error(HttpStatusCode httpStatusCode) {
        return error(httpStatusCode, httpStatusCode.getMessage());
    }

    public static R error(HttpStatusCode httpStatusCode, String message) {
        return error(httpStatusCode, message, null);
    }

    public static R error(HttpStatusCode httpStatusCode, String message, Object data) {
        return error(httpStatusCode.code, null != message ? message : httpStatusCode.message, data);
    }

    public static R error(int code, String message, Object data) {
        return initInstance(code, message, data);
    }

    public static R error(int code, String message) {
        return initInstance(code, message, null);
    }

    /**
     * Http状态码
     */
    public enum HttpStatusCode {
        //继续
        CONTINUE(100, "CONTINUE", "继续"),
        //切换协议
        CHANGE_PROTOCOL(101, "CHANGE_PROTOCOL", "切换协议"),
        //执行成功
        SUCCESS(200, "SUCCEED", "执行成功"),
        //已创建
        CREATED(201, "CREATED", "已创建"),
        //已接受
        ACCEPTED(202, "ACCEPTED", "已接受"),
        //非授权信息
        AUTH_ILLEGALITY(203, "AUTH_ILLEGALITY", "非授权信息"),
        //无内容
        EMPTY_CONTENT(204, "EMPTY_CONTENT", "无内容"),
        //重置内容
        RESET_CONTENT(205, "RESET_CONTENT", "重置内容"),
        //部分内容
        PART_CONTENT(206, "PART_CONTENT", "部分内容"),
        //多种选择
        MANY_CHOICE(300, "MANY_CHOICE", "多种选择"),
        //永久移动
        FOREVER_MOVED(301, "FOREVER_MOVED", "永久移动"),
        //临时移动
        TEMP_MOVED(302, "TEMP_MOVED", "临时移动"),
        //查看其他位置
        LOOK_FOR_OTHER(303, "LOOK_FOR_OTHER", "查看其他位置"),
        //未修改
        UN_MODIFY(304, "UN_MODIFY", "未修改"),
        //使用代理
        USE_PROXY(305, "USE_PROXY", "使用代理"),
        //临时重定向
        TEMP_REDIRECT(307, "TEMP_REDIRECT", "临时重定向"),
        //错误请求
        BAD_REQUEST(400, "BAD_REQUEST", "错误请求"),
        //未授权
        UN_AUTH(401, "UN_AUTHORIZATION", "未授权"),
        //校验错误
        VALIDATE_ERROR(402, "VALIDATE_ERROR", "校验错误"),
        //禁止
        FORBIDDEN(403, "FORBIDDEN", "禁止"),
        //未找到
        NOT_FOUND(404, "NOT_FOUND", "未找到"),
        //方法禁用
        METHOD_NOT_ALLOW(405, "METHOD_NOT_ALLOW", "方法禁用"),
        //不接受
        UN_ACCEPT(406, "UN_ACCEPT", "不接受"),
        //需要代理授权
        NEED_PROXY_AUTH(407, "NEED_PROXY_AUTH", "需要代理授权"),
        //请求超时
        TIMEOUT(408, "TIMEOUT", "请求超时"),
        //冲突
        CONFLICT(409, "CONFLICT", "冲突"),
        //已删除
        DELETED(410, "DELETED", "已删除"),
        //需要有效长度
        LENGTH_INVALID(411, "LENGTH_INVALID", "需要有效长度"),
        //未满足前提条件
        UNSATISFIED_PRECONDITION(412, "UNSATISFIED_PRECONDITION", "未满足前提条件"),
        //请求实体过大
        BODY_TOO_LONG(413, "BODY_TOO_LONG", "请求实体过大"),
        //请求的 URI 过长
        URI_TOO_LONG(414, "URI_TOO_LONG", "请求的 URI 过长"),
        //不支持的媒体类型
        UN_SUPPORT_MEDIA_MIME(414, "UN_SUPPORT_MEDIA_MIME", "不支持的媒体类型"),
        //请求范围不符合要求
        SCOPE_INVALID(416, "SCOPE_INVALID", "请求范围不符合要求"),
        //未满足期望值
        UN_SATISFY_EXPECT(417, "UN_SATISFY_EXPECT", "未满足期望值"),
        //执行失败
        ERROR(500, "ERROR", "执行失败"),
        //尚未实施
        UN_IMPLEMENTS(501, "UN IMPLEMENTS", "尚未实施"),
        //错误网关
        GATEWAY_ERROR(502, "GATEWAY_ERROR", "错误网关"),
        //服务不可用
        NOT_AVAILABLE(503, "NOT_AVAILABLE", "服务不可用"),
        //网关超时
        GATEWAY_TIMEOUT(504, "GATEWAY_TIMEOUT", "网关超时"),
        //HTTP 版本不受支持
        UN_SUPPORT_PROTOCOL(505, "UN_SUPPORT_PROTOCOL", "HTTP 版本不受支持");

        int code;
        String message;
        String memo;

        HttpStatusCode(int code, String message, String memo) {
            this.code = code;
            this.message = message;
            this.memo = memo;
        }

        public int getCode() {
            return this.code;
        }

        public String getMessage() {
            return this.message;
        }

        public String getMemo() {
            return this.memo;
        }
    }

    public int getCode() {
        return code;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public R setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public static String getZoneOffset() {
        return ZONE_OFFSET;
    }

    public String getTrackId() {
        return trackId;
    }
}
