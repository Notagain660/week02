import React, { useState, useEffect } from 'react';
import { Card, Button, List, Avatar, Input, message, Space, Tag, Modal, Popconfirm } from 'antd';
import { MessageOutlined, ExclamationCircleOutlined, DeleteOutlined, EditOutlined } from '@ant-design/icons';
import { useParams, useNavigate } from 'react-router-dom';
import { postService, commentService, reportService, authService } from '../services/api';
import { Post, CommentVO, ReportType, OtherInfo } from '../types';
import { useAuth } from '../context/AuthContext';
import dayjs from 'dayjs';

const { TextArea } = Input;

const PostDetail: React.FC = () => {
  const [post, setPost] = useState<Post | null>(null);
  const [posterInfo, setPosterInfo] = useState<OtherInfo | null>(null);
  const [comments, setComments] = useState<CommentVO[]>([]);
  const [loading, setLoading] = useState(false);
  const [commentText, setCommentText] = useState('');
  const [replyTo, setReplyTo] = useState<string | null>(null);
  const [reportModalVisible, setReportModalVisible] = useState(false);
  const [reportReason, setReportReason] = useState('');
  const [reportType, setReportType] = useState<ReportType>(ReportType.POST);
  const [reportContentId, setReportContentId] = useState<string>('');
  const navigate = useNavigate();
  const { postId } = useParams();
  const { user } = useAuth();

  useEffect(() => {
    const loadData = async () => {
      if (postId) {
        await fetchPost();
        await fetchComments();
      }
    };
    loadData().catch(console.error);
  }, [postId]);

  const fetchPost = async () => {
    setLoading(true);
    try {
      const response = await postService.checkPost(postId!);
      if (response.code === 200) {
        setPost(response.data);
        if (response.data.posterId !== user?.userId) {
          const userResponse = await authService.getOtherProfile(response.data.posterId);
          if (userResponse.code === 200) {
            const userData = userResponse.data as any;
            setPosterInfo({
              userId: response.data.posterId,
              userName: userData.nickname,
              userAvatar: userData.avatar,
              userPostNum: userData.postNum,
              status: userData.status,
              ourStatus: 0
            });
          }
        }
      }
    } catch (error) {
      message.error('获取帖子详情失败');
    } finally {
      setLoading(false);
    }
  };

  const fetchComments = async () => {
    try {
      const response = await commentService.getComments(postId!);
      if (response.code === 200) {
        setComments(response.data.records);
      }
    } catch (error) {
      message.error('获取评论失败');
    }
  };

  const handleComment = async () => {
    if (!commentText.trim()) {
      message.warning('请输入评论内容');
      return;
    }

    try {
      await commentService.sendComment(
        replyTo || '0',
        postId!,
        commentText
      );
      message.success('评论成功');
      setCommentText('');
      setReplyTo(null);
      await fetchComments();
    } catch (error) {
      message.error('评论失败');
    }
  };

  const handleReply = (commentId: string) => {
    setReplyTo(commentId);
    setCommentText('');
  };

  const handleDeleteComment = async (commentId: string) => {
    try {
      await commentService.deleteComment(commentId);
      message.success('删除成功');
      await fetchComments();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleDeletePost = async () => {
    try {
      await postService.deletePost(postId!);
      message.success('删除成功');
      navigate('/');
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleReport = async () => {
    if (!reportReason.trim()) {
      message.warning('请输入举报原因');
      return;
    }

    try {
      await reportService.report({
        type: reportType,
        contentId: reportContentId,
        reason: reportReason
      });
      message.success('举报成功');
      setReportModalVisible(false);
      setReportReason('');
    } catch (error) {
      message.error('举报失败');
    }
  };

  const openReportModal = (type: ReportType, contentId: string) => {
    setReportType(type);
    setReportContentId(contentId);
    setReportModalVisible(true);
  };

  const handleEditPost = () => {
    navigate(`/post/edit/${postId}`);
  };

  if (!post) {
    return <div style={{ padding: '20px', textAlign: 'center' }}>加载中...</div>;
  }

  return (
    <div style={{ padding: '20px', maxWidth: '800px', margin: '0 auto' }}>
      <Card loading={loading}>
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: '16px' }}>
          <Avatar 
            src={post.posterId === user?.userId ? user.userAvatar : posterInfo?.userAvatar}
            size={64}
            style={{ marginRight: '16px' }}
            onClick={() => navigate(`/user/${post.posterId}`)}
          />
          <div>
            <h3 style={{ margin: 0 }}>{post.posterId === user?.userId ? user.userName : posterInfo?.userName || '用户'}</h3>
            <p style={{ margin: '4px 0', color: '#666' }}>
              {dayjs(post.postTime).format('YYYY-MM-DD HH:mm')}
            </p>
          </div>
        </div>

        <Space style={{ marginBottom: '16px' }}>
          <Tag color={post.type === 0 ? 'red' : 'green'}>
            {post.type === 0 ? '丢失' : '拾取'}
          </Tag>
          <Tag color={post.postStatus === 0 ? 'blue' : 'default'}>
            {post.postStatus === 0 ? '进行中' : '已完成'}
          </Tag>
          {post.pinOrNot && <Tag color="gold">置顶</Tag>}
        </Space>

        <h1 style={{ marginBottom: '16px' }}>{post.itemName}</h1>

        <p style={{ marginBottom: '8px' }}><strong>地点:</strong> {post.itemPlace}</p>
        <p style={{ marginBottom: '8px' }}><strong>时间:</strong> {dayjs(post.itemTime).format('YYYY-MM-DD HH:mm')}</p>
        <p style={{ marginBottom: '16px' }}><strong>联系方式:</strong> {post.contact}</p>

        <p style={{ marginBottom: '16px', lineHeight: '1.6' }}>{post.userDescription}</p>

        {post.itemPhoto && (
          <img 
            src={post.itemPhoto} 
            alt={post.itemName}
            style={{ width: '100%', maxWidth: '500px', borderRadius: '8px', marginBottom: '16px' }}
          />
        )}

        <Space style={{ marginBottom: '16px' }}>
          {post.posterId === user?.userId && (
            <>
              <Button icon={<EditOutlined />} onClick={handleEditPost}>
                修改
              </Button>
              <Popconfirm
                title="确定要删除这个帖子吗？"
                onConfirm={handleDeletePost}
                okText="确定"
                cancelText="取消"
              >
                <Button icon={<DeleteOutlined />} danger>
                  删除
                </Button>
              </Popconfirm>
            </>
          )}
          <Button 
            icon={<ExclamationCircleOutlined />} 
            onClick={() => openReportModal(ReportType.POST, post.postId)}
          >
            举报
          </Button>
        </Space>
      </Card>

      <Card title="评论" style={{ marginTop: '16px' }}>
        <div style={{ marginBottom: '16px' }}>
          <TextArea
            rows={4}
            value={commentText}
            onChange={(e) => setCommentText(e.target.value)}
            placeholder={replyTo ? '回复评论...' : '发表评论...'}
          />
          <Space style={{ marginTop: '8px' }}>
            <Button type="primary" onClick={handleComment}>
              {replyTo ? '回复' : '发表评论'}
            </Button>
            {replyTo && (
              <Button onClick={() => setReplyTo(null)}>取消回复</Button>
            )}
          </Space>
        </div>

        <List
          dataSource={comments}
          renderItem={(comment) => (
            <List.Item key={comment.batchco}>
              <List.Item.Meta
                avatar={
                  <Avatar 
                    src={comment.commenterAvatar}
                    onClick={() => navigate(`/user/${comment.commenterId}`)}
                  />
                }
                title={
                  <div>
                    <span style={{ marginRight: '8px' }}>{comment.commenterName}</span>
                    <span style={{ color: '#999', fontSize: '12px' }}>
                      {comment.floor}楼 · {dayjs(comment.replyTime).format('YYYY-MM-DD HH:mm')}
                    </span>
                  </div>
                }
                description={
                  <div>
                    <p style={{ marginBottom: '8px' }}>{comment.commentText}</p>
                    <Space>
                      <Button 
                        size="small" 
                        icon={<MessageOutlined />}
                        onClick={() => handleReply(comment.batchco)}
                      >
                        回复
                      </Button>
                      {comment.commenterId === user?.userId && (
                        <Popconfirm
                          title="确定要删除这条评论吗？"
                          onConfirm={() => handleDeleteComment(comment.batchco)}
                          okText="确定"
                          cancelText="取消"
                        >
                          <Button size="small" danger icon={<DeleteOutlined />}>
                            删除
                          </Button>
                        </Popconfirm>
                      )}
                      {comment.commenterId !== user?.userId && (
                        <Button 
                          size="small" 
                          icon={<ExclamationCircleOutlined />}
                          onClick={() => openReportModal(ReportType.COMMENT, comment.batchco)}
                        >
                          举报
                        </Button>
                      )}
                    </Space>
                  </div>
                }
              />
            </List.Item>
          )}
        />
      </Card>

      <Modal
        title="举报"
        open={reportModalVisible}
        onOk={handleReport}
        onCancel={() => setReportModalVisible(false)}
        okText="提交"
        cancelText="取消"
      >
        <TextArea
          rows={4}
          value={reportReason}
          onChange={(e) => setReportReason(e.target.value)}
          placeholder="请输入举报原因"
        />
      </Modal>
    </div>
  );
};

export default PostDetail;