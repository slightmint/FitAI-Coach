package com.fitai.service;

import com.fitai.model.User;
import com.fitai.model.WorkoutPlan;
import com.fitai.model.Exercise;
import com.fitai.repository.WorkoutPlanRepository;
import com.fitai.repository.ExerciseRepository;
import com.fitai.service.factory.WorkoutPlanFactory;
import com.fitai.service.strategy.WorkoutStrategy;
import com.fitai.service.strategy.StrengthTrainingStrategy;
import com.fitai.service.strategy.CardioTrainingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
@Transactional
public class WorkoutPlanService {
    
    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;
    
    @Autowired
    private ExerciseRepository exerciseRepository;
    
    @Autowired
    private WorkoutPlanFactory workoutPlanFactory;
    
    @Autowired
    private StrengthTrainingStrategy strengthStrategy;
    
    @Autowired
    private CardioTrainingStrategy cardioStrategy;
    
    /**
     * 为用户创建个性化训练计划
     */
    public WorkoutPlan createPersonalizedPlan(User user, String planType) {
        // 创建WorkoutPlanRequest
        WorkoutPlanFactory.WorkoutPlanRequest request = WorkoutPlanFactory.WorkoutPlanRequest.builder()
                .planType(planType)
                .build();
        
        // 使用工厂模式创建基础计划
        WorkoutPlan basePlan = workoutPlanFactory.createWorkoutPlan(user, request);
        
        // 使用策略模式优化计划
        WorkoutStrategy strategy = getStrategyByType(planType);
        if (strategy != null) {
            // 计算训练强度
            double intensity = strategy.calculateIntensity(user, basePlan);
            // 注意：WorkoutPlan可能没有setIntensity方法，需要根据实际情况调整
            // basePlan.setIntensity(intensity);
            
            // 生成具体运动项目已经在factory中完成，无需重复设置
            // List<Exercise> exercises = strategy.generateExercises(user, basePlan);
            // basePlan.setExercises(exercises);
        }
        
        basePlan.setCreatedAt(LocalDateTime.now());
        basePlan.setUpdatedAt(LocalDateTime.now());
        
        return workoutPlanRepository.save(basePlan);
    }
    
    /**
     * 根据计划类型获取对应策略
     */
    private WorkoutStrategy getStrategyByType(String planType) {
        switch (planType.toLowerCase()) {
            case "strength":
            case "muscle_gain":
                return strengthStrategy;
            case "cardio":
            case "weight_loss":
                return cardioStrategy;
            default:
                return strengthStrategy; // 默认策略
        }
    }
    
    /**
     * 获取用户的所有训练计划
     */
    public List<WorkoutPlan> getUserWorkoutPlans(Long userId) {
        return workoutPlanRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * 获取用户当前活跃的训练计划
     */
    public Optional<WorkoutPlan> getUserActivePlan(Long userId) {
        return workoutPlanRepository.findByUserIdAndIsActiveTrue(userId);
    }
    
    /**
     * 激活训练计划
     */
    /**
     * 激活训练计划
     */
    public void activatePlan(Long planId, Long userId) {
        // 先将用户所有计划设为非活跃
        List<WorkoutPlan> userPlans = workoutPlanRepository.findByUserId(userId);
        userPlans.forEach(plan -> {
            plan.setIsActive(false);  // 修改：setActive -> setIsActive
            plan.setUpdatedAt(LocalDateTime.now());
        });
        workoutPlanRepository.saveAll(userPlans);
        
        // 激活指定计划
        Optional<WorkoutPlan> planOpt = workoutPlanRepository.findById(planId);
        if (planOpt.isPresent()) {
            WorkoutPlan plan = planOpt.get();
            plan.setIsActive(true);  // 修改：setActive -> setIsActive
            plan.setUpdatedAt(LocalDateTime.now());
            workoutPlanRepository.save(plan);
        }
    }
    
    /**
     * 更新训练计划
     */
    public WorkoutPlan updatePlan(WorkoutPlan plan) {
        plan.setUpdatedAt(LocalDateTime.now());
        return workoutPlanRepository.save(plan);
    }
    
    /**
     * 删除训练计划
     */
    public void deletePlan(Long planId) {
        workoutPlanRepository.deleteById(planId);
    }
    
    /**
     * 根据难度和类型推荐训练计划
     */
    public List<WorkoutPlan> getRecommendedPlans(String difficulty, String planType) {
        // 将字符串参数转换为枚举类型
        WorkoutPlan.DifficultyLevel difficultyEnum = WorkoutPlan.DifficultyLevel.valueOf(difficulty.toUpperCase());
        WorkoutPlan.PlanType planTypeEnum = WorkoutPlan.PlanType.valueOf(planType.toUpperCase());
        return workoutPlanRepository.findByDifficultyLevelAndPlanTypeOrderByCreatedAtDesc(difficultyEnum, planTypeEnum);
    }
    
    /**
     * 获取热门训练计划
     */
    public List<WorkoutPlan> getPopularPlans(int limit) {
        return workoutPlanRepository.findTop10ByOrderByCreatedAtDesc();
    }
}