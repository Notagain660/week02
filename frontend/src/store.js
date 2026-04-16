import { defineStore } from 'pinia'
import {checkme, login, register, updateAvatar} from "@/api/user.js";

export const useStore = defineStore('main', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        currentUser: null
    }),
    actions: {
        // 登录成功时调用，保存 token 和用户信息
        async login(account, password) {
            const res = await login({ account, password })
            if (res.code === 200) {
                const { token, user } = res.data   // 假设后端返回 { token, user }
                this.token = token
                this.currentUser = user
                localStorage.setItem('token', token)
                return true
            }
            return false
        },
        // 退出登录
        logout() {
            this.token = ''
            this.currentUser = null
            localStorage.removeItem('token')
        },

        async register(userData) {
            const res = await register(userData)   // 调用 API
            return res.code === 201;
        },
        async fetchUserInfo() {
            const res = await checkme()
            if (res.code === 200) {
                this.currentUser = res.data
            }
            return res
        },
        async updateAvatar(file) {
            const res = await updateAvatar(file)
            if (res.code === 200) {
                await this.fetchUserInfo()
                return true
            }
            return false
        }
    }
})