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
 * 瑜伽训练策略
 * 适合柔韧性提升和身心放松
 */
@Component
@Slf4j
public class YogaTrainingStrategy implements WorkoutStrategy {
    
    @Override
    public List<Exercise> generateExercises(User user, WorkoutPlan plan) {
        log.info("为用户 {} 生成瑜伽训练计划", user.getUsername());
        
        List<Exercise> exercises = new ArrayList<>();
        
        // 根据用户水平选择瑜伽序列
        switch (user.getFitnessLevel()) {
            case BEGINNER:
                exercises.addAll(generateBeginnerYogaSequence());
                break;
            case INTERMEDIATE:
                exercises.addAll(generateIntermediateYogaSequence());
                break;
            case ADVANCED:  // 将 EXPERT 改为 ADVANCED
                exercises.addAll(generateAdvancedYogaSequence());
                break;
            default:
                exercises.addAll(generateBeginnerYogaSequence());
        }
        
        log.info("瑜伽训练计划生成完成，共 {} 个体式", exercises.size());
        return exercises;
    }
    
    private List<Exercise> generateBeginnerYogaSequence() {
        return Arrays.asList(
            createYogaPose("山式", "基础站立姿势，建立身体觉知", 2),
            createYogaPose("前屈式", "温和的前弯，拉伸后背", 3),
            createYogaPose("下犬式", "经典倒V字型，全身拉伸", 3),
            createYogaPose("婴儿式", "跪坐休息姿势", 2),
            createYogaPose("猫牛式", "脊柱灵活性练习", 2),
            createYogaPose("战士一式", "力量与平衡的结合", 2),
            createYogaPose("三角式", "侧弯拉伸体式", 2),
            createYogaPose("树式", "单腿平衡练习", 1),
            createYogaPose("仰卧放松式", "最终放松", 5)
        );
    }
    
    private List<Exercise> generateIntermediateYogaSequence() {
        return Arrays.asList(
            createYogaPose("太阳致敬式A", "流动序列热身", 5),
            createYogaPose("战士二式", "强化腿部力量", 2),
            createYogaPose("侧角式", "深度侧弯拉伸", 2),
            createYogaPose("反向战士式", "开胸后弯", 2),
            createYogaPose("鹰式", "平衡与专注", 1),
            createYogaPose("扭转三角式", "脊柱扭转", 2),
            createYogaPose("骆驼式", "后弯开胸", 1),
            createYogaPose("坐立前屈", "深度前弯", 3),
            createYogaPose("仰卧放松式", "深度放松", 8)
        );
    }
    
    private List<Exercise> generateAdvancedYogaSequence() {
        return Arrays.asList(
            createYogaPose("太阳致敬式B", "完整流动序列", 8),
            createYogaPose("战士三式", "高级平衡体式", 1),
            createYogaPose("舞王式", "后弯平衡挑战", 1),
            createYogaPose("乌鸦式", "手臂平衡", 1),
            createYogaPose("头倒立", "倒立之王", 3),
            createYogaPose("轮式", "深度后弯", 1),
            createYogaPose("莲花坐扭转", "深度脊柱扭转", 2),
            createYogaPose("双腿背部伸展式", "深度前弯", 3),
            createYogaPose("仰卧放松式", "完全放松", 10)
        );
    }
    
    private Exercise createYogaPose(String name, String description, int holdMinutes) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setDescription(description);
        exercise.setCategory(Exercise.ExerciseCategory.FLEXIBILITY);
        exercise.setDifficultyLevel(Exercise.DifficultyLevel.BEGINNER);
        // 修改第94行
        exercise.setDifficultyLevel(Exercise.DifficultyLevel.BEGINNER);
        // 删除第95-96行
        // exercise.setDuration(holdMinutes);  // 删除这行
        // exercise.setSets(1);                // 删除这行
        
        // 如果需要设置duration和sets，应该在创建WorkoutExercise时设置
        exercise.setCaloriesPerMinute(2.5); // 瑜伽相对低消耗
        exercise.setInstructions("保持深长呼吸，专注当下感受");
        return exercise;
    }
    
    @Override
    public String getStrategyName() {
        return "瑜伽训练";
    }
    
    @Override
    public boolean isApplicable(User user) {
        // 瑜伽适合所有水平的用户
        return true;
    }
    
    @Override
    public double calculateIntensity(User user, WorkoutPlan plan) {
        // 瑜伽强度相对较低
        double baseIntensity = 0.6;
        
        switch (user.getFitnessLevel()) {
            case BEGINNER:
                baseIntensity = 0.4;
                break;
            case INTERMEDIATE:
                baseIntensity = 0.6;
                break;
            case ADVANCED:
                baseIntensity = 0.8;
                break;
        }
        
        return Math.max(0.1, Math.min(2.0, baseIntensity));
    }
}