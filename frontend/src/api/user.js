import api from "@/api/api";

export const login = (data) => api.post('/login', data)
export const register = (data) => api.post('/register', data)
export const checkme = () => api.get('/user/checkme')