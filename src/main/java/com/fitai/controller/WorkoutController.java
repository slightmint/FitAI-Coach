package com.fitai.controller;

import com.fitai.model.User;
import com.fitai.model.WorkoutPlan;
import com.fitai.model.Exercise;
import com.fitai.service.WorkoutPlanService;
import com.fitai.service.ExerciseService;
import com.fitai.service.UserService;
import com.fitai.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity; // Add this import

import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.List;  // 添加这行导入
import java.util.Map;     // Add this import
import java.util.HashMap; // Add this import

// 添加以下import
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/workout")
@Slf4j  // 添加这个注解
public class WorkoutController {
    
    @Autowired
    private WorkoutPlanService workoutPlanService;
    
    @Autowired
    private ExerciseService exerciseService;
    
    @Autowired
    private UserService userService;
    
    // 删除这个重复的方法（第28-38行）
    // @GetMapping("/plans")
    // public String showWorkoutPlans(HttpSession session, Model model) {
    //     User currentUser = (User) session.getAttribute("currentUser");
    //     if (currentUser == null) {
    //         return "redirect:/user/login";
    //     }
    //     
    //     model.addAttribute("workoutPlans", workoutPlanService.getUserWorkoutPlans(currentUser.getId()));
    //     model.addAttribute("activePlan", workoutPlanService.getUserActivePlan(currentUser.getId()));
    //     return "workout/plans";
    // }
    
    /**
     * 显示创建训练计划页面
     */
    @GetMapping("/create")
    public String showCreatePlan(Model model) {
        model.addAttribute("planTypes", new String[]{"strength", "cardio", "weight_loss", "muscle_gain"});
        return "workout/create";
    }
    
