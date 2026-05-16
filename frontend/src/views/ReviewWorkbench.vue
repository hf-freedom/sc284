<template>
  <div class="review-workbench">
    <h2>审核工作台</h2>

    <el-card>
      <div slot="header">
        <span>审核团队负载状态</span>
        <span style="float: right; color: #909399; font-size: 12px;">数据每5秒自动刷新</span>
      </div>
      <el-row :gutter="20">
        <el-col :span="6" v-for="reviewer in reviewers" :key="reviewer.id">
          <div 
            class="reviewer-card" 
            :class="{ active: selectedReviewer === reviewer.id }"
            @click="selectReviewer(reviewer.id)"
          >
            <div class="reviewer-header">
              <div class="reviewer-avatar">
                <i class="el-icon-user-solid"></i>
              </div>
              <div class="reviewer-info">
                <div class="reviewer-name">{{ reviewer.name }}</div>
                <div class="reviewer-status">
                  <el-tag :type="getWorkloadStatus(reviewer).type" size="mini">
                    {{ getWorkloadStatus(reviewer).text }}
                  </el-tag>
                </div>
              </div>
            </div>
            <div class="workload-progress">
              <div class="progress-label">
                <span>今日进度</span>
                <span class="progress-value">{{ reviewer.todayProcessed }} / {{ reviewer.dailyLimit }}</span>
              </div>
              <el-progress 
                :percentage="getWorkloadPercentage(reviewer)" 
                :color="getProgressColor(reviewer)"
                :stroke-width="8"
              ></el-progress>
            </div>
            <div class="workload-stats">
              <div class="stat-item">
                <span class="stat-label">剩余额度</span>
                <span class="stat-value" :class="getRemainingClass(reviewer)">{{ reviewer.dailyLimit - reviewer.todayProcessed }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">完成率</span>
                <span class="stat-value">{{ getWorkloadPercentage(reviewer) }}%</span>
              </div>
            </div>
            <div class="reviewer-action">
              <el-button 
                size="small" 
                :type="selectedReviewer === reviewer.id ? 'primary' : 'default'"
                @click.stop="selectReviewer(reviewer.id)"
              >
                {{ selectedReviewer === reviewer.id ? '已选中' : '选中' }}
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-divider></el-divider>

      <el-row :gutter="20">
        <el-col :span="6">
          <div class="summary-card">
            <div class="summary-icon total">
              <i class="el-icon-s-custom"></i>
            </div>
            <div class="summary-info">
              <div class="summary-value">{{ reviewers.length }}</div>
              <div class="summary-label">审核员总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-card">
            <div class="summary-icon busy">
              <i class="el-icon-time"></i>
            </div>
            <div class="summary-info">
              <div class="summary-value">{{ totalTodayProcessed }}</div>
              <div class="summary-label">今日已审核</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-card">
            <div class="summary-icon remaining">
              <i class="el-icon-coordinate"></i>
            </div>
            <div class="summary-info">
              <div class="summary-value">{{ totalRemaining }}</div>
              <div class="summary-label">剩余总额度</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-card">
            <div class="summary-icon pending">
              <i class="el-icon-document-checked"></i>
            </div>
            <div class="summary-info">
              <div class="summary-value">{{ totalPendingTasks }}</div>
              <div class="summary-label">待审核总数</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card style="margin-top: 20px;">
      <div slot="header">
        <span>审核队列</span>
        <span style="float: right; color: #909399; font-size: 12px;">点击卡片获取对应类型内容</span>
      </div>
      <el-row :gutter="20">
        <el-col :span="6" v-for="(count, type) in queueStats" :key="type">
          <div 
            class="queue-card" 
            :class="{ active: selectedType === type }"
            @click="selectQueueType(type)"
          >
            <div class="queue-icon">
              <i :class="getTypeIcon(type)"></i>
            </div>
            <div class="queue-name">{{ getTypeName(type) }}</div>
            <div class="queue-count">{{ count }}</div>
            <div class="queue-action">
              <el-button size="mini" type="primary" @click.stop="loadNextTaskByType(type)" :disabled="count === 0 || !selectedReviewer">
                获取
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
      <div style="margin-top: 20px; text-align: center;">
        <el-button type="success" @click="loadNextTask" :disabled="!selectedReviewer">
          <i class="el-icon-refresh-right"></i> 随机获取下一条
        </el-button>
      </div>
    </el-card>

    <el-card v-if="currentTask" style="margin-top: 20px;">
      <div slot="header">
        <span>待审核内容</span>
        <el-tag :type="getRiskType(currentTask.riskScore)" style="margin-left: 10px;">
          风险分: {{ currentTask.riskScore }}
        </el-tag>
        <el-tag type="info" style="margin-left: 5px;">
          {{ getTypeName(currentTask.type) }}
        </el-tag>
      </div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="内容ID">{{ currentTask.id }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentTask.userId }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ currentTask.title }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ currentTask.content }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ getTypeName(currentTask.type) }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ currentTask.submitTime }}</el-descriptions-item>
      </el-descriptions>
      <div style="margin-top: 20px;">
        <el-form :model="reviewForm" label-width="100px">
          <el-form-item label="审核结果">
            <el-radio-group v-model="reviewForm.result">
              <el-radio label="APPROVED">通过</el-radio>
              <el-radio label="REJECTED">拒绝</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="审核备注">
            <el-input type="textarea" v-model="reviewForm.comment" :rows="3"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitReview" :loading="submitting">提交审核</el-button>
            <el-button @click="skipTask">跳过</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card v-else style="margin-top: 20px;">
      <el-empty description="暂无待审核内容，请先选择审核员并点击获取"></el-empty>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ReviewWorkbench',
  data() {
    return {
      reviewers: [],
      selectedReviewer: '',
      selectedType: '',
      currentTask: null,
      reviewForm: {
        result: 'APPROVED',
        comment: ''
      },
      submitting: false,
      queueStats: {
        TEXT: 0,
        COMMENT: 0,
        IMAGE: 0,
        VIDEO: 0
      }
    }
  },
  computed: {
    selectedReviewerDetail() {
      return this.reviewers.find(r => r.id === this.selectedReviewer)
    },
    totalTodayProcessed() {
      return this.reviewers.reduce((sum, r) => sum + r.todayProcessed, 0)
    },
    totalRemaining() {
      return this.reviewers.reduce((sum, r) => sum + (r.dailyLimit - r.todayProcessed), 0)
    },
    totalPendingTasks() {
      return Object.values(this.queueStats).reduce((sum, count) => sum + count, 0)
    }
  },
  mounted() {
    this.loadReviewers()
    this.loadQueueStats()
    this.interval = setInterval(() => {
      this.loadReviewers()
      this.loadQueueStats()
    }, 5000)
  },
  beforeDestroy() {
    clearInterval(this.interval)
  },
  methods: {
    loadReviewers() {
      axios.get('/api/reviewers').then(res => {
        this.reviewers = res.data
        if (this.reviewers.length > 0 && !this.selectedReviewer) {
          this.selectedReviewer = this.reviewers[0].id
        }
      })
    },
    loadQueueStats() {
      axios.get('/api/stats/queues').then(res => {
        this.queueStats = res.data
      })
    },
    selectReviewer(reviewerId) {
      this.selectedReviewer = reviewerId
    },
    selectQueueType(type) {
      this.selectedType = type
    },
    loadNextTaskByType(type) {
      if (!this.selectedReviewer) {
        this.$message.warning('请先选择审核员')
        return
      }
      axios.get(`/api/review/next/${this.selectedReviewer}/${type}`).then(res => {
        this.currentTask = res.data
        this.selectedType = type
        this.reviewForm.result = 'APPROVED'
        this.reviewForm.comment = ''
        this.loadReviewers()
        this.loadQueueStats()
      }).catch(() => {
        this.$message.info(`${this.getTypeName(type)}队列暂无待审核内容`)
      })
    },
    loadNextTask() {
      if (!this.selectedReviewer) {
        this.$message.warning('请先选择审核员')
        return
      }
      axios.get(`/api/review/next/${this.selectedReviewer}`).then(res => {
        this.currentTask = res.data
        this.selectedType = ''
        this.reviewForm.result = 'APPROVED'
        this.reviewForm.comment = ''
        this.loadReviewers()
        this.loadQueueStats()
      }).catch(() => {
        this.currentTask = null
        this.$message.info('没有更多待审核内容了')
      })
    },
    submitReview() {
      if (!this.currentTask) return
      this.submitting = true
      axios.post('/api/review/process', {
        contentId: this.currentTask.id,
        reviewerId: this.selectedReviewer,
        result: this.reviewForm.result,
        comment: this.reviewForm.comment
      }).then(() => {
        this.$message.success('审核完成！')
        this.currentTask = null
        this.submitting = false
        this.loadReviewers()
        this.loadQueueStats()
      }).catch(() => {
        this.$message.error('审核提交失败！')
        this.submitting = false
      })
    },
    skipTask() {
      this.currentTask = null
      this.$message.info('已跳过当前内容')
    },
    getRiskType(score) {
      if (score < 30) return 'success'
      if (score < 60) return 'warning'
      return 'danger'
    },
    getTypeName(type) {
      const names = {
        TEXT: '文本内容',
        COMMENT: '评论内容',
        IMAGE: '图片内容',
        VIDEO: '视频内容'
      }
      return names[type] || type
    },
    getTypeIcon(type) {
      const icons = {
        TEXT: 'el-icon-document',
        COMMENT: 'el-icon-chat-dot-round',
        IMAGE: 'el-icon-picture',
        VIDEO: 'el-icon-video-camera'
      }
      return icons[type] || 'el-icon-document'
    },
    getWorkloadPercentage(reviewer) {
      return Math.round((reviewer.todayProcessed / reviewer.dailyLimit) * 100)
    },
    getWorkloadStatus(reviewer) {
      const percentage = this.getWorkloadPercentage(reviewer)
      if (percentage >= 100) return { type: 'danger', text: '已满额' }
      if (percentage >= 80) return { type: 'warning', text: '快满了' }
      if (percentage >= 50) return { type: 'info', text: '进行中' }
      return { type: 'success', text: '充足' }
    },
    getProgressColor(reviewer) {
      const percentage = this.getWorkloadPercentage(reviewer)
      if (percentage >= 80) return '#F56C6C'
      if (percentage >= 50) return '#E6A23C'
      return '#67C23A'
    },
    getRemainingClass(reviewer) {
      const remaining = reviewer.dailyLimit - reviewer.todayProcessed
      if (remaining <= 0) return 'text-danger'
      if (remaining <= 10) return 'text-warning'
      return 'text-success'
    }
  }
}
</script>

