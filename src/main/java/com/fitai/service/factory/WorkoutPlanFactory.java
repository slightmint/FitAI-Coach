package com.fitai.service.factory;


import com.fitai.model.*;
import com.fitai.service.strategy.*;
import com.fitai.service.ExerciseService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * 训练计划工厂 - 工厂模式实现
 * 根据用户特征和目标创建个性化训练计划
 */
import com.fitai.service.ExerciseService;

@Component
@Slf4j
public class WorkoutPlanFactory {
    
    private final Map<String, WorkoutStrategy> strategyMap = new HashMap<>();
    private final ExerciseService exerciseService;  // 添加这行
    
    @Autowired
    public WorkoutPlanFactory(
            StrengthTrainingStrategy strengthStrategy,
            CardioTrainingStrategy cardioStrategy,
            HIITTrainingStrategy hiitStrategy,
            YogaTrainingStrategy yogaStrategy,
            MixedTrainingStrategy mixedStrategy,
            ExerciseService exerciseService) {  // 添加这个参数
        
        this.exerciseService = exerciseService;  // 添加这行
        strategyMap.put("STRENGTH", strengthStrategy);
        strategyMap.put("CARDIO", cardioStrategy);
        strategyMap.put("HIIT", hiitStrategy);
        strategyMap.put("YOGA", yogaStrategy);
        strategyMap.put("MIXED", mixedStrategy);
    }
    
    /**
     * 创建训练计划
     */
    public WorkoutPlan createWorkoutPlan(User user, WorkoutPlanRequest request) {
        log.info("为用户 {} 创建训练计划，类型: {}", user.getUsername(), request.planType);
        
        // 根据用户特征分析最适合的训练类型
        String recommendedType = analyzeOptimalTrainingType(user, request);
        
        // 获取对应的训练策略
        WorkoutStrategy strategy = getStrategy(recommendedType);
        
        // 创建基础计划
        WorkoutPlan plan = createBasePlan(user, request, recommendedType);
        
        // 使用策略生成具体的训练内容
        List<Exercise> exercises = strategy.generateExercises(user, plan);
        
        // 保存Exercise对象并转换为WorkoutExercise
        List<WorkoutExercise> workoutExercises = exercises.stream()
            .map(exercise -> {
                // 先检查是否已存在，如果存在则使用现有的，否则创建新的
                Exercise savedExercise = exerciseService.createOrGetExercise(exercise);
                
                WorkoutExercise workoutExercise = new WorkoutExercise();
                workoutExercise.setExercise(savedExercise);  // 使用保存后的Exercise
                workoutExercise.setWorkoutPlan(plan);
                // 设置默认值，可以根据需要调整
                workoutExercise.setSets(3);
                workoutExercise.setReps(12);
                workoutExercise.setDurationSeconds(60);
                return workoutExercise;
            })
            .collect(Collectors.toList());
            
        plan.setExercises(workoutExercises);
        
        // 优化计划参数
        optimizePlanParameters(plan, user);
        
        log.info("训练计划创建完成，包含 {} 个运动项目", exercises.size());
        return plan;
    }
    
    /**
     * 分析最优训练类型
     */
    private String analyzeOptimalTrainingType(User user, WorkoutPlanRequest request) {
        // 如果用户指定了类型，优先使用
        if (request.planType != null) {
            return request.planType;  // 直接访问字段而不是通过getter
        }
        
        // 根据用户目标推荐训练类型
        switch (user.getFitnessGoal()) {
            case WEIGHT_LOSS:
                return user.getFitnessLevel() == User.FitnessLevel.BEGINNER ? "CARDIO" : "HIIT";
            case MUSCLE_GAIN:
                return "STRENGTH";
            case STRENGTH:
                return "STRENGTH";
            case ENDURANCE:
                return "CARDIO";
            case GENERAL_FITNESS:
            default:
                return "MIXED";
        }
    }
    
