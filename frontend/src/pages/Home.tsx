import React, { useState, useEffect } from 'react';
import { Input, Select, Card, List, Tag, Button, Space, Pagination, message, Row, Col, Avatar, Empty } from 'antd';
import { SearchOutlined, PlusOutlined, MessageOutlined, BellOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { postService, authService } from '../services/api';
import { PostDTO, Type, PostStatus, OtherInfo } from '../types';
import { useAuth } from '../context/AuthContext';
import dayjs from 'dayjs';

const { Option } = Select;
const { Search } = Input;

const Home: React.FC = () => {
  const [posts, setPosts] = useState<PostDTO[]>([]);
  const [userInfos, setUserInfos] = useState<Map<string, OtherInfo>>(new Map());
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [total, setTotal] = useState(0);
  const [filters, setFilters] = useState({
    type: undefined as number | undefined,
    status: undefined as number | undefined,
    itemName: '',
    itemPlace: ''
  });
  const navigate = useNavigate();
  const { logout } = useAuth();

  const fetchPosts = async (page: number =1) => {
    setLoading(true);
    try {
      const response = await postService.listPosts({
        pageCode: page,
        size: 10,
        ...filters
      });
      if (response.code === 200) {
        const posts = response.data.records;
        setPosts(posts);
        
        const userIds = [...new Set(posts.map(post => post.posterId))];
        const userInfoPromises = userIds.map(async (userId) => {
          try {
            const userResponse = await authService.getOtherProfile(userId);
            if (userResponse.code === 200) {
              const userData = userResponse.data as any;
              return {
                userId,
                info: {
                  userId,
                  userName: userData.nickname,
                  userAvatar: userData.avatar,
                  userPostNum: userData.postNum,
                  status: userData.status,
                  ourStatus: 0
                }
              };
            }
          } catch (error) {
            console.error('获取用户信息失败:', userId, error);
          }
          return null;
        });
        
        const userInfoResults = await Promise.all(userInfoPromises);
        const newUserInfoMap = new Map<string, OtherInfo>();
        userInfoResults.forEach(result => {
          if (result) {
            newUserInfoMap.set(result.userId, result.info);
          }
        });
        setUserInfos(newUserInfoMap);
        
        setTotal(response.data.total);
        setCurrentPage(page);
      }
    } catch (error) {
      message.error('获取帖子列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const loadPosts = async () => {
      await fetchPosts();
    };
    loadPosts().catch(console.error);
  }, [filters]);

  const handleSearch = (value: string) => {
    setFilters(prev => ({ ...prev, itemName: value }));
    setCurrentPage(1);
  };

  const handleTypeChange = (value: number | undefined) => {
    setFilters(prev => ({ ...prev, type: value }));
    setCurrentPage(1);
  };

  const handleStatusChange = (value: number | undefined) => {
    setFilters(prev => ({ ...prev, status: value }));
    setCurrentPage(1);
  };

  const handlePostClick = (postId: string) => {
    navigate(`/post/${postId}`);
  };

  const handleCreatePost = () => {
    navigate('/post/create');
  };

  const handleUserClick = (userId: string) => {
    navigate(`/user/${userId}`);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
      <div style={{ marginBottom: '20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1>校园AI失物招领平台</h1>
        <Space>
          <Button 
            icon={<MessageOutlined />} 
            onClick={() => navigate('/messages')}
          >
            消息
          </Button>
          <Button 
            icon={<BellOutlined />} 
            onClick={() => navigate('/notifications')}
          >
            通知
          </Button>
          <Button 
            type="primary" 
            icon={<PlusOutlined />} 
            onClick={handleCreatePost}
          >
            发布帖子
          </Button>
          <Button 
            onClick={handleLogout}
          >
            退出登录
          </Button>
        </Space>
      </div>

      <Card style={{ marginBottom: '20px' }}>
        <Space size="large" style={{ width: '100%' }}>
          <Search
            placeholder="搜索帖子ID、评论ID、用户ID、物品名称、地点"
            allowClear
            enterButton={<SearchOutlined />}
            size="large"
            onSearch={handleSearch}
            style={{ flex: 1 }}
          />
          <Select
            placeholder="物品类型"
            style={{ width: 120 }}
            allowClear
            onChange={handleTypeChange}
          >
            <Option value={Type.LOST}>丢失</Option>
            <Option value={Type.FOUND}>拾取</Option>
          </Select>
          <Select
            placeholder="状态"
            style={{ width: 120 }}
            allowClear
            onChange={handleStatusChange}
          >
            <Option value={PostStatus.OPEN}>进行中</Option>
            <Option value={PostStatus.COMPLETED}>已完成</Option>
          </Select>
        </Space>
      </Card>

      <List
        loading={loading}
        dataSource={posts}
        renderItem={(post) => (
          <Card 
            key={post.postId} 
            style={{ marginBottom: '16px', cursor: 'pointer' }}
            hoverable
            onClick={() => handlePostClick(post.postId)}
          >
            <Row gutter={16}>
              <Col span={2}>
                <Avatar 
                  src={userInfos.get(post.posterId)?.userAvatar} 
                  size={48}
                  onClick={(e) => {
                    e?.stopPropagation();
                    handleUserClick(post.posterId);
                  }}
                />
              </Col>
              <Col span={22}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <div style={{ flex: 1 }}>
                    <Space>
                      <span style={{ fontWeight: 'bold', fontSize: '16px' }}>{userInfos.get(post.posterId)?.userName || '用户'}</span>
                      <Tag color={post.type === Type.LOST ? 'red' : 'green'}>
                        {post.type === Type.LOST ? '丢失' : '拾取'}
                      </Tag>
                      {post.pinOrNot && <Tag color="gold">置顶</Tag>}
                      <Tag color={post.postStatus === PostStatus.OPEN ? 'blue' : 'default'}>
                        {post.postStatus === PostStatus.OPEN ? '进行中' : '已完成'}
                      </Tag>
                    </Space>
                    <h3 style={{ margin: '8px 0' }}>{post.itemName}</h3>
                    <p style={{ color: '#666', margin: '4px 0' }}>
                      地点: {post.itemPlace} | 时间: {dayjs(post.itemTime).format('YYYY-MM-DD HH:mm')}
                    </p>
                    <p style={{ color: '#888', margin: '4px 0' }}>{post.userDescription || '暂无描述'}</p>
                    <p style={{ color: '#999', fontSize: '12px', margin: '4px 0' }}>
                      发布时间: {post.postTime ? dayjs(post.postTime).format('YYYY-MM-DD HH:mm') : '未知'}
                    </p>
                  </div>
                  {post.itemPhoto && (
                    <img 
                      src={post.itemPhoto} 
                      alt={post.itemName}
                      style={{ width: '100px', height: '100px', objectFit: 'cover', borderRadius: '4px' }}
                    />
                  )}
                </div>
              </Col>
            </Row>
          </Card>
        )}
      />

      {posts.length === 0 && !loading && (
        <Empty description="暂无帖子" />
      )}

      <div style={{ marginTop: '20px', textAlign: 'center' }}>
        <Pagination
          current={currentPage}
          total={total}
          pageSize={10}
          onChange={fetchPosts}
          showSizeChanger={false}
        />
      </div>
    </div>
  );
};

export default Home;