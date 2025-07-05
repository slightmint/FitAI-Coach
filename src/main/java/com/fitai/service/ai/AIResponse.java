package com.fitai.service.ai;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * AI响应结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIResponse {
    
    /**
     * 响应ID
     */
    private String responseId;
    
    /**
     * 对应的请求ID
     */
    private String requestId;
    
    /**
     * 响应状态
     */
    @Builder.Default
    private ResponseStatus status = ResponseStatus.SUCCESS;
    
    /**
     * 响应内容
     */
    private String content;
    
    /**
     * 响应数据
     */
    private Map<String, Object> data;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 错误代码
     */
    private String errorCode;
    
    /**
     * 处理时间（毫秒）
     */
    private Long processingTimeMs;
    
    /**
     * 响应时间
     */
    private LocalDateTime responseTime;
    
    /**
     * 置信度（0-1）
     */
    private Double confidence;
    
    /**
     * 建议列表
     */
    private List<String> suggestions;
    
    /**
     * 元数据
     */
    private Map<String, Object> metadata;
    
    /**
     * 响应状态枚举
     */
    public enum ResponseStatus {
        SUCCESS("成功"),
        PARTIAL_SUCCESS("部分成功"),
        FAILED("失败"),
        TIMEOUT("超时"),
        INVALID_REQUEST("无效请求"),
        SERVICE_UNAVAILABLE("服务不可用");
        
        private final String description;
        
        ResponseStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 创建成功响应
     */
    public static AIResponse success(String content) {
        return AIResponse.builder()
                .status(ResponseStatus.SUCCESS)
                .content(content)
                .responseTime(LocalDateTime.now())
                .build();
    }
    
    /**
     * 创建成功响应（带数据）
     */
    public static AIResponse success(String content, Map<String, Object> data) {
        return AIResponse.builder()
                .status(ResponseStatus.SUCCESS)
                .content(content)
                .data(data)
                .responseTime(LocalDateTime.now())
                .build();
    }
    
    /**
     * 创建失败响应
     */
    public static AIResponse failure(String errorMessage) {
        return AIResponse.builder()
                .status(ResponseStatus.FAILED)
                .errorMessage(errorMessage)
                .responseTime(LocalDateTime.now())
                .build();
    }
    
    /**
     * 创建失败响应（带错误代码）
     */
    public static AIResponse failure(String errorCode, String errorMessage) {
        return AIResponse.builder()
                .status(ResponseStatus.FAILED)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .responseTime(LocalDateTime.now())
                .build();
    }
    
    /**
     * 添加数据
     */
    public void addData(String key, Object value) {
        if (data == null) {
            data = new HashMap<>();
        }
        data.put(key, value);
    }
    
    /**
     * 获取数据
     */
    public Object getData(String key) {
        return data != null ? data.get(key) : null;
    }
    
    /**
     * 添加元数据
     */
    public void addMetadata(String key, Object value) {
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        metadata.put(key, value);
    }
    
    /**
     * 是否成功
     */
    public boolean isSuccess() {
        return status == ResponseStatus.SUCCESS || status == ResponseStatus.PARTIAL_SUCCESS;
    }
    
    /**
     * 是否失败
     */
    public boolean isFailed() {
        return !isSuccess();
    }
}