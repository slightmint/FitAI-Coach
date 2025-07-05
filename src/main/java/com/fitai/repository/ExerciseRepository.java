package com.fitai.repository;

import com.fitai.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    
    /**
     * 根据运动名称搜索（忽略大小写）
     */
    List<Exercise> findByNameContainingIgnoreCase(String name);
    
    /**
     * 根据精确的运动名称查找
     */
    /**
     * 根据精确的运动名称查找
     */
    Optional<Exercise> findByName(String name);
    
    /**
     * 根据运动类别查找
     */
    List<Exercise> findByCategory(Exercise.ExerciseCategory category);
    
    /**
     * 根据主要肌肉群查找
     */
    List<Exercise> findByPrimaryMuscleGroup(Exercise.MuscleGroup muscleGroup);
    
    /**
     * 根据器械要求查找
     */
    List<Exercise> findByEquipmentNeeded(Exercise.Equipment equipment);
    
    /**
     * 根据难度查找
     */
    List<Exercise> findByDifficultyLevel(Exercise.DifficultyLevel difficulty);
    
    /**
     * 根据卡路里消耗范围查找
     */
    List<Exercise> findByCaloriesPerMinuteBetween(double minCalories, double maxCalories);
    
    /**
     * 根据运动类别查找，按卡路里消耗倒序
     */
    List<Exercise> findByCategoryOrderByCaloriesPerMinuteDesc(Exercise.ExerciseCategory category);
    
    /**
     * 查找卡路里消耗最高的前10个运动
     */
    List<Exercise> findTop10ByOrderByCaloriesPerMinuteDesc();
}