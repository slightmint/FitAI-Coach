package com.fitai.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "workout_exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExercise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "exercise_order")
    @Min(value = 1, message = "动作顺序必须大于0")
    private Integer order;
    
    @Column(name = "sets")
    @Min(value = 1, message = "组数至少为1")
    @Max(value = 20, message = "组数不能超过20")
    private Integer sets;
    
    @Column(name = "reps")
    @Min(value = 1, message = "次数至少为1")
    private Integer reps;
    
    @Column(name = "duration_seconds")
    @Min(value = 1, message = "持续时间至少为1秒")
    private Integer durationSeconds;
    
    @Column(name = "rest_seconds")
    @Min(value = 0, message = "休息时间不能为负数")
    private Integer restSeconds;
    
    @Column(name = "weight_kg")
    @DecimalMin(value = "0.0", message = "重量不能为负数")
    private Double weight;
    
    @Column(name = "distance_meters")
    @DecimalMin(value = "0.0", message = "距离不能为负数")
    private Double distance;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlan workoutPlan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;
    
    // 业务方法
    public String getDisplayText() {
        StringBuilder sb = new StringBuilder();
        sb.append(exercise.getName());
        
        if (sets != null && reps != null) {
            sb.append(" - ").append(sets).append("组 x ").append(reps).append("次");
        } else if (sets != null && durationSeconds != null) {
            sb.append(" - ").append(sets).append("组 x ").append(durationSeconds).append("秒");
        } else if (durationSeconds != null) {
            sb.append(" - ").append(durationSeconds).append("秒");
        }
        
        if (weight != null && weight > 0) {
            sb.append(" (").append(weight).append("kg)");
        }
        
        return sb.toString();
    }
    
    public double calculateEstimatedCalories() {
        if (exercise.getCaloriesPerMinute() == null) return 0.0;
        
        double totalMinutes = 0.0;
        if (durationSeconds != null) {
            totalMinutes = durationSeconds / 60.0;
        } else if (reps != null) {
            // 估算每次动作2秒
            totalMinutes = (reps * 2.0) / 60.0;
        }
        
        if (sets != null) {
            totalMinutes *= sets;
        }
        
        return exercise.getCaloriesPerMinute() * totalMinutes;
    }
}