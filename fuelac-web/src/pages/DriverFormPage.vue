<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { driverApi } from '@/api/index.js'
import AppHeader from '@/components/common/AppHeader.vue'

const route = useRoute()
const router = useRouter()
const isEdit = ref(!!route.params.id)
const loading = ref(false)
const error = ref(null)
const saving = ref(false)

const form = ref({
  personnelNumber: '',
  lastName: '',
  firstName: '',
  patronymic: '',
  snils: '',
  licenseNumber: '',
  licenseCategory: '',
  licenseIssueDate: '',
  licenseExpirationDate: '',
})

const loadDriver = async () => {
  if (!isEdit.value) return
  loading.value = true
  try {
    const response = await driverApi.findById(route.params.id)
    const d = response.data
    form.value = {
      personnelNumber: d.personnelNumber || '',
      lastName: d.lastName || '',
      firstName: d.firstName || '',
      patronymic: d.patronymic || '',
      snils: d.snils || '',
      licenseNumber: d.licenseNumber || '',
      licenseCategory: d.licenseCategory || '',
      licenseIssueDate: d.licenseIssueDate || '',
      licenseExpirationDate: d.licenseExpirationDate || '',
    }
  } catch (err) {
    error.value = 'Ошибка загрузки данных водителя'
  } finally {
    loading.value = false
  }
}

const save = async () => {
  saving.value = true
  error.value = null
  try {
    if (isEdit.value) {
      await driverApi.update(route.params.id, form.value)
    } else {
      await driverApi.create(form.value)
    }
    router.push('/drivers')
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка сохранения'
  } finally {
    saving.value = false
  }
}

const cancel = () => {
  router.push('/drivers')
}

onMounted(loadDriver)
</script>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <h1>{{ isEdit ? 'Редактирование водителя' : 'Добавление водителя' }}</h1>
      
      <div v-if="error" class="alert alert-error">{{ error }}</div>
      
      <div v-if="loading" class="loading">Загрузка...</div>
      
      <form v-else @submit.prevent="save" class="form">
        <!-- Основные данные -->
        <div class="form-section">
          <h3 class="section-title">Основные данные</h3>
          <div class="form-grid">
            <div class="form-group">
              <label>Табельный номер *</label>
              <input v-model="form.personnelNumber" required />
            </div>
            
            <div class="form-group">
              <label>Фамилия *</label>
              <input v-model="form.lastName" required />
            </div>
            
            <div class="form-group">
              <label>Имя *</label>
              <input v-model="form.firstName" required />
            </div>
            
            <div class="form-group">
              <label>Отчество</label>
              <input v-model="form.patronymic" />
            </div>
            
            <div class="form-group">
              <label>СНИЛС *</label>
              <input v-model="form.snils" required />
            </div>
          </div>
        </div>
        
        <!-- Водительское удостоверение -->
        <div class="form-section license-section">
          <h3 class="section-title">Водительское удостоверение</h3>
          <div class="form-grid">
            <div class="form-group">
              <label>Номер *</label>
              <input v-model="form.licenseNumber" required />
            </div>
            
            <div class="form-group">
              <label>Категория *</label>
              <input v-model="form.licenseCategory" placeholder="B, C, D..." required />
            </div>
            
            <div class="form-group">
              <label>Дата выдачи *</label>
              <input v-model="form.licenseIssueDate" type="date" required />
            </div>
            
            <div class="form-group">
              <label>Действует до *</label>
              <input v-model="form.licenseExpirationDate" type="date" required />
            </div>
          </div>
        </div>
        
        <div class="form-actions">
          <button type="button" @click="cancel" class="btn-secondary">Отмена</button>
          <button type="submit" :disabled="saving" class="btn-primary">
            {{ saving ? 'Сохранение...' : 'Сохранить' }}
          </button>
        </div>
      </form>
    </main>
  </div>
</template>

<style scoped>
.main-content {
  padding: 24px;
  max-width: 800px;
  margin: 0 auto;
}

h1 {
  margin-bottom: 20px;
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
  margin-bottom: 16px;
  border: 1px solid #ef9a9a;
}

.form {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.form-section {
  margin-bottom: 24px;
}

.form-section:last-of-type {
  margin-bottom: 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #e0e0e0;
}

.license-section {
  background: #fafafa;
  padding: 20px;
  border-radius: 6px;
  border: 1px solid #e8e8e8;
}

.license-section .section-title {
  border-bottom-color: #1976d2;
  color: #1976d2;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

label {
  font-weight: 500;
  font-size: 14px;
  color: #555;
}

input {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

input:focus {
  outline: none;
  border-color: #1976d2;
}

.form-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

button {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-primary {
  background: #1976d2;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #1565c0;
}

.btn-secondary {
  background: #f5f5f5;
  color: #333;
  border: 1px solid #ddd;
}

.btn-secondary:hover {
  background: #e0e0e0;
}

button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
</style>
