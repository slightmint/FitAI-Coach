package com.fitai.service.ai;

import com.fitai.model.User;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 营养建议AI服务实现类
 */
@Service
@Slf4j
public class NutritionAIService implements AIService {
    
    @Override
    public AIResponse processRequest(User user, AIRequest request) {
        log.info("处理营养建议AI请求 - 用户ID: {}, 请求类型: {}", user.getId(), request.getRequestType());
        
        try {
            if (!validateRequest(request)) {
                return AIResponse.failure("INVALID_REQUEST", "请求参数无效");
            }
            
            String nutritionAdvice = generateNutritionAdvice(user, request);
            
            AIResponse response = AIResponse.success(nutritionAdvice);
            response.setResponseId(UUID.randomUUID().toString());
            response.setRequestId(request.getRequestId());
            response.setConfidence(0.80);
            
            // 添加营养数据
            response.addData("dailyCalories", calculateDailyCalories(user));
            response.addData("proteinGrams", calculateProteinNeeds(user));
            response.addData("mealPlan", generateMealPlan(user));
            
            log.info("营养建议生成成功 - 响应ID: {}", response.getResponseId());
            return response;
            
        } catch (Exception e) {
            log.error("营养建议生成失败", e);
            return AIResponse.failure("GENERATION_ERROR", "营养建议生成失败: " + e.getMessage());
        }
    }
    
    @Override
    public AIServiceFactory.AIServiceType getServiceType() {
        return AIServiceFactory.AIServiceType.NUTRITION;
    }
    
    @Override
    public boolean validateRequest(AIRequest request) {
        if (request == null || request.getRequestType() == null) {
            return false;
        }
        
        return request.getRequestType() == AIRequest.RequestType.NUTRITION_ADVICE;
    }
    
    private String generateNutritionAdvice(User user, AIRequest request) {
        StringBuilder advice = new StringBuilder();
        
        advice.append("个性化营养建议\n\n");
        advice.append("用户: ").append(user.getUsername()).append("\n");
        advice.append("健身目标: ").append(user.getFitnessGoal().getDisplayName()).append("\n\n");
        
        // 基础营养建议
        advice.append("每日营养需求:\n");
        advice.append("- 总热量: ").append(calculateDailyCalories(user)).append(" 卡路里\n");
        advice.append("- 蛋白质: ").append(calculateProteinNeeds(user)).append(" 克\n");
        advice.append("- 碳水化合物: ").append(calculateCarbNeeds(user)).append(" 克\n");
        advice.append("- 脂肪: ").append(calculateFatNeeds(user)).append(" 克\n\n");
        
        // 根据健身目标提供具体建议
        switch (user.getFitnessGoal()) {
            case WEIGHT_LOSS:
                advice.append(getWeightLossNutritionAdvice());
                break;
            case MUSCLE_GAIN:
                advice.append(getMuscleGainNutritionAdvice());
                break;
            default:
                advice.append(getGeneralNutritionAdvice());
        }
        
        return advice.toString();
    }
    
    private int calculateDailyCalories(User user) {
        // 简化的BMR计算（实际应用中需要更复杂的算法）
        int baseCalories = 2000; // 默认值
        
        // 根据健身目标调整
        switch (user.getFitnessGoal()) {
            case WEIGHT_LOSS:
                return (int) (baseCalories * 0.8); // 减少20%
            case MUSCLE_GAIN:
                return (int) (baseCalories * 1.2); // 增加20%
            default:
                return baseCalories;
        }
    }
    
    private int calculateProteinNeeds(User user) {
        int baseProtein = 80; // 默认值
        
        switch (user.getFitnessGoal()) {
            case MUSCLE_GAIN:
                return (int) (baseProtein * 1.5);
            case STRENGTH:
                return (int) (baseProtein * 1.3);
            default:
                return baseProtein;
        }
    }
    
    private int calculateCarbNeeds(User user) {
        return calculateDailyCalories(user) * 45 / 100 / 4; // 45%热量来自碳水
    }
    
    private int calculateFatNeeds(User user) {
        return calculateDailyCalories(user) * 25 / 100 / 9; // 25%热量来自脂肪
    }
    
    private String getWeightLossNutritionAdvice() {
        return "减脂营养建议:\n" +
               "1. 创造热量缺口，但不要过度节食\n" +
               "2. 增加蛋白质摄入，保持肌肉量\n" +
               "3. 选择复合碳水化合物\n" +
               "4. 多吃蔬菜和水果\n" +
               "5. 控制精制糖和加工食品\n" +
               "6. 保持充足水分摄入";
    }
    
    private String getMuscleGainNutritionAdvice() {
        return "增肌营养建议:\n" +
               "1. 创造适度热量盈余\n" +
               "2. 高蛋白质摄入（每公斤体重1.6-2.2克）\n" +
               "3. 训练前后合理补充碳水\n" +
               "4. 健康脂肪来源\n" +
               "5. 少食多餐\n" +
               "6. 充足的水分和休息";
    }
    
    private String getGeneralNutritionAdvice() {
        return "均衡营养建议:\n" +
               "1. 保持营养均衡\n" +
               "2. 规律进餐\n" +
               "3. 多样化食物选择\n" +
               "4. 适量运动\n" +
               "5. 充足睡眠";
    }
    
    private String generateMealPlan(User user) {
        return "建议一日三餐 + 健康加餐";
    }
}