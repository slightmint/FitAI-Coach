package com.fitai.service.strategy;

import com.fitai.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 有氧训练策略实现
 */
@Component
@Slf4j
public class CardioTrainingStrategy implements WorkoutStrategy {
    
    private final Random random = new Random();
    
    // 有氧训练动作库
    private final List<ExerciseTemplate> cardioExercises = Arrays.asList(
        new ExerciseTemplate("跑步", "经典有氧运动，提升心肺功能", 
                           Exercise.Equipment.NONE, 1, 0, 1800, 12.0),
        new ExerciseTemplate("快走", "低强度有氧运动，适合初学者", 
                           Exercise.Equipment.NONE, 1, 0, 2400, 6.0),
        new ExerciseTemplate("跳绳", "高效燃脂有氧运动", 
                           Exercise.Equipment.NONE, 3, 0, 300, 15.0),
        new ExerciseTemplate("开合跳", "全身有氧运动", 
                           Exercise.Equipment.NONE, 3, 50, 0, 10.0),
        new ExerciseTemplate("高抬腿", "下肢有氧训练", 
                           Exercise.Equipment.NONE, 3, 30, 0, 8.0),
        new ExerciseTemplate("波比跳", "全身高强度有氧", 
                           Exercise.Equipment.NONE, 3, 10, 0, 18.0),
        new ExerciseTemplate("登山者", "核心有氧训练", 
                           Exercise.Equipment.NONE, 3, 20, 0, 12.0),
        new ExerciseTemplate("踏步", "简单有氧运动", 
                           Exercise.Equipment.NONE, 1, 0, 1200, 7.0),
        new ExerciseTemplate("椭圆机", "低冲击有氧运动", 
                           Exercise.Equipment.TREADMILL, 1, 0, 1800, 10.0),
        new ExerciseTemplate("动感单车", "下肢有氧训练", 
                           Exercise.Equipment.STATIONARY_BIKE, 1, 0, 2400, 11.0)
    );
    
    @Override
    public List<Exercise> generateExercises(User user, WorkoutPlan plan) {
        log.info("为用户 {} 生成有氧训练计划", user.getUsername());
        
        List<Exercise> exercises = new ArrayList<>();
        double intensity = calculateIntensity(user, plan);
        
        // 有氧训练通常包含3-6个动作
        int exerciseCount = calculateExerciseCount(plan.getSessionDurationMinutes(), user);
        
        // 选择适合的有氧动作，确保创建可变列表
        List<ExerciseTemplate> availableExercises = new ArrayList<>(filterAvailableExercises(user));
        
        for (int i = 0; i < exerciseCount && !availableExercises.isEmpty(); i++) {
            ExerciseTemplate template = selectExercise(availableExercises, user, i);
            Exercise exercise = createExerciseFromTemplate(template, plan, intensity);
            exercises.add(exercise);
            
            // 避免重复选择相同动作
            availableExercises.remove(template);
        }
        
        log.info("生成了 {} 个有氧训练动作", exercises.size());
        return exercises;
    }
    
    @Override
    public String getStrategyName() {
        return "有氧训练策略";
    }
    
    @Override
    public boolean isApplicable(User user) {
        // 有氧训练适用于所有用户，特别是减脂目标
        return true;
    }
    
    @Override
    public double calculateIntensity(User user, WorkoutPlan plan) {
        double baseIntensity = 1.0;
        
        // 根据健身目标调整
        if (user.getFitnessGoal() == User.FitnessGoal.WEIGHT_LOSS) {
            baseIntensity = 1.2; // 减脂用户提高强度
        } else if (user.getFitnessGoal() == User.FitnessGoal.ENDURANCE) {
            baseIntensity = 1.1; // 耐力用户适度提高
        }
        
        // 根据健身水平调整
        switch (user.getFitnessLevel()) {
            case BEGINNER:
                baseIntensity *= 0.7;
                break;
            case INTERMEDIATE:
                baseIntensity *= 0.85;
                break;
            case ADVANCED:
                baseIntensity *= 1.0;
                break;
            // 删除这个不存在的 case
            // case EXPERT:
            //     baseIntensity *= 1.1;
            //     break;
        }
        
        // 根据BMI调整
        double bmi = user.calculateBMI();
        if (bmi > 30) {
            baseIntensity *= 0.8; // 肥胖用户从低强度开始
        }
        
        // 根据年龄调整
        if (user.getAge() != null && user.getAge() > 50) {
            baseIntensity *= 0.85;
        }
        
        return Math.max(0.5, Math.min(1.5, baseIntensity));
    }
    
    private int calculateExerciseCount(int sessionDuration, User user) {
        if (user.getFitnessLevel() == User.FitnessLevel.BEGINNER) {
            return Math.max(3, Math.min(5, sessionDuration / 12));
        } else {
            return Math.max(4, Math.min(6, sessionDuration / 10));
        }
    }
    
    private List<ExerciseTemplate> filterAvailableExercises(User user) {
        return cardioExercises.stream()
            .filter(ex -> isExerciseSuitable(ex, user))
            .collect(Collectors.toList());  // 改为使用 collect(Collectors.toList())
    }
    
