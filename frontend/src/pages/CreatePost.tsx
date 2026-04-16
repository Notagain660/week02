import React, { useState, useEffect } from 'react';
import { Form, Input, Button, Card, Select, DatePicker, Upload, message, Space } from 'antd';
import { PlusOutlined, LoadingOutlined } from '@ant-design/icons';
import { useNavigate, useParams } from 'react-router-dom';
import { postService } from '../services/api';
import { Post, Type, PostStatus, Visibility } from '../types';
// import { useAuth } from '../context/AuthContext'; // 暂时未使用
import dayjs from 'dayjs';

const { Option } = Select;
const { TextArea } = Input;

const CreatePost: React.FC = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [imageUrl, setImageUrl] = useState<string>('');
  const [generating, setGenerating] = useState(false);
  const navigate = useNavigate();
  // const { user } = useAuth(); // 暂时未使用
  const { postId } = useParams();

  const isEdit = !!postId;

  useEffect(() => {
    const loadPost = async () => {
      if (isEdit && postId) {
        await fetchPost();
      }
    };
    loadPost().catch(console.error);
  }, [isEdit, postId]);

  const fetchPost = async () => {
    try {
      const response = await postService.checkPost(postId!);
      if (response.code === 200) {
        const post = response.data;
        form.setFieldsValue({
          type: post.type,
          itemName: post.itemName,
          itemPlace: post.itemPlace,
          itemTime: dayjs(post.itemTime),
          userDescription: post.userDescription,
          contact: post.contact,
          visible: post.visible,
          postStatus: post.postStatus
        });
        if (post.itemPhoto) {
          setImageUrl(post.itemPhoto);
        }
      }
    } catch (error) {
      message.error('获取帖子信息失败');
    }
  };

  const handleUpload = async (file: File) => {
    setUploading(true);
    try {
      const response = await postService.postImage(file);
      if (response.code === 200) {
        setImageUrl(response.data);
        message.success('图片上传成功');
      }
    } catch (error) {
      message.error('图片上传失败');
    } finally {
      setUploading(false);
    }
    return false;
  };

  const handleGenerateAI = async () => {
    const description = form.getFieldValue('userDescription');
    if (!description) {
      message.warning('请先输入物品描述');
      return;
    }

    setGenerating(true);
    try {
      const response = await fetch('/api/test/ai');
      const aiText = await response.text();
      form.setFieldsValue({ userDescription: description + '\n\n' + aiText });
      message.success('AI描述生成成功');
    } catch (error) {
      message.error('AI描述生成失败');
    } finally {
      setGenerating(false);
    }
  };

  const handleSubmit = async (values: any) => {
    setLoading(true);
    try {
      const postData: Partial<Post> = {
        type: values.type,
        itemName: values.itemName,
        itemPlace: values.itemPlace,
        itemTime: values.itemTime.format('YYYY-MM-DD HH:mm:ss'),
        userDescription: values.userDescription,
        contact: values.contact,
        visible: values.visible,
        itemPhoto: imageUrl || undefined,
        pinOrNot: false
      };

      if (isEdit && postId) {
        postData.postId = postId;
        postData.postStatus = values.postStatus;
        await postService.updatePost(postData);
        message.success('帖子更新成功');
      } else {
        await postService.createPost(postData);
        message.success('帖子发布成功');
      }
      navigate('/');
    } catch (error) {
      message.error(isEdit ? '帖子更新失败' : '帖子发布失败');
    } finally {
      setLoading(false);
    }
  };

  const uploadButton = (
    <div>
      {uploading ? <LoadingOutlined /> : <PlusOutlined />}
      <div style={{ marginTop: 8 }}>上传图片</div>
    </div>
  );

  return (
    <div style={{ padding: '20px', maxWidth: '800px', margin: '0 auto' }}>
      <Card title={isEdit ? '修改帖子' : '发布帖子'}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={{
            type: Type.LOST,
            visible: Visibility.PUBLIC,
            postStatus: PostStatus.OPEN
          }}
        >
          <Form.Item
            name="type"
            label="物品类型"
            rules={[{ required: true, message: '请选择物品类型' }]}
          >
            <Select disabled={isEdit}>
              <Option value={Type.LOST}>丢失</Option>
              <Option value={Type.FOUND}>拾取</Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="itemName"
            label="物品名称"
            rules={[{ required: true, message: '请输入物品名称' }]}
          >
            <Input placeholder="请输入物品名称" />
          </Form.Item>

          <Form.Item
            name="itemPlace"
            label="物品地点"
            rules={[{ required: true, message: '请输入物品地点' }]}
          >
            <Input placeholder="请输入物品地点" />
          </Form.Item>

          <Form.Item
            name="itemTime"
            label="物品时间"
            rules={[{ required: true, message: '请选择物品时间' }]}
          >
            <DatePicker 
              showTime 
              style={{ width: '100%' }}
              placeholder="请选择时间"
            />
          </Form.Item>

          <Form.Item
            name="userDescription"
            label="物品描述"
            rules={[{ required: true, message: '请输入物品描述' }]}
          >
            <TextArea 
              rows={4} 
              placeholder="请详细描述物品的特征、颜色、大小等信息"
            />
          </Form.Item>

          <Form.Item>
            <Button 
              onClick={handleGenerateAI} 
              loading={generating}
              style={{ marginBottom: '8px' }}
            >
              AI生成描述
            </Button>
          </Form.Item>

          <Form.Item label="物品照片">
            <Upload
              listType="picture-card"
              showUploadList={false}
              beforeUpload={handleUpload}
              accept="image/*"
            >
              {imageUrl ? (
                <img src={imageUrl} alt="avatar" style={{ width: '100%' }} />
              ) : (
                uploadButton
              )}
            </Upload>
          </Form.Item>

          <Form.Item
            name="contact"
            label="联系方式"
            rules={[{ required: true, message: '请输入联系方式' }]}
          >
            <Input placeholder="请输入联系方式（手机号、QQ、微信等）" />
          </Form.Item>

          <Form.Item
            name="visible"
            label="可见范围"
            rules={[{ required: true, message: '请选择可见范围' }]}
          >
            <Select>
              <Option value={Visibility.PUBLIC}>公开</Option>
              <Option value={Visibility.PRIVATE}>仅好友可见</Option>
            </Select>
          </Form.Item>

          {isEdit && (
            <Form.Item
              name="postStatus"
              label="帖子状态"
              rules={[{ required: true, message: '请选择帖子状态' }]}
            >
              <Select>
                <Option value={PostStatus.OPEN}>进行中</Option>
                <Option value={PostStatus.COMPLETED}>已完成</Option>
              </Select>
            </Form.Item>
          )}

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" loading={loading}>
                {isEdit ? '更新' : '发布'}
              </Button>
              <Button onClick={() => navigate(-1)}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default CreatePost;