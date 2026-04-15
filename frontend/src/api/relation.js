import request from '@/utils/request';


export const sendFriendRequest = (userId) => request.post('/relation/request', null, { params: { userId } });
export const getRelationsByStatus = (ourStatus) => request.get(`/relation/${ourStatus}`);
export const dealRelation = (userId, ourStatus, act) =>
    request.put(`/relation/${userId}/${ourStatus}/${act}`);