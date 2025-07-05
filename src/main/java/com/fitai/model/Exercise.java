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
@Table(name = "exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    @NotBlank(message = "动作名称不能为空")
    @Size(max = 200, message = "动作名称长度不能超过200个字符")
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String instructions;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private ExerciseCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "muscle_group")
    private MuscleGroup primaryMuscleGroup;
    
    @ElementCollection(targetClass = MuscleGroup.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "exercise_secondary_muscles", 
                    joinColumns = @JoinColumn(name = "exercise_id"))
    @Column(name = "muscle_group")
    private List<MuscleGroup> secondaryMuscleGroups = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_needed")
    private Equipment equipmentNeeded;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    private DifficultyLevel difficultyLevel;
    
    @Column(name = "calories_per_minute")
    @DecimalMin(value = "0.0", message = "卡路里消耗不能为负数")
    private Double caloriesPerMinute;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "video_url")
    private String videoUrl;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // 关联关系
    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkoutExercise> workoutExercises = new ArrayList<>();
    
    // 枚举定义
    public enum ExerciseCategory {
        CARDIO("有氧运动"), STRENGTH("力量训练"), FLEXIBILITY("柔韧性"), 
        BALANCE("平衡性"), SPORTS("运动技能"), REHABILITATION("康复训练");
        
        private final String displayName;
        
        ExerciseCategory(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum MuscleGroup {
        CHEST("胸部"), BACK("背部"), SHOULDERS("肩部"), ARMS("手臂"), 
        CORE("核心"), LEGS("腿部"), GLUTES("臀部"), FULL_BODY("全身");
        
        private final String displayName;
        
        MuscleGroup(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum Equipment {
        NONE("无器械"), DUMBBELLS("哑铃"), BARBELL("杠铃"), 
        RESISTANCE_BANDS("弹力带"), KETTLEBELL("壶铃"), 
        PULL_UP_BAR("引体向上杆"), YOGA_MAT("瑜伽垫"), 
        TREADMILL("跑步机"), STATIONARY_BIKE("动感单车");
        
        private final String displayName;
        
        Equipment(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum DifficultyLevel {
        BEGINNER("初级"), INTERMEDIATE("中级"), ADVANCED("高级");
        
        private final String displayName;
        
        DifficultyLevel(String displayName) {
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
}