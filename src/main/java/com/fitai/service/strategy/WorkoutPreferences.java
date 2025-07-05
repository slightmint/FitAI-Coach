package com.fitai.service.strategy;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 训练偏好设置类
 * 用于存储用户的训练偏好和要求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPreferences {
    
    /**
     * 训练持续周数
     */
    private Integer durationWeeks;
    
    /**
     * 每周训练次数
     */
    private Integer sessionsPerWeek;
    
    /**
     * 每次训练时长（分钟）
     */
    private Integer sessionDurationMinutes;
    
    /**
     * 偏好的训练类型
     */
    private String preferredTrainingType;
    
    /**
     * 是否有器械限制
     */
    private Boolean hasEquipmentLimitation;
    
    /**
     * 可用器械列表
     */
    private String availableEquipment;
    
    /**
     * 训练强度偏好 (LOW, MEDIUM, HIGH)
     */
    private String intensityPreference;
    
    /**
     * 特殊要求或限制
     */
    private String specialRequirements;
    
    /**
     * 是否偏好室内训练
     */
    private Boolean preferIndoor;
    
    /**
     * 目标卡路里消耗
     */
    private Double targetCalories;
    
    /**
     * 创建默认偏好设置
     */
    public static WorkoutPreferences createDefault() {
        return WorkoutPreferences.builder()
            .durationWeeks(8)
            .sessionsPerWeek(3)
            .sessionDurationMinutes(45)
            .intensityPreference("MEDIUM")
            .hasEquipmentLimitation(false)
            .preferIndoor(true)
            .build();
    }
}