    /**
     * 获取训练策略
     */
    private WorkoutStrategy getStrategy(String type) {
        WorkoutStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            log.warn("未找到训练策略: {}，使用混合训练策略", type);
            return strategyMap.get("MIXED");
        }
        return strategy;
    }
    
    /**
     * 创建基础计划
     */
    private WorkoutPlan createBasePlan(User user, WorkoutPlanRequest request, String planType) {
        WorkoutPlan plan = new WorkoutPlan();
        plan.setUser(user);
        plan.setName(generatePlanName(user, planType));
        plan.setDescription(generatePlanDescription(user, planType));
        plan.setPlanType(WorkoutPlan.PlanType.valueOf(planType));
        plan.setDifficultyLevel(determineDifficulty(user));
        plan.setDurationWeeks(request.getDurationWeeks() != null ? 
                              request.getDurationWeeks() : getDefaultDuration(user));
        plan.setSessionsPerWeek(request.getSessionsPerWeek() != null ? 
                               request.getSessionsPerWeek() : getDefaultFrequency(user));
        // 第132行
        plan.setEstimatedDurationMinutes(request.getSessionDuration() != null ?
                request.getSessionDuration() : getDefaultSessionDuration(user));
        
        // 第241行 (adjustForObeseUser 方法中)
        plan.setEstimatedDurationMinutes(60);
        
        // 第258行 (其他调用位置)
        plan.setEstimatedDurationMinutes(45);
        return plan;
    }
    
    /**
     * 生成计划名称
     */
    private String generatePlanName(User user, String planType) {
        String typeDesc = getTypeDescription(planType);
        String levelDesc = getLevelDescription(user.getFitnessLevel());
        return String.format("%s的%s%s计划", user.getFullName(), levelDesc, typeDesc);
    }
    
    /**
     * 生成计划描述
     */
    private String generatePlanDescription(User user, String planType) {
        return String.format("为%s量身定制的%s训练计划，目标：%s，适合%s水平的训练者",
                user.getFullName(),
                getTypeDescription(planType),
                getGoalDescription(user.getFitnessGoal()),
                getLevelDescription(user.getFitnessLevel()));
    }
    
    /**
     * 确定训练难度
     */
    private WorkoutPlan.DifficultyLevel determineDifficulty(User user) {
        switch (user.getFitnessLevel()) {
            case BEGINNER:
                return WorkoutPlan.DifficultyLevel.EASY;  // 改为 EASY
            case INTERMEDIATE:
                return WorkoutPlan.DifficultyLevel.MEDIUM;  // 改为 MEDIUM
            case ADVANCED:
                return WorkoutPlan.DifficultyLevel.EXPERT;  // 保持 EXPERT
            default:
                return WorkoutPlan.DifficultyLevel.EASY;  // 改为 EASY
        }
    }
    
    /**
     * 获取默认训练周数
     */
    private Integer getDefaultDuration(User user) {
        switch (user.getFitnessLevel()) {
            case BEGINNER:
                return 8;  // 8周入门计划
            case INTERMEDIATE:
                return 12; // 12周进阶计划
            case ADVANCED:  // 删除 case EXPERT:
                return 16; // 16周高级计划
            default:
                return 8;
        }
    }
    
    private Integer getDefaultFrequency(User user) {
        switch (user.getFitnessLevel()) {
            case BEGINNER:
                return 3; // 每周3次
            case INTERMEDIATE:
                return 4; // 每周4次
            case ADVANCED:  // 删除 case EXPERT:
                return 5; // 每周5次
            default:
                return 3;
        }
    }
    
    /**
     * 获取默认单次训练时长
     */
    private Integer getDefaultSessionDuration(User user) {
        switch (user.getFitnessLevel()) {
            case BEGINNER:
                return 45; // 45分钟
            case INTERMEDIATE:
                return 60; // 60分钟
            case ADVANCED:  // 删除 case EXPERT:
                return 75; // 75分钟
            default:
                return 45;
        }
    }
    
    /**
     * 优化计划参数
     */
    private void optimizePlanParameters(WorkoutPlan plan, User user) {
        // 根据用户BMI调整训练强度
        double bmi = user.calculateBMI();
        if (bmi > 30) { // 肥胖用户
            // 增加有氧运动比例，降低高强度训练
            adjustForObeseUser(plan);
        } else if (bmi < 18.5) { // 偏瘦用户
            // 增加力量训练比例
            adjustForUnderweightUser(plan);
        }
        
        // 根据年龄调整
        if (user.getAge() != null && user.getAge() > 50) {
            adjustForSeniorUser(plan);
        }
    }
    
    private void adjustForObeseUser(WorkoutPlan plan) {
        // 降低训练强度，增加有氧运动
        if (plan.getSessionDurationMinutes() > 60) {
            plan.setEstimatedDurationMinutes(60);  // 第248行：改为 setEstimatedDurationMinutes
        }
    }
    
    private void adjustForUnderweightUser(WorkoutPlan plan) {
        // 重点关注力量训练
        if (plan.getPlanType() == WorkoutPlan.PlanType.CARDIO) {
            plan.setPlanType(WorkoutPlan.PlanType.MIXED);
        }
        // 其他调整逻辑
        plan.setEstimatedDurationMinutes(45);  // 第265行：改为 setEstimatedDurationMinutes
    }
    
    private void adjustForSeniorUser(WorkoutPlan plan) {
        // 降低训练频率和强度
        if (plan.getSessionsPerWeek() > 4) {
            plan.setSessionsPerWeek(4);
        }
        if (plan.getSessionDurationMinutes() > 45) {
            plan.setEstimatedDurationMinutes(45);  // 修改这一行
        }
    }
    
    // 辅助方法
    private String getTypeDescription(String type) {
        switch (type) {
            case "STRENGTH": return "力量训练";
            case "CARDIO": return "有氧训练";
            case "HIIT": return "高强度间歇训练";
            case "YOGA": return "瑜伽";
            case "MIXED": return "综合训练";
            default: return "训练";
        }
    }
    
    private String getLevelDescription(User.FitnessLevel level) {
        switch (level) {
            case BEGINNER: return "初级";
            case INTERMEDIATE: return "中级";
            case ADVANCED: return "高级";  // 删除 case EXPERT: return "专业";
            default: return "";
        }
    }
    
    private String getGoalDescription(User.FitnessGoal goal) {
        switch (goal) {
            case WEIGHT_LOSS: return "减脂塑形";
            case MUSCLE_GAIN: return "增肌";
            case STRENGTH: return "力量提升";
            case ENDURANCE: return "耐力提升";
            case GENERAL_FITNESS: return "综合健身";
            default: return "健身";
        }
    }
    
    /**
     * 训练计划请求类
     */
    @Data
    @Builder
    public static class WorkoutPlanRequest {
        private String planType;
        private Integer durationWeeks;
        private Integer sessionsPerWeek;
        private Integer sessionDuration;
        private String specialRequirements;
        
        // 手动添加 getter 方法（如果 Lombok 不工作）
        public Integer getDurationWeeks() {
            return durationWeeks;
        }
        
        public Integer getSessionsPerWeek() {
            return sessionsPerWeek;
        }
        
        public Integer getSessionDuration() {
            return sessionDuration;
        }
    }
}