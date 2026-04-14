import { defineStore } from 'pinia';
import { login as loginApi, getMe } from '@/api/user';

export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        userInfo: null,
    }),
    actions: {
        async login(account, password) {
            const res = await loginApi({ account, password });
            if (res.code === 200) {
                this.token = res.data.token;
                this.userInfo = res.data.user;
                console.log('登录后 userInfo.status:', this.userInfo.status);  // 添加这一行
                localStorage.setItem('token', this.token);
                return true;
            }
            return false;
        },
        async fetchUserInfo() {
            if (!this.token) return;
            const res = await getMe();
            if (res.code === 200) {
                this.userInfo = res.data;
            }
        },
        logout() {
            this.token = '';
            this.userInfo = null;
            localStorage.removeItem('token');
        },
    },
});