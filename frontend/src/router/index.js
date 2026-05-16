import Vue from 'vue'
import VueRouter from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import ContentSubmit from '../views/ContentSubmit.vue'
import ReviewWorkbench from '../views/ReviewWorkbench.vue'
import AppealManage from '../views/AppealManage.vue'
import BanApproval from '../views/BanApproval.vue'
import ContentList from '../views/ContentList.vue'
import UserList from '../views/UserList.vue'
import SchedulerManage from '../views/SchedulerManage.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/submit',
    name: 'ContentSubmit',
    component: ContentSubmit
  },
  {
    path: '/review',
    name: 'ReviewWorkbench',
    component: ReviewWorkbench
  },
  {
    path: '/appeal',
    name: 'AppealManage',
    component: AppealManage
  },
  {
    path: '/ban',
    name: 'BanApproval',
    component: BanApproval
  },
  {
    path: '/contents',
    name: 'ContentList',
    component: ContentList
  },
  {
    path: '/users',
    name: 'UserList',
    component: UserList
  },
  {
    path: '/scheduler',
    name: 'SchedulerManage',
    component: SchedulerManage
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
