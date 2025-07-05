package com.fitai.service.observer;

import com.fitai.model.User;
import com.fitai.model.WorkoutSession;
import com.fitai.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * 成就系统观察者
 * 监控用户训练进度，触发成就奖励
 */
@Component
@Slf4j
public class AchievementObserver implements ProgressObserver {
    
    @Autowired
    private NotificationService notificationService;
    
    // 用户统计缓存
    private final Map<Long, UserStats> userStatsCache = new ConcurrentHashMap<>();
    
    @Override
    public void onWorkoutStarted(User user, WorkoutSession session) {
        log.debug("用户 {} 开始训练，会话ID: {}", user.getUsername(), session.getId());
        // 记录训练开始时间，用于计算连续训练天数
        updateUserStats(user.getId(), stats -> {
            stats.setLastWorkoutDate(LocalDateTime.now());
            stats.incrementWorkoutCount();
        });
    }
    
    @Override
    public void onWorkoutCompleted(User user, WorkoutSession session) {
        log.info("用户 {} 完成训练，会话ID: {}", user.getUsername(), session.getId());
        
        UserStats stats = getUserStats(user.getId());
        stats.incrementCompletedWorkouts();
        stats.addWorkoutDuration(session.getDurationMinutes());
        stats.addCaloriesBurned(session.getCaloriesBurned());
        
        // 检查各种成就
        checkWorkoutCountAchievements(user, stats);
        checkConsecutiveDaysAchievements(user, stats);
        checkCaloriesAchievements(user, stats);
        checkDurationAchievements(user, stats);
        checkWeightLossAchievements(user, stats);
    }
    
    // 删除或注释掉这个方法，因为签名不匹配接口定义
    /*
    @Override
    public void onProgressUpdated(User user, double oldWeight, double newWeight, int workoutDays) {
        log.info("用户 {} 进度更新: 体重 {} -> {}, 训练天数: {}", 
                user.getUsername(), oldWeight, newWeight, workoutDays);
        
        UserStats stats = getUserStats(user.getId());
        stats.setCurrentWeight(newWeight);
        stats.setTotalWorkoutDays(workoutDays);
        
        // 计算体重变化
        double weightChange = oldWeight - newWeight;
        if (weightChange > 0) {
            stats.addWeightLoss(weightChange);
        }
        
        // 检查体重相关成就
        checkWeightLossAchievements(user, stats);
        checkWorkoutDaysAchievements(user, stats);
    }
    */
    // 删除这个重复的方法（第81-88行）
    /*
    @Override
    public void onGoalAchieved(User user, String goalType, String goalValue) {
        log.info("用户 {} 达成目标: {} = {}", user.getUsername(), goalType, goalValue);
        
        String achievementTitle = "目标达成";
        String achievementMessage = String.format("恭喜您达成%s目标：%s！", goalType, goalValue);
        
        sendAchievementNotification(user, achievementTitle, achievementMessage);
    }
    */
    
    /**
     * 检查训练次数相关成就
     */
    private void checkWorkoutCountAchievements(User user, UserStats stats) {
        int completedWorkouts = stats.getCompletedWorkouts();
        
        if (completedWorkouts == 1) {
            unlockAchievement(user, "初次训练", "完成第一次训练，健身之路正式开始！");
        } else if (completedWorkouts == 10) {
            unlockAchievement(user, "坚持不懈", "完成10次训练，展现了良好的坚持性！");
        } else if (completedWorkouts == 50) {
            unlockAchievement(user, "训练达人", "完成50次训练，您已经是训练达人了！");
        } else if (completedWorkouts == 100) {
            unlockAchievement(user, "百炼成钢", "完成100次训练，百炼成钢的毅力！");
        } else if (completedWorkouts == 365) {
            unlockAchievement(user, "年度战士", "完成365次训练，一年的坚持成就了更好的自己！");
        }
    }
    
    /**
     * 检查连续训练天数成就
     */
    private void checkConsecutiveDaysAchievements(User user, UserStats stats) {
        int consecutiveDays = calculateConsecutiveDays(stats);
        
        if (consecutiveDays == 7) {
            unlockAchievement(user, "一周坚持", "连续训练7天，养成了良好的运动习惯！");
        } else if (consecutiveDays == 30) {
            unlockAchievement(user, "月度挑战者", "连续训练30天，月度挑战成功完成！");
        } else if (consecutiveDays == 100) {
            unlockAchievement(user, "百日坚持", "连续训练100天，展现了超强的意志力！");
        }
    }
    
