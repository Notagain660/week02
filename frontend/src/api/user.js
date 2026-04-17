import api from "@/api/api.js";

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
export const updatePassword = (data) => api.put('/user/change/password', data)
export const updatePhone = (data) => api.put('/user/change/phone', data)
export const updateEmail = (data) => api.put('/user/change/email', data)

export const getRelation = (ourStatus) => api.get(`/relation/${ourStatus}`)
export const dealRelation = (userId, ourStatus, act) =>
    api.put(`/relation/${userId}/${ourStatus}/${act}`)
export const getOtherProfile = (userId) => api.get(`/user/${userId}`)
export const sendFriendRequest = (userId) => api.post('/relation/request', null, { params: { userId } })

export const getChatsWithUser = (userId) => api.get('/chat/check', { params: { userId } })
export const markChatRead = (chatId) => api.put(`/chat/read/${chatId}`)
export const deleteChat = (chatId) => api.put(`/chat/delete/${chatId}`)
export const sendChat = (userId, content) => api.post(`/chat/${userId}`, null, { params: { content } })

export const getPostList = (params) => api.get('/post', { params })
export const getPostDetail = (postId) => api.get(`/post/${postId}`)
export const createPost = (data) => api.post('/post/send', data)
export const updatePost = (data) => api.post('/post/update', data)
export const uploadPostImage = (formData) => api.post('/post/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
})
export const deletePost = (postId) => api.put(`/post/delete/${postId}`)

export const getCommentList = (postId, page, size) => api.get(`/comment/list/${postId}`, { params: { page, size } })
export const addComment = (postId, replyId, text) =>
    api.post(`/comment/${postId}/${replyId}`, null, { params: { text } })
export const deleteComment = (commentId) => api.put(`/comment/${commentId}`)

export const getReportList = () => api.get('/report/check')
export const addReport = (data) => api.post('/report', data)
export const dealReport = (reportId, status) => api.put(`/report/deal/${reportId}`, null, { params: { status } })
export const blockContent = (type, contentId) => api.put(`/report/${type}/${contentId}`)

export const getTotalPosts = () => api.get('/admin/statistics/post')
export const getTotalFound = () => api.get('/admin/statistics/found')
export const getItemStatistics = () => api.get('/admin/statistics/item')
export const getPlaceStatistics = () => api.get('/admin/statistics/place')
export const getActiveUsers = (start, end) => api.get('/admin/statistics/active', { params: { start, end } })
export const adminDelete = (type, id) => api.put(`/admin/delete/${type}/${id}`)
export const adminCheckUser = (userId) => api.get(`/user/check/${userId}`)
export const pinPost = (postId) => api.put(`/admin/pin/${postId}`)
export const releasePin = (postId) => api.put(`/admin/relasepin/${postId}`)