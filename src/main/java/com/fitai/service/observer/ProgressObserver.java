package com.fitai.service.observer;

import com.fitai.model.User;
import com.fitai.model.WorkoutSession;

/**
 * 进度观察者接口 - 观察者模式
 * 监控用户训练进度变化
 */
public interface ProgressObserver {
    
    /**
     * 训练开始通知
     * @param user 用户
     * @param session 训练会话
     */
    void onWorkoutStarted(User user, WorkoutSession session);
    
    /**
     * 训练完成通知
     * @param user 用户
     * @param session 训练会话
     */
    void onWorkoutCompleted(User user, WorkoutSession session);
    
    /**
     * 目标达成通知
     * @param user 用户
     * @param achievementType 成就类型
     * @param details 详细信息
     */
    void onGoalAchieved(User user, String achievementType, String details);
    
    /**
     * 进度更新通知
     * @param user 用户
     * @param progressType 进度类型
     * @param oldValue 旧值
     * @param newValue 新值
     */
    void onProgressUpdated(User user, String progressType, Object oldValue, Object newValue);
    
    /**
     * 获取观察者名称
     * @return 观察者名称
     */
    String getObserverName();
}