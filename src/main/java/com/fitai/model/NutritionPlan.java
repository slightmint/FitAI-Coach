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
@Table(name = "nutrition_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutritionPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    @NotBlank(message = "营养计划名称不能为空")
    @Size(max = 200, message = "计划名称长度不能超过200个字符")
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type")
    private PlanType planType;
    
    @Column(name = "daily_calories")
    @Min(value = 800, message = "每日卡路里不能少于800")
    @Max(value = 5000, message = "每日卡路里不能超过5000")
    private Integer dailyCalories;
    
    @Column(name = "protein_grams")
    @Min(value = 0, message = "蛋白质含量不能为负数")
    private Double proteinGrams;
    
    @Column(name = "carbs_grams")
    @Min(value = 0, message = "碳水化合物含量不能为负数")
    private Double carbsGrams;
    
    @Column(name = "fat_grams")
    @Min(value = 0, message = "脂肪含量不能为负数")
    private Double fatGrams;
    
    @Column(name = "fiber_grams")
    @Min(value = 0, message = "纤维含量不能为负数")
    private Double fiberGrams;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Enumerated(EnumType.STRING)
    private PlanStatus status = PlanStatus.ACTIVE;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    // 营养计划类型枚举
    public enum PlanType {
        WEIGHT_LOSS("减重计划"),
        MUSCLE_GAIN("增肌计划"),
        MAINTENANCE("维持计划"),
        CUTTING("减脂计划"),
        BULKING("增重计划"),
        BALANCED("均衡饮食");
        
        private final String displayName;
        
        PlanType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // 计划状态枚举
    public enum PlanStatus {
        ACTIVE("进行中"),
        COMPLETED("已完成"),
        PAUSED("已暂停"),
        CANCELLED("已取消");
        
        private final String displayName;
        
        PlanStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 计算蛋白质卡路里
    public Double getProteinCalories() {
        return proteinGrams != null ? proteinGrams * 4 : 0.0;
    }
    
    // 计算碳水化合物卡路里
    public Double getCarbsCalories() {
        return carbsGrams != null ? carbsGrams * 4 : 0.0;
    }
    
    // 计算脂肪卡路里
    public Double getFatCalories() {
        return fatGrams != null ? fatGrams * 9 : 0.0;
    }
    
    // 计算总营养素卡路里
    public Double getTotalMacroCalories() {
        return getProteinCalories() + getCarbsCalories() + getFatCalories();
    }
    
    // 检查计划是否有效
    public boolean isValid() {
        return isActive && status == PlanStatus.ACTIVE;
    }
}