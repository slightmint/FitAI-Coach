package com.fitai.service.ai;

import com.fitai.model.User;

/**
 * AI服务基础接口
 */
public interface AIService {
    
    /**
     * 处理AI请求
     * @param user 用户信息
     * @param request 请求参数
     * @return 处理结果
     */
    AIResponse processRequest(User user, AIRequest request);
    
    /**
     * 获取服务类型
     * @return 服务类型
     */
    AIServiceFactory.AIServiceType getServiceType();
    
    /**
     * 验证请求参数
     * @param request 请求参数
     * @return 是否有效
     */
    boolean validateRequest(AIRequest request);
}