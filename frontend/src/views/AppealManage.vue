<template>
  <div class="appeal-manage">
    <h2>申诉管理</h2>
    
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <div slot="header">
            <span>待处理申诉列表</span>
            <el-button type="primary" size="small" style="float: right;" @click="loadAppeals">
              <i class="el-icon-refresh"></i> 刷新
            </el-button>
          </div>
          <el-table :data="appeals" v-loading="loading" border>
            <el-table-column prop="id" label="内容ID" width="150" show-overflow-tooltip></el-table-column>
            <el-table-column prop="userId" label="用户ID" width="120" show-overflow-tooltip></el-table-column>
            <el-table-column prop="title" label="内容标题" width="200" show-overflow-tooltip></el-table-column>
            <el-table-column prop="appealReason" label="申诉理由" show-overflow-tooltip></el-table-column>
            <el-table-column prop="appealTime" label="申诉时间" width="160"></el-table-column>
            <el-table-column prop="riskScore" label="风险分" width="100" align="center">
              <template slot-scope="scope">
                <el-tag :type="getRiskType(scope.row.riskScore)" size="small">{{ scope.row.riskScore }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" align="center">
              <template slot-scope="scope">
                <el-button size="small" type="success" @click="openProcessDialog(scope.row, true)">
                  <i class="el-icon-check"></i> 通过
                </el-button>
                <el-button size="small" type="danger" @click="openProcessDialog(scope.row, false)">
                  <i class="el-icon-close"></i> 驳回
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="appeals.length === 0 && !loading" description="暂无待处理申诉"></el-empty>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card v-if="appealResult">
          <div slot="header">
            <span>申诉处理结果</span>
            <el-tag :type="appealResult.appealSuccess ? 'success' : 'danger'" style="margin-left: 10px;">
              {{ appealResult.appealSuccess ? '申诉通过' : '申诉驳回' }}
            </el-tag>
          </div>
          
          <div class="result-summary">
            <p><strong>内容标题:</strong> {{ appealResult.contentTitle }}</p>
            <p><strong>处理时间:</strong> {{ appealResult.processTime }}</p>
            <p v-if="appealResult.processComment"><strong>处理备注:</strong> {{ appealResult.processComment }}</p>
          </div>
          
          <el-divider v-if="appealResult.appealSuccess">内容恢复详情</el-divider>
          
          <div v-if="appealResult.appealSuccess && appealResult.contentRestoreInfo" class="restore-info">
            <el-descriptions :column="1" border size="small">
              <el-descriptions-item label="内容状态">
                <span class="old-value">{{ appealResult.contentRestoreInfo.previousStatus }}</span>
                <i class="el-icon-right arrow-icon"></i>
                <el-tag type="success" size="small">{{ appealResult.contentRestoreInfo.newStatus }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="曝光状态">
                <span class="old-value">{{ appealResult.contentRestoreInfo.previousExposed ? '已曝光' : '未曝光' }}</span>
                <i class="el-icon-right arrow-icon"></i>
                <el-tag type="success" size="small">{{ appealResult.contentRestoreInfo.newExposed ? '已曝光' : '未曝光' }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="审核结果">
                <span class="old-value">{{ appealResult.contentRestoreInfo.previousReviewResult }}</span>
                <i class="el-icon-right arrow-icon"></i>
                <el-tag type="success" size="small">{{ appealResult.contentRestoreInfo.newReviewResult }}</el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </div>
          
          <el-divider v-if="appealResult.appealSuccess && appealResult.userRollbackInfo">处罚回滚详情</el-divider>
          
          <div v-if="appealResult.appealSuccess && appealResult.userRollbackInfo" class="rollback-info">
            <el-descriptions :column="1" border size="small">
              <el-descriptions-item label="用户信息">
                {{ appealResult.userRollbackInfo.username }} ({{ appealResult.userRollbackInfo.userId }})
              </el-descriptions-item>
              <el-descriptions-item label="信用分">
                <span class="old-value">{{ appealResult.userRollbackInfo.previousCreditScore }}</span>
                <i class="el-icon-right arrow-icon"></i>
                <span class="new-value success">{{ appealResult.userRollbackInfo.newCreditScore }}</span>
                <el-tag type="success" size="small">+{{ appealResult.userRollbackInfo.creditScoreChange }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="违规次数">
                <span class="old-value">{{ appealResult.userRollbackInfo.previousViolationCount }}</span>
                <i class="el-icon-right arrow-icon"></i>
                <span class="new-value success">{{ appealResult.userRollbackInfo.newViolationCount }}</span>
                <el-tag type="success" size="small" v-if="appealResult.userRollbackInfo.previousViolationCount > appealResult.userRollbackInfo.newViolationCount">-1</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="用户状态">
                <span class="old-value">{{ appealResult.userRollbackInfo.previousStatus }}</span>
                <i class="el-icon-right arrow-icon"></i>
                <el-tag type="success" size="small">{{ appealResult.userRollbackInfo.newStatus }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="封禁状态" v-if="appealResult.userRollbackInfo.wasPendingBan">
                <el-tag type="warning" size="small">待封禁</el-tag>
                <i class="el-icon-right arrow-icon"></i>
                <el-tag type="success" size="small">已解除</el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </div>
          
          <el-divider v-if="appealResult.rollbackLogs && appealResult.rollbackLogs.length > 0">回滚日志</el-divider>
          
          <div v-if="appealResult.rollbackLogs && appealResult.rollbackLogs.length > 0" class="rollback-logs">
            <div v-for="(log, index) in appealResult.rollbackLogs" :key="index" class="log-item">
              <i class="el-icon-caret-right log-icon"></i>
              <span>{{ log }}</span>
            </div>
          </div>
        </el-card>
        
        <el-card v-else>
          <div slot="header">处理结果预览</div>
          <el-empty description="请先处理申诉查看结果"></el-empty>
        </el-card>
      </el-col>
    </el-row>
    
    <el-dialog title="处理申诉" :visible.sync="processDialogVisible" width="550px" @close="closeProcessDialog">
      <el-alert
        :title="form.appealSuccess ? '申诉通过将恢复内容并回滚用户处罚' : '申诉驳回将维持原有审核结果'"
        :type="form.appealSuccess ? 'success' : 'warning'"
        :closable="false"
        style="margin-bottom: 20px;"
        show-icon
      >
      </el-alert>
      
      <el-descriptions :column="1" border size="small" style="margin-bottom: 20px;">
        <el-descriptions-item label="内容标题">{{ currentContent?.title }}</el-descriptions-item>
        <el-descriptions-item label="申诉理由">{{ currentContent?.appealReason }}</el-descriptions-item>
      </el-descriptions>
      
      <el-form :model="form" label-width="80px">
        <el-form-item label="处理结果">
          <el-radio-group v-model="form.appealSuccess">
            <el-radio :label="true" style="color: #67C23A;">通过申诉</el-radio>
            <el-radio :label="false" style="color: #F56C6C;">驳回申诉</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input type="textarea" v-model="form.comment" :rows="3" placeholder="请输入处理备注..."></el-input>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAppeal" :loading="submitting">确认处理</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'AppealManage',
  data() {
    return {
      appeals: [],
      loading: false,
      processDialogVisible: false,
      currentContent: null,
      appealResult: null,
      form: {
        appealSuccess: true,
        comment: ''
      },
      submitting: false
    }
  },
  mounted() {
    this.loadAppeals()
    this.interval = setInterval(() => this.loadAppeals(), 10000)
  },
  beforeDestroy() {
    clearInterval(this.interval)
  },
  methods: {
    loadAppeals() {
      this.loading = true
      axios.get('/api/content/appealed').then(res => {
        this.appeals = res.data
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    openProcessDialog(content, appealSuccess) {
      this.currentContent = content
      this.form.appealSuccess = appealSuccess
      this.form.comment = ''
      this.processDialogVisible = true
    },
    closeProcessDialog() {
      this.currentContent = null
      this.form.comment = ''
    },
    submitAppeal() {
      if (!this.currentContent) return
      
      this.submitting = true
      axios.post('/api/appeal/process', {
        contentId: this.currentContent.id,
        appealSuccess: this.form.appealSuccess,
        comment: this.form.comment
      }).then(res => {
        this.appealResult = res.data
        this.$message.success({
          message: this.form.appealSuccess ? '申诉通过，已恢复内容并回滚处罚！' : '申诉已驳回！',
          duration: 3000
        })
        this.processDialogVisible = false
        this.submitting = false
        this.loadAppeals()
      }).catch(() => {
        this.$message.error('处理失败！')
        this.submitting = false
      })
    },
    getRiskType(score) {
      if (score < 30) return 'success'
      if (score < 60) return 'warning'
      return 'danger'
    }
  }
}
</script>

<style scoped>
.appeal-manage h2 {
  margin-bottom: 20px;
}

.result-summary {
  line-height: 2;
}

.result-summary p {
  margin: 5px 0;
}

.restore-info,
.rollback-info {
  margin-bottom: 15px;
}

.old-value {
  color: #909399;
  text-decoration: line-through;
}

.new-value {
  font-weight: bold;
}

.new-value.success {
  color: #67C23A;
}

.arrow-icon {
  margin: 0 8px;
  color: #409EFF;
}

.rollback-logs {
  background: #f5f7fa;
  border-radius: 4px;
  padding: 10px;
}

.log-item {
  display: flex;
  align-items: center;
  padding: 5px 0;
  font-size: 13px;
  color: #606266;
}

.log-icon {
  color: #67C23A;
  margin-right: 8px;
}
</style>
