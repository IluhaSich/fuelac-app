import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/pages/LoginPage.vue'),
      meta: { public: true },
    },
    {
      path: '/',
      name: 'Dashboard',
      component: () => import('@/pages/DashboardPage.vue'),
      meta: { requiresAuth: true },
    },
    // Waybills
    {
      path: '/waybills',
      name: 'Waybills',
      component: () => import('@/pages/WaybillListPage.vue'),
      meta: { requiresAuth: true, roles: ['DISPATCHER', 'ORGANIZATION_MANAGER'] },
    },
    {
      path: '/waybills/new',
      name: 'WaybillCreate',
      component: () => import('@/pages/WaybillFormPage.vue'),
      meta: { requiresAuth: true, roles: ['DISPATCHER', 'ORGANIZATION_MANAGER'] },
    },
    {
      path: '/waybills/:id',
      name: 'WaybillDetail',
      component: () => import('@/pages/WaybillDetailPage.vue'),
      meta: { requiresAuth: true, roles: ['DISPATCHER', 'ORGANIZATION_MANAGER'] },
    },
    {
      path: '/waybills/:id/edit',
      name: 'WaybillEdit',
      component: () => import('@/pages/WaybillFormPage.vue'),
      meta: { requiresAuth: true, roles: ['DISPATCHER', 'ORGANIZATION_MANAGER'] },
    },
    // Drivers
    {
      path: '/drivers',
      name: 'Drivers',
      component: () => import('@/pages/DriverListPage.vue'),
      meta: { requiresAuth: true, roles: ['ORGANIZATION_MANAGER'] },
    },
    {
      path: '/drivers/new',
      name: 'DriverCreate',
      component: () => import('@/pages/DriverFormPage.vue'),
      meta: { requiresAuth: true, roles: ['ORGANIZATION_MANAGER'] },
    },
    {
      path: '/drivers/:id',
      name: 'DriverDetail',
      component: () => import('@/pages/DriverDetailPage.vue'),
      meta: { requiresAuth: true, roles: ['ORGANIZATION_MANAGER'] },
    },
    {
      path: '/drivers/:id/edit',
      name: 'DriverEdit',
      component: () => import('@/pages/DriverFormPage.vue'),
      meta: { requiresAuth: true, roles: ['ORGANIZATION_MANAGER'] },
    },
    // Vehicles
    {
      path: '/vehicles',
      name: 'Vehicles',
      component: () => import('@/pages/VehicleListPage.vue'),
      meta: { requiresAuth: true, roles: ['ORGANIZATION_MANAGER'] },
    },
    {
      path: '/vehicles/new',
      name: 'VehicleCreate',
      component: () => import('@/pages/VehicleFormPage.vue'),
      meta: { requiresAuth: true, roles: ['ORGANIZATION_MANAGER'] },
    },
    {
      path: '/vehicles/:id',
      name: 'VehicleDetail',
      component: () => import('@/pages/VehicleDetailPage.vue'),
      meta: { requiresAuth: true, roles: ['ORGANIZATION_MANAGER'] },
    },
    {
      path: '/vehicles/:id/edit',
      name: 'VehicleEdit',
      component: () => import('@/pages/VehicleFormPage.vue'),
      meta: { requiresAuth: true, roles: ['ORGANIZATION_MANAGER'] },
    },
    // Fuel Norms
    {
      path: '/fuel-norms',
      name: 'FuelNorms',
      component: () => import('@/pages/FuelNormListPage.vue'),
      meta: { requiresAuth: true, roles: ['ORGANIZATION_MANAGER'] },
    },
    {
      path: '/fuel-norms/new',
      name: 'FuelNormCreate',
      component: () => import('@/pages/FuelNormFormPage.vue'),
      meta: { requiresAuth: true, roles: ['ORGANIZATION_MANAGER'] },
    },
    {
      path: '/fuel-norms/:id',
      name: 'FuelNormDetail',
      component: () => import('@/pages/FuelNormDetailPage.vue'),
      meta: { requiresAuth: true, roles: ['ORGANIZATION_MANAGER'] },
    },
    {
      path: '/fuel-norms/:id/edit',
      name: 'FuelNormEdit',
      component: () => import('@/pages/FuelNormFormPage.vue'),
      meta: { requiresAuth: true, roles: ['ORGANIZATION_MANAGER'] },
    },
    // Organizations (Admin)
    {
      path: '/organizations',
      name: 'Organizations',
      component: () => import('@/pages/OrganizationListPage.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/organizations/new',
      name: 'OrganizationCreate',
      component: () => import('@/pages/OrganizationFormPage.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/organizations/:id',
      name: 'OrganizationDetail',
      component: () => import('@/pages/OrganizationDetailPage.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/organizations/:id/edit',
      name: 'OrganizationEdit',
      component: () => import('@/pages/OrganizationFormPage.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    // Users (Admin)
    {
      path: '/users',
      name: 'Users',
      component: () => import('@/pages/UserListPage.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/users/new',
      name: 'UserCreate',
      component: () => import('@/pages/UserFormPage.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/users/:id',
      name: 'UserDetail',
      component: () => import('@/pages/UserDetailPage.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    {
      path: '/users/:id/edit',
      name: 'UserEdit',
      component: () => import('@/pages/UserFormPage.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
    },
    // Reports
    {
      path: '/reports',
      name: 'Reports',
      component: () => import('@/pages/ReportsPage.vue'),
      meta: { requiresAuth: true, roles: ['DISPATCHER', 'ORGANIZATION_MANAGER'] },
    },
    // 404
    {
      path: '/:pathMatch(.*)*',
      redirect: '/',
    },
  ],
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return next('/login')
  }

  if (to.meta.public && authStore.isAuthenticated) {
    return next('/')
  }

  if (to.meta.roles && !to.meta.roles.includes(authStore.user?.role)) {
    return next('/')
  }

  next()
})

export default router
