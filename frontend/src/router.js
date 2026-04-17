import { createRouter, createWebHistory } from 'vue-router'


const routes = [
    {
        path: '/login', name: 'Login', component: () => import('@/view/user.vue')   // 路径根据你实际文件位置调整
    },{
        path: '/', name: 'Home', component: () => import('@/view/PostList.vue')   // 你的帖子列表组件
    },
    { path: '/register', name: 'Register', component: () => import('@/view/Register.vue') },
    { path: '/user/checkme', name: 'CheckMe', component: () => import('@/view/CheckMe.vue') },
    { path: '/user/change/password', name: 'PasswordEdit',
        component: () => import('@/view/PasswordUpdater.vue')
    },
    {
        path: '/user/change/phone', name: 'ChangePhone',component: () => import('@/view/PhoneUpdater.vue')
    },
    { path: '/user/change/email', name: 'ChangeEmail',component:()=> import('@/view/EmailUpdater.vue')},
    {
        path: '/friend-requests',      // 前端页面路径
        name: 'FriendRequests',
        component: () => import('@/view/Relation.vue')
    },
    {
        path: '/friends',
        name: 'FriendsList',
        component: () => import('@/view/Friend.vue')
    },
    {
        path: '/user/:userId',
        name: 'OtherProfile',
        component: () => import('@/view/Others.vue')
    },
    {
        path: '/chat/:userId',
        name: 'ChatRoom',
        component: () => import('@/view/ChatRoom.vue')
    },
    {
        path: '/posts',
        name: 'PostList',
        component: () => import('@/view/PostList.vue')
    },
    {
        path: '/post/:postId',
        name: 'PostDetail',
        component: () => import('@/view/PostDetails.vue')
    },
    {
        path: '/post/create',
        name: 'PostCreate',
        component: () => import('@/view/PostForm.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/post/edit/:postId',
        name: 'PostEdit',
        component: () => import('@/components/EditPost.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/report/check',
        name: 'ReportList',
        component: () => import('@/view/ReportList.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/admin/stats',
        name: 'AdminStats',
        component: () => import('@/view/AdminStats.vue'),
        meta: { requiresAuth: true, admin: true }  // 可选，用于权限控制
    },
    {
        path: '/admin/user/:userId',
        name: 'UserInfo',
        component: () => import('@/components/AdminUserDetail.vue'),
        meta: { requiresAuth: true, admin: true }
    }


    // 后续可以添加其他路由，如登录、注册、首页等
]

const router = createRouter({
    history: createWebHistory(),  // 使用浏览器历史模式
    routes,
})

export default router