    /**
     * 创建个性化训练计划
     */
    @PostMapping("/create")
    public String createPlan(@RequestParam String planType,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            // 创建一个具有默认属性的临时用户
            User currentUser = new User();
            long timestamp = System.currentTimeMillis();
            currentUser.setUsername("testuser_" + timestamp);
            currentUser.setEmail("test_" + timestamp + "@example.com"); // 使用时间戳确保邮箱唯一
            currentUser.setPassword("defaultpassword");
            currentUser.setFitnessLevel(User.FitnessLevel.BEGINNER);
            currentUser.setAge(25);
            currentUser.setWeight(70.0);
            currentUser.setHeight(170.0);
            currentUser.setGender(User.Gender.MALE);
            currentUser.setFitnessGoal(User.FitnessGoal.GENERAL_FITNESS);
            
            // 先保存用户到数据库
            User savedUser = userService.registerUser(currentUser);
            
            // 转换前端传递的planType为正确的格式
            String convertedPlanType = convertPlanType(planType);
            
            WorkoutPlan plan = workoutPlanService.createPersonalizedPlan(savedUser, convertedPlanType);
            
            // 添加null检查
            if (plan == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "创建训练计划失败：计划创建返回空值");
                return "redirect:/workout/create";
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "训练计划创建成功！");
            return "redirect:/workout/plan/" + plan.getId();
        } catch (Exception e) {
            // 改进异常信息处理
            String errorMessage = "创建训练计划失败：" + 
                (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            log.error("创建训练计划时发生异常", e);  // 添加日志记录
            return "redirect:/workout/create";
        }
    }
    
    /**
     * 转换前端传递的planType为WorkoutPlanFactory识别的格式
     */
    private String convertPlanType(String planType) {
        switch (planType.toLowerCase()) {
            case "strength":
            case "muscle_gain":
                return "STRENGTH";
            case "cardio":
            case "weight_loss":
                return "CARDIO";
            case "hiit":
                return "HIIT";
            case "yoga":
                return "YOGA";
            default:
                return "MIXED";
        }
    }
    
    /**
     * 显示训练计划详情
     */
    @GetMapping("/plans")
    public String showWorkoutPlans(HttpSession session, Model model) {
        // 创建具有默认属性的临时用户
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setFitnessLevel(User.FitnessLevel.BEGINNER);
        
        model.addAttribute("workoutPlans", workoutPlanService.getUserWorkoutPlans(currentUser.getId()));
        
        // 修复：将Optional解包
        Optional<WorkoutPlan> activePlanOpt = workoutPlanService.getUserActivePlan(currentUser.getId());
        if (activePlanOpt.isPresent()) {
            model.addAttribute("activePlan", activePlanOpt.get());
        } else {
            model.addAttribute("activePlan", null);
        }
        
        return "workout/plans";
    }
    
    @GetMapping("/plan/{id}")
    public String showPlanDetails(@PathVariable Long id, Model model, HttpSession session) {
        // 创建具有默认属性的临时用户
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setFitnessLevel(User.FitnessLevel.BEGINNER);
        
        Optional<WorkoutPlan> planOpt = workoutPlanService.getUserWorkoutPlans(currentUser.getId())
                .stream().filter(p -> p.getId().equals(id)).findFirst();
        
        if (planOpt.isPresent()) {
            model.addAttribute("plan", planOpt.get());
            return "workout/detail";
        } else {
            return "redirect:/workout/plans";
        }
    }
    
    /**
     * 激活训练计划
     */
    @PostMapping("/activate/{id}")
    public String activatePlan(@PathVariable Long id,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        // 创建具有默认属性的临时用户
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setFitnessLevel(User.FitnessLevel.BEGINNER);
        
        workoutPlanService.activatePlan(id, currentUser.getId());
        redirectAttributes.addFlashAttribute("successMessage", "训练计划已激活！");
        return "redirect:/workout/plans";
    }
    
    /**
     * 显示运动库
     */
    @GetMapping("/exercises")
    public String showExercises(@RequestParam(required = false) String type,
                               @RequestParam(required = false) String muscle,
                               Model model) {
        List<Exercise> exercises;
        
        if (type != null && !type.isEmpty()) {
            // 按运动类型筛选
            try {
                Exercise.ExerciseCategory category = Exercise.ExerciseCategory.valueOf(type.toUpperCase());
                exercises = exerciseService.findByCategory(category);
            } catch (IllegalArgumentException e) {
                exercises = exerciseService.getAllExercises();
            }
        } else if (muscle != null && !muscle.isEmpty()) {
            // 按肌群筛选
            exercises = exerciseService.findByMuscleGroupDisplayName(muscle);
        } else {
            // 显示所有运动
            exercises = exerciseService.getAllExercises();
        }
        
        model.addAttribute("exercises", exercises);
        
        // 提供筛选选项
        model.addAttribute("exerciseTypes", new String[]{"STRENGTH", "CARDIO", "FLEXIBILITY", "BALANCE"});
        
        // 使用枚举的显示名称作为肌群选项
        String[] muscleGroupOptions = new String[Exercise.MuscleGroup.values().length];
        for (int i = 0; i < Exercise.MuscleGroup.values().length; i++) {
            muscleGroupOptions[i] = Exercise.MuscleGroup.values()[i].getDisplayName();
        }
        model.addAttribute("muscleGroups", muscleGroupOptions);
        
        return "workout/exercises";
    }
    
    /**
     * 开始训练会话
     */
    @PostMapping("/start")
    @ResponseBody
    public String startWorkout(@RequestParam Long planId, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "error";
        }
        
        // 这里可以创建训练会话记录
        // WorkoutSession session = workoutSessionService.startSession(currentUser, planId);
        return "success";
    }
    
    // 在WorkoutController类中添加以下内容
    
    @Autowired
    private AIService aiService;
    
    /**
     * 获取AI训练建议
     */
    @GetMapping("/ai-advice/{planId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getAIAdvice(@PathVariable Long planId, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // 创建临时用户（实际应用中应从session获取）
            User currentUser = new User();
            currentUser.setAge(25);
            currentUser.setGender(User.Gender.MALE);
            currentUser.setHeight(170.0);
            currentUser.setWeight(70.0);
            currentUser.setFitnessLevel(User.FitnessLevel.BEGINNER);
            currentUser.setFitnessGoal(User.FitnessGoal.GENERAL_FITNESS);
            
            // 获取训练计划
            Optional<WorkoutPlan> planOpt = workoutPlanService.getUserWorkoutPlans(currentUser.getId())
                    .stream().filter(p -> p.getId().equals(planId)).findFirst();
            
            if (planOpt.isPresent()) {
                String advice = aiService.generateWorkoutAdvice(currentUser, planOpt.get());
                response.put("advice", advice);
                response.put("status", "success");
            } else {
                response.put("error", "训练计划不存在");
                response.put("status", "error");
            }
            
        } catch (Exception e) {
            log.error("获取AI建议失败", e);
            response.put("error", "获取AI建议失败：" + e.getMessage());
            response.put("status", "error");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取AI营养建议
     */
    @GetMapping("/ai-nutrition")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getNutritionAdvice(HttpSession session) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // 创建临时用户
            User currentUser = new User();
            currentUser.setAge(25);
            currentUser.setGender(User.Gender.MALE);
            currentUser.setHeight(170.0);
            currentUser.setWeight(70.0);
            currentUser.setFitnessGoal(User.FitnessGoal.GENERAL_FITNESS);
            
            String advice = aiService.generateNutritionAdvice(currentUser);
            response.put("advice", advice);
            response.put("status", "success");
            
        } catch (Exception e) {
            log.error("获取营养建议失败", e);
            response.put("error", "获取营养建议失败：" + e.getMessage());
            response.put("status", "error");
        }
        
        return ResponseEntity.ok(response);
    }
}