    /**
     * 检查卡路里消耗成就
     */
    private void checkCaloriesAchievements(User user, UserStats stats) {
        double totalCalories = stats.getTotalCaloriesBurned();
        
        if (totalCalories >= 1000) {
            unlockAchievement(user, "卡路里杀手", "累计消耗1000卡路里，燃烧脂肪的战士！");
        } else if (totalCalories >= 5000) {
            unlockAchievement(user, "燃脂达人", "累计消耗5000卡路里，燃脂效果显著！");
        } else if (totalCalories >= 10000) {
            unlockAchievement(user, "超级燃脂王", "累计消耗10000卡路里，超级燃脂王者！");
        }
    }
    
    /**
     * 检查训练时长成就
     */
    private void checkDurationAchievements(User user, UserStats stats) {
        int totalMinutes = stats.getTotalWorkoutMinutes();
        
        if (totalMinutes >= 60) {
            unlockAchievement(user, "时间管理者", "累计训练1小时，时间管理能力出色！");
        } else if (totalMinutes >= 600) {
            unlockAchievement(user, "训练专家", "累计训练10小时，已成为训练专家！");
        } else if (totalMinutes >= 3000) {
            unlockAchievement(user, "时间投资家", "累计训练50小时，时间投资获得丰厚回报！");
        }
    }
    
    /**
     * 检查减重成就
     */
    private void checkWeightLossAchievements(User user, UserStats stats) {
        double totalWeightLoss = stats.getTotalWeightLoss();
        
        if (totalWeightLoss >= 1.0) {
            unlockAchievement(user, "减重新手", "成功减重1公斤，减重之路开始了！");
        } else if (totalWeightLoss >= 5.0) {
            unlockAchievement(user, "减重达人", "成功减重5公斤，效果显著！");
        } else if (totalWeightLoss >= 10.0) {
            unlockAchievement(user, "减重专家", "成功减重10公斤，身材变化明显！");
        } else if (totalWeightLoss >= 20.0) {
            unlockAchievement(user, "蜕变大师", "成功减重20公斤，完成了惊人的蜕变！");
        }
    }
    
    /**
     * 检查训练天数成就
     */
    private void checkWorkoutDaysAchievements(User user, UserStats stats) {
        int workoutDays = stats.getTotalWorkoutDays();
        
        if (workoutDays >= 30) {
            unlockAchievement(user, "月度勇士", "训练满30天，展现了持续的决心！");
        } else if (workoutDays >= 90) {
            unlockAchievement(user, "季度冠军", "训练满90天，季度训练冠军！");
        } else if (workoutDays >= 180) {
            unlockAchievement(user, "半年坚持者", "训练满180天，半年的坚持值得赞扬！");
        } else if (workoutDays >= 365) {
            unlockAchievement(user, "年度传奇", "训练满365天，成为年度训练传奇！");
        }
    }
    
    /**
     * 解锁成就
     */
    private void unlockAchievement(User user, String title, String description) {
        log.info("用户 {} 解锁成就: {}", user.getUsername(), title);
        
        // 检查是否已经解锁过此成就
        UserStats stats = getUserStats(user.getId());
        if (stats.hasAchievement(title)) {
            return; // 已经解锁过，不重复通知
        }
        
        // 记录成就
        stats.addAchievement(title);
        
        // 发送成就通知
        sendAchievementNotification(user, title, description);
    }
    
