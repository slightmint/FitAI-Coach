package com.fitai.repository;

import com.fitai.model.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    
    /**
     * 根据用户ID查找训练计划
     */
    List<WorkoutPlan> findByUserId(Long userId);
    
    /**
     * 根据用户ID查找训练计划，按创建时间倒序
     */
    List<WorkoutPlan> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 查找用户当前活跃的训练计划
     */
    Optional<WorkoutPlan> findByUserIdAndIsActiveTrue(Long userId);
    
    /**
     * 根据计划类型查找训练计划
     */
    List<WorkoutPlan> findByPlanType(WorkoutPlan.PlanType planType);
    
    /**
     * 根据难度查找训练计划
     */
    List<WorkoutPlan> findByDifficultyLevel(WorkoutPlan.DifficultyLevel difficultyLevel);
    
    /**
     * 根据难度和计划类型查找训练计划
     */
    List<WorkoutPlan> findByDifficultyLevelAndPlanTypeOrderByCreatedAtDesc(WorkoutPlan.DifficultyLevel difficultyLevel, WorkoutPlan.PlanType planType);
    
    /**
     * 查找热门训练计划（按创建时间倒序，取前10个）
     */
    @Query("SELECT wp FROM WorkoutPlan wp ORDER BY wp.createdAt DESC")
    List<WorkoutPlan> findTop10ByOrderByCreatedAtDesc();
    
    /**
     * 根据持续时间范围查找训练计划
     */
    List<WorkoutPlan> findByEstimatedDurationMinutesBetween(int minDuration, int maxDuration);
    
    /**
     * 查找活跃的训练计划
     */
    List<WorkoutPlan> findByIsActiveTrue();
}