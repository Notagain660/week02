import api from "@/api/api";

export const login = (data) => api.post('/login', data)
export const register = (data) => api.post('/register', data)
export const checkme = () => api.get('/user/checkme')
export const updateAvatar = (file) => {
    const formData = new FormData()
    formData.append('image', file)
    return api.put('/user/change/avatar', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    })}
export const updateName = (name) => api.put('/user/change/name', null, { params: { name } })