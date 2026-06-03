<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { vehicleApi } from '@/api/index.js'
import AppHeader from '@/components/common/AppHeader.vue'

const route = useRoute()
const router = useRouter()
const isEdit = ref(!!route.params.id)
const loading = ref(false)
const error = ref(null)
const saving = ref(false)

const types = [
  { value: 'LIGHT', label: 'Легковой' },
  { value: 'TRUCK', label: 'Грузовой' },
  { value: 'SPECIAL', label: 'Спецтехника' },
]

const form = ref({
  type: 'LIGHT',
  brand: '',
  model: '',
  year: new Date().getFullYear(),
  registrationNumber: '',
  garageNumber: '',
})

const loadVehicle = async () => {
  if (!isEdit.value) return
  loading.value = true
  try {
    const response = await vehicleApi.findById(route.params.id)
    const v = response.data
    form.value = {
      type: v.type || 'LIGHT',
      brand: v.brand || '',
      model: v.model || '',
      year: v.year || new Date().getFullYear(),
      registrationNumber: v.registrationNumber || '',
      garageNumber: v.garageNumber || '',
    }
  } catch (err) {
    error.value = 'Ошибка загрузки данных ТС'
  } finally {
    loading.value = false
  }
}

const save = async () => {
  saving.value = true
  error.value = null
  try {
    if (isEdit.value) {
      await vehicleApi.update(route.params.id, form.value)
    } else {
      await vehicleApi.create(form.value)
    }
    router.push('/vehicles')
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка сохранения'
  } finally {
    saving.value = false
  }
}

const cancel = () => {
  router.push('/vehicles')
}

onMounted(loadVehicle)
</script>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <h1>{{ isEdit ? 'Редактирование ТС' : 'Добавление ТС' }}</h1>
      
      <div v-if="error" class="alert alert-error">{{ error }}</div>
      
      <div v-if="loading" class="loading">Загрузка...</div>
      
      <form v-else @submit.prevent="save" class="form">
        <div class="form-grid">
          <div class="form-group">
            <label>Тип *</label>
            <select v-model="form.type" required>
              <option v-for="t in types" :key="t.value" :value="t.value">{{ t.label }}</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>Марка *</label>
            <input v-model="form.brand" required />
          </div>
          
          <div class="form-group">
            <label>Модель *</label>
            <input v-model="form.model" required />
          </div>
          
          <div class="form-group">
            <label>Год выпуска</label>
            <input v-model="form.year" type="number" />
          </div>
          
          <div class="form-group">
            <label>Регистрационный номер</label>
            <input v-model="form.registrationNumber" />
          </div>
          
          <div class="form-group">
            <label>Гаражный номер</label>
            <input v-model="form.garageNumber" />
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

input, select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  background: white;
}

input:focus, select:focus {
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
