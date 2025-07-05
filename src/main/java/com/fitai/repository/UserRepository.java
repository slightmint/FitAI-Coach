package com.fitai.repository;

import com.fitai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据健身目标查找用户
     */
    List<User> findByFitnessGoal(User.FitnessGoal fitnessGoal);
    
    /**
     * 根据健身水平查找用户
     */
    List<User> findByFitnessLevel(User.FitnessLevel fitnessLevel);
    
    /**
     * 查找活跃用户（最近30天有登录）
     */
    @Query("SELECT u FROM User u WHERE u.updatedAt >= CURRENT_DATE - 30 DAY")
    List<User> findActiveUsers();
    
    /**
     * 根据年龄范围查找用户
     */
    List<User> findByAgeBetween(int minAge, int maxAge);
    
    /**
     * 根据BMI范围查找用户
     */
    @Query("SELECT u FROM User u WHERE (u.weight / ((u.height/100) * (u.height/100))) BETWEEN :minBmi AND :maxBmi")
    List<User> findByBmiRange(@Param("minBmi") double minBmi, @Param("maxBmi") double maxBmi);
    
    /**
     * 查找训练天数超过指定值的用户
     */
    List<User> findByWorkoutDaysGreaterThan(int workoutDays);
}