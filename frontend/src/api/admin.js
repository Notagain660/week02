import request from '@/utils/request';

export const getActiveUsers = (start, end) =>
    request.post('/admin/statistics/active', null, { params: { start, end } });
export const getTotalPosts = () => request.get('/admin/statistics/post');
export const getTotalFound = () => request.get('/admin/statistics/found');
export const getItemStatistics = () => request.get('/admin/statistics/item');
export const getPlaceStatistics = () => request.get('/admin/statistics/place');
export const pinPost = (postId) => request.put(`/admin/pin/${postId}`);
export const releasePin = (postId) => request.put(`/admin/relasepin/${postId}`);
export const adminDelete = (type, id) => request.put(`/admin/delete/${type}/${id}`);
export const blockContent = (type, contentId) => request.put(`/report/${type}/${contentId}`);
export const getReportList = () => request.get('/report/list');  // 后端需实现，这里略