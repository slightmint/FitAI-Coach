package com.fitai.service.observer;

import com.fitai.model.User;
import com.fitai.model.WorkoutSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 进度主题 - 观察者模式的主题
 * 管理观察者并通知进度变化
 */
@Component
@Slf4j
public class ProgressSubject {
    
    private final List<ProgressObserver> observers = new CopyOnWriteArrayList<>();
    
    /**
     * 添加观察者
     */
    public void addObserver(ProgressObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            log.info("添加进度观察者: {}", observer.getObserverName());
        }
    }
    
    /**
     * 移除观察者
     */
    public void removeObserver(ProgressObserver observer) {
        if (observers.remove(observer)) {
            log.info("移除进度观察者: {}", observer.getObserverName());
        }
    }
    
    /**
     * 通知训练开始
     */
    public void notifyWorkoutStarted(User user, WorkoutSession session) {
        log.info("通知训练开始: 用户={}, 会话ID={}", user.getUsername(), session.getId());
        observers.forEach(observer -> {
            try {
                observer.onWorkoutStarted(user, session);
            } catch (Exception e) {
                log.error("观察者 {} 处理训练开始事件时发生错误", observer.getObserverName(), e);
            }
        });
    }
    
    /**
     * 通知训练完成
     */
    public void notifyWorkoutCompleted(User user, WorkoutSession session) {
        log.info("通知训练完成: 用户={}, 会话ID={}", user.getUsername(), session.getId());
        observers.forEach(observer -> {
            try {
                observer.onWorkoutCompleted(user, session);
            } catch (Exception e) {
                log.error("观察者 {} 处理训练完成事件时发生错误", observer.getObserverName(), e);
            }
        });
    }
    
    /**
     * 通知目标达成
     */
    public void notifyGoalAchieved(User user, String achievementType, String details) {
        log.info("通知目标达成: 用户={}, 成就={}", user.getUsername(), achievementType);
        observers.forEach(observer -> {
            try {
                observer.onGoalAchieved(user, achievementType, details);
            } catch (Exception e) {
                log.error("观察者 {} 处理目标达成事件时发生错误", observer.getObserverName(), e);
            }
        });
    }
    
    /**
     * 通知进度更新
     */
    public void notifyProgressUpdated(User user, String progressType, Object oldValue, Object newValue) {
        log.debug("通知进度更新: 用户={}, 类型={}, {}→{}", 
                 user.getUsername(), progressType, oldValue, newValue);
        observers.forEach(observer -> {
            try {
                observer.onProgressUpdated(user, progressType, oldValue, newValue);
            } catch (Exception e) {
                log.error("观察者 {} 处理进度更新事件时发生错误", observer.getObserverName(), e);
            }
        });
    }
    
    /**
     * 获取观察者数量
     */
    public int getObserverCount() {
        return observers.size();
    }
    
    /**
     * 获取所有观察者名称
     */
    public List<String> getObserverNames() {
        return observers.stream()
            .map(ProgressObserver::getObserverName)
            .toList();
    }
}