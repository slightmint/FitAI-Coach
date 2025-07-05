package com.fitai.service.strategy.impl;

import com.fitai.model.*;
import com.fitai.repository.ExerciseRepository;
import com.fitai.service.strategy.WorkoutPlanStrategy;
import com.fitai.service.strategy.WorkoutPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 减重训练策略实现
 */
@Component
public class WeightLossStrategy implements WorkoutPlanStrategy {
    
    @Autowired
    private ExerciseRepository exerciseRepository;
    
    @Override
    public WorkoutPlan generatePlan(User user, WorkoutPreferences preferences) {
        WorkoutPlan plan = new WorkoutPlan();
        plan.setName("AI定制减重计划 - " + user.getUsername());
        plan.setDescription("基于您的身体状况和目标定制的减重训练计划，注重有氧运动和全身训练。");
        plan.setUser(user);
        plan.setTargetGoal(User.FitnessGoal.WEIGHT_LOSS);
        plan.setDifficultyLevel(mapUserLevelToPlanLevel(user.getFitnessLevel()));
        plan.setDurationWeeks(preferences.getDurationWeeks() != null ? preferences.getDurationWeeks() : 8);
        plan.setSessionsPerWeek(4); // 减重建议每周4次训练
        plan.setEstimatedDurationMinutes(45); // 每次45分钟
        plan.setIsAiGenerated(true);
        plan.setAiModelVersion("WeightLoss-v1.0");
        
        // 获取适合减重的运动
        List<Exercise> cardioExercises = exerciseRepository.findByCategory(Exercise.ExerciseCategory.CARDIO);
        List<Exercise> strengthExercises = exerciseRepository.findByCategory(Exercise.ExerciseCategory.STRENGTH);
        
        // 创建训练动作组合 (70%有氧 + 30%力量)
        addCardioExercises(plan, cardioExercises, user);
        addStrengthExercises(plan, strengthExercises, user);
        
        return plan;
    }
    
    @Override
    public StrategyType getStrategyType() {
        return StrategyType.WEIGHT_LOSS_STRATEGY;
    }
    
    @Override
    public boolean isApplicable(User user) {
        return user.getFitnessGoal() == User.FitnessGoal.WEIGHT_LOSS ||
               (user.calculateBMI() > 24 && user.getFitnessGoal() == User.FitnessGoal.GENERAL_FITNESS);
    }
    
    private WorkoutPlan.DifficultyLevel mapUserLevelToPlanLevel(User.FitnessLevel userLevel) {
        switch (userLevel) {
            case BEGINNER:
                return WorkoutPlan.DifficultyLevel.EASY;
            case INTERMEDIATE:
                return WorkoutPlan.DifficultyLevel.MEDIUM;
            case ADVANCED:
                return WorkoutPlan.DifficultyLevel.HARD;
            default:
                return WorkoutPlan.DifficultyLevel.EASY;
        }
    }
    
    private void addCardioExercises(WorkoutPlan plan, List<Exercise> cardioExercises, User user) {
        // 根据用户水平选择合适的有氧运动
        List<Exercise> suitableCardio = cardioExercises.stream()
                .filter(ex -> ex.getDifficultyLevel().ordinal() <= user.getFitnessLevel().ordinal() + 1)
                .limit(4)
                .collect(Collectors.toList());
        
        int order = 1;
        for (Exercise exercise : suitableCardio) {
            WorkoutExercise workoutExercise = new WorkoutExercise();
            workoutExercise.setWorkoutPlan(plan);
            workoutExercise.setExercise(exercise);
            workoutExercise.setOrder(order++);
            workoutExercise.setSets(1);
            workoutExercise.setDurationSeconds(user.getFitnessLevel() == User.FitnessLevel.BEGINNER ? 300 : 600); // 5-10分钟
            workoutExercise.setRestSeconds(60);
            workoutExercise.setNotes("保持中等强度，能够正常对话的节奏");
            
            plan.getExercises().add(workoutExercise);
        }
    }
    
    private void addStrengthExercises(WorkoutPlan plan, List<Exercise> strengthExercises, User user) {
        // 选择全身性力量训练动作
        List<Exercise> suitableStrength = strengthExercises.stream()
                .filter(ex -> ex.getPrimaryMuscleGroup() == Exercise.MuscleGroup.FULL_BODY ||
                             ex.getPrimaryMuscleGroup() == Exercise.MuscleGroup.CORE)
                .filter(ex -> ex.getDifficultyLevel().ordinal() <= user.getFitnessLevel().ordinal() + 1)
                .limit(3)
                .collect(Collectors.toList());
        
        int order = plan.getExercises().size() + 1;
        for (Exercise exercise : suitableStrength) {
            WorkoutExercise workoutExercise = new WorkoutExercise();
            workoutExercise.setWorkoutPlan(plan);
            workoutExercise.setExercise(exercise);
            workoutExercise.setOrder(order++);
            workoutExercise.setSets(user.getFitnessLevel() == User.FitnessLevel.BEGINNER ? 2 : 3);
            workoutExercise.setReps(user.getFitnessLevel() == User.FitnessLevel.BEGINNER ? 12 : 15);
            workoutExercise.setRestSeconds(45);
            workoutExercise.setNotes("注重动作质量，控制节奏");
            
            plan.getExercises().add(workoutExercise);
        }
    }
}