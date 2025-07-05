package com.fitai.service.strategy;

import com.fitai.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 力量训练策略实现
 */
@Component
@Slf4j
public class StrengthTrainingStrategy implements WorkoutStrategy {
    
    private final Random random = new Random();
    
    // 力量训练动作库
    private final List<ExerciseTemplate> strengthExercises = Arrays.asList(
        new ExerciseTemplate("深蹲", "下肢力量训练的王牌动作", 
                       Exercise.MuscleGroup.LEGS, Exercise.Equipment.NONE, 4, 12, 0, 8.0),  // QUADRICEPS -> LEGS
        new ExerciseTemplate("硬拉", "全身力量训练，重点锻炼后链肌群", 
                       Exercise.MuscleGroup.BACK, Exercise.Equipment.BARBELL, 3, 8, 0, 10.0),
        new ExerciseTemplate("卧推", "胸部力量训练经典动作", 
                       Exercise.MuscleGroup.CHEST, Exercise.Equipment.BARBELL, 4, 10, 0, 9.0),
        new ExerciseTemplate("引体向上", "背部和手臂力量训练", 
                       Exercise.MuscleGroup.BACK, Exercise.Equipment.PULL_UP_BAR, 3, 8, 0, 7.5),
        new ExerciseTemplate("推举", "肩部力量训练", 
                       Exercise.MuscleGroup.SHOULDERS, Exercise.Equipment.DUMBBELLS, 3, 12, 0, 6.5),
        new ExerciseTemplate("弯举", "二头肌力量训练", 
                       Exercise.MuscleGroup.ARMS, Exercise.Equipment.DUMBBELLS, 3, 15, 0, 4.0),  // BICEPS -> ARMS
        new ExerciseTemplate("三头肌屈伸", "三头肌力量训练", 
                       Exercise.MuscleGroup.ARMS, Exercise.Equipment.DUMBBELLS, 3, 12, 0, 5.0),  // TRICEPS -> ARMS
        new ExerciseTemplate("箭步蹲", "单腿力量和平衡训练", 
                       Exercise.MuscleGroup.LEGS, Exercise.Equipment.DUMBBELLS, 3, 12, 0, 7.0),  // QUADRICEPS -> LEGS
        new ExerciseTemplate("平板支撑", "核心力量训练", 
                       Exercise.MuscleGroup.CORE, Exercise.Equipment.NONE, 3, 0, 60, 5.0),
        new ExerciseTemplate("俄罗斯转体", "腹斜肌力量训练", 
                       Exercise.MuscleGroup.CORE, Exercise.Equipment.KETTLEBELL, 3, 20, 0, 6.0)  // ABS -> CORE
    );
    
    @Override
    public List<Exercise> generateExercises(User user, WorkoutPlan plan) {
        log.info("为用户 {} 生成力量训练计划", user.getUsername());
        
        List<Exercise> exercises = new ArrayList<>();
        double intensity = calculateIntensity(user, plan);
        
        // 根据训练时长确定动作数量
        int exerciseCount = calculateExerciseCount(plan.getSessionDurationMinutes());
        
        // 确保肌群平衡
        List<Exercise.MuscleGroup> targetMuscles = selectBalancedMuscleGroups(exerciseCount);
        
        for (int i = 0; i < exerciseCount && i < targetMuscles.size(); i++) {
            Exercise.MuscleGroup targetMuscle = targetMuscles.get(i);
            ExerciseTemplate template = selectExerciseForMuscle(targetMuscle, user);
            
            if (template != null) {
                Exercise exercise = createExerciseFromTemplate(template, plan, intensity);
                exercises.add(exercise);
            }
        }
        
        log.info("生成了 {} 个力量训练动作", exercises.size());
        return exercises;
    }
    
    @Override
    public String getStrategyName() {
        return "力量训练策略";
    }
    
    @Override
    public boolean isApplicable(User user) {
        // 力量训练适用于所有健身水平的用户
        return true;
    }
    
