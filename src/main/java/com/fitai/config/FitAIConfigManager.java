package com.fitai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FitAI配置管理器 - 单例模式实现
 * 负责管理应用程序的所有配置信息
 */
@Component
@Slf4j
@Getter
public class FitAIConfigManager {
    
    private static FitAIConfigManager instance;
    private final Map<String, Object> configCache = new ConcurrentHashMap<>();
    
    @Value("${fitai.coach.version:1.0.0}")
    private String version;
    
    @Value("${fitai.coach.ai.model:gpt-3.5-turbo}")
    private String aiModel;
    
    @Value("${fitai.coach.ai.api-key:}")
    private String aiApiKey;
    
    @Value("${fitai.coach.fitness.default-workout-duration:60}")
    private Integer defaultWorkoutDuration;
    
    @Value("${fitai.coach.fitness.max-daily-workouts:3}")
    private Integer maxDailyWorkouts;
    
    @Value("${spring.datasource.url}")
    private String databaseUrl;
    
    @Value("${server.port:8080}")
    private Integer serverPort;
    
    // AI相关配置
    private static final String AI_API_TIMEOUT = "30000"; // 30秒
    private static final String AI_MAX_RETRIES = "3";
    private static final String AI_TEMPERATURE = "0.7";
    
    // 健身相关配置
    private static final Integer MIN_WORKOUT_DURATION = 15;
    private static final Integer MAX_WORKOUT_DURATION = 180;
    private static final Integer DEFAULT_REST_TIME = 60;
    
    // 营养相关配置
    private static final Double DEFAULT_PROTEIN_RATIO = 0.25;
    private static final Double DEFAULT_CARB_RATIO = 0.45;
    private static final Double DEFAULT_FAT_RATIO = 0.30;
    
    @PostConstruct
    public void init() {
        instance = this;
        initializeConfigCache();
        validateConfiguration();
        log.info("FitAI配置管理器初始化完成 - 版本: {}", version);
    }
    
    /**
     * 获取单例实例
     */
    public static FitAIConfigManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("FitAIConfigManager尚未初始化");
        }
        return instance;
    }
    
    /**
     * 初始化配置缓存
     */
    private void initializeConfigCache() {
        // AI配置
        configCache.put("ai.model", aiModel);
        configCache.put("ai.api.timeout", AI_API_TIMEOUT);
        configCache.put("ai.max.retries", AI_MAX_RETRIES);
        configCache.put("ai.temperature", AI_TEMPERATURE);
        
        // 健身配置
        configCache.put("fitness.default.duration", defaultWorkoutDuration);
        configCache.put("fitness.max.daily.workouts", maxDailyWorkouts);
        configCache.put("fitness.min.duration", MIN_WORKOUT_DURATION);
        configCache.put("fitness.max.duration", MAX_WORKOUT_DURATION);
        configCache.put("fitness.default.rest.time", DEFAULT_REST_TIME);
        
        // 营养配置
        configCache.put("nutrition.protein.ratio", DEFAULT_PROTEIN_RATIO);
        configCache.put("nutrition.carb.ratio", DEFAULT_CARB_RATIO);
        configCache.put("nutrition.fat.ratio", DEFAULT_FAT_RATIO);
        
        // 系统配置
        configCache.put("system.version", version);
        configCache.put("system.port", serverPort);
    }
    
    /**
     * 验证配置
     */
    private void validateConfiguration() {
        if (aiApiKey == null || aiApiKey.trim().isEmpty()) {
            log.warn("AI API密钥未配置，AI功能将不可用");
        }
        
        if (defaultWorkoutDuration < MIN_WORKOUT_DURATION || 
            defaultWorkoutDuration > MAX_WORKOUT_DURATION) {
            log.warn("默认训练时长配置异常: {} 分钟", defaultWorkoutDuration);
        }
        
        if (maxDailyWorkouts < 1 || maxDailyWorkouts > 10) {
            log.warn("每日最大训练次数配置异常: {} 次", maxDailyWorkouts);
        }
    }
    
    /**
     * 获取配置值
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfig(String key, Class<T> type) {
        Object value = configCache.get(key);
        if (value == null) {
            log.warn("配置项不存在: {}", key);
            return null;
        }
        
        try {
            return (T) value;
        } catch (ClassCastException e) {
            log.error("配置项类型转换失败: {} -> {}", key, type.getSimpleName(), e);
            return null;
        }
    }
    
    /**
     * 设置配置值（运行时动态配置）
     */
    public void setConfig(String key, Object value) {
        configCache.put(key, value);
        log.info("配置项已更新: {} = {}", key, value);
    }
    
    /**
     * 获取AI相关配置
     */
    public AIConfig getAIConfig() {
        return AIConfig.builder()
            .model(aiModel)
            .apiKey(aiApiKey)
            .timeout(Integer.parseInt(AI_API_TIMEOUT))
            .maxRetries(Integer.parseInt(AI_MAX_RETRIES))
            .temperature(Double.parseDouble(AI_TEMPERATURE))
            .build();
    }
    
    /**
     * 获取健身相关配置
     */
    public FitnessConfig getFitnessConfig() {
        return FitnessConfig.builder()
            .defaultDuration(defaultWorkoutDuration)
            .maxDailyWorkouts(maxDailyWorkouts)
            .minDuration(MIN_WORKOUT_DURATION)
            .maxDuration(MAX_WORKOUT_DURATION)
            .defaultRestTime(DEFAULT_REST_TIME)
            .build();
    }
    
    /**
     * 获取营养相关配置
     */
    public NutritionConfig getNutritionConfig() {
        return NutritionConfig.builder()
            .proteinRatio(DEFAULT_PROTEIN_RATIO)
            .carbRatio(DEFAULT_CARB_RATIO)
            .fatRatio(DEFAULT_FAT_RATIO)
            .build();
    }
    
    /**
     * 检查AI功能是否可用
     */
    public boolean isAIEnabled() {
        return aiApiKey != null && !aiApiKey.trim().isEmpty();
    }
    
    /**
     * 获取所有配置信息（用于调试）
     */
    public Map<String, Object> getAllConfigs() {
        return new ConcurrentHashMap<>(configCache);
    }
    
    // 内部配置类
    @lombok.Builder
    @lombok.Data
    public static class AIConfig {
        private String model;
        private String apiKey;
        private Integer timeout;
        private Integer maxRetries;
        private Double temperature;
    }
    
    @lombok.Builder
    @lombok.Data
    public static class FitnessConfig {
        private Integer defaultDuration;
        private Integer maxDailyWorkouts;
        private Integer minDuration;
        private Integer maxDuration;
        private Integer defaultRestTime;
    }
    
    @lombok.Builder
    @lombok.Data
    public static class NutritionConfig {
        private Double proteinRatio;
        private Double carbRatio;
        private Double fatRatio;
    }
}