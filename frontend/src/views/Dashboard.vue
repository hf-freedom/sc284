<template>
  <div class="dashboard">
    <h2>首页概览</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">
              <i class="el-icon-document"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.total || 0 }}</div>
              <div class="stat-label">总内容数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon approved">
              <i class="el-icon-check"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.approved || 0 }}</div>
              <div class="stat-label">已通过</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon rejected">
              <i class="el-icon-close"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.rejected || 0 }}</div>
              <div class="stat-label">已拒绝</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon in-review">
              <i class="el-icon-time"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.inReview || 0 }}</div>
              <div class="stat-label">审核中</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon appealed">
              <i class="el-icon-message"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.appealed || 0 }}</div>
              <div class="stat-label">待申诉处理</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon reviewer">
              <i class="el-icon-user"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.reviewers || 0 }}</div>
              <div class="stat-label">审核员数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon user">
              <i class="el-icon-s-custom"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.users || 0 }}</div>
              <div class="stat-label">用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon ban">
              <i class="el-icon-lock"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingBan || 0 }}</div>
              <div class="stat-label">待封禁审批</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-card style="margin-top: 20px;">
      <div slot="header">队列统计</div>
      <el-row :gutter="20">
        <el-col :span="6" v-for="(value, key) in stats.queues" :key="key">
          <div class="queue-item">
            <span class="queue-name">{{ key }}</span>
            <span class="queue-count">{{ value }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ReviewDashboard',
  data() {
    return {
      stats: {}
    }
  },
  mounted() {
    this.loadStats()
    this.interval = setInterval(() => this.loadStats(), 5000)
  },
  beforeDestroy() {
    clearInterval(this.interval)
  },
  methods: {
    loadStats() {
      axios.get('/api/stats/summary').then(res => {
        this.stats = res.data
      })
    }
  }
}
</script>

<style scoped>
.dashboard h2 {
  margin-bottom: 20px;
}
.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}
.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}
.stat-content {
  display: flex;
  align-items: center;
}
.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  margin-right: 15px;
}
.stat-icon.total { background: #409EFF; }
.stat-icon.approved { background: #67C23A; }
.stat-icon.rejected { background: #F56C6C; }
.stat-icon.in-review { background: #E6A23C; }
.stat-icon.appealed { background: #909399; }
.stat-icon.reviewer { background: #303133; }
.stat-icon.user { background: #606266; }
.stat-icon.ban { background: #F56C6C; }
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.stat-label {
  color: #909399;
  font-size: 14px;
}
.queue-item {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.queue-name {
  font-weight: bold;
  color: #606266;
}
.queue-count {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}
</style>