    /**
     * 发送成就通知
     */
    private void sendAchievementNotification(User user, String title, String message) {
        try {
            if (notificationService != null) {
                notificationService.sendAchievementNotification(user, title, message);
            }
            log.info("成就通知已发送给用户 {}: {}", user.getUsername(), title);
        } catch (Exception e) {
            log.error("发送成就通知失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 计算连续训练天数
     */
    private int calculateConsecutiveDays(UserStats stats) {
        LocalDateTime lastWorkout = stats.getLastWorkoutDate();
        if (lastWorkout == null) {
            return 0;
        }
        
        LocalDateTime now = LocalDateTime.now();
        long daysBetween = ChronoUnit.DAYS.between(lastWorkout.toLocalDate(), now.toLocalDate());
        
        if (daysBetween <= 1) {
            return stats.getConsecutiveDays() + 1;
        } else {
            // 中断了连续记录
            stats.resetConsecutiveDays();
            return 1;
        }
    }
    
    /**
     * 获取用户统计信息
     */
    private UserStats getUserStats(Long userId) {
        return userStatsCache.computeIfAbsent(userId, id -> new UserStats());
    }
    
    /**
     * 更新用户统计信息
     */
    private void updateUserStats(Long userId, StatsUpdater updater) {
        UserStats stats = getUserStats(userId);
        updater.update(stats);
    }
    
    /**
     * 统计信息更新器接口
     */
    @FunctionalInterface
    private interface StatsUpdater {
        void update(UserStats stats);
    }
    
    /**
     * 用户统计信息内部类
     */
    private static class UserStats {
        private int completedWorkouts = 0;
        private int totalWorkoutMinutes = 0;
        private double totalCaloriesBurned = 0.0;
        private double totalWeightLoss = 0.0;
        private double currentWeight = 0.0;
        private int totalWorkoutDays = 0;
        private int consecutiveDays = 0;
        private LocalDateTime lastWorkoutDate;
        private final List<String> achievements = new ArrayList<>();
        
        // Getters and Setters
        public int getCompletedWorkouts() { return completedWorkouts; }
        public void incrementCompletedWorkouts() { this.completedWorkouts++; }
        public void incrementWorkoutCount() { /* 可以用于其他统计 */ }
        
        public int getTotalWorkoutMinutes() { return totalWorkoutMinutes; }
        public void addWorkoutDuration(int minutes) { this.totalWorkoutMinutes += minutes; }
        
        public double getTotalCaloriesBurned() { return totalCaloriesBurned; }
        public void addCaloriesBurned(double calories) { this.totalCaloriesBurned += calories; }
        
        public double getTotalWeightLoss() { return totalWeightLoss; }
        public void addWeightLoss(double weightLoss) { this.totalWeightLoss += weightLoss; }
        
        public double getCurrentWeight() { return currentWeight; }
        public void setCurrentWeight(double currentWeight) { this.currentWeight = currentWeight; }
        
        public int getTotalWorkoutDays() { return totalWorkoutDays; }
        public void setTotalWorkoutDays(int totalWorkoutDays) { this.totalWorkoutDays = totalWorkoutDays; }
        
        public int getConsecutiveDays() { return consecutiveDays; }
        public void resetConsecutiveDays() { this.consecutiveDays = 0; }
        
        public LocalDateTime getLastWorkoutDate() { return lastWorkoutDate; }
        public void setLastWorkoutDate(LocalDateTime lastWorkoutDate) { this.lastWorkoutDate = lastWorkoutDate; }
        
        public boolean hasAchievement(String achievement) { return achievements.contains(achievement); }
        public void addAchievement(String achievement) { 
            if (!achievements.contains(achievement)) {
                achievements.add(achievement); 
            }
        }
        public List<String> getAchievements() { return new ArrayList<>(achievements); }
    }



@Override
public void onGoalAchieved(User user, String achievementType, String details) {
    log.info("用户 {} 达成目标: {} - {}", user.getUsername(), achievementType, details);
    
    String achievementTitle = "目标达成";
    String achievementMessage = String.format("恭喜您达成%s目标：%s！", achievementType, details);
    
    sendAchievementNotification(user, achievementTitle, achievementMessage);
}

@Override
public void onProgressUpdated(User user, String progressType, Object oldValue, Object newValue) {
    log.info("用户 {} 进度更新: {} {} -> {}", 
            user.getUsername(), progressType, oldValue, newValue);
    
    UserStats stats = getUserStats(user.getId());
    
    // 根据进度类型处理不同的更新
    switch (progressType.toLowerCase()) {
        case "weight":
            if (oldValue instanceof Number && newValue instanceof Number) {
                double oldWeight = ((Number) oldValue).doubleValue();
                double newWeight = ((Number) newValue).doubleValue();
                stats.setCurrentWeight(newWeight);
                
                // 计算体重变化
                double weightChange = oldWeight - newWeight;
                if (weightChange > 0) {
                    stats.addWeightLoss(weightChange);
                }
                
                // 检查体重相关成就
                checkWeightLossAchievements(user, stats);
            }
            break;
            
        case "workout_days":
            if (newValue instanceof Number) {
                int workoutDays = ((Number) newValue).intValue();
                stats.setTotalWorkoutDays(workoutDays);
                checkWorkoutDaysAchievements(user, stats);
            }
            break;
            
        default:
            log.debug("未处理的进度类型: {}", progressType);
            break;
    }
}

@Override
public String getObserverName() {
    return "AchievementObserver";
}
}