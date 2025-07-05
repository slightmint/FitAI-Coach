package com.fitai.service.observer.impl;

import com.fitai.model.WorkoutSession;
import com.fitai.service.observer.WorkoutProgressObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * 成就跟踪观察者实现
 */
@Component
@Slf4j
public class AchievementTracker implements WorkoutProgressObserver {
    
    @Override
    public void onWorkoutStarted(WorkoutSession session) {
        log.info("用户 {} 开始训练: {}", session.getUser().getUsername(), session.getWorkoutPlan().getName());
        // 可以在这里记录训练开始的成就相关逻辑
    }
    
    @Override
    public void onWorkoutCompleted(WorkoutSession session) {
        log.info("用户 {} 完成训练: {}", session.getUser().getUsername(), session.getWorkoutPlan().getName());
        
        // 成就检查逻辑已经在 AchievementObserver 中实现
        // 这里只需要记录日志即可
        log.info("训练完成，成就检查将由 AchievementObserver 处理");
    }
    
    @Override
    public void onWorkoutPaused(WorkoutSession session) {
        log.info("用户 {} 暂停训练: {}", session.getUser().getUsername(), session.getWorkoutPlan().getName());
    }
    
    @Override
    public void onWorkoutCancelled(WorkoutSession session) {
        log.info("用户 {} 取消训练: {}", session.getUser().getUsername(), session.getWorkoutPlan().getName());
    }
    
    @Override
    public ObserverType getObserverType() {
        return ObserverType.ACHIEVEMENT_TRACKER;
    }
}