    private boolean isExerciseSuitable(ExerciseTemplate exercise, User user) {
        // 初学者避免高强度动作
        if (user.getFitnessLevel() == User.FitnessLevel.BEGINNER) {
            return !exercise.name.equals("波比跳") && exercise.caloriesPerMinute < 15.0;
        }
        
        // 年长用户避免高冲击动作
        if (user.getAge() != null && user.getAge() > 55) {
            return !exercise.name.equals("跳绳") && !exercise.name.equals("波比跳");
        }
        
        // 肥胖用户优先选择低冲击动作
        if (user.calculateBMI() > 30) {
            return exercise.name.equals("快走") || exercise.name.equals("椭圆机") || 
                   exercise.name.equals("动感单车") || exercise.name.equals("踏步");
        }
        
        return true;
    }
    
    private ExerciseTemplate selectExercise(List<ExerciseTemplate> available, User user, int index) {
        // 第一个动作选择热身性质的
        if (index == 0) {
            return available.stream()
                .filter(ex -> ex.name.equals("快走") || ex.name.equals("踏步"))
                .findFirst()
                .orElse(available.get(0));
        }
        
        // 最后一个动作选择放松性质的
        if (index == available.size() - 1) {
            return available.stream()
                .filter(ex -> ex.caloriesPerMinute < 8.0)
                .findFirst()
                .orElse(available.get(available.size() - 1));
        }
        
        // 中间动作随机选择
        return available.get(random.nextInt(available.size()));
    }
    
    private Exercise createExerciseFromTemplate(ExerciseTemplate template, WorkoutPlan plan, double intensity) {
        Exercise exercise = new Exercise();
        exercise.setName(template.name);
        exercise.setDescription(template.description);
        exercise.setCategory(Exercise.ExerciseCategory.CARDIO);
        exercise.setPrimaryMuscleGroup(Exercise.MuscleGroup.FULL_BODY);
        exercise.setEquipmentNeeded(template.equipment);
        exercise.setDifficultyLevel(mapPlanDifficultyToExercise(plan.getDifficultyLevel()));
        // 删除或注释这行错误的代码
        // exercise.setWorkoutPlan(plan);
        
        // 根据强度调整训练参数
        // exercise.setRecommendedSets((int) Math.max(1, template.sets * intensity));
       //exercise.setRecommendedReps(template.reps > 0 ? 
                                   //(int) Math.max(10, template.reps * intensity) : 0);
        //exercise.setRecommendedDurationSeconds(template.duration > 0 ? 
                                              //(int) Math.max(60, template.duration * intensity) : 0);
        exercise.setCaloriesPerMinute(template.caloriesPerMinute);
        
        // 设置指导信息
        exercise.setInstructions(generateInstructions(template));
        //exercise.setSafetyTips(generateSafetyTips(template));
        
        return exercise;
    }
    
    private Exercise.DifficultyLevel mapPlanDifficultyToExercise(WorkoutPlan.DifficultyLevel planDifficulty) {
        switch (planDifficulty) {
            case EASY: return Exercise.DifficultyLevel.BEGINNER;
            case MEDIUM: return Exercise.DifficultyLevel.INTERMEDIATE;
            case HARD: return Exercise.DifficultyLevel.ADVANCED;
            case EXPERT: return Exercise.DifficultyLevel.ADVANCED; // 或者需要在 Exercise.DifficultyLevel 中添加 EXPERT
            default: return Exercise.DifficultyLevel.BEGINNER;
        }
    }
    
    private String generateInstructions(ExerciseTemplate template) {
        switch (template.name) {
            case "跑步":
                return "保持匀速，呼吸节奏稳定，脚掌中部着地，避免脚跟重击地面。";
            case "跳绳":
                return "双脚轻跳，保持膝盖微屈，手腕发力转绳，保持身体直立。";
            case "波比跳":
                return "下蹲-俯卧撑-跳跃的连贯动作，保持动作标准，控制节奏。";
            case "开合跳":
                return "双脚开合跳跃，同时手臂上下摆动，保持核心收紧。";
            default:
                return "保持动作标准，控制呼吸节奏，根据自身能力调整强度。";
        }
    }
    
    private String generateSafetyTips(ExerciseTemplate template) {
        return "运动前充分热身，运动中注意心率变化，感到头晕或胸闷立即停止休息。";
    }
    
    // 动作模板内部类
    private static class ExerciseTemplate {
        final String name;
        final String description;
        final Exercise.Equipment equipment;  // 修改这里
        final int sets;
        final int reps;
        final int duration;
        final double caloriesPerMinute;
        
        ExerciseTemplate(String name, String description, Exercise.Equipment equipment,  // 修改这里
                        int sets, int reps, int duration, double caloriesPerMinute) {
            this.name = name;
            this.description = description;
            this.equipment = equipment;
            this.sets = sets;
            this.reps = reps;
            this.duration = duration;
            this.caloriesPerMinute = caloriesPerMinute;
        }
    }
}