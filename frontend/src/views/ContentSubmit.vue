<template>
  <div class="content-submit">
    <h2>内容提交</h2>
    <el-card>
      <el-form :model="form" label-width="100px" :rules="rules" ref="form">
        <el-form-item label="用户ID" prop="userId">
          <el-select v-model="form.userId" placeholder="请选择用户" style="width: 100%;">
            <el-option v-for="user in users" :key="user.id" :label="user.username" :value="user.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input type="textarea" v-model="form.content" :rows="6" placeholder="请输入内容"></el-input>
        </el-form-item>
        <el-form-item label="内容类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio label="TEXT">文本</el-radio>
            <el-radio label="IMAGE">图片</el-radio>
            <el-radio label="VIDEO">视频</el-radio>
            <el-radio label="COMMENT">评论</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting">提交</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card v-if="result" style="margin-top: 20px;">
      <div slot="header">提交结果</div>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="内容ID">{{ result.id }}</el-descriptions-item>
        <el-descriptions-item label="风险评分">
          <el-tag :type="getRiskType(result.riskScore)">{{ result.riskScore }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(result.status)">{{ result.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核结果">{{ result.reviewResult }}</el-descriptions-item>
        <el-descriptions-item label="是否曝光">{{ result.isExposed ? '是' : '否' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ContentSubmit',
  data() {
    return {
      form: {
        userId: '',
        title: '',
        content: '',
        type: 'TEXT'
      },
      rules: {
        userId: [{ required: true, message: '请选择用户', trigger: 'change' }],
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
      },
      users: [],
      submitting: false,
      result: null
    }
  },
  mounted() {
    this.loadUsers()
  },
  methods: {
    loadUsers() {
      axios.get('/api/users').then(res => {
        this.users = res.data
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.submitting = true
          axios.post('/api/content/submit', this.form).then(res => {
            this.result = res.data
            this.$message.success('提交成功！')
            this.submitting = false
          }).catch(() => {
            this.$message.error('提交失败！')
            this.submitting = false
          })
        }
      })
    },
    resetForm() {
      this.$refs.form.resetFields()
      this.result = null
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
        default: return 'info'
      }
    }
  }
}
</script>

<style scoped>
.content-submit h2 {
  margin-bottom: 20px;
}
</style>
