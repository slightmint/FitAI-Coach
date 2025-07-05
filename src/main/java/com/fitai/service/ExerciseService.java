package com.fitai.service;

import com.fitai.model.Exercise;
import com.fitai.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExerciseService {
    
    @Autowired
    private ExerciseRepository exerciseRepository;
    
    /**
     * 创建新的运动项目
     */
    public Exercise createExercise(Exercise exercise) {
        exercise.setCreatedAt(LocalDateTime.now());
        exercise.setUpdatedAt(LocalDateTime.now());
        return exerciseRepository.save(exercise);
    }
    
    /**
     * 根据分类查找运动（替代原来的肌肉群查找）
     */
    public List<Exercise> findByCategory(Exercise.ExerciseCategory category) {
        return exerciseRepository.findByCategory(category);
    }
    
    /**
     * 根据器械要求查找运动
     */
    public List<Exercise> findByEquipment(String equipment) {
        // 需要将字符串转换为枚举类型
        Exercise.Equipment equipmentEnum = Exercise.Equipment.valueOf(equipment.toUpperCase());
        return exerciseRepository.findByEquipmentNeeded(equipmentEnum);
    }
    
    /**
     * 根据难度级别查找运动
     */
    public List<Exercise> findByDifficulty(Exercise.DifficultyLevel difficulty) {
        return exerciseRepository.findByDifficultyLevel(difficulty);
    }
    
    /**
     * 获取所有运动项目
     */
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }
    
    /**
     * 根据ID查找运动
     */
    public Optional<Exercise> findById(Long id) {
        return exerciseRepository.findById(id);
    }
    
    /**
     * 更新运动项目
     */
    public Exercise updateExercise(Exercise exercise) {
        exercise.setUpdatedAt(LocalDateTime.now());
        return exerciseRepository.save(exercise);
    }
    
    /**
     * 删除运动项目
     */
    public void deleteExercise(Long id) {
        exerciseRepository.deleteById(id);
    }
    
    /**
     * 搜索运动项目
     */
    public List<Exercise> searchExercises(String keyword) {
        return exerciseRepository.findByNameContainingIgnoreCase(keyword);
    }
    
    /**
     * 根据卡路里消耗范围查找运动
     */
    public List<Exercise> findByCalorieRange(double minCalories, double maxCalories) {
        return exerciseRepository.findByCaloriesPerMinuteBetween(minCalories, maxCalories);
    }
    
    /**
     * 获取推荐的运动组合
     */
    public List<Exercise> getRecommendedExercises(String goal, String fitnessLevel) {
        // 根据目标和健身水平推荐运动
        if ("weight_loss".equals(goal)) {
            return exerciseRepository.findByCategoryOrderByCaloriesPerMinuteDesc(Exercise.ExerciseCategory.CARDIO);
        } else if ("muscle_gain".equals(goal)) {
            return exerciseRepository.findByCategoryOrderByCaloriesPerMinuteDesc(Exercise.ExerciseCategory.STRENGTH);
        } else {
            return exerciseRepository.findTop10ByOrderByCaloriesPerMinuteDesc();
        }
    }
    
    /**
     * 创建或获取已存在的运动项目
     */
    public Exercise createOrGetExercise(Exercise exercise) {
        // 使用synchronized确保线程安全
        synchronized (this) {
            // 先尝试根据名称查找是否已存在
            Optional<Exercise> existingExercise = exerciseRepository.findByName(exercise.getName());
            
            if (existingExercise.isPresent()) {
                // 如果已存在，返回现有的运动项目
                return existingExercise.get();
            } else {
                try {
                    // 如果不存在，创建新的运动项目
                    exercise.setCreatedAt(LocalDateTime.now());
                    exercise.setUpdatedAt(LocalDateTime.now());
                    return exerciseRepository.save(exercise);
                } catch (DataIntegrityViolationException e) {
                    // 如果保存时发生唯一约束冲突，再次查询并返回已存在的记录
                    Optional<Exercise> retryExercise = exerciseRepository.findByName(exercise.getName());
                    if (retryExercise.isPresent()) {
                        return retryExercise.get();
                    }
                    throw e; // 如果仍然找不到，重新抛出异常
                }
            }
        }
    }
    
    /**
     * 根据名称查找运动项目
     */
    public Optional<Exercise> findByName(String name) {
        return exerciseRepository.findByName(name);
    }
    
    /**
     * 根据主要肌肉群查找运动
     */
    public List<Exercise> findByPrimaryMuscleGroup(Exercise.MuscleGroup muscleGroup) {
        return exerciseRepository.findByPrimaryMuscleGroup(muscleGroup);
    }
    
    /**
     * 根据肌群显示名称查找运动
     */
    public List<Exercise> findByMuscleGroupDisplayName(String displayName) {
        for (Exercise.MuscleGroup muscleGroup : Exercise.MuscleGroup.values()) {
            if (muscleGroup.getDisplayName().equals(displayName)) {
                return exerciseRepository.findByPrimaryMuscleGroup(muscleGroup);
            }
        }
        return getAllExercises(); // 如果找不到匹配的肌群，返回所有运动
    }
}