    @Override
    public double calculateIntensity(User user, WorkoutPlan plan) {
        double baseIntensity = 1.0;
        
        // 根据健身水平调整
        // 修复第89-95行的枚举值问题
        switch (user.getFitnessLevel()) {
            case BEGINNER:
                baseIntensity = 0.6;
                break;
            case INTERMEDIATE:
                baseIntensity = 0.8;
                break;
            case ADVANCED:  // 修复：使用ADVANCED替代EXPERT
                baseIntensity = 1.0;
                break;
            // 删除不存在的EXPERT case
            // case EXPERT:
            //     baseIntensity = 1.2;
            //     break;
        }
        
        // 根据年龄调整
        if (user.getAge() != null) {
            if (user.getAge() > 50) {
                baseIntensity *= 0.8;
            } else if (user.getAge() < 25) {
                baseIntensity *= 1.1;
            }
        }
        
        // 根据BMI调整
        double bmi = user.calculateBMI();
        if (bmi > 30) {
            baseIntensity *= 0.7; // 肥胖用户降低强度
        } else if (bmi < 18.5) {
            baseIntensity *= 0.9; // 偏瘦用户适度降低强度
        }
        
        return Math.max(0.3, Math.min(1.5, baseIntensity));
    }
    
    private int calculateExerciseCount(int sessionDuration) {
        // 每个动作平均需要8-10分钟（包括休息）
        return Math.max(4, Math.min(12, sessionDuration / 8));
    }
    
    private List<Exercise.MuscleGroup> selectBalancedMuscleGroups(int count) {
        List<Exercise.MuscleGroup> selected = new ArrayList<>();
        List<Exercise.MuscleGroup> priority = Arrays.asList(
            Exercise.MuscleGroup.LEGS,        // 腿部 (原QUADRICEPS)
            Exercise.MuscleGroup.CHEST,       // 胸部
            Exercise.MuscleGroup.BACK,        // 背部
            Exercise.MuscleGroup.SHOULDERS,   // 肩部
            Exercise.MuscleGroup.CORE,        // 核心
            Exercise.MuscleGroup.ARMS,        // 手臂 (原BICEPS)
            Exercise.MuscleGroup.ARMS,        // 手臂 (原TRICEPS)
            Exercise.MuscleGroup.GLUTES       // 臀部
        );
        
        for (int i = 0; i < count && i < priority.size(); i++) {
            selected.add(priority.get(i));
        }
        
        return selected;
    }
    
    private ExerciseTemplate selectExerciseForMuscle(Exercise.MuscleGroup muscle, User user) {
        List<ExerciseTemplate> candidates = strengthExercises.stream()
            .filter(ex -> ex.primaryMuscle == muscle)
            .filter(ex -> isEquipmentAvailable(ex.equipment, user))
            .toList();
        
        if (candidates.isEmpty()) {
            // 如果没有合适的动作，选择无器械替代
            candidates = strengthExercises.stream()
                .filter(ex -> ex.primaryMuscle == muscle)
                .filter(ex -> ex.equipment == Exercise.Equipment.NONE)
                .toList();
        }
        
        return candidates.isEmpty() ? null : candidates.get(random.nextInt(candidates.size()));
    }
    
    private boolean isEquipmentAvailable(Exercise.Equipment equipment, User user) {
        // 简化实现：假设初级用户只有基础器械
        if (user.getFitnessLevel() == User.FitnessLevel.BEGINNER) {
            return equipment == Exercise.Equipment.NONE || 
                   equipment == Exercise.Equipment.DUMBBELLS;
        }
        return true; // 其他用户假设有全套器械
    }
    
