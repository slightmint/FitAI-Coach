package com.fitai.service.observer;

import com.fitai.model.WorkoutSession;

/**
 * 训练进度观察者接口 - 观察者模式
 */
public interface WorkoutProgressObserver {
    
    /**
     * 训练开始时触发
     * @param session 训练会话
     */
    void onWorkoutStarted(WorkoutSession session);
    
    /**
     * 训练完成时触发
     * @param session 训练会话
     */
    void onWorkoutCompleted(WorkoutSession session);
    
    /**
     * 训练暂停时触发
     * @param session 训练会话
     */
    void onWorkoutPaused(WorkoutSession session);
    
    /**
     * 训练取消时触发
     * @param session 训练会话
     */
    void onWorkoutCancelled(WorkoutSession session);
    
    /**
     * 获取观察者类型
     * @return 观察者类型
     */
    ObserverType getObserverType();
    
    /**
     * 观察者类型枚举
     */
    enum ObserverType {
        ACHIEVEMENT_TRACKER("成就跟踪器"),
        NOTIFICATION_SENDER("通知发送器"),
        STATISTICS_UPDATER("统计更新器"),
        RECOMMENDATION_ENGINE("推荐引擎");
        
        private final String description;
        
        ObserverType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}