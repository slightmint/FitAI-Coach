package com.fitai.service;

import com.fitai.model.User;
import com.fitai.repository.UserRepository;
import com.fitai.service.observer.ProgressSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ProgressSubject progressSubject;
    
    /**
     * 用户注册
     */
    public User registerUser(User user) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    /**
     * 用户登录验证
     */
    public Optional<User> authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }
    
    /**
     * 更新用户信息
     */
    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    /**
     * 更新用户进度
     */
    public void updateUserProgress(Long userId, double currentWeight, int workoutDays) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            double oldWeight = user.getWeight();
            
            user.setWeight(currentWeight);
            user.setWorkoutDays(workoutDays);
            user.setUpdatedAt(LocalDateTime.now());
            
            userRepository.save(user);
            
            // 通知观察者进度变化 - 分别通知体重和训练天数变化
            progressSubject.notifyProgressUpdated(user, "weight", oldWeight, currentWeight);
            progressSubject.notifyProgressUpdated(user, "workoutDays", user.getWorkoutDays(), workoutDays);
        }
    }
    
    /**
     * 根据ID查找用户
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * 根据用户名查找用户
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    /**
     * 计算用户BMI并更新健身建议
     */
    public void updateUserFitnessAdvice(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            double bmi = user.calculateBMI();
            
            String advice;
            if (bmi < 18.5) {
                advice = "您的BMI偏低，建议增加力量训练和营养摄入";
            } else if (bmi < 25) {
                advice = "您的BMI正常，保持当前的训练计划";
            } else if (bmi < 30) {
                advice = "您的BMI偏高，建议增加有氧运动和控制饮食";
            } else {
                advice = "建议咨询专业医生，制定科学的减重计划";
            }
            
            // 这里可以将建议保存到用户配置或发送通知
            System.out.println("用户 " + user.getUsername() + " 的健身建议: " + advice);
        }
    }
}