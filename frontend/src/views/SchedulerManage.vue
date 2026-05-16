<template>
  <div class="scheduler-manage">
    <h2>定时任务管理</h2>

    <el-alert
      title="系统自动定时扫描说明"
      type="info"
      :closable="false"
      style="margin-bottom: 20px;"
      show-icon
    >
      <div slot="title">
        <i class="el-icon-time"></i> 系统自动定时扫描说明
      </div>
      <ul>
        <li><strong>超时未审扫描</strong>：每60秒自动执行，超过24小时未审核的内容自动通过</li>
        <li><strong>申诉超时扫描</strong>：每60秒自动执行，超过48小时未处理的申诉自动通过</li>
        <li><strong>重复违规扫描</strong>：每60秒自动执行，违规次数≥5次的用户自动进入封禁审批</li>
        <li><strong>封禁到期扫描</strong>：每5分钟自动执行，封禁到期的用户自动解封</li>
      </ul>
    </el-alert>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="task-card">
          <div slot="header" class="card-header">
            <div>
              <i class="el-icon-time task-icon timeout"></i>
              <span class="task-title">超时未审扫描</span>
            </div>
            <el-tag type="info">每60秒</el-tag>
          </div>
          
          <div class="task-preview">
            <div class="preview-item">
              <span class="preview-label">待处理数量</span>
              <span class="preview-value highlight">{{ preview.timeoutContentCount || 0 }}</span>
            </div>
            <div class="preview-desc">
              超过24小时未审核的内容将自动通过
            </div>
          </div>

          <el-table 
            v-if="preview.timeoutContents && preview.timeoutContents.length > 0"
            :data="preview.timeoutContents.slice(0, 5)"
            size="small"
            style="margin: 15px 0;"
            max-height="200"
          >
            <el-table-column prop="title" label="内容标题" show-overflow-tooltip></el-table-column>
            <el-table-column prop="type" label="类型" width="80" align="center">
              <template slot-scope="scope">
                <el-tag size="mini">{{ scope.row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="submitTime" label="提交时间" width="160"></el-table-column>
          </el-table>

          <div class="task-actions">
            <el-button type="primary" @click="executeScan('timeout')" :loading="scanning.timeout">
              <i class="el-icon-video-play"></i> 立即执行扫描
            </el-button>
            <el-button @click="executeScan('timeout', true)" :loading="scanning.timeout">
              <i class="el-icon-warning"></i> 强制执行
            </el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="task-card">
          <div slot="header" class="card-header">
            <div>
              <i class="el-icon-message task-icon appeal"></i>
              <span class="task-title">申诉超时扫描</span>
            </div>
            <el-tag type="info">每60秒</el-tag>
          </div>
          
          <div class="task-preview">
            <div class="preview-item">
              <span class="preview-label">待处理数量</span>
              <span class="preview-value highlight">{{ preview.appealTimeoutContentCount || 0 }}</span>
            </div>
            <div class="preview-desc">
              超过48小时未处理的申诉自动通过
            </div>
          </div>

          <el-table 
            v-if="preview.appealTimeoutContents && preview.appealTimeoutContents.length > 0"
            :data="preview.appealTimeoutContents.slice(0, 5)"
            size="small"
            style="margin: 15px 0;"
            max-height="200"
          >
            <el-table-column prop="title" label="内容标题" show-overflow-tooltip></el-table-column>
            <el-table-column prop="appealTime" label="申诉时间" width="160"></el-table-column>
          </el-table>

          <div class="task-actions">
            <el-button type="primary" @click="executeScan('appealTimeout')" :loading="scanning.appealTimeout">
              <i class="el-icon-video-play"></i> 立即执行扫描
            </el-button>
            <el-button @click="executeScan('appealTimeout', true)" :loading="scanning.appealTimeout">
              <i class="el-icon-warning"></i> 强制执行
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="task-card">
          <div slot="header" class="card-header">
            <div>
              <i class="el-icon-warning-outline task-icon violation"></i>
              <span class="task-title">重复违规用户扫描</span>
            </div>
            <el-tag type="info">每60秒</el-tag>
          </div>
          
          <div class="task-preview">
            <div class="preview-item">
              <span class="preview-label">待处理数量</span>
              <span class="preview-value highlight">{{ preview.repeatViolationUserCount || 0 }}</span>
            </div>
            <div class="preview-desc">
              违规次数≥5次的用户将自动进入封禁审批
            </div>
          </div>

          <el-table 
            v-if="preview.repeatViolationUsers && preview.repeatViolationUsers.length > 0"
            :data="preview.repeatViolationUsers.slice(0, 5)"
            size="small"
            style="margin: 15px 0;"
            max-height="200"
          >
            <el-table-column prop="username" label="用户名" width="120"></el-table-column>
            <el-table-column prop="violationCount" label="违规次数" width="100" align="center">
              <template slot-scope="scope">
                <el-tag type="danger" size="mini">{{ scope.row.violationCount }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="creditScore" label="信用分" width="100" align="center">
              <template slot-scope="scope">
                <el-tag :type="scope.row.creditScore >= 60 ? 'success' : 'danger'" size="mini">
                  {{ scope.row.creditScore }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>

          <div class="task-actions">
            <el-button type="primary" @click="executeScan('repeatViolations')" :loading="scanning.repeatViolations">
              <i class="el-icon-video-play"></i> 立即执行扫描
            </el-button>
            <el-button @click="executeScan('repeatViolations', true)" :loading="scanning.repeatViolations">
              <i class="el-icon-warning"></i> 强制执行
            </el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="task-card">
          <div slot="header" class="card-header">
            <div>
              <i class="el-icon-unlock task-icon expire"></i>
              <span class="task-title">封禁到期自动解封</span>
            </div>
            <el-tag type="info">每5分钟</el-tag>
          </div>
          
          <div class="task-preview">
            <div class="preview-item">
              <span class="preview-label">封禁中用户</span>
              <span class="preview-value highlight">{{ preview.expiringBanCount || 0 }}</span>
            </div>
            <div class="preview-desc">
              封禁到期的用户将自动恢复正常状态
            </div>
          </div>

          <el-table 
            v-if="preview.expiringBans && preview.expiringBans.length > 0"
            :data="preview.expiringBans.slice(0, 5)"
            size="small"
            style="margin: 15px 0;"
            max-height="200"
          >
            <el-table-column prop="username" label="用户名" width="120"></el-table-column>
            <el-table-column prop="banStartTime" label="封禁开始" width="160"></el-table-column>
            <el-table-column prop="banEndTime" label="解封时间" width="160"></el-table-column>
          </el-table>

          <div class="task-actions">
            <el-button type="primary" @click="executeScan('expireBans')" :loading="scanning.expireBans">
              <i class="el-icon-video-play"></i> 立即执行扫描
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog
      title="扫描执行结果"
      :visible.sync="resultDialogVisible"
      width="500px"
    >
      <div v-if="currentResult">
        <el-alert
          :title="'成功处理 ' + currentResult.processedCount + ' 条记录'"
          type="success"
          :closable="false"
          show-icon
          style="margin-bottom: 20px;"
        ></el-alert>
        <p><strong>执行时间：</strong>{{ currentResult.executeTime }}</p>
        <div v-if="currentResult.processedContents && currentResult.processedContents.length > 0" style="margin-top: 15px;">
          <p><strong>处理的内容：</strong></p>
          <ul>
            <li v-for="content in currentResult.processedContents" :key="content.id" style="margin: 5px 0;">
              {{ content.title }}
            </li>
          </ul>
        </div>
        <div v-if="currentResult.processedUsers && currentResult.processedUsers.length > 0" style="margin-top: 15px;">
          <p><strong>处理的用户：</strong></p>
          <ul>
            <li v-for="user in currentResult.processedUsers" :key="user.id" style="margin: 5px 0;">
              {{ user.username }}
            </li>
          </ul>
        </div>
      </div>
      <div slot="footer">
        <el-button type="primary" @click="resultDialogVisible = false">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'SchedulerManage',
  data() {
    return {
      preview: {},
      scanning: {
        timeout: false,
        appealTimeout: false,
        repeatViolations: false,
        expireBans: false
      },
      resultDialogVisible: false,
      currentResult: null,
      refreshInterval: null
    }
  },
  mounted() {
    this.loadPreview()
    this.refreshInterval = setInterval(() => this.loadPreview(), 5000)
  },
  beforeDestroy() {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval)
    }
  },
  methods: {
    loadPreview() {
      axios.get('/api/scheduler/preview').then(res => {
        this.preview = res.data
      })
    },
    executeScan(type, forceExecute = false) {
      this.scanning[type] = true
      let endpoint = ''
      
      switch(type) {
        case 'timeout':
          endpoint = '/api/scheduler/scan-timeout'
          break
        case 'appealTimeout':
          endpoint = '/api/scheduler/scan-appeal-timeout'
          break
        case 'repeatViolations':
          endpoint = '/api/scheduler/scan-repeat-violations'
          break
        case 'expireBans':
          endpoint = '/api/scheduler/expire-bans'
          break
      }

      axios.post(endpoint, { forceExecute }).then(res => {
        this.currentResult = res.data
        this.resultDialogVisible = true
        this.$message.success('扫描执行完成！')
        this.loadPreview()
      }).catch(() => {
        this.$message.error('扫描执行失败！')
      }).finally(() => {
        this.scanning[type] = false
      })
    }
  }
}
</script>

<style scoped>
.scheduler-manage h2 {
  margin-bottom: 20px;
}

.task-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-icon {
  font-size: 20px;
  margin-right: 10px;
}

.task-icon.timeout {
  color: #E6A23C;
}

.task-icon.appeal {
  color: #909399;
}

.task-icon.violation {
  color: #F56C6C;
}

.task-icon.expire {
  color: #67C23A;
}

.task-title {
  font-size: 16px;
  font-weight: bold;
}

.task-preview {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 15px;
}

.preview-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.preview-label {
  color: #606266;
}

.preview-value {
  font-size: 18px;
  font-weight: bold;
}

.preview-value.highlight {
  color: #409EFF;
}

.preview-desc {
  font-size: 12px;
  color: #909399;
}

.task-actions {
  display: flex;
  gap: 10px;
}
</style>