<style scoped>
.review-workbench h2 {
  margin-bottom: 20px;
}

.reviewer-card {
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
}

.reviewer-card:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.reviewer-card.active {
  border-color: #409EFF;
  background: #ecf5ff;
}

.reviewer-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.reviewer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #409EFF;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  margin-right: 12px;
}

.reviewer-info {
  flex: 1;
}

.reviewer-name {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.workload-progress {
  margin-bottom: 15px;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 12px;
  color: #606266;
}

.progress-value {
  font-weight: bold;
}

.workload-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.stat-item {
  text-align: center;
  flex: 1;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.text-success {
  color: #67C23A;
}

.text-warning {
  color: #E6A23C;
}

.text-danger {
  color: #F56C6C;
}

.reviewer-action {
  text-align: center;
}

.summary-card {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.summary-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  margin-right: 15px;
}

.summary-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.summary-icon.busy {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.summary-icon.remaining {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.summary-icon.pending {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.summary-info {
  flex: 1;
}

.summary-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.summary-label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.queue-card {
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
}

.queue-card:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.queue-card.active {
  border-color: #409EFF;
  background: #ecf5ff;
}

.queue-icon {
  font-size: 32px;
  color: #409EFF;
  margin-bottom: 10px;
}

.queue-name {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.queue-count {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.queue-action {
  margin-top: 10px;
}
</style>
