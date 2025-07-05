package com.fitai.model.exercise;

import com.fitai.model.WorkoutSession;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "workout_exercise_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExerciseRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 添加这个字段来建立与WorkoutSession的关联
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_session_id")
    private WorkoutSession workoutSession;
    
    @Column(name = "exercise_name", nullable = false)
    @NotBlank(message = "运动名称不能为空")
    private String exerciseName;
    
    @Column(name = "sets_completed")
    @Min(value = 0, message = "完成组数不能为负数")
    private Integer setsCompleted;
    
    @Column(name = "reps_completed")
    @Min(value = 0, message = "完成次数不能为负数")
    private Integer repsCompleted;
    
    @Column(name = "weight_used")
    @DecimalMin(value = "0.0", message = "使用重量不能为负数")
    private Double weightUsed;
    
    @Column(name = "duration_minutes")
    @Min(value = 0, message = "持续时间不能为负数")
    private Integer durationMinutes;
    
    @Column(name = "calories_burned")
    @Min(value = 0, message = "消耗卡路里不能为负数")
    private Integer caloriesBurned;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 辅助方法
    public boolean isCardioExercise() {
        return durationMinutes != null && durationMinutes > 0;
    }
    
    public boolean isStrengthExercise() {
        return setsCompleted != null && repsCompleted != null;
    }
    
    public double getVolume() {
        if (isStrengthExercise() && weightUsed != null) {
            return setsCompleted * repsCompleted * weightUsed;
        }
        return 0.0;
    }
}