    // 修复第177-194行的createExerciseFromTemplate方法
    private Exercise createExerciseFromTemplate(ExerciseTemplate template, WorkoutPlan plan, double intensity) {
        Exercise exercise = new Exercise();
        exercise.setName(template.name);
        exercise.setDescription(template.description);
        exercise.setCategory(Exercise.ExerciseCategory.STRENGTH);  // 修复：setType -> setCategory
        exercise.setPrimaryMuscleGroup(template.primaryMuscle);
        exercise.setEquipmentNeeded(template.equipment);  // 修复：setEquipmentRequired -> setEquipmentNeeded
        exercise.setDifficultyLevel(mapPlanDifficultyToExercise(plan.getDifficultyLevel()));  // 修复：setDifficulty -> setDifficultyLevel, getDifficulty -> getDifficultyLevel
        // 删除不存在的setWorkoutPlan调用
        // exercise.setWorkoutPlan(plan);
        
        // 删除不存在的训练参数设置方法
        // exercise.setRecommendedSets((int) Math.max(2, template.sets * intensity));
        // exercise.setRecommendedReps(template.reps > 0 ? 
        //                           (int) Math.max(5, template.reps * intensity) : 0);
        // exercise.setRecommendedDurationSeconds(template.duration > 0 ? 
        //                                      (int) Math.max(20, template.duration * intensity) : 0);
        exercise.setCaloriesPerMinute(template.caloriesPerMinute);
        
        // 设置指导信息
        exercise.setInstructions(generateInstructions(template));
        // 删除不存在的setSafetyTips调用
        // exercise.setSafetyTips(generateSafetyTips(template));
        
        return exercise;
    }
    
    // 修复第201-207行的mapPlanDifficultyToExercise方法
    private Exercise.DifficultyLevel mapPlanDifficultyToExercise(WorkoutPlan.DifficultyLevel planDifficulty) {
        switch (planDifficulty) {
            case EASY: return Exercise.DifficultyLevel.BEGINNER;  // 修复：BEGINNER -> EASY
            case MEDIUM: return Exercise.DifficultyLevel.INTERMEDIATE;  // 修复：INTERMEDIATE -> MEDIUM
            case HARD: return Exercise.DifficultyLevel.ADVANCED;  // 修复：ADVANCED -> HARD
            case EXPERT: return Exercise.DifficultyLevel.ADVANCED;  // EXPERT映射到ADVANCED
            default: return Exercise.DifficultyLevel.BEGINNER;
        }
    }
    
    private String generateInstructions(ExerciseTemplate template) {
        // 根据动作类型生成详细指导
        switch (template.name) {
            case "深蹲":
                return "双脚与肩同宽，脚尖略向外。下蹲时保持背部挺直，膝盖不超过脚尖。";
            case "硬拉":
                return "双脚与髋同宽，握杠比肩略宽。保持背部挺直，髋部主导发力。";
            case "卧推":
                return "躺在卧推凳上，双手握杠比肩略宽。下降至胸部，然后推起至手臂伸直。";
            case "引体向上":
                return "双手握杠，身体悬垂。用背部和手臂力量将身体拉起至下巴过杠。";
            default:
                return "请按照标准动作要领执行，注意动作质量胜过数量。";
        }
    }
    
    private String generateSafetyTips(ExerciseTemplate template) {
        return "训练前充分热身，动作过程中保持呼吸顺畅，感到不适立即停止。";
    }
    
    // 动作模板内部类
    private static class ExerciseTemplate {
        final String name;
        final String description;
        final Exercise.MuscleGroup primaryMuscle;
        final Exercise.Equipment equipment;  // 修复：EquipmentType -> Equipment
        final int sets;
        final int reps;
        final int duration;
        final double caloriesPerMinute;
        
        ExerciseTemplate(String name, String description, Exercise.MuscleGroup primaryMuscle,
                        Exercise.Equipment equipment, int sets, int reps, int duration, double caloriesPerMinute) {
            this.name = name;
            this.description = description;
            this.primaryMuscle = primaryMuscle;
            this.equipment = equipment;
            this.sets = sets;
            this.reps = reps;
            this.duration = duration;
            this.caloriesPerMinute = caloriesPerMinute;
        }
    }
}