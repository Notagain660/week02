import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import { createPinia } from 'pinia'
import router from '@/router.js'
import ElementPlus from 'element-plus'
import {useStore} from "@/store.js";

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ElementPlus)

const store = useStore()
if (store.token) {
    store.fetchUserInfo().catch(() => {
        // token 无效，清除并跳转登录
        store.logout()
        router.push('/login')
    })
}

app.mount('#app')