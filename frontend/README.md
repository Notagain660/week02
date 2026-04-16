# 校园AI失物招领平台 - 前端项目

基于React + TypeScript + Vite + Ant Design构建的校园失物招领平台前端应用。

## 技术栈

- **框架**: React 18 + TypeScript
- **构建工具**: Vite
- **UI组件库**: Ant Design 5
- **路由**: React Router 6
- **HTTP客户端**: Axios
- **状态管理**: React Context API
- **日期处理**: Day.js
- **图表**: @ant-design/charts

## 项目结构

```
frontend/
├── src/
│   ├── context/           # 上下文管理
│   │   └── AuthContext.tsx # 认证上下文
│   ├── pages/             # 页面组件
│   │   ├── Login.tsx      # 登录/注册页面
│   │   ├── Home.tsx       # 主页（帖子列表）
│   │   ├── CreatePost.tsx # 创建/编辑帖子
│   │   ├── PostDetail.tsx # 帖子详情
│   │   ├── UserProfile.tsx # 用户资料
│   │   ├── Messages.tsx   # 消息中心
│   │   └── Admin.tsx      # 管理员控制台
│   ├── services/          # API服务
│   │   └── api.ts         # 所有API调用
│   ├── types/             # TypeScript类型定义
│   │   └── index.ts       # 类型定义
│   ├── utils/             # 工具函数
│   │   └── api.ts         # Axios配置
│   ├── main.tsx           # 应用入口
│   └── index.css          # 全局样式
├── index.html             # HTML模板
├── package.json           # 项目依赖
├── tsconfig.json          # TypeScript配置
├── vite.config.ts         # Vite配置
└── .gitignore            # Git忽略文件
```

## 功能特性

### 用户功能
- ✅ 用户注册/登录
- ✅ 查看个人信息
- ✅ 修改个人信息（昵称、头像、密码、手机号、邮箱）
- ✅ 发布失物/拾物帖子
- ✅ 编辑/删除自己的帖子
- ✅ 浏览帖子列表（支持筛选、搜索）
- ✅ 查看帖子详情
- ✅ 发表/回复/删除评论
- ✅ 添加/删除好友
- ✅ 发送/接收消息
- ✅ 举报功能（帖子、评论、用户）

### 管理员功能
- ✅ 查看统计数据（总帖子数、已找回物品数、活跃用户数）
- ✅ 物品统计图表
- ✅ 地点统计图表
- ✅ 举报管理
- ✅ 帖子置顶/取消置顶
- ✅ 删除用户内容

## 安装和运行

### 前置要求
- Node.js 16+
- npm 或 yarn

### 安装依赖
```bash
cd frontend
npm install
```

### 开发模式
```bash
npm run dev
```
前端将运行在 http://localhost:5173

### 构建生产版本
```bash
npm run build
```

### 预览生产版本
```bash
npm run preview
```

## API配置

前端默认配置为与后端API通信：
- 后端地址: http://localhost:8080
- API路径: /api

如需修改，请编辑 `vite.config.ts` 中的proxy配置。

## 主要页面路由

- `/login` - 登录/注册
- `/` - 主页（帖子列表）
- `/post/create` - 创建帖子
- `/post/edit/:postId` - 编辑帖子
- `/post/:postId` - 帖子详情
- `/user/:userId?` - 用户资料
- `/messages` - 消息中心
- `/chat/:userId` - 聊天界面
- `/admin` - 管理员控制台

## 注意事项

1. **后端依赖**: 本项目需要后端服务运行在 http://localhost:8080
2. **认证**: 使用JWT进行身份认证，token存储在localStorage中
3. **图片上传**: 支持上传头像和物品照片
4. **AI功能**: 集成了AI描述生成功能
5. **权限控制**: 普通用户和管理员有不同的权限

## 开发说明

- 所有API调用都在 `src/services/api.ts` 中统一管理
- TypeScript类型定义在 `src/types/index.ts` 中
- 使用React Context进行全局状态管理
- 使用Ant Design组件库构建UI
- 遵循React Hooks最佳实践

## 浏览器支持

- Chrome (推荐)
- Firefox
- Safari
- Edge

## 许可证

MIT