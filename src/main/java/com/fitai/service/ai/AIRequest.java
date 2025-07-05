package com.fitai.service.ai;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * AI请求参数类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIRequest {
    
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 请求类型
     */
    @NotNull(message = "请求类型不能为空")
    private RequestType requestType;
    
    /**
     * 请求内容
     */
    @NotBlank(message = "请求内容不能为空")
    private String content;
    
    /**
     * 请求参数
     */
    private Map<String, Object> parameters;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 请求时间
     */
    private LocalDateTime requestTime;
    
    /**
     * 优先级
     */
    @Builder.Default
    private Priority priority = Priority.NORMAL;
    
    /**
     * 超时时间（秒）
     */
    @Builder.Default
    private Integer timeoutSeconds = 30;
    
    /**
     * 请求类型枚举
     */
    public enum RequestType {
        WORKOUT_PLAN_GENERATION("训练计划生成"),
        NUTRITION_ADVICE("营养建议"),
        EXERCISE_RECOMMENDATION("运动推荐"),
        PROGRESS_ANALYSIS("进度分析"),
        HEALTH_ASSESSMENT("健康评估"),
        CHAT("智能对话");
        
        private final String description;
        
        RequestType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 优先级枚举
     */
    public enum Priority {
        LOW(1, "低"),
        NORMAL(2, "普通"),
        HIGH(3, "高"),
        URGENT(4, "紧急");
        
        private final int level;
        private final String description;
        
        Priority(int level, String description) {
            this.level = level;
            this.description = description;
        }
        
        public int getLevel() {
            return level;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 添加参数
     */
    public void addParameter(String key, Object value) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put(key, value);
    }
    
    /**
     * 获取参数
     */
    public Object getParameter(String key) {
        return parameters != null ? parameters.get(key) : null;
    }
    
    /**
     * 初始化请求时间
     */
    public void initRequestTime() {
        if (requestTime == null) {
            requestTime = LocalDateTime.now();
        }
    }
}