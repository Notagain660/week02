export enum Role {
  STUDENT = 0,
  ADMIN = 1
}

export enum UserStatus {
  NORMAL = 0,
  BANNED = 1
}

export enum Type {
  LOST = 0,
  FOUND = 1
}

export enum PostStatus {
  OPEN = 0,
  COMPLETED = 1
}

export enum Visibility {
  PUBLIC = 0,
  PRIVATE = 1
}

export enum OurStatus {
  STRANGERS = 0,
  FRIENDS = 1,
  PENDING = 2
}

export enum CommentStatus {
  NORMAL = 0,
  DELETED = 1
}

export enum ReportType {
  POST = 0,
  COMMENT = 1,
  USER = 2
}

export enum ReportStatus {
  PENDING = 0,
  APPROVED = 1,
  REJECTED = 2
}

export interface User {
  userId: string;
  role: Role;
  userName: string;
  userEmail: string;
  userPhone: string;
  userPostNum: number;
  userAvatar: string;
  status: UserStatus;
}

export interface Post {
  postId: string;
  posterId: string;
  type: Type;
  contact: string;
  visible: Visibility;
  postStatus: PostStatus;
  itemName: string;
  itemPlace: string;
  itemTime: string;
  userDescription: string;
  aiDescription: string;
  itemPhoto: string;
  pinOrNot: boolean;
  postTime: string;
}

export interface PostDTO {
  postId: string;
  type: Type;
  posterId: string;
  postStatus: PostStatus;
  itemName: string;
  itemPlace: string;
  posterName?: string;
  posterAvatar?: string;
  pinOrNot?: boolean;
  postTime?: string;
  userDescription?: string;
}

export interface Comment {
  batchco: string;
  floor: number;
  commenterId: string;
  replyId: string;
  postId: string;
  commentText: string;
  commentStatus: CommentStatus;
  replyTime: string;
}

export interface CommentVO extends Comment {
  commenterName: string;
  commenterAvatar: string;
}

export interface Relation {
  myId: string;
  itsId: string;
  ourStatus: OurStatus;
  requestTime: string;
  okTime: string;
}

export interface Chat {
  chatId: string;
  senderId: string;
  receiverId: string;
  content: string;
  sendTime: string;
  isRead: boolean;
}

export interface Report {
  reportId: string;
  reporterId: string;
  type: ReportType;
  contentId: string;
  reason: string;
  reportStatus: ReportStatus;
  reportTime: string;
}

export interface OtherInfo {
  userId: string;
  userName: string;
  userAvatar: string;
  userPostNum: number;
  status: UserStatus;
  ourStatus: OurStatus;
}

export interface OtherInfoResponse {
  avatar: string;
  nickname: string;
  status: UserStatus;
  postNum: number;
}

export interface LoginReturnData {
  token: string;
  user: User;
}

export interface SecurityDTO {
  oldPassword?: string;
  newPassword?: string;
  newPhone?: string;
  newEmail?: string;
}

export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
}