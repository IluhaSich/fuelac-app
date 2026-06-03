<script setup>
import { useAuthStore } from '@/stores/auth.js'
import AppHeader from '@/components/common/AppHeader.vue'

const authStore = useAuthStore()
</script>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <h1>Панель управления</h1>
      <p>Добро пожаловать, <strong>{{ authStore.user?.email }}</strong>!</p>
      <p>Ваша роль: <code>{{ authStore.user?.role }}</code></p>
      
      <nav class="quick-links">
        <router-link v-if="authStore.isAdmin" to="/organizations" class="card">
          <h3>Организации</h3>
          <p>Управление организациями</p>
        </router-link>
        
        <router-link v-if="authStore.isAdmin" to="/users" class="card">
          <h3>Пользователи</h3>
          <p>Управление пользователями</p>
        </router-link>
        
        <router-link v-if="authStore.canManageWaybills" to="/waybills" class="card">
          <h3>Путевые листы</h3>
          <p>Работа с путевыми листами</p>
        </router-link>
        
        <router-link v-if="authStore.isManager" to="/drivers" class="card">
          <h3>Водители</h3>
          <p>Справочник водителей</p>
        </router-link>
        
        <router-link v-if="authStore.isManager" to="/vehicles" class="card">
          <h3>Транспорт</h3>
          <p>Справочник транспортных средств</p>
        </router-link>
        
        <router-link v-if="authStore.isManager" to="/fuel-norms" class="card">
          <h3>Нормы расхода</h3>
          <p>Нормы расхода топлива</p>
        </router-link>
      </nav>
    </main>
  </div>
</template>

<style scoped>
.layout {
  min-height: 100vh;
}

.main-content {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

h1 {
  margin-bottom: 8px;
}

p {
  margin-bottom: 8px;
  color: #555;
}

.quick-links {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
  margin-top: 24px;
}

.card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  text-decoration: none;
  color: inherit;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  transition: box-shadow 0.2s, transform 0.1s;
}

.card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  transform: translateY(-2px);
}

.card h3 {
  color: #1976d2;
  margin-bottom: 8px;
  font-size: 16px;
}

.card p {
  color: #666;
  font-size: 13px;
  margin: 0;
}

code {
  background: #e3f2fd;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 13px;
}
</style>
