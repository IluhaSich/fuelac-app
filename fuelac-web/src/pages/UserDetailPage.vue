<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userApi } from '@/api/index.js'
import AppHeader from '@/components/common/AppHeader.vue'

const route = useRoute()
const router = useRouter()
const user = ref(null)
const loading = ref(false)
const error = ref(null)

const loadUser = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await userApi.findById(route.params.id)
    user.value = response.data
  } catch (err) {
    error.value = 'Ошибка загрузки пользователя'
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/users')
}

const goToEdit = () => {
  router.push(`/users/${route.params.id}/edit`)
}

const getRoleLabel = (role) => {
  const map = {
    ADMIN: 'Администратор',
    ORGANIZATION_MANAGER: 'Менеджер',
    DISPATCHER: 'Диспетчер',
  }
  return map[role] || role
}

onMounted(loadUser)
</script>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <button class="back-btn" @click="goBack">← Назад к списку</button>
      
      <div v-if="loading" class="loading">Загрузка...</div>
      <div v-else-if="error" class="alert alert-error">{{ error }}</div>
      
      <div v-else-if="user" class="detail-card">
        <div class="detail-header">
          <h1>{{ user.fullName }}</h1>
          <button class="btn-edit" @click="goToEdit">Редактировать</button>
        </div>

        <div class="detail-grid">
          <div class="detail-section">
            <h3>Основная информация</h3>
            <div class="field"><label>ФИО:</label> <span>{{ user.fullName }}</span></div>
            <div class="field"><label>Email:</label> <span>{{ user.email }}</span></div>
            <div class="field"><label>Роль:</label> <span class="role-badge">{{ getRoleLabel(user.role) }}</span></div>
          </div>

          <div class="detail-section">
            <h3>Системная информация</h3>
            <div class="field"><label>Создан:</label> <span>{{ new Date(user.createdAt).toLocaleString('ru-RU') }}</span></div>
            <div class="field"><label>Обновлён:</label> <span>{{ new Date(user.updatedAt).toLocaleString('ru-RU') }}</span></div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.main-content {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.back-btn {
  margin-bottom: 16px;
  padding: 6px 12px;
  background: transparent;
  border: 1px solid #1976d2;
  color: #1976d2;
  border-radius: 4px;
  cursor: pointer;
}

.back-btn:hover {
  background: #e3f2fd;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #999;
}

.alert-error {
  background: #ffebee;
  color: #c62828;
  padding: 12px 16px;
  border-radius: 4px;
  border: 1px solid #ef9a9a;
}

.detail-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  padding: 24px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.detail-header h1 {
  margin: 0;
  font-size: 20px;
}

.btn-edit {
  padding: 8px 16px;
  background: #1976d2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-edit:hover {
  background: #1565c0;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.detail-section {
  background: #fafafa;
  padding: 16px;
  border-radius: 6px;
}

.detail-section h3 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #555;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.field {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  border-bottom: 1px solid #eee;
  font-size: 14px;
}

.field:last-child {
  border-bottom: none;
}

.field label {
  color: #666;
  font-weight: 500;
}

.role-badge {
  background: #e3f2fd;
  color: #1565c0;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}
</style>
