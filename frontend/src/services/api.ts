import api from '../utils/api';
import { ApiResponse, User, Post, PostDTO, CommentVO, Relation, Chat, Report, OtherInfo, LoginReturnData, SecurityDTO } from '../types';

export const authService = {
  login: (account: string, password: string): Promise<ApiResponse<LoginReturnData>> => {
    return api.post('/login', { account, password });
  },

  register: (user: { userName?: string; userEmail: string; userPhone: string; password: string }): Promise<ApiResponse> => {
    return api.post('/register', user);
  },

  checkme: (): Promise<ApiResponse<User>> => {
    return api.get('/user/checkme');
  },

  changeName: (name: string): Promise<ApiResponse> => {
    return api.put('/user/change/name', null, { params: { name } });
  },

  changeAvatar: (file: File): Promise<ApiResponse> => {
    const formData = new FormData();
    formData.append('image', file);
    return api.put('/user/change/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  },

  changePassword: (data: SecurityDTO): Promise<ApiResponse> => {
    return api.put('/user/change/password', data);
  },

  changePhone: (data: SecurityDTO): Promise<ApiResponse> => {
    return api.put('/user/change/phone', data);
  },

  changeEmail: (data: SecurityDTO): Promise<ApiResponse> => {
    return api.put('/user/change/email', data);
  },

  getOtherProfile: (userId: string): Promise<ApiResponse<any>> => {
    return api.get(`/user/${userId}`);
  },

  checkUser: (userId: string): Promise<ApiResponse<User>> => {
    return api.get(`/user/check/${userId}`);
  }
};

export const relationService = {
  getRelations: (ourStatus: number): Promise<ApiResponse<Relation[]>> => {
    return api.get(`/relation/${ourStatus}`);
  },

  requestFriend: (userId: string): Promise<ApiResponse> => {
    return api.post('/relation/request', null, { params: { userId } });
  },

  dealRelation: (userId: string, ourStatus: number, act: number): Promise<ApiResponse> => {
    return api.put(`/relation/${userId}/${ourStatus}/${act}`);
  }
};

export const chatService = {
  sendChat: (userId: string, content: string): Promise<ApiResponse> => {
    return api.post(`/chat/${userId}`, null, { params: { content } });
  },

  checkChats: (userId: string): Promise<ApiResponse<Chat[]>> => {
    return api.get('/chat/check', { params: { userId } });
  },

  readChat: (chatId: string): Promise<ApiResponse> => {
    return api.put(`/chat/read/${chatId}`);
  },

  deleteChat: (chatId: string): Promise<ApiResponse> => {
    return api.put(`/chat/delete/${chatId}`);
  }
};

export const postService = {
  postImage: (file?: File): Promise<ApiResponse<string>> => {
    const formData = new FormData();
    if (file) {
      formData.append('image', file);
    }
    return api.post('/post/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  },

  createPost: (post: Partial<Post>): Promise<ApiResponse> => {
    return api.post('/post/send', post);
  },

  listPosts: (params: {
    pageCode?: number;
    size?: number;
    type?: number;
    status?: number;
    itemName?: string;
    itemPlace?: string;
  }): Promise<ApiResponse<{ records: PostDTO[]; total: number }>> => {
    return api.get('/post', { params });
  },

  updatePost: (post: Partial<Post>): Promise<ApiResponse> => {
    return api.put('/post/update', post);
  },

  checkPost: (postId: string): Promise<ApiResponse<Post>> => {
    return api.get(`/post/${postId}`);
  },

  deletePost: (postId: string): Promise<ApiResponse> => {
    return api.put(`/post/delete/${postId}`);
  }
};

export const commentService = {
  sendComment: (replyId: string, postId: string, text: string): Promise<ApiResponse> => {
    return api.post(`/comment/${postId}/${replyId}`, null, { params: { text } });
  },

  deleteComment: (commentId: string): Promise<ApiResponse> => {
    return api.put(`/comment/${commentId}`);
  },

  getComments: (postId: string, page: number = 1, size: number = 10): Promise<ApiResponse<{ records: CommentVO[]; total: number }>> => {
    return api.get(`/comment/list/${postId}`, { params: { page, size } });
  }
};

export const reportService = {
  report: (report: Partial<Report>): Promise<ApiResponse> => {
    return api.post('/report', report);
  },

  dealReport: (reportId: string, status: number): Promise<ApiResponse> => {
    return api.put(`/report/deal/${reportId}`, null, { params: { status } });
  },

  checkReports: (): Promise<ApiResponse<Report[]>> => {
    return api.get('/report/check');
  },

  block: (type: number, contentid: string): Promise<ApiResponse> => {
    return api.put(`/report/${type}/${contentid}`);
  }
};

export const adminService = {
  pinPost: (postId: string): Promise<ApiResponse> => {
    return api.put(`/admin/pin/${postId}`);
  },

  releasePin: (postId: string): Promise<ApiResponse> => {
    return api.put(`/admin/relasepin/${postId}`);
  },

  deleteAdmin: (type: number, id: string): Promise<ApiResponse> => {
    return api.put(`/admin/delete/${type}/${id}`);
  },

  getActiveUsers: (start: string, end: string): Promise<ApiResponse<number>> => {
    return api.get('/admin/statistics/active', { params: { start, end } });
  },

  getTotalPosts: (): Promise<ApiResponse<number>> => {
    return api.get('/admin/statistics/post');
  },

  getTotalFound: (): Promise<ApiResponse<number>> => {
    return api.get('/admin/statistics/found');
  },

  getItemStatistics: (): Promise<ApiResponse<Map<string, string>>> => {
    return api.get('/admin/statistics/item');
  },

  getPlaceStatistics: (): Promise<ApiResponse<Map<string, string>>> => {
    return api.get('/admin/statistics/place');
  }
};