package com.fitai.service.strategy;

import com.fitai.model.User;
import com.fitai.model.WorkoutPlan;
import com.fitai.model.Exercise;

import java.util.List;

/**
 * 训练策略接口 - 策略模式
 * 定义不同类型训练的生成策略
 */
public interface WorkoutStrategy {
    
    /**
     * 生成训练项目列表
     * @param user 用户信息
     * @param plan 训练计划
     * @return 训练项目列表
     */
    List<Exercise> generateExercises(User user, WorkoutPlan plan);
    
    /**
     * 获取策略名称
     * @return 策略名称
     */
    String getStrategyName();
    
    /**
     * 验证策略是否适用于用户
     * @param user 用户信息
     * @return 是否适用
     */
    boolean isApplicable(User user);
    
    /**
     * 计算训练强度
     * @param user 用户信息
     * @param plan 训练计划
     * @return 强度系数 (0.1-2.0)
     */
    double calculateIntensity(User user, WorkoutPlan plan);
}