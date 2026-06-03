<script setup>
import { useAuthStore } from '@/stores/auth.js'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <header class="app-header">
    <div class="header-content">
      <router-link to="/" class="logo">FuelAC</router-link>
      
      <nav class="nav">
        <router-link v-if="authStore.isAdmin" to="/organizations">Организации</router-link>
        <router-link v-if="authStore.isAdmin" to="/users">Пользователи</router-link>
        <router-link v-if="authStore.canManageWaybills" to="/waybills">Путевые листы</router-link>
        <router-link v-if="authStore.isManager" to="/drivers">Водители</router-link>
        <router-link v-if="authStore.isManager" to="/vehicles">Транспорт</router-link>
        <router-link v-if="authStore.isManager" to="/fuel-norms">Нормы расхода</router-link>
        <router-link v-if="authStore.canManageWaybills" to="/reports">Отчёты</router-link>
      </nav>
      
      <div class="user-section">
        <span class="user-name">{{ authStore.user?.email }}</span>
        <span class="user-role">({{ authStore.user?.role }})</span>
        <button class="logout-btn" @click="logout">Выход</button>
      </div>
    </div>
  </header>
</template>

<style scoped>
.app-header {
  background: #1976d2;
  color: white;
  padding: 0 24px;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: white;
  text-decoration: none;
}

.nav {
  display: flex;
  gap: 20px;
}

.nav a {
  color: white;
  text-decoration: none;
  opacity: 0.9;
  font-size: 14px;
}

.nav a:hover,
.nav a.router-link-active {
  opacity: 1;
  text-decoration: underline;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}

.user-role {
  opacity: 0.8;
  font-size: 11px;
  text-transform: lowercase;
}

.logout-btn {
  background: rgba(255,255,255,0.2);
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
}

.logout-btn:hover {
  background: rgba(255,255,255,0.3);
}
</style>
