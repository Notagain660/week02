import request from '@/utils/request';

export const uploadPostImage = (file) => {
    const fd = new FormData();
    fd.append('image', file);
    return request.post('/post/image', fd);
};
export const createPost = (data) => request.post('/post/send', data);
export const getPostList = (params) => request.post('/post', null, { params });
export const getPostDetail = (postId) => request.get(`/post/${postId}`);
export const updatePost = (data) => request.post('/post/update', data);
export const deletePost = (postId) => request.put(`/post/delete/${postId}`);