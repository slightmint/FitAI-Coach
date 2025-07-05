package com.fitai.service.strategy;

import com.fitai.model.User;
import com.fitai.model.WorkoutPlan;

/**
 * 训练计划生成策略接口 - 策略模式
 */
public interface WorkoutPlanStrategy {
    
    /**
     * 生成训练计划
     * @param user 用户信息
     * @param preferences 用户偏好
     * @return 生成的训练计划
     */
    WorkoutPlan generatePlan(User user, WorkoutPreferences preferences);
    
    /**
     * 获取策略类型
     * @return 策略类型
     */
    StrategyType getStrategyType();
    
    /**
     * 判断策略是否适用于用户
     * @param user 用户信息
     * @return 是否适用
     */
    boolean isApplicable(User user);
    
    /**
     * 策略类型枚举
     */
    enum StrategyType {
        BEGINNER_STRATEGY("初学者策略"),
        WEIGHT_LOSS_STRATEGY("减重策略"),
        MUSCLE_GAIN_STRATEGY("增肌策略"),
        ENDURANCE_STRATEGY("耐力策略"),
        STRENGTH_STRATEGY("力量策略");
        
        private final String description;
        
        StrategyType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}