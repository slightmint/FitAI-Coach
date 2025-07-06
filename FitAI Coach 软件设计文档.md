
# FitAI Coach 软件设计文档

## 1. 系统概述

FitAI Coach 是一个基于 Spring Boot 的智能健身教练系统，采用分层架构设计，集成了多种设计模式，提供个性化训练计划、AI智能建议和进度跟踪等功能。

## 2. 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                    表现层 (Presentation Layer)                │
├─────────────────────────────────────────────────────────────┤
│  Web界面 (Thymeleaf)  │  REST API  │  静态资源 (CSS/JS)      │
│  - 用户界面           │  - JSON响应 │  - 前端交互逻辑         │
│  - 模板渲染           │  - 数据接口 │  - 样式定义             │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    控制层 (Controller Layer)                  │
├─────────────────────────────────────────────────────────────┤
│  HomeController  │  WorkoutController  │  UserController     │
│  - 首页路由      │  - 训练计划管理     │  - 用户管理         │
│  - 基础导航      │  - 计划创建/查看    │  - 认证授权         │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    业务层 (Service Layer)                     │
├─────────────────────────────────────────────────────────────┤
│  核心服务                │  设计模式实现                      │
│  ├─ WorkoutPlanService  │  ├─ Factory Pattern                │
│  ├─ ExerciseService     │  │  └─ WorkoutPlanFactory          │
│  ├─ UserService         │  ├─ Strategy Pattern               │
│  ├─ AIService           │  │  ├─ StrengthTrainingStrategy    │
│  └─ NutritionService    │  │  ├─ CardioTrainingStrategy      │
│                         │  │  ├─ HIITTrainingStrategy        │
│                         │  │  ├─ YogaTrainingStrategy        │
│                         │  │  └─ MixedTrainingStrategy       │
│                         │  └─ Observer Pattern              │
│                         │     ├─ ProgressSubject            │
│                         │     ├─ AchievementObserver        │
│                         │     └─ WorkoutProgressObserver    │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    数据访问层 (Repository Layer)              │
├─────────────────────────────────────────────────────────────┤
│  UserRepository  │  WorkoutPlanRepository  │  ExerciseRepository │
│  - JPA接口       │  - 训练计划数据访问     │  - 运动数据访问        │
│  - 用户CRUD      │  - 自定义查询方法       │  - 运动库管理          │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    数据层 (Data Layer)                        │
├─────────────────────────────────────────────────────────────┤
│  关系型数据库 (MySQL/H2)  │  外部服务                        │
│  ├─ 用户表 (users)        │  ├─ AI API服务                   │
│  ├─ 训练计划表            │  │  └─ 智能建议生成              │
│  ├─ 运动库表              │  └─ 第三方集成                   │
│  ├─ 训练记录表            │     └─ 健康数据同步              │
│  └─ 营养计划表            │                                  │
└─────────────────────────────────────────────────────────────┘
```

## 3. 核心类图

### 3.1 用户模块类图

```
┌─────────────────────────────────────┐
│              User                   │
├─────────────────────────────────────┤
│ - id: Long                          │
│ - username: String                  │
│ - email: String                     │
│ - password: String                  │
│ - firstName: String                 │
│ - lastName: String                  │
│ - age: Integer                      │
│ - gender: Gender                    │
│ - height: Double                    │
│ - weight: Double                    │
│ - fitnessLevel: FitnessLevel        │
│ - fitnessGoal: FitnessGoal          │
│ - role: Role                        │
│ - workoutPlans: List<WorkoutPlan>   │
│ - workoutSessions: List<WorkoutSession> │
│ - nutritionPlans: List<NutritionPlan>   │
├─────────────────────────────────────┤
│ + getAuthorities(): Collection      │
│ + isAccountNonExpired(): boolean    │
│ + isAccountNonLocked(): boolean     │
│ + isCredentialsNonExpired(): boolean│
│ + isEnabled(): boolean              │
└─────────────────────────────────────┘
                │
                │ implements
                ▼
