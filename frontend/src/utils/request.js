import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const request = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 15000,
})

request.interceptors.request.use(
    (config) => {
        const store = useUserStore()
        if (store.token) {
            config.headers.Authorization = `Bearer ${store.token}`
        }
        return config
    },
    (error) => Promise.reject(error)
)

request.interceptors.response.use(
    (response) => {
        const res = response.data
        if (res.code !== 200 && res.code !== 201) {
            ElMessage.error(res.message || '请求失败')
            if (res.code === 401) {
                const store = useUserStore()
                store.logout()
                window.location.href = '/login'
            }
            return Promise.reject(new Error(res.message || 'Error'))
        }
        return res
    },
    (error) => {
        ElMessage.error(error.message || '网络错误')
        return Promise.reject(error)
    }
)

export default request