<template>
  <div class="content-list">
    <h2>内容列表</h2>
    <el-card>
      <div slot="header">
        <span>所有内容</span>
        <el-button type="primary" size="small" style="float: right;" @click="loadContents">刷新</el-button>
      </div>
      <el-table :data="contents" v-loading="loading" border>
        <el-table-column prop="id" label="内容ID" width="150"></el-table-column>
        <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="content" label="内容" show-overflow-tooltip></el-table-column>
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template slot-scope="scope">
            <el-tag size="small">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="riskScore" label="风险分" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getRiskType(scope.row.riskScore)" size="small">{{ scope.row.riskScore }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isExposed" label="曝光" width="80" align="center">
          <template slot-scope="scope">
            <i :class="scope.row.isExposed ? 'el-icon-check' : 'el-icon-close'" style="color: scope.row.isExposed ? '#67C23A' : '#F56C6C'"></i>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button size="small" type="warning" @click="openAppeal(scope.row)" v-if="scope.row.status === 'REJECTED' && !scope.row.isAppealed">申诉</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog title="提交申诉" :visible.sync="appealDialogVisible" width="500px">
      <el-form :model="appealForm" label-width="80px">
        <el-form-item label="内容标题">
          <span>{{ currentAppealContent?.title }}</span>
        </el-form-item>
        <el-form-item label="申诉理由">
          <el-input type="textarea" v-model="appealForm.appealReason" :rows="4" placeholder="请输入申诉理由"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="appealDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAppeal" :loading="submitting">提交申诉</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ContentList',
  data() {
    return {
      contents: [],
      loading: false,
      appealDialogVisible: false,
      currentAppealContent: null,
      appealForm: {
        appealReason: ''
      },
      submitting: false
    }
  },
  mounted() {
    this.loadContents()
  },
  methods: {
    loadContents() {
      this.loading = true
      axios.get('/api/content/all').then(res => {
        this.contents = res.data
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    openAppeal(content) {
      this.currentAppealContent = content
      this.appealForm.appealReason = ''
      this.appealDialogVisible = true
    },
    submitAppeal() {
      if (!this.appealForm.appealReason) {
        this.$message.warning('请输入申诉理由')
        return
      }
      this.submitting = true
      axios.post('/api/content/appeal', {
        contentId: this.currentAppealContent.id,
        appealReason: this.appealForm.appealReason
      }).then(() => {
        this.$message.success('申诉提交成功！')
        this.appealDialogVisible = false
        this.submitting = false
        this.loadContents()
      }).catch(() => {
        this.$message.error('申诉提交失败！')
        this.submitting = false
      })
    },
    getRiskType(score) {
      if (score < 30) return 'success'
      if (score < 60) return 'warning'
      return 'danger'
    },
    getStatusType(status) {
      switch (status) {
        case 'APPROVED': return 'success'
        case 'REJECTED': return 'danger'
        case 'IN_REVIEW': return 'warning'
        case 'APPEALED': return 'info'
        default: return 'info'
      }
    }
  }
}
</script>

<style scoped>
.content-list h2 {
  margin-bottom: 20px;
}
</style>
