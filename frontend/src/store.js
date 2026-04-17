import { defineStore } from 'pinia'
import {checkme, login, register, updateAvatar, updateName} from "@/api/user.js";
import { updatePassword as apiUpdatePassword } from '@/api/user.js'
import { updatePhone as apiUpdatePhone } from '@/api/user.js'
import { updateEmail as apiUpdateEmail } from '@/api/user.js'

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
            const res = await register(userData)   // 调用 api
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
        },
        async updateName(newName) {
            const res = await updateName(newName)
            if (res.code === 200) {
                await this.fetchUserInfo()  // 刷新用户信息
                return true
            }
            return false
        },
        async updatePassword(data) {
            const res = await apiUpdatePassword(data)
            return res.code === 200;
        },
        async updatePhone(data) {
            const res = await apiUpdatePhone(data)
            if (res.code === 200) {
                await this.fetchUserInfo()   // 更新用户信息
                return true
            }
            return false
        },
        async updateEmail(data) {
            const res = await apiUpdateEmail(data)
            if (res.code === 200) {
                await this.fetchUserInfo()   // 更新用户信息
                return true
            }
            return false
        },

    }
})