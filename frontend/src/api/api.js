import axios from 'axios';
import { ElMessage } from 'element-plus';

const api = axios.create({
    baseURL: '/api',
    timeout: 15000,
});

// 请求拦截器
api.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config;
});

// 响应拦截器
api.interceptors.response.use(
    response => {
        const res = response.data;
        if (res.code !== 200 && res.code !== 201) {
            ElMessage.error(res.message || '请求失败');
            if (res.code === 401) {
                import('@/store').then(({ useStore }) => {
                const store = useStore();
                store.logout();
                }).catch(() => {})
                // 跳转登录页
            }
            return Promise.reject(new Error(res.message));
        }
        return res; // 直接返回 {code, message, data}
    },
    error => {
        ElMessage.error(error.message || '网络错误');
        return Promise.reject(error);
    }
);

export default api;