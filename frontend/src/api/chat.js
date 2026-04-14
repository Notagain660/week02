import request from '@/utils/request';


export const sendChat = (userId, content) => request.post(`/chat/${userId}`, null, { params: { content } });
export const getChatsWithUser = (userId) => request.get('/chat/check', { params: { userId } });
export const markChatRead = (chatId) => request.put(`/chat/read/${chatId}`);
export const deleteChat = (chatId) => request.put(`/chat/delete/${chatId}`);