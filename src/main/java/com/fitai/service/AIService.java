package com.fitai.service;

import com.fitai.config.AIConfig;
import com.fitai.model.User;
import com.fitai.model.WorkoutPlan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
@Slf4j
public class AIService {
    
    @Autowired
    private AIConfig aiConfig;
    
    private final RestTemplate restTemplate;
    
    public AIService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * 生成个性化训练建议
     */
    public String generateWorkoutAdvice(User user, WorkoutPlan plan) {
        try {
            String prompt = buildWorkoutPrompt(user, plan);
            return callAIAPI(prompt);
        } catch (Exception e) {
            log.error("AI服务调用失败", e);
            return "暂时无法生成AI建议，请稍后重试。";
        }
    }
    
    /**
     * 生成营养建议
     */
    public String generateNutritionAdvice(User user) {
        try {
            String prompt = buildNutritionPrompt(user);
            return callAIAPI(prompt);
        } catch (Exception e) {
            log.error("AI营养建议生成失败", e);
            return "暂时无法生成营养建议，请稍后重试。";
        }
    }
    
    /**
     * 分析训练数据并提供改进建议
     */
    public String analyzeWorkoutProgress(User user, List<WorkoutPlan> recentPlans) {
        try {
            String prompt = buildProgressAnalysisPrompt(user, recentPlans);
            return callAIAPI(prompt);
        } catch (Exception e) {
            log.error("AI进度分析失败", e);
            return "暂时无法分析训练进度，请稍后重试。";
        }
    }
    
    /**
     * 调用AI API
     */
    private String callAIAPI(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(aiConfig.getKey());
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", aiConfig.getModel());
        requestBody.put("max_tokens", aiConfig.getMaxTokens());
        requestBody.put("temperature", 0.7);
        
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        requestBody.put("messages", messages);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                aiConfig.getBaseUrl() + "/chat/completions",
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, String> message1 = (Map<String, String>) firstChoice.get("message");
                    return message1.get("content");
                }
            }
            
            return "AI服务返回了意外的响应格式。";
            
        } catch (ResourceAccessException e) {
            log.error("AI API连接超时", e);
            return "AI服务连接超时，请检查网络连接。";
        } catch (Exception e) {
            log.error("AI API调用异常", e);
            return "AI服务暂时不可用，请稍后重试。";
        }
    }
    
    /**
     * 构建训练建议提示词
     */
    private String buildWorkoutPrompt(User user, WorkoutPlan plan) {
        return String.format(
            "作为专业的健身教练，请为以下用户提供个性化的训练建议：\n" +
            "用户信息：\n" +
            "- 年龄：%d岁\n" +
            "- 性别：%s\n" +
            "- 身高：%.1fcm\n" +
            "- 体重：%.1fkg\n" +
            "- 健身水平：%s\n" +
            "- 健身目标：%s\n" +
            "\n当前训练计划：%s\n" +
            "\n请提供具体的训练建议，包括：\n" +
            "1. 训练强度调整建议\n" +
            "2. 动作技巧要点\n" +
            "3. 注意事项\n" +
            "4. 预期效果\n" +
            "请用中文回答，控制在300字以内。",
            user.getAge() != null ? user.getAge() : 25,
            user.getGender() != null ? user.getGender().getDisplayName() : "未知",
            user.getHeight() != null ? user.getHeight() : 170.0,
            user.getWeight() != null ? user.getWeight() : 70.0,
            user.getFitnessLevel() != null ? user.getFitnessLevel().getDisplayName() : "初级",
            user.getFitnessGoal() != null ? user.getFitnessGoal().getDisplayName() : "综合健身",
            plan.getName()
        );
    }
    
    /**
     * 构建营养建议提示词
     */
    private String buildNutritionPrompt(User user) {
        double bmi = user.calculateBMI();
        return String.format(
            "作为专业的营养师，请为以下用户提供个性化的营养建议：\n" +
            "用户信息：\n" +
            "- 年龄：%d岁\n" +
            "- 性别：%s\n" +
            "- BMI：%.1f\n" +
            "- 健身目标：%s\n" +
            "\n请提供具体的营养建议，包括：\n" +
            "1. 每日热量摄入建议\n" +
            "2. 三大营养素比例\n" +
            "3. 推荐食物类型\n" +
            "4. 饮食时间安排\n" +
            "请用中文回答，控制在250字以内。",
            user.getAge() != null ? user.getAge() : 25,
            user.getGender() != null ? user.getGender().getDisplayName() : "未知",
            bmi,
            user.getFitnessGoal() != null ? user.getFitnessGoal().getDisplayName() : "综合健身"
        );
    }
    
    /**
     * 构建进度分析提示词
     */
    private String buildProgressAnalysisPrompt(User user, List<WorkoutPlan> recentPlans) {
        StringBuilder plansInfo = new StringBuilder();
        for (int i = 0; i < Math.min(recentPlans.size(), 3); i++) {
            WorkoutPlan plan = recentPlans.get(i);
            plansInfo.append(String.format("- %s（%s）\n", 
                plan.getName(), 
                plan.getCreatedAt().toLocalDate().toString()));
        }
        
        return String.format(
            "作为专业的健身教练，请分析用户的训练进度并提供改进建议：\n" +
            "用户信息：\n" +
            "- 健身水平：%s\n" +
            "- 健身目标：%s\n" +
            "\n最近的训练计划：\n%s" +
            "\n请提供：\n" +
            "1. 训练进度评估\n" +
            "2. 改进建议\n" +
            "3. 下一阶段目标\n" +
            "请用中文回答，控制在200字以内。",
            user.getFitnessLevel() != null ? user.getFitnessLevel().getDisplayName() : "初级",
            user.getFitnessGoal() != null ? user.getFitnessGoal().getDisplayName() : "综合健身",
            plansInfo.toString()
        );
    }
}