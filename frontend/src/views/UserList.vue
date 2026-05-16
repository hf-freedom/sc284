<template>
  <div class="user-list">
    <h2>用户管理</h2>
    <el-card>
      <div slot="header">
        <span>所有用户</span>
        <el-button type="primary" size="small" style="float: right;" @click="loadUsers">刷新</el-button>
      </div>
      <el-table :data="users" v-loading="loading" border>
        <el-table-column prop="id" label="用户ID" width="150"></el-table-column>
        <el-table-column prop="username" label="用户名" width="150"></el-table-column>
        <el-table-column prop="level" label="用户等级" width="100" align="center">
          <template slot-scope="scope">
            <el-tag type="primary" size="small">Lv.{{ scope.row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="creditScore" label="信用分" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getCreditType(scope.row.creditScore)" size="small">{{ scope.row.creditScore }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="violationCount" label="违规次数" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.violationCount > 0 ? 'danger' : 'success'" size="small">{{ scope.row.violationCount }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isPendingBan" label="待封禁" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isPendingBan ? 'warning' : 'info'" size="small">{{ scope.row.isPendingBan ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'UserList',
  data() {
    return {
      users: [],
      loading: false
    }
  },
  mounted() {
    this.loadUsers()
  },
  methods: {
    loadUsers() {
      this.loading = true
      axios.get('/api/users').then(res => {
        this.users = res.data
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    getCreditType(score) {
      if (score >= 80) return 'success'
      if (score >= 60) return 'warning'
      return 'danger'
    },
    getStatusType(status) {
      switch (status) {
        case 'NORMAL': return 'success'
        case 'BANNED': return 'danger'
        case 'PENDING_BAN': return 'warning'
        default: return 'info'
      }
    }
  }
}
</script>

<style scoped>
.user-list h2 {
  margin-bottom: 20px;
}
</style>
