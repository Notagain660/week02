import { createRouter, createWebHistory } from 'vue-router'


const routes = [
    {
        path: '/login', name: 'Login', component: () => import('@/view/user.vue')   // 路径根据你实际文件位置调整
    },{
        path: '/', name: 'Home', component: () => import('@/view/PostList.vue')   // 你的帖子列表组件
    },
    { path: '/register', name: 'Register', component: () => import('@/view/Register.vue') },
    { path: '/user/checkme', name: 'CheckMe', component: () => import('@/view/CheckMe.vue') }
    // 后续可以添加其他路由，如登录、注册、首页等
]

const router = createRouter({
    history: createWebHistory(),  // 使用浏览器历史模式
    routes,
})

export default router
