package com.fitai.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @NotBlank(message = "计划名称不能为空")
    @Size(max = 200, message = "计划名称长度不能超过200个字符")
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type")
    private PlanType planType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    private DifficultyLevel difficultyLevel;
    
    @Column(name = "duration_weeks")
    @Min(value = 1, message = "计划持续时间至少1周")
    @Max(value = 52, message = "计划持续时间不能超过52周")
    private Integer durationWeeks;
    
    @Column(name = "sessions_per_week")
    @Min(value = 1, message = "每周训练次数至少1次")
    @Max(value = 7, message = "每周训练次数不能超过7次")
    private Integer sessionsPerWeek;
    
    @Column(name = "estimated_duration_minutes")
    @Min(value = 15, message = "预估时长至少15分钟")
    @Max(value = 180, message = "预估时长不能超过180分钟")
    private Integer estimatedDurationMinutes;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "target_goal")
    private User.FitnessGoal targetGoal;
    
    @Column(name = "is_ai_generated")
    private Boolean isAiGenerated = false;
    
    @Column(name = "ai_model_version")
    private String aiModelVersion;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkoutExercise> exercises = new ArrayList<>();
    
    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkoutSession> sessions = new ArrayList<>();
    
    // 枚举定义
    public enum DifficultyLevel {
        EASY("简单"), MEDIUM("中等"), HARD("困难"), EXPERT("专家级");
        
        private final String displayName;
        
        DifficultyLevel(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum PlanType {
        STRENGTH("力量训练"),
        CARDIO("有氧训练"), 
        HIIT("高强度间歇训练"),
        YOGA("瑜伽"),
        MIXED("综合训练"),
        WEIGHT_LOSS("减重训练"),
        MUSCLE_GAIN("增肌训练");
        
        private final String displayName;
        
        PlanType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // 生命周期回调
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
        // 业务方法
    public int getSessionDurationMinutes() {
        return estimatedDurationMinutes != null ? estimatedDurationMinutes : 0;
    }
    
    public int getTotalExercises() {
        return exercises.size();
    }
    
    public int getCompletedSessions() {
        return (int) sessions.stream()
                .filter(session -> session.getStatus() == WorkoutSession.SessionStatus.COMPLETED)
                .count();
    }
    
    public double getCompletionRate() {
        if (sessions.isEmpty()) return 0.0;
        return (double) getCompletedSessions() / sessions.size() * 100;
    }
}