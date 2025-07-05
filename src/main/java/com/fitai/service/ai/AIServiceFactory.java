package com.fitai.service.ai;

import com.fitai.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AI服务工厂类 - 工厂模式实现
 * 根据不同的需求创建相应的AI服务实例
 */
@Component
public class AIServiceFactory {
    
    @Autowired
    private WorkoutPlanAIService workoutPlanAIService;
    
    @Autowired
    private NutritionAIService nutritionAIService;
    
    @Autowired
    private MotionRecognitionAIService motionRecognitionAIService;
    
    /**
     * 创建AI服务实例
     * @param serviceType AI服务类型
     * @return AI服务实例
     */
    public AIService createAIService(AIServiceType serviceType) {
        switch (serviceType) {
            case WORKOUT_PLAN:
                return workoutPlanAIService;
            case NUTRITION:
                return nutritionAIService;
            case MOTION_RECOGNITION:
                return motionRecognitionAIService;
            default:
                throw new IllegalArgumentException("不支持的AI服务类型: " + serviceType);
        }
    }
    
    /**
     * 根据用户特征创建推荐的AI服务
     * @param user 用户信息
     * @return 推荐的AI服务
     */
    public AIService createRecommendedAIService(User user) {
        // 根据用户的健身目标推荐合适的AI服务
        if (user.getFitnessGoal() == User.FitnessGoal.WEIGHT_LOSS) {
            return nutritionAIService;
        } else if (user.getFitnessGoal() == User.FitnessGoal.MUSCLE_GAIN || 
                   user.getFitnessGoal() == User.FitnessGoal.STRENGTH) {
            return workoutPlanAIService;
        } else {
            return workoutPlanAIService; // 默认返回训练计划服务
        }
    }
    
    /**
     * AI服务类型枚举
     */
    public enum AIServiceType {
        WORKOUT_PLAN("训练计划生成"),
        NUTRITION("营养建议"),
        MOTION_RECOGNITION("动作识别");
        
        private final String description;
        
        AIServiceType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}