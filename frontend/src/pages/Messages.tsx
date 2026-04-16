import React, { useState, useEffect } from 'react';
import { Card, List, Avatar, Input, Button, message, Tabs } from 'antd';
import { SendOutlined, DeleteOutlined, CheckOutlined } from '@ant-design/icons';
import { useParams, useNavigate } from 'react-router-dom';
import { chatService, relationService } from '../services/api';
import { Chat, Relation, OurStatus } from '../types';
import { useAuth } from '../context/AuthContext';
import dayjs from 'dayjs';

const { TextArea } = Input;

const Messages: React.FC = () => {
  const [activeTab, setActiveTab] = useState('list');
  const [relations, setRelations] = useState<Relation[]>([]);
  const [chats, setChats] = useState<Chat[]>([]);
  const [selectedUserId, setSelectedUserId] = useState<string | null>(null);
  const [messageText, setMessageText] = useState('');
  const { userId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  useEffect(() => {
    const loadData = async () => {
      if (userId) {
        setSelectedUserId(userId);
        setActiveTab('chat');
        await fetchChats(userId);
      }
      await fetchRelations();
    };
    loadData().catch(console.error);
  }, [userId]);

  const fetchRelations = async () => {
    try {
      const friendsResponse = await relationService.getRelations(OurStatus.FRIENDS);
      const pendingResponse = await relationService.getRelations(OurStatus.PENDING);
      
      if (friendsResponse.code === 200) {
        setRelations(friendsResponse.data);
      }
      if (pendingResponse.code === 200) {
        setRelations(prev => [...prev, ...pendingResponse.data]);
      }
    } catch (error) {
      message.error('获取好友列表失败');
    }
  };

  const fetchChats = async (targetUserId: string) => {
    try {
      const response = await chatService.checkChats(targetUserId);
      if (response.code === 200) {
        setChats(response.data);
      }
    } catch (error) {
      message.error('获取消息失败');
    }
  };

  const handleSendMessage = async () => {
    if (!messageText.trim() || !selectedUserId) {
      message.warning('请输入消息内容');
      return;
    }

    try {
      await chatService.sendChat(selectedUserId, messageText);
      message.success('发送成功');
      setMessageText('');
      await fetchChats(selectedUserId);
    } catch (error) {
      message.error('发送失败');
    }
  };

  const handleMarkAsRead = async (chatId: string) => {
    try {
      await chatService.readChat(chatId);
      await fetchChats(selectedUserId!);
    } catch (error) {
      message.error('标记失败');
    }
  };

  const handleDeleteChat = async (chatId: string) => {
    try {
      await chatService.deleteChat(chatId);
      message.success('删除成功');
      if (selectedUserId) {
        await fetchChats(selectedUserId);
      }
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleSelectUser = (userId: string) => {
    navigate(`/chat/${userId}`);
  };

  const handleDealRequest = async (userId: string, act: number) => {
    try {
      await relationService.dealRelation(userId, OurStatus.PENDING, act);
      message.success(act === 1 ? '已同意好友申请' : '已拒绝好友申请');
      await fetchRelations();
    } catch (error) {
      message.error('操作失败');
    }
  };

  const pendingRequests = relations.filter(r => r.ourStatus === OurStatus.PENDING);
  const friends = relations.filter(r => r.ourStatus === OurStatus.FRIENDS);

  return (
    <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
      <Card title="消息中心">
        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          <Tabs.TabPane tab={`好友申请 (${pendingRequests.length})`} key="requests">
            <List
              dataSource={pendingRequests}
              renderItem={(relation) => (
                <List.Item
                  key={relation.itsId}
                  actions={[
                    <Button type="primary" size="small" onClick={() => handleDealRequest(relation.itsId, 1)}>
                      同意
                    </Button>,
                    <Button size="small" onClick={() => handleDealRequest(relation.itsId, 0)}>
                      拒绝
                    </Button>
                  ]}
                >
                  <List.Item.Meta
                    avatar={<Avatar icon={<SendOutlined />} />}
                    title={`用户 ${relation.itsId}`}
                    description={`申请时间: ${dayjs(relation.requestTime).format('YYYY-MM-DD HH:mm')}`}
                  />
                </List.Item>
              )}
            />
            {pendingRequests.length === 0 && (
              <div style={{ textAlign: 'center', padding: '20px', color: '#999' }}>
                暂无好友申请
              </div>
            )}
          </Tabs.TabPane>

          <Tabs.TabPane tab={`聊天 (${friends.length})`} key="list">
            <List
              dataSource={friends}
              renderItem={(relation) => (
                <List.Item
                  key={relation.itsId}
                  onClick={() => handleSelectUser(relation.itsId)}
                  style={{ cursor: 'pointer' }}
                >
                  <List.Item.Meta
                    avatar={<Avatar icon={<SendOutlined />} />}
                    title={`用户 ${relation.itsId}`}
                    description={`添加时间: ${dayjs(relation.okTime || relation.requestTime).format('YYYY-MM-DD HH:mm')}`}
                  />
                </List.Item>
              )}
            />
            {friends.length === 0 && (
              <div style={{ textAlign: 'center', padding: '20px', color: '#999' }}>
                暂无好友
              </div>
            )}
          </Tabs.TabPane>

          {selectedUserId && (
            <Tabs.TabPane tab={`与用户 ${selectedUserId} 聊天`} key="chat">
              <div style={{ height: '500px', display: 'flex', flexDirection: 'column' }}>
                <div style={{ flex: 1, overflowY: 'auto', marginBottom: '16px', padding: '16px', background: '#f5f5f5', borderRadius: '8px' }}>
                  {chats.map((chat) => (
                    <div
                      key={chat.chatId}
                      style={{
                        display: 'flex',
                        justifyContent: chat.senderId === user?.userId ? 'flex-end' : 'flex-start',
                        marginBottom: '12px'
                      }}
                    >
                      <div
                        style={{
                          maxWidth: '70%',
                          padding: '8px 12px',
                          borderRadius: '8px',
                          background: chat.senderId === user?.userId ? '#1890ff' : '#fff',
                          color: chat.senderId === user?.userId ? '#fff' : '#000',
                          position: 'relative'
                        }}
                      >
                        <div>{chat.content}</div>
                        <div style={{ fontSize: '10px', marginTop: '4px', opacity: 0.7 }}>
                          {dayjs(chat.sendTime).format('HH:mm')}
                          {chat.senderId !== user?.userId && !chat.isRead && (
                            <span style={{ marginLeft: '8px' }}>未读</span>
                          )}
                        </div>
                        {chat.senderId !== user?.userId && !chat.isRead && (
                          <Button
                            size="small"
                            type="link"
                            icon={<CheckOutlined />}
                            onClick={() => handleMarkAsRead(chat.chatId)}
                            style={{ padding: '0 4px', fontSize: '10px', position: 'absolute', right: '4px', bottom: '-20px' }}
                          >
                            标记已读
                          </Button>
                        )}
                      </div>
                      {chat.senderId === user?.userId && (
                        <Button
                          size="small"
                          type="link"
                          icon={<DeleteOutlined />}
                          onClick={() => handleDeleteChat(chat.chatId)}
                          style={{ marginLeft: '8px' }}
                        >
                          删除
                        </Button>
                      )}
                    </div>
                  ))}
                  {chats.length === 0 && (
                    <div style={{ textAlign: 'center', color: '#999' }}>
                      暂无消息，开始聊天吧！
                    </div>
                  )}
                </div>
                <div style={{ display: 'flex', gap: '8px' }}>
                  <TextArea
                    rows={2}
                    value={messageText}
                    onChange={(e) => setMessageText(e.target.value)}
                    placeholder="输入消息..."
                    onPressEnter={(e) => {
                      if (!e.shiftKey) {
                        e.preventDefault();
                        handleSendMessage().catch(console.error);
                      }
                    }}
                  />
                  <Button type="primary" icon={<SendOutlined />} onClick={handleSendMessage}>
                    发送
                  </Button>
                </div>
              </div>
            </Tabs.TabPane>
          )}
        </Tabs>
      </Card>
    </div>
  );
};

export default Messages;