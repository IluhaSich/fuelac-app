<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { driverApi } from '@/api/index.js'
import AppHeader from '@/components/common/AppHeader.vue'

const route = useRoute()
const router = useRouter()
const driver = ref(null)
const loading = ref(false)
const error = ref(null)

const loadDriver = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await driverApi.findById(route.params.id)
    driver.value = response.data
  } catch (err) {
    error.value = 'Ошибка загрузки водителя'
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/drivers')
}

const goToEdit = () => {
  router.push(`/drivers/${route.params.id}/edit`)
}

const getLicenseStatus = () => {
  if (!driver.value?.licenseExpirationDate) return '—'
  const exp = new Date(driver.value.licenseExpirationDate)
  return exp > new Date() ? 'Действителен' : 'Просрочено'
}

onMounted(loadDriver)
</script>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <button class="back-btn" @click="goBack">← Назад к списку</button>
      
      <div v-if="loading" class="loading">Загрузка...</div>
      <div v-else-if="error" class="alert alert-error">{{ error }}</div>
      
      <div v-else-if="driver" class="detail-card">
        <div class="detail-header">
          <h1>{{ driver.fullName }}</h1>
          <button class="btn-edit" @click="goToEdit">Редактировать</button>
        </div>

        <div class="detail-grid">
          <div class="detail-section">
            <h3>Основная информация</h3>
            <div class="field"><label>Табельный номер:</label> <span>{{ driver.personnelNumber }}</span></div>
            <div class="field"><label>ФИО:</label> <span>{{ driver.fullName }}</span></div>
            <div class="field"><label>СНИЛС:</label> <span>{{ driver.snils }}</span></div>
          </div>

          <div class="detail-section">
            <h3>Водительское удостоверение</h3>
            <div class="field"><label>Номер:</label> <span>{{ driver.licenseNumber }}</span></div>
            <div class="field"><label>Категория:</label> <span>{{ driver.licenseCategory }}</span></div>
            <div class="field"><label>Дата выдачи:</label> <span>{{ driver.licenseIssueDate }}</span></div>
            <div class="field"><label>Действует до:</label> <span>{{ driver.licenseExpirationDate }}</span></div>
            <div class="field"><label>Статус:</label> <span :class="getLicenseStatus() === 'Действителен' ? 'text-success' : getLicenseStatus() === '—' ? '' : 'text-danger'">{{ getLicenseStatus() }}</span></div>
          </div>

          <div class="detail-section">
            <h3>Системная информация</h3>
            <div class="field"><label>Создан:</label> <span>{{ new Date(driver.createdAt).toLocaleString('ru-RU') }}</span></div>
            <div class="field"><label>Обновлён:</label> <span>{{ new Date(driver.updatedAt).toLocaleString('ru-RU') }}</span></div>
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

.text-success {
  color: #2e7d32;
  font-weight: 600;
}

.text-danger {
  color: #d32f2f;
  font-weight: 600;
}
</style>
