import { createRouter, createWebHistory } from 'vue-router';
import { useUserStore } from '@/stores/user';

const routes = [
    { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
    { path: '/register', name: 'Register', component: () => import('@/views/Register.vue') },
    {
        path: '/',
        component: () => import('@/layouts/MainLayout.vue'),
        meta: { requiresAuth: true },
        children: [
            { path: '', name: 'PostList', component: () => import('@/views/PostList.vue') },
            { path: 'profile', name: 'Profile', component: () => import('@/views/Profile.vue') },
            { path: 'profile/edit', name: 'ProfileEdit', component: () => import('@/views/ProfileEdit.vue') },
            { path: 'user/:userId', name: 'OtherProfile', component: () => import('@/views/OtherProfile.vue') },
            { path: 'post/:postId', name: 'PostDetail', component: () => import('@/views/PostDetail.vue') },
            { path: 'post/create', name: 'PostCreate', component: () => import('@/views/PostForm.vue') },
            { path: 'post/edit/:postId', name: 'PostEdit', component: () => import('@/views/PostForm.vue') },
            { path: 'chats', name: 'ChatList', component: () => import('@/views/ChatList.vue') },
            { path: 'chat/:userId', name: 'ChatRoom', component: () => import('@/views/ChatRoom.vue') },
            { path: 'friend-requests', name: 'FriendRequests', component: () => import('@/views/FriendRequests.vue') },
            { path: 'admin/stats', name: 'AdminStats', component: () => import('@/views/AdminStats.vue') },
        ],
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach((to, from, next) => {
    const store = useUserStore();
    if (to.meta.requiresAuth && !store.token) {
        next('/login');
    } else {
        next();
    }
});

export default router;