import request from '@/utils/request';

export const addComment = (postId, replyId, text) =>
    request.post(`/comment/${postId}/${replyId}`, null, { params: { text } });
export const deleteComment = (commentId) => request.put(`/comment/${commentId}`);
export const getCommentList = (postId, page, size) =>
    request.get(`/comment/list/${postId}`, { params: { page, size } })