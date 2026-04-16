import React, { useState, useEffect } from 'react';
import { Card, Avatar, Tabs, Button, Modal, Form, Input, Upload, message, Descriptions, Tag, List, Space } from 'antd';
import { EditOutlined, CameraOutlined, LockOutlined, UserOutlined } from '@ant-design/icons';
import { useParams, useNavigate } from 'react-router-dom';
import { authService, relationService } from '../services/api';
import { OtherInfo, PostDTO, OurStatus, UserStatus } from '../types';
import { useAuth } from '../context/AuthContext';
import dayjs from 'dayjs';

const { TabPane } = Tabs;

const UserProfile: React.FC = () => {
  const [userInfo, setUserInfo] = useState<OtherInfo | null>(null);
  const [posts] = useState<PostDTO[]>([]);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [securityModalVisible, setSecurityModalVisible] = useState(false);
  const [editForm] = Form.useForm();
  const [securityForm] = Form.useForm();
  const [uploading, setUploading] = useState(false);
  const { userId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  const isOwnProfile = !userId || userId === user?.userId;

  useEffect(() => {
    const loadProfile = async () => {
      if (isOwnProfile) {
        await fetchOwnProfile();
      } else {
        await fetchOtherProfile();
      }
    };
    loadProfile().catch(console.error);
  }, [userId, user]);

  const fetchOwnProfile = async () => {
    try {
      const response = await authService.checkme();
      if (response.code === 200) {
        setUserInfo({
          userId: response.data.userId,
          userName: response.data.userName,
          userAvatar: response.data.userAvatar,
          userPostNum: response.data.userPostNum,
          status: response.data.status,
          ourStatus: OurStatus.FRIENDS
        });
        editForm.setFieldsValue({ userName: response.data.userName });
      }
    } catch (error) {
      message.error('获取个人信息失败');
    }
  };

  const fetchOtherProfile = async () => {
    try {
      const response = await authService.getOtherProfile(userId!);
      if (response.code === 200) {
        const userData = response.data as any;
        setUserInfo({
          userId: userId!,
          userName: userData.nickname,
          userAvatar: userData.avatar,
          userPostNum: userData.postNum,
          status: userData.status,
          ourStatus: OurStatus.STRANGERS
        });
      }
    } catch (error) {
      message.error('获取用户信息失败');
    }
  };

  // 暂时未使用的函数
  // const fetchUserPosts = async () => {
  //   try {
  //     const response = await postService.listPosts({
  //       pageCode: 1,
  //       size: 10
  //     });
  //     if (response.code === 200) {
  //       const userPosts = response.data.records.filter(post => post.posterId === (isOwnProfile ? user?.userId : userId));
  //       setPosts(userPosts);
  //     }
  //   } catch (error) {
  //     message.error('获取帖子失败');
  //   }
  // };

  const handleUpdateName = async (values: any) => {
    try {
      await authService.changeName(values.userName);
      message.success('昵称修改成功');
      setEditModalVisible(false);
      if (isOwnProfile) {
        await fetchOwnProfile();
      }
    } catch (error) {
      message.error('昵称修改失败');
    }
  };

  const handleUpdateAvatar = async (file: File) => {
    setUploading(true);
    try {
      await authService.changeAvatar(file);
      message.success('头像更新成功');
      if (isOwnProfile) {
        await fetchOwnProfile();
      }
    } catch (error) {
      message.error('头像更新失败');
    } finally {
      setUploading(false);
    }
  };

  const handleUpdatePassword = async (values: any) => {
    try {
      await authService.changePassword({
        oldPassword: values.oldPassword,
        newPassword: values.newPassword
      });
      message.success('密码修改成功');
      setSecurityModalVisible(false);
      securityForm.resetFields();
    } catch (error) {
      message.error('密码修改失败');
    }
  };

  const handleUpdatePhone = async (values: any) => {
    try {
      await authService.changePhone({
        newPassword: values.newPhone
      });
      message.success('手机号修改成功');
      setSecurityModalVisible(false);
      securityForm.resetFields();
    } catch (error) {
      message.error('手机号修改失败');
    }
  };

  const handleUpdateEmail = async (values: any) => {
    try {
      await authService.changeEmail({
        newPassword: values.newEmail
      });
      message.success('邮箱修改成功');
      setSecurityModalVisible(false);
      securityForm.resetFields();
    } catch (error) {
      message.error('邮箱修改失败');
    }
  };

  const handleAddFriend = async () => {
    try {
      await relationService.requestFriend(userId!);
      message.success('好友申请已发送');
    } catch (error) {
      message.error('发送好友申请失败');
    }
  };

  const handleDeleteFriend = async () => {
    try {
      await relationService.dealRelation(userId!, OurStatus.FRIENDS, 0);
      message.success('删除好友成功');
      if (!isOwnProfile) {
        await fetchOtherProfile();
      }
    } catch (error) {
      message.error('删除好友失败');
    }
  };

  const handleSendMessage = () => {
    navigate(`/chat/${userId}`);
  };

  if (!userInfo) {
    return <div style={{ padding: '20px', textAlign: 'center' }}>加载中...</div>;
  }

  return (
    <div style={{ padding: '20px', maxWidth: '800px', margin: '0 auto' }}>
      <Card>
        <div style={{ textAlign: 'center', marginBottom: '24px' }}>
          <Avatar 
            src={userInfo.userAvatar} 
            size={120}
            style={{ marginBottom: '16px' }}
          />
          <h2 style={{ marginBottom: '8px' }}>{userInfo.userName}</h2>
          <p style={{ color: '#666', marginBottom: '16px' }}>ID: {userInfo.userId}</p>
          
          <Space style={{ justifyContent: 'center', marginBottom: '16px' }}>
            <Tag color={userInfo.status === UserStatus.NORMAL ? 'green' : 'red'}>
              {userInfo.status === UserStatus.NORMAL ? '正常' : '已封禁'}
            </Tag>
            <Tag>发帖数: {userInfo.userPostNum}</Tag>
          </Space>


          {!isOwnProfile && (
            <Space style={{ justifyContent: 'center' }}>
              {userInfo.ourStatus === OurStatus.FRIENDS ? (
                <>
                  <Button type="primary" onClick={handleSendMessage}>
                    发送消息
                  </Button>
                  <Button danger onClick={handleDeleteFriend}>
                    删除好友
                  </Button>
                </>
              ) : userInfo.ourStatus === OurStatus.PENDING ? (
                <Button disabled>等待对方同意</Button>
              ) : (
                <Button type="primary" onClick={handleAddFriend}>
                  添加好友
                </Button>
              )}
            </Space>
          )}
        </div>

        <Tabs defaultActiveKey="1">
          <TabPane tab="基本信息" key="1">
            <Descriptions column={1} bordered>
              <Descriptions.Item label="用户ID">{userInfo.userId}</Descriptions.Item>
              <Descriptions.Item label="用户昵称">{userInfo.userName}</Descriptions.Item>
              <Descriptions.Item label="用户状态">
                <Tag color={userInfo.status === UserStatus.NORMAL ? 'green' : 'red'}>
                  {userInfo.status === UserStatus.NORMAL ? '正常' : '已封禁'}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="发帖数">{userInfo.userPostNum}</Descriptions.Item>
              <Descriptions.Item label="好友状态">
                <Tag color={userInfo.ourStatus === OurStatus.FRIENDS ? 'blue' : 'default'}>
                  {userInfo.ourStatus === OurStatus.FRIENDS ? '好友' : 
                   userInfo.ourStatus === OurStatus.PENDING ? '申请中' : '陌生人'}
                </Tag>
              </Descriptions.Item>
            </Descriptions>

            {isOwnProfile && (
              <div style={{ marginTop: '16px', textAlign: 'center' }}>
                <Button 
                  type="primary" 
                  icon={<EditOutlined />}
                  onClick={() => setEditModalVisible(true)}
                >
                  修改个人信息
                </Button>
              </div>
            )}
          </TabPane>

          {isOwnProfile && (
            <TabPane tab="安全信息" key="2">
              <div style={{ textAlign: 'center', padding: '20px' }}>
                <Button 
                  icon={<LockOutlined />}
                  onClick={() => {
                    securityForm.resetFields();
                    setSecurityModalVisible(true);
                  }}
                  style={{ marginBottom: '16px' }}
                >
                  修改密码
                </Button>
                <br />
                <Button 
                  icon={<UserOutlined />}
                  onClick={() => {
                    securityForm.resetFields();
                    setSecurityModalVisible(true);
                  }}
                >
                  修改手机号/邮箱
                </Button>
              </div>
            </TabPane>
          )}

          <TabPane tab="发布的帖子" key="3">
            <List
              dataSource={posts}
              renderItem={(post) => (
                <List.Item
                  key={post.postId}
                  onClick={() => navigate(`/post/${post.postId}`)}
                  style={{ cursor: 'pointer' }}
                >
                  <List.Item.Meta
                    title={post.itemName}
                    description={
                      <div>
                        <p>{post.itemPlace} | {dayjs(post.itemTime).format('YYYY-MM-DD HH:mm')}</p>
                        <p style={{ color: '#666' }}>{post.userDescription}</p>
                      </div>
                    }
                  />
                </List.Item>
              )}
            />
            {posts.length === 0 && (
              <div style={{ textAlign: 'center', padding: '20px', color: '#999' }}>
                暂无发布的帖子
              </div>
            )}
          </TabPane>
        </Tabs>
      </Card>

      <Modal
        title="修改个人信息"
        open={editModalVisible}
        onCancel={() => setEditModalVisible(false)}
        footer={null}
      >
        <Form form={editForm} onFinish={handleUpdateName} layout="vertical">
          <Form.Item
            name="userName"
            label="用户昵称"
            rules={[{ required: true, message: '请输入用户昵称' }]}
          >
            <Input placeholder="请输入用户昵称" />
          </Form.Item>
          <Form.Item label="用户头像">
            <Upload
              showUploadList={false}
              beforeUpload={handleUpdateAvatar}
              accept="image/*"
            >
              <Button icon={<CameraOutlined />} loading={uploading}>
                更换头像
              </Button>
            </Upload>
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block>
              保存
            </Button>
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="修改安全信息"
        open={securityModalVisible}
        onCancel={() => setSecurityModalVisible(false)}
        footer={null}
      >
        <Form form={securityForm} layout="vertical">
          <Form.Item
            name="oldPassword"
            label="旧密码"
            rules={[{ required: true, message: '请输入旧密码' }]}
          >
            <Input.Password placeholder="请输入旧密码" />
          </Form.Item>
          <Form.Item
            name="newPassword"
            label="新密码"
            rules={[{ required: true, message: '请输入新密码' }]}
          >
            <Input.Password placeholder="请输入新密码" />
          </Form.Item>
          <Form.Item
            name="confirmPassword"
            label="确认新密码"
            dependencies={['newPassword']}
            rules={[
              { required: true, message: '请确认新密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('newPassword') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'));
                },
              }),
            ]}
          >
            <Input.Password placeholder="请确认新密码" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block onClick={() => {
              const values = securityForm.getFieldsValue();
              if (values.oldPassword && values.newPassword) {
                handleUpdatePassword(values).catch(console.error);
              }
            }}>
              修改密码
            </Button>
          </Form.Item>
          <Form.Item
            name="newPhone"
            label="新手机号"
            rules={[{ pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号' }]}
          >
            <Input placeholder="请输入新手机号" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block onClick={() => {
              const values = securityForm.getFieldsValue();
              if (values.newPhone) {
                handleUpdatePhone(values).catch(console.error);
              }
            }}>
              修改手机号
            </Button>
          </Form.Item>
          <Form.Item
            name="newEmail"
            label="新邮箱"
            rules={[{ type: 'email', message: '请输入有效的邮箱地址' }]}
          >
            <Input placeholder="请输入新邮箱" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block onClick={() => {
              const values = securityForm.getFieldsValue();
              if (values.newEmail) {
                handleUpdateEmail(values).catch(console.error);
              }
            }}>
              修改邮箱
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default UserProfile;