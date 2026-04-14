import request from '@/utils/request';

export const addReport = (data) =>
    request.post('/report', data);