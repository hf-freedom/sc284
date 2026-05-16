<template>
  <div class="ban-approval">
    <h2>封禁审批</h2>
    <el-card>
      <div slot="header">
        <span>待封禁审批用户列表</span>
        <el-button type="primary" size="small" style="float: right;" @click="loadUsers">刷新</el-button>
      </div>
      <el-table :data="users" v-loading="loading" border>
        <el-table-column prop="id" label="用户ID" width="150"></el-table-column>
        <el-table-column prop="username" label="用户名" width="150"></el-table-column>
        <el-table-column prop="level" label="用户等级" width="100" align="center">
          <template slot-scope="scope">
            <el-tag size="small">Lv.{{ scope.row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="creditScore" label="信用分" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getCreditType(scope.row.creditScore)" size="small">{{ scope.row.creditScore }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="violationCount" label="违规次数" width="100" align="center"></el-table-column>
        <el-table-column prop="banReason" label="封禁理由" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="250">
          <template slot-scope="scope">
            <el-button size="small" type="danger" @click="processBan(scope.row, true)">批准封禁</el-button>
            <el-button size="small" type="success" @click="processBan(scope.row, false)">取消封禁</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="users.length === 0 && !loading" description="暂无待审批用户"></el-empty>
    </el-card>
    <el-dialog title="封禁审批" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <span>{{ currentUser?.username }}</span>
        </el-form-item>
        <el-form-item label="违规次数">
          <span>{{ currentUser?.violationCount }}</span>
        </el-form-item>
        <el-form-item label="封禁理由">
          <span>{{ currentUser?.banReason }}</span>
        </el-form-item>
        <el-form-item label="处理结果">
          <el-radio-group v-model="form.approved">
            <el-radio :label="true">批准封禁</el-radio>
            <el-radio :label="false">取消封禁</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.comment" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBan" :loading="submitting">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'BanApproval',
  data() {
    return {
      users: [],
      loading: false,
      dialogVisible: false,
      currentUser: null,
      form: {
        approved: true,
        comment: ''
      },
      submitting: false
    }
  },
  mounted() {
    this.loadUsers()
    this.interval = setInterval(() => this.loadUsers(), 10000)
  },
  beforeDestroy() {
    clearInterval(this.interval)
  },
  methods: {
    loadUsers() {
      this.loading = true
      axios.get('/api/users/pending-ban').then(res => {
        this.users = res.data
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    processBan(user, approved) {
      this.currentUser = user
      this.form.approved = approved
      this.form.comment = ''
      this.dialogVisible = true
    },
    submitBan() {
      this.submitting = true
      axios.post('/api/ban/process', {
        userId: this.currentUser.id,
        approved: this.form.approved,
        comment: this.form.comment
      }).then(() => {
        this.$message.success('审批完成！')
        this.dialogVisible = false
        this.submitting = false
        this.loadUsers()
      }).catch(() => {
        this.$message.error('处理失败！')
        this.submitting = false
      })
    },
    getCreditType(score) {
      if (score >= 80) return 'success'
      if (score >= 60) return 'warning'
      return 'danger'
    }
  }
}
</script>

<style scoped>
.ban-approval h2 {
  margin-bottom: 20px;
}
</style>