┌─────────────────────────────────────┐
│          UserDetails                │
├─────────────────────────────────────┤
│ + getAuthorities(): Collection      │
│ + getPassword(): String             │
│ + getUsername(): String             │
│ + isAccountNonExpired(): boolean    │
│ + isAccountNonLocked(): boolean     │
│ + isCredentialsNonExpired(): boolean│
│ + isEnabled(): boolean              │
└─────────────────────────────────────┘

┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│     Gender      │  │  FitnessLevel   │  │  FitnessGoal    │
├─────────────────┤  ├─────────────────┤  ├─────────────────┤
│ + MALE          │  │ + BEGINNER      │  │ + WEIGHT_LOSS   │
│ + FEMALE        │  │ + INTERMEDIATE  │  │ + MUSCLE_GAIN   │
│ + OTHER         │  │ + ADVANCED      │  │ + ENDURANCE     │
└─────────────────┘  └─────────────────┘  │ + STRENGTH      │
                                          │ + FLEXIBILITY   │
                                          │ + GENERAL_FITNESS│
                                          └─────────────────┘
```

### 3.2 训练计划模块类图

```
┌─────────────────────────────────────┐
│           WorkoutPlan               │
├─────────────────────────────────────┤
│ - id: Long                          │
│ - name: String                      │
│ - description: String               │
│ - type: String                      │
│ - difficulty: String                │
│ - durationWeeks: Integer            │
│ - frequency: Integer                │
│ - estimatedDuration: Integer        │
│ - status: String                    │
│ - user: User                        │
│ - workoutItems: List<WorkoutItem>   │
├─────────────────────────────────────┤
│ + getId(): Long                     │
│ + getName(): String                 │
│ + getDescription(): String          │
│ + activate(): void                  │
│ + deactivate(): void                │
└─────────────────────────────────────┘
                │
                │ 1:N
                ▼
┌─────────────────────────────────────┐
│           WorkoutItem               │
├─────────────────────────────────────┤
│ - id: Long                          │
│ - exercise: Exercise                │
│ - sets: Integer                     │
│ - reps: String                      │
│ - duration: String                  │
│ - restTime: String                  │
│ - workoutPlan: WorkoutPlan          │
├─────────────────────────────────────┤
│ + getId(): Long                     │
│ + getExercise(): Exercise           │
│ + getSets(): Integer                │
└─────────────────────────────────────┘
                │
                │ N:1
                ▼
┌─────────────────────────────────────┐
│            Exercise                 │
├─────────────────────────────────────┤
│ - id: Long                          │
│ - name: String                      │
│ - description: String               │
│ - muscleGroup: String               │
│ - equipment: String                 │
│ - difficulty: String                │
│ - instructions: String              │
├─────────────────────────────────────┤
│ + getId(): Long                     │
│ + getName(): String                 │
│ + getMuscleGroup(): String          │
└─────────────────────────────────────┘
```

## 4. 设计模式详细说明

### 4.1 工厂模式 (Factory Pattern)

**实现位置**: <mcfile name="WorkoutPlanFactory.java" path="d:\code\FitAI Coach\src\main\java\com\fitai\service\factory\WorkoutPlanFactory.java"></mcfile>

**设计目的**: 根据用户特征和健身目标创建个性化训练计划

**实现细节**:
```java
@Service
public class WorkoutPlanFactory {
    @Autowired
    private StrengthTrainingStrategy strengthStrategy;
    @Autowired
    private CardioTrainingStrategy cardioStrategy;
    @Autowired
    private HIITTrainingStrategy hiitStrategy;
    @Autowired
    private YogaTrainingStrategy yogaStrategy;
    @Autowired
    private MixedTrainingStrategy mixedStrategy;
    
