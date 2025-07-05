package com.fitai.service.ai;

import com.fitai.model.User;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 训练计划AI服务实现类
 */
@Service
@Slf4j
public class WorkoutPlanAIService implements AIService {
    
    @Override
    public AIResponse processRequest(User user, AIRequest request) {
        log.info("处理训练计划AI请求 - 用户ID: {}, 请求类型: {}", user.getId(), request.getRequestType());
        
        try {
            // 验证请求参数
            if (!validateRequest(request)) {
                return AIResponse.failure("INVALID_REQUEST", "请求参数无效");
            }
            
            // 根据用户信息生成个性化训练计划
            String workoutPlan = generateWorkoutPlan(user, request);
            
            AIResponse response = AIResponse.success(workoutPlan);
            response.setResponseId(UUID.randomUUID().toString());
            response.setRequestId(request.getRequestId());
            response.setConfidence(0.85);
            
            // 添加额外数据
            response.addData("planType", determinePlanType(user));
            response.addData("difficulty", determineDifficulty(user));
            response.addData("duration", "4-6周");
            
            log.info("训练计划生成成功 - 响应ID: {}", response.getResponseId());
            return response;
            
        } catch (Exception e) {
            log.error("训练计划生成失败", e);
            return AIResponse.failure("GENERATION_ERROR", "训练计划生成失败: " + e.getMessage());
        }
    }
    
    @Override
    public AIServiceFactory.AIServiceType getServiceType() {
        return AIServiceFactory.AIServiceType.WORKOUT_PLAN;
    }
    
    @Override
    public boolean validateRequest(AIRequest request) {
        if (request == null || request.getRequestType() == null) {
            return false;
        }
        
        // 验证请求类型是否适用于训练计划服务
        return request.getRequestType() == AIRequest.RequestType.WORKOUT_PLAN_GENERATION ||
               request.getRequestType() == AIRequest.RequestType.EXERCISE_RECOMMENDATION;
    }
    
    /**
     * 生成个性化训练计划
     */
    private String generateWorkoutPlan(User user, AIRequest request) {
        StringBuilder plan = new StringBuilder();
        
        plan.append("个性化训练计划\n\n");
        plan.append("用户: ").append(user.getUsername()).append("\n");
        plan.append("健身目标: ").append(user.getFitnessGoal().getDisplayName()).append("\n");
        plan.append("健身水平: ").append(user.getFitnessLevel().getDisplayName()).append("\n\n");
        
        // 根据健身目标生成不同的训练计划
        switch (user.getFitnessGoal()) {
            case WEIGHT_LOSS:
                plan.append(generateWeightLossPlan());
                break;
            case MUSCLE_GAIN:
                plan.append(generateMuscleGainPlan());
                break;
            case STRENGTH:
                plan.append(generateStrengthPlan());
                break;
            case ENDURANCE:
                plan.append(generateEndurancePlan());
                break;
            default:
                plan.append(generateGeneralFitnessPlan());
        }
        
        return plan.toString();
    }
    
    private String generateWeightLossPlan() {
        return "减脂训练计划:\n" +
               "1. 有氧运动 (30-45分钟)\n" +
               "   - 跑步机或户外跑步\n" +
               "   - 椭圆机训练\n" +
               "   - 游泳\n\n" +
               "2. 力量训练 (20-30分钟)\n" +
               "   - 复合动作为主\n" +
               "   - 高次数、中等重量\n" +
               "   - 超级组训练\n\n" +
               "3. 训练频率: 每周4-5次\n" +
               "4. 休息时间: 组间30-60秒";
    }
    
    private String generateMuscleGainPlan() {
        return "增肌训练计划:\n" +
               "1. 力量训练 (45-60分钟)\n" +
               "   - 大重量、低次数\n" +
               "   - 复合动作优先\n" +
               "   - 渐进式超负荷\n\n" +
               "2. 有氧运动 (15-20分钟)\n" +
               "   - 低强度有氧\n" +
               "   - 训练后进行\n\n" +
               "3. 训练频率: 每周4-6次\n" +
               "4. 休息时间: 组间2-3分钟";
    }
    
    private String generateStrengthPlan() {
        return "力量训练计划:\n" +
               "1. 核心力量动作\n" +
               "   - 深蹲、硬拉、卧推\n" +
               "   - 大重量、低次数\n" +
               "   - 完美动作形式\n\n" +
               "2. 辅助训练\n" +
               "   - 针对弱点部位\n" +
               "   - 单侧训练\n\n" +
               "3. 训练频率: 每周3-4次\n" +
               "4. 休息时间: 组间3-5分钟";
    }
    
    private String generateEndurancePlan() {
        return "耐力训练计划:\n" +
               "1. 有氧耐力 (45-60分钟)\n" +
               "   - 长时间中等强度\n" +
               "   - 间歇训练\n" +
               "   - 交叉训练\n\n" +
               "2. 肌肉耐力\n" +
               "   - 高次数、轻重量\n" +
               "   - 循环训练\n\n" +
               "3. 训练频率: 每周5-6次\n" +
               "4. 休息时间: 组间30-45秒";
    }
    
    private String generateGeneralFitnessPlan() {
        return "综合健身计划:\n" +
               "1. 有氧运动 (30分钟)\n" +
               "2. 力量训练 (30分钟)\n" +
               "3. 柔韧性训练 (10分钟)\n" +
               "4. 训练频率: 每周3-4次";
    }
    
    private String determinePlanType(User user) {
        return user.getFitnessGoal().getDisplayName() + "专项计划";
    }
    
    private String determineDifficulty(User user) {
        switch (user.getFitnessLevel()) {
            case BEGINNER:
                return "初级";
            case INTERMEDIATE:
                return "中级";
            case ADVANCED:
                return "高级";
            default:
                return "中级";
        }
    }
}