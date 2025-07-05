package com.fitai.service.strategy;

import com.fitai.model.Exercise;
import com.fitai.model.User;
import com.fitai.model.WorkoutPlan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 混合训练策略
 * 结合力量、有氧、柔韧性训练的综合方案
 */
@Component
@Slf4j
public class MixedTrainingStrategy implements WorkoutStrategy {
    
    @Autowired
    private StrengthTrainingStrategy strengthStrategy;
    
    @Autowired
    private CardioTrainingStrategy cardioStrategy;
    
    @Autowired
    private YogaTrainingStrategy yogaStrategy;
    
    @Override
    public List<Exercise> generateExercises(User user, WorkoutPlan plan) {
        log.info("为用户 {} 生成混合训练计划", user.getUsername());
        
        List<Exercise> exercises = new ArrayList<>();
        
        // 根据用户目标调整各类训练的比例
        switch (user.getFitnessGoal()) {
            case WEIGHT_LOSS:
                // 减脂：40%有氧 + 40%力量 + 20%柔韧
                exercises.addAll(getPortionOfExercises(cardioStrategy.generateExercises(user, plan), 0.4));
                exercises.addAll(getPortionOfExercises(strengthStrategy.generateExercises(user, plan), 0.4));
                exercises.addAll(getPortionOfExercises(yogaStrategy.generateExercises(user, plan), 0.2));
                break;
                
            case MUSCLE_GAIN:
                // 增肌：60%力量 + 20%有氧 + 20%柔韧
                exercises.addAll(getPortionOfExercises(strengthStrategy.generateExercises(user, plan), 0.6));
                exercises.addAll(getPortionOfExercises(cardioStrategy.generateExercises(user, plan), 0.2));
                exercises.addAll(getPortionOfExercises(yogaStrategy.generateExercises(user, plan), 0.2));
                break;
                
            case ENDURANCE:
                // 耐力：50%有氧 + 30%力量 + 20%柔韧
                exercises.addAll(getPortionOfExercises(cardioStrategy.generateExercises(user, plan), 0.5));
                exercises.addAll(getPortionOfExercises(strengthStrategy.generateExercises(user, plan), 0.3));
                exercises.addAll(getPortionOfExercises(yogaStrategy.generateExercises(user, plan), 0.2));
                break;
                
            case GENERAL_FITNESS:
            default:
                // 综合健身：35%力量 + 35%有氧 + 30%柔韧
                exercises.addAll(getPortionOfExercises(strengthStrategy.generateExercises(user, plan), 0.35));
                exercises.addAll(getPortionOfExercises(cardioStrategy.generateExercises(user, plan), 0.35));
                exercises.addAll(getPortionOfExercises(yogaStrategy.generateExercises(user, plan), 0.3));
                break;
        }
        
        log.info("混合训练计划生成完成，共 {} 个运动项目", exercises.size());
        return exercises;
    }
    
    /**
     * 获取指定比例的运动列表
     */
    private List<Exercise> getPortionOfExercises(List<Exercise> allExercises, double portion) {
        int count = Math.max(1, (int) (allExercises.size() * portion));
        return allExercises.subList(0, Math.min(count, allExercises.size()));
    }
    
    @Override
    public String getStrategyName() {
        return "混合训练";
    }
    
    @Override
    public boolean isApplicable(User user) {
        // 混合训练适合所有用户
        return true;
    }
    
    @Override
    public double calculateIntensity(User user, WorkoutPlan plan) {
        // 混合训练强度取各策略的平均值
        double strengthIntensity = strengthStrategy.calculateIntensity(user, plan);
        double cardioIntensity = cardioStrategy.calculateIntensity(user, plan);
        double yogaIntensity = yogaStrategy.calculateIntensity(user, plan);
        
        // 根据用户目标调整权重
        double intensity;
        switch (user.getFitnessGoal()) {
            case WEIGHT_LOSS:
                intensity = cardioIntensity * 0.4 + strengthIntensity * 0.4 + yogaIntensity * 0.2;
                break;
            case MUSCLE_GAIN:
                intensity = strengthIntensity * 0.6 + cardioIntensity * 0.2 + yogaIntensity * 0.2;
                break;
            case ENDURANCE:
                intensity = cardioIntensity * 0.5 + strengthIntensity * 0.3 + yogaIntensity * 0.2;
                break;
            default:
                intensity = (strengthIntensity + cardioIntensity + yogaIntensity) / 3.0;
        }
        
        return Math.max(0.1, Math.min(2.0, intensity));
    }
}