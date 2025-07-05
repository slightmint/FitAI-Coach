package com.fitai.service;

import com.fitai.model.User;

/**
 * 通知服务接口
 * 负责发送各种类型的通知给用户
 */
public interface NotificationService {
    
    /**
     * 发送成就通知
     * @param user 用户
     * @param title 通知标题
     * @param message 通知消息
     */
    void sendAchievementNotification(User user, String title, String message);
    
    /**
     * 发送普通通知
     * @param user 用户
     * @param message 通知消息
     */
    void sendNotification(User user, String message);
    
    /**
     * 发送训练提醒
     * @param user 用户
     * @param reminderMessage 提醒消息
     */
    void sendWorkoutReminder(User user, String reminderMessage);
}