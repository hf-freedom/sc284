<template>
  <div id="app">
    <el-container style="height: 100vh; border: 1px solid #eee;">
      <el-aside width="200px" style="background-color: rgb(238, 241, 246);">
        <el-menu router :default-active="$route.path" class="el-menu-vertical-demo">
          <el-menu-item index="/">
            <i class="el-icon-s-home"></i>
            <span>首页概览</span>
          </el-menu-item>
          <el-menu-item index="/submit">
            <i class="el-icon-edit"></i>
            <span>内容提交</span>
          </el-menu-item>
          <el-menu-item index="/review">
            <i class="el-icon-check"></i>
            <span>审核工作台</span>
          </el-menu-item>
          <el-menu-item index="/appeal">
            <i class="el-icon-message"></i>
            <span>申诉管理</span>
          </el-menu-item>
          <el-menu-item index="/ban">
            <i class="el-icon-lock"></i>
            <span>封禁审批</span>
          </el-menu-item>
          <el-menu-item index="/contents">
            <i class="el-icon-document"></i>
            <span>内容列表</span>
          </el-menu-item>
          <el-menu-item index="/users">
            <i class="el-icon-user"></i>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/scheduler">
            <i class="el-icon-time"></i>
            <span>定时任务</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header style="text-align: right; font-size: 12px; background: #fff; border-bottom: 1px solid #eee;">
          <div style="line-height: 60px; margin-right: 20px;">
            <span>内容审核队列系统</span>
          </div>
        </el-header>
        <el-main style="background: #f5f7fa;">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>
