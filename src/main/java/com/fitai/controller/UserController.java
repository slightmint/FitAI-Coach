package com.fitai.controller;

import com.fitai.model.User;
import com.fitai.service.UserService;
import com.fitai.service.WorkoutPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private WorkoutPlanService workoutPlanService;
    
    // 删除注册相关方法
    // 删除登录相关方法
    // 删除登出方法
    
    /**
     * 显示用户资料页面
     */
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/";  // 改为重定向到首页
        }
        
        // 获取最新的用户信息
        Optional<User> userOpt = userService.findById(currentUser.getId());
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            model.addAttribute("workoutPlans", workoutPlanService.getUserWorkoutPlans(currentUser.getId()));
        }
        
        return "user/profile";
    }
    
    /**
     * 更新用户资料
     */
    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute User user,
                               BindingResult result,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user/profile";
        }
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/";  // 改为重定向到首页
        }
        
        user.setId(currentUser.getId());
        User updatedUser = userService.updateUser(user);
        session.setAttribute("currentUser", updatedUser);
        
        redirectAttributes.addFlashAttribute("successMessage", "资料更新成功！");
        return "redirect:/user/profile";
    }
    
    /**
     * 更新用户进度
     */
    @PostMapping("/progress")
    @ResponseBody
    public String updateProgress(@RequestParam double currentWeight,
                                @RequestParam int workoutDays,
                                HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "error";
        }
        
        userService.updateUserProgress(currentUser.getId(), currentWeight, workoutDays);
        return "success";
    }
}