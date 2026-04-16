import React, { useState, useEffect } from 'react';
import { Card, Statistic, Row, Col, DatePicker, Select, Table, Tag, Button, message, Modal, Form, Space } from 'antd';
import { Pie } from '@ant-design/charts';
import { UserOutlined, FileTextOutlined, CheckCircleOutlined, BarChartOutlined } from '@ant-design/icons';
import { adminService, reportService } from '../services/api';
import { Report, ReportStatus, ReportType } from '../types';
import dayjs from 'dayjs';

const { RangePicker } = DatePicker;
const { Option } = Select;

const Admin: React.FC = () => {
  const [totalPosts, setTotalPosts] = useState<number>(0);
  const [totalFound, setTotalFound] = useState<number>(0);
  const [activeUsers, setActiveUsers] = useState<number>(0);
  const [itemData, setItemData] = useState<{ name: string; value: number }[]>([]);
  const [placeData, setPlaceData] = useState<{ name: string; value: number }[]>([]);
  const [reports, setReports] = useState<Report[]>([]);
  const [loading, setLoading] = useState(false);
  const [dateRange, setDateRange] = useState<[dayjs.Dayjs, dayjs.Dayjs] | null>(null);
  const [dealModalVisible, setDealModalVisible] = useState(false);
  const [selectedReport, setSelectedReport] = useState<Report | null>(null);
  const [dealForm] = Form.useForm();

  useEffect(() => {
    const loadData = async () => {
      try {
        await fetchStatistics();
        await fetchReports();
      } catch (error) {
        console.error('加载数据失败:', error);
      }
    };
    loadData().catch(console.error);
  }, []);

  const fetchStatistics = async () => {
    try {
      const [postsRes, foundRes, activeRes, itemRes, placeRes] = await Promise.all([
        adminService.getTotalPosts(),
        adminService.getTotalFound(),
        dateRange ? adminService.getActiveUsers(dateRange[0].format('YYYY-MM-DD'), dateRange[1].format('YYYY-MM-DD')) : Promise.resolve({ code: 200, data: 0 }),
        adminService.getItemStatistics(),
        adminService.getPlaceStatistics()
      ]);

      if (postsRes.code === 200) setTotalPosts(postsRes.data);
      if (foundRes.code === 200) setTotalFound(foundRes.data);
      if (activeRes.code === 200) setActiveUsers(activeRes.data);
      if (itemRes.code === 200) {
        const items = Object.entries(itemRes.data).map(([name, value]) => ({ name, value: Number(value) }));
        setItemData(items);
      }
      if (placeRes.code === 200) {
        const places = Object.entries(placeRes.data).map(([name, value]) => ({ name, value: Number(value) }));
        setPlaceData(places);
      }
    } catch (error) {
      message.error('获取统计数据失败');
    }
  };

  const fetchReports = async () => {
    setLoading(true);
    try {
      const response = await reportService.checkReports();
      if (response.code === 200) {
        setReports(response.data);
      }
    } catch (error) {
      message.error('获取举报列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleDateRangeChange = async (dates: any) => {
    setDateRange(dates);
    if (dates) {
      await fetchActiveUsers(dates[0].format('YYYY-MM-DD'), dates[1].format('YYYY-MM-DD'));
    }
  };

  const fetchActiveUsers = async (start: string, end: string) => {
    try {
      const response = await adminService.getActiveUsers(start, end);
      if (response.code === 200) {
        setActiveUsers(response.data);
      }
    } catch (error) {
      message.error('获取活跃用户数失败');
    }
  };

  const handleDealReport = async (values: any) => {
    if (!selectedReport) return;

    try {
      await reportService.dealReport(selectedReport.reportId, values.status);
      message.success('处理成功');
      setDealModalVisible(false);
      dealForm.resetFields();
      await fetchReports();
    } catch (error) {
      message.error('处理失败');
    }
  };

  const handleBlock = async (report: Report) => {
    try {
      await reportService.block(report.type, report.contentId);
      message.success('封禁成功');
      await fetchReports();
    } catch (error) {
      message.error('封禁失败');
    }
  };

  const itemConfig = {
    data: itemData,
    angleField: 'value',
    colorField: 'name',
    radius: 0.8,
    label: {
      type: 'outer',
      content: '{name} {percentage}',
    },
    interactions: [
      {
        type: 'pie-legend-active',
      },
      {
        type: 'element-active',
      },
    ],
  };

  const placeConfig = {
    data: placeData,
    angleField: 'value',
    colorField: 'name',
    radius: 0.8,
    label: {
      type: 'outer',
      content: '{name} {percentage}',
    },
    interactions: [
      {
        type: 'pie-legend-active',
      },
      {
        type: 'element-active',
      },
    ],
  };

  const reportColumns = [
    {
      title: '举报ID',
      dataIndex: 'reportId',
      key: 'reportId',
    },
    {
      title: '举报类型',
      dataIndex: 'type',
      key: 'type',
      render: (type: ReportType) => (
        <Tag color={type === ReportType.POST ? 'blue' : type === ReportType.COMMENT ? 'green' : 'orange'}>
          {type === ReportType.POST ? '帖子' : type === ReportType.COMMENT ? '评论' : '用户'}
        </Tag>
      ),
    },
    {
      title: '内容ID',
      dataIndex: 'contentId',
      key: 'contentId',
    },
    {
      title: '举报原因',
      dataIndex: 'reason',
      key: 'reason',
    },
    {
      title: '状态',
      dataIndex: 'reportStatus',
      key: 'reportStatus',
      render: (status: ReportStatus) => (
        <Tag color={status === ReportStatus.PENDING ? 'orange' : status === ReportStatus.APPROVED ? 'green' : 'red'}>
          {status === ReportStatus.PENDING ? '待处理' : status === ReportStatus.APPROVED ? '已通过' : '已拒绝'}
        </Tag>
      ),
    },
    {
      title: '举报时间',
      dataIndex: 'reportTime',
      key: 'reportTime',
      render: (time: string) => dayjs(time).format('YYYY-MM-DD HH:mm'),
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: Report) => (
        <Space>
          {record.reportStatus === ReportStatus.PENDING && (
            <Button size="small" onClick={() => {
              setSelectedReport(record);
              setDealModalVisible(true);
            }}>
              处理
            </Button>
          )}
          <Button size="small" danger onClick={() => handleBlock(record)}>
            封禁
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: '20px', maxWidth: '1400px', margin: '0 auto' }}>
      <h1>管理员控制台</h1>

      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="总帖子数"
              value={totalPosts}
              prefix={<FileTextOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="已找回物品数"
              value={totalFound}
              prefix={<CheckCircleOutlined />}
              valueStyle={{ color: '#cf1322' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="活跃用户数"
              value={activeUsers}
              prefix={<UserOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
            <div style={{ marginTop: '8px' }}>
              <RangePicker
                onChange={handleDateRangeChange}
                style={{ width: '100%' }}
              />
            </div>
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="统计图表"
              prefix={<BarChartOutlined />}
              valueStyle={{ fontSize: '14px', color: '#666' }}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col span={12}>
          <Card title="物品统计">
            <Pie {...itemConfig} height={300} />
          </Card>
        </Col>
        <Col span={12}>
          <Card title="地点统计">
            <Pie {...placeConfig} height={300} />
          </Card>
        </Col>
      </Row>

      <Card title="举报管理">
        <Table
          columns={reportColumns}
          dataSource={reports}
          rowKey="reportId"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title="处理举报"
        open={dealModalVisible}
        onCancel={() => setDealModalVisible(false)}
        footer={null}
      >
        <Form form={dealForm} onFinish={handleDealReport} layout="vertical">
          <Form.Item
            name="status"
            label="处理结果"
            rules={[{ required: true, message: '请选择处理结果' }]}
          >
            <Select>
              <Option value={ReportStatus.APPROVED}>通过</Option>
              <Option value={ReportStatus.REJECTED}>拒绝</Option>
            </Select>
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block>
              提交
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Admin;