    public WorkoutPlan createWorkoutPlan(User user, String recommendationType) {
        // 根据推荐类型选择相应的策略
        WorkoutStrategy strategy = getStrategy(recommendationType);
        return strategy.generateWorkout(user, new WorkoutPlan());
    }
}
```

**优势**:
- 封装了训练计划创建的复杂逻辑
- 支持多种训练类型的扩展
- 降低了客户端代码的复杂度

### 4.2 策略模式 (Strategy Pattern)

**实现位置**: <mcfolder name="strategy" path="d:\code\FitAI Coach\src\main\java\com\fitai\service\strategy"></mcfolder>

**核心接口**: <mcsymbol name="WorkoutStrategy" filename="WorkoutStrategy.java" path="d:\code\FitAI Coach\src\main\java\com\fitai\service\strategy\WorkoutStrategy.java" startline="8" type="class"></mcsymbol>

**设计目的**: 为不同的训练类型提供可互换的算法实现

**策略实现类**:
- <mcsymbol name="StrengthTrainingStrategy" filename="StrengthTrainingStrategy.java" path="d:\code\FitAI Coach\src\main\java\com\fitai\service\strategy\StrengthTrainingStrategy.java" startline="12" type="class"></mcsymbol>: 力量训练策略
- `CardioTrainingStrategy`: 有氧训练策略
- `HIITTrainingStrategy`: 高强度间歇训练策略
- `YogaTrainingStrategy`: 瑜伽训练策略
- `MixedTrainingStrategy`: 混合训练策略

**接口定义**:
```java
public interface WorkoutStrategy {
    List<WorkoutItem> generateWorkoutItems(User user, WorkoutPlan plan);
    String getStrategyName();
    boolean isApplicable(User user);
    double calculateIntensity(User user, WorkoutPlan plan);
}
```

**优势**:
- 算法可以独立于使用它的客户端变化
- 支持运行时切换算法
- 遵循开闭原则，易于扩展新的训练策略

### 4.3 观察者模式 (Observer Pattern)

**实现位置**: <mcfolder name="observer" path="d:\code\FitAI Coach\src\main\java\com\fitai\service\observer"></mcfolder>

**核心类**: <mcsymbol name="ProgressSubject" filename="ProgressSubject.java" path="d:\code\FitAI Coach\src\main\java\com\fitai\service\observer\ProgressSubject.java" startline="8" type="class"></mcsymbol>

**设计目的**: 实现训练进度的实时监控和通知机制

**实现结构**:
```java
// 主题接口
public class ProgressSubject {
    private List<ProgressObserver> observers = new ArrayList<>();
    
