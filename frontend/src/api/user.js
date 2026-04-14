import request from '@/utils/request';


export const register = (data) => request.post('/register', data);
export const login = (data) => request.post('/login', data);
export const getMe = () => request.get('/user/checkme');
export const updateName = (name) => request.put('/user/change/name', null, { params: { name } });
export const updateAvatar = (formData) => request.put('/user/change/avatar', formData);
export const updatePassword = (data) => request.put('/user/change/password', data);
export const updatePhone = (data) => request.put('/user/change/phone', data);
export const updateEmail = (data) => request.put('/user/change/email', data);
export const getOtherProfile = (userId) => request.get(`/user/${userId}`);