// FitAI Coach 主要JavaScript文件

// DOM加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    console.log('FitAI Coach 应用已加载');
    
    // 初始化应用
    initializeApp();
});

// 应用初始化
function initializeApp() {
    // 绑定事件监听器
    bindEventListeners();
    
    // 检查用户登录状态
    checkUserSession();
    
    // 初始化导航
    initializeNavigation();
}

// 绑定事件监听器
function bindEventListeners() {
    // CTA按钮点击事件
    const ctaButton = document.querySelector('.cta-button');
    if (ctaButton) {
        ctaButton.addEventListener('click', function() {
            // 跳转到注册或登录页面
            window.location.href = '/dashboard';
        });
    }
    
    // 导航菜单点击事件
    const navLinks = document.querySelectorAll('.nav-menu a');
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            // 添加活动状态
            navLinks.forEach(l => l.classList.remove('active'));
            this.classList.add('active');
        });
    });
}

// 检查用户会话
function checkUserSession() {
    // 检查本地存储中的用户信息
    const userSession = localStorage.getItem('fitai_user_session');
    if (userSession) {
        try {
            const userData = JSON.parse(userSession);
            console.log('用户已登录:', userData.username);
            // 更新UI显示用户信息
            updateUserInterface(userData);
        } catch (error) {
            console.error('解析用户会话数据失败:', error);
            localStorage.removeItem('fitai_user_session');
        }
    }
}

// 初始化导航
function initializeNavigation() {
    // 高亮当前页面的导航项
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-menu a');
    
    navLinks.forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        }
    });
}

// 更新用户界面
function updateUserInterface(userData) {
    // 如果用户已登录，可以显示个性化内容
    const heroMessage = document.querySelector('.hero-content h2');
    if (heroMessage && userData.username) {
        heroMessage.textContent = `欢迎回来，${userData.username}！`;
    }
}

// 工具函数：显示通知
function showNotification(message, type = 'info') {
    // 创建通知元素
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    
    // 添加样式
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 1rem 1.5rem;
        border-radius: 5px;
        color: white;
        font-weight: 500;
        z-index: 1000;
        animation: slideIn 0.3s ease;
    `;
    
    // 根据类型设置背景色
    switch (type) {
        case 'success':
            notification.style.backgroundColor = '#4CAF50';
            break;
        case 'error':
            notification.style.backgroundColor = '#f44336';
            break;
        case 'warning':
            notification.style.backgroundColor = '#ff9800';
            break;
        default:
            notification.style.backgroundColor = '#2196F3';
    }
    
    // 添加到页面
    document.body.appendChild(notification);
    
    // 3秒后自动移除
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// 添加CSS动画
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
    
    .nav-menu a.active {
        background-color: rgba(255, 255, 255, 0.2);
        border-radius: 5px;
        padding: 0.5rem 1rem;
    }
`;
document.head.appendChild(style);

// 导出函数供其他模块使用
window.FitAI = {
    showNotification,
    checkUserSession,
    updateUserInterface
};