    public void addObserver(ProgressObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(ProgressObserver observer) {
        observers.remove(observer);
    }
    
    public void notifyWorkoutStarted(WorkoutSession session) {
        for (ProgressObserver observer : observers) {
            observer.onWorkoutStarted(session);
        }
    }
}

// 观察者实现
- AchievementObserver: 成就跟踪观察者
- WorkoutProgressObserver: 训练进度观察者
```

**优势**:
- 实现了松耦合的通知机制
- 支持动态添加和移除观察者
- 便于扩展新的进度监控功能

### 4.4 MVC模式 (Model-View-Controller)

**实现说明**: 整个应用采用标准的Spring MVC架构

**组件分层**:
- **Model**: 实体类 (`User`, `WorkoutPlan`, `Exercise` 等)
- **View**: Thymeleaf模板 (`workout/detail.html`, `home.html` 等)
- **Controller**: 控制器类 (<mcsymbol name="WorkoutController" filename="WorkoutController.java" path="d:\code\FitAI Coach\src\main\java\com\fitai\controller\WorkoutController.java" startline="19" type="class"></mcsymbol>, `HomeController` 等)

**优势**:
- 清晰的职责分离
- 便于维护和测试
- 支持多种视图技术

### 4.5 Repository模式

**实现位置**: <mcfolder name="repository" path="d:\code\FitAI Coach\src\main\java\com\fitai\repository"></mcfolder>

**设计目的**: 封装数据访问逻辑，提供统一的数据操作接口

**实现示例**:
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByFitnessLevel(FitnessLevel level);
}
```

**优势**:
- 数据访问逻辑与业务逻辑分离
- 支持多种数据源切换
- 提供统一的CRUD操作接口

## 5. 数据库设计

### 5.1 核心表结构

```sql
-- 用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    age INTEGER,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    height DOUBLE,
    weight DOUBLE,
    fitness_level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED'),
    fitness_goal ENUM('WEIGHT_LOSS', 'MUSCLE_GAIN', 'ENDURANCE', 'STRENGTH', 'FLEXIBILITY', 'GENERAL_FITNESS'),
    role ENUM('USER', 'ADMIN', 'TRAINER'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 训练计划表
CREATE TABLE workout_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    type VARCHAR(50),
    difficulty VARCHAR(20),
    duration_weeks INTEGER,
    frequency INTEGER,
    estimated_duration INTEGER,
    status VARCHAR(20) DEFAULT 'INACTIVE',
    user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 运动库表
CREATE TABLE exercises (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    muscle_group VARCHAR(50),
    equipment VARCHAR(100),
    difficulty VARCHAR(20),
    instructions TEXT
);

-- 训练项目表
CREATE TABLE workout_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    workout_plan_id BIGINT,
    exercise_id BIGINT,
    sets INTEGER,
    reps VARCHAR(20),
    duration VARCHAR(20),
    rest_time VARCHAR(20),
    FOREIGN KEY (workout_plan_id) REFERENCES workout_plans(id),
    FOREIGN KEY (exercise_id) REFERENCES exercises(id)
);
```

### 5.2 数据库关系图

```
users (1) ──────── (N) workout_plans
                        │
                        │ (1)
                        │
                        ▼
                   (N) workout_items (N) ──────── (1) exercises
```

## 6. 安全设计

### 6.1 Spring Security配置

**配置类**: <mcsymbol name="SecurityConfig" filename="SecurityConfig.java" path="d:\code\FitAI Coach\src\main\java\com\fitai\config\SecurityConfig.java" startline="13" type="class"></mcsymbol>

**安全特性**:
- BCrypt密码加密
- 基于角色的访问控制
- CSRF保护
- 会话管理

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/home", "/frontend/**", "/static/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions().sameOrigin());
        return http.build();
    }
}
```

## 7. AI服务集成

### 7.1 AI服务架构

**服务类**: <mcsymbol name="AIService" filename="AIService.java" path="d:\code\FitAI Coach\src\main\java\com\fitai\service\AIService.java" startline="15" type="class"></mcsymbol>

**功能模块**:
- 个性化训练建议生成
- 营养计划推荐
- 训练数据分析
- 智能问答系统

**实现方式**:
```java
@Service
public class AIService {
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private AIConfig aiConfig;
    
    public String generateWorkoutAdvice(User user, WorkoutPlan plan) {
        // 调用外部AI API生成建议
        String prompt = buildWorkoutPrompt(user, plan);
        return callAIAPI(prompt);
    }
    
    public String generateNutritionAdvice(User user) {
        // 生成营养建议
        String prompt = buildNutritionPrompt(user);
        return callAIAPI(prompt);
    }
}
```

## 8. 性能优化设计

### 8.1 缓存策略
- Spring Cache注解缓存常用数据
- 运动库数据缓存
- 用户会话缓存

### 8.2 数据库优化
- 合理的索引设计
- 分页查询优化
- 连接池配置

### 8.3 前端优化
- 静态资源压缩
- 异步加载
- 响应式设计

## 9. 扩展性设计

### 9.1 微服务架构准备
- 模块化设计
- 接口标准化
- 配置外部化

### 9.2 插件化扩展
- 策略模式支持新训练类型
- 观察者模式支持新监控功能
- 工厂模式支持新计划类型

### 9.3 多租户支持
- 数据隔离设计
- 配置个性化
- 权限细粒度控制

## 10. 技术栈总结

- **后端框架**: Spring Boot 2.x
- **数据访问**: Spring Data JPA
- **安全框架**: Spring Security
- **模板引擎**: Thymeleaf
- **数据库**: MySQL/H2
- **前端技术**: HTML5, CSS3, JavaScript
- **构建工具**: Maven
- **设计模式**: Factory, Strategy, Observer, MVC, Repository

该设计文档展示了FitAI Coach系统的完整架构设计，通过多种设计模式的合理运用，实现了高内聚、低耦合的系统架构，为后续的功能扩展和维护提供了良好的基础。
        