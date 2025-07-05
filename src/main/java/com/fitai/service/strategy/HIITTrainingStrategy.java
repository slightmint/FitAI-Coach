package com.fitai.service.strategy;

import com.fitai.model.Exercise;
import com.fitai.model.User;
import com.fitai.model.WorkoutPlan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * HIIT训练策略 - 高强度间歇训练
 * 适合减脂和提高心肺功能
 */
@Component
@Slf4j
public class HIITTrainingStrategy implements WorkoutStrategy {
    
    @Override
    public List<Exercise> generateExercises(User user, WorkoutPlan plan) {
        log.info("为用户 {} 生成HIIT训练计划", user.getUsername());
        
        List<Exercise> exercises = new ArrayList<>();
        
        // 根据用户健身水平调整训练强度
        int workInterval = getWorkInterval(user.getFitnessLevel());
        int restInterval = getRestInterval(user.getFitnessLevel());
        int rounds = getRounds(user.getFitnessLevel());
        
        // 热身运动
        exercises.addAll(generateWarmUpExercises());
        
        // HIIT主体训练
        exercises.addAll(generateHIITExercises(workInterval, restInterval, rounds));
        
        // 放松运动
        exercises.addAll(generateCoolDownExercises());
        
        log.info("HIIT训练计划生成完成，共 {} 个运动项目", exercises.size());
        return exercises;
    }
    
    @Override
    public String getStrategyName() {
        return "HIIT高强度间歇训练";
    }
    
    @Override
    public boolean isApplicable(User user) {
        // HIIT适合中级以上用户，且没有心血管疾病
        if (user.getFitnessLevel() == User.FitnessLevel.BEGINNER) {
            return false;
        }
        
        // 检查年龄限制（建议60岁以下）
        if (user.getAge() != null && user.getAge() > 60) {
            return false;
        }
        
        // 适合减脂和提高心肺功能的目标
        return user.getFitnessGoal() == User.FitnessGoal.WEIGHT_LOSS ||
               user.getFitnessGoal() == User.FitnessGoal.ENDURANCE ||
               user.getFitnessGoal() == User.FitnessGoal.GENERAL_FITNESS;
    }
    
    @Override
    public double calculateIntensity(User user, WorkoutPlan plan) {
        double baseIntensity = 1.5; // HIIT基础强度较高
        
        // 根据用户健身水平调整
        switch (user.getFitnessLevel()) {
            case INTERMEDIATE:
                baseIntensity = 1.3;
                break;
            case ADVANCED:
                baseIntensity = 1.6;
                break;
            default:
                baseIntensity = 1.2;
        }
        
        // 根据年龄调整
        if (user.getAge() != null) {
            if (user.getAge() > 50) {
                baseIntensity *= 0.8;
            } else if (user.getAge() < 25) {
                baseIntensity *= 1.1;
            }
        }
        
        // 确保强度在合理范围内
        return Math.max(0.1, Math.min(2.0, baseIntensity));
    }
    
    private int getWorkInterval(User.FitnessLevel level) {
        switch (level) {
            case BEGINNER: return 20; // 20秒工作
            case INTERMEDIATE: return 30; // 30秒工作
            case ADVANCED: return 40; // 40秒工作
            default: return 20;
        }
    }
    
    private int getRestInterval(User.FitnessLevel level) {
        switch (level) {
            case BEGINNER: return 40; // 40秒休息
            case INTERMEDIATE: return 30; // 30秒休息
            case ADVANCED: return 20; // 20秒休息
            default: return 40;
        }
    }
    
    private int getRounds(User.FitnessLevel level) {
        switch (level) {
            case BEGINNER: return 4; // 4轮
            case INTERMEDIATE: return 6; // 6轮
            case ADVANCED: return 8; // 8轮
            default: return 4;
        }
    }
    
    private List<Exercise> generateWarmUpExercises() {
        return Arrays.asList(
            createExercise("动态热身", "全身关节活动", 5, 1, "分钟"),
            createExercise("轻松跳跃", "原地轻跳准备", 30, 1, "秒")
        );
    }
    
    private List<Exercise> generateHIITExercises(int workInterval, int restInterval, int rounds) {
        List<Exercise> exercises = new ArrayList<>();
        
        String[] hiitExercises = {
            "波比跳", "高抬腿", "开合跳", "山地跑", 
            "深蹲跳", "俯卧撑", "平板支撑", "登山者"
        };
        
        for (int round = 1; round <= rounds; round++) {
            for (String exerciseName : hiitExercises) {
                // 工作间歇
                exercises.add(createExercise(
                    exerciseName, 
                    "高强度执行" + workInterval + "秒", 
                    workInterval, 1, "秒"
                ));
                
                // 休息间歇（除了最后一个动作）
                if (!exerciseName.equals(hiitExercises[hiitExercises.length - 1]) || round < rounds) {
                    exercises.add(createExercise(
                        "休息", 
                        "主动恢复" + restInterval + "秒", 
                        restInterval, 1, "秒"
                    ));
                }
            }
        }
        
        return exercises;
    }
    
    private List<Exercise> generateCoolDownExercises() {
        return Arrays.asList(
            createExercise("慢走恢复", "缓慢步行恢复心率", 3, 1, "分钟"),
            createExercise("拉伸放松", "全身肌肉拉伸", 5, 1, "分钟")
        );
    }
    
    private Exercise createExercise(String name, String description, int duration, int sets, String unit) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setDescription(description);
        exercise.setCategory(Exercise.ExerciseCategory.CARDIO);
        exercise.setDifficultyLevel(Exercise.DifficultyLevel.INTERMEDIATE);
        // 删除第175-176行，因为duration和sets属于WorkoutExercise
        // exercise.setDuration(duration);  // 删除这行
        // exercise.setSets(sets);          // 删除这行
        
        // 如果需要设置duration和sets，应该在创建WorkoutExercise时设置：
        // WorkoutExercise workoutExercise = new WorkoutExercise();
        // workoutExercise.setExercise(exercise);
        // workoutExercise.setDurationSeconds(duration * 60); // 转换为秒
        // workoutExercise.setSets(sets);
        exercise.setCaloriesPerMinute(8.0); // HIIT高消耗
        exercise.setInstructions("保持正确姿势，控制呼吸节奏");
        return exercise;
    }
}