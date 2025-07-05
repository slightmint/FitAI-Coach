package com.fitai.controller;

import com.fitai.model.User;
import com.fitai.service.WorkoutPlanService;
import com.fitai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
    
    @Autowired
    private WorkoutPlanService workoutPlanService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 首页
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    /**
     * 仪表板
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // 创建默认用户用于展示
        User defaultUser = new User();
        defaultUser.setUsername("访客用户");
        defaultUser.setHeight(170.0);
        defaultUser.setWeight(65.0);
        defaultUser.setWorkoutDays(15);
        defaultUser.setFitnessGoal(User.FitnessGoal.WEIGHT_LOSS);
        
        // 设置默认数据 - 使用 Optional.empty() 而不是 null
        model.addAttribute("activePlan", java.util.Optional.empty());
        model.addAttribute("recentPlans", java.util.Collections.emptyList());
        model.addAttribute("popularPlans", java.util.Collections.emptyList());
        
        // 用户统计信息
        model.addAttribute("user", defaultUser);
        model.addAttribute("bmi", defaultUser.calculateBMI());
        
        return "dashboard";
    }
    
    /**
     * 关于页面
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }
}