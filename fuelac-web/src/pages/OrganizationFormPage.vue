<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { organizationApi } from '@/api/index.js'
import AppHeader from '@/components/common/AppHeader.vue'

const route = useRoute()
const router = useRouter()
const isEdit = ref(!!route.params.id)
const loading = ref(false)
const error = ref(null)
const saving = ref(false)

const form = ref({
  name: '',
  ogrn: '',
  codeOKPO: '',
  codeOKUD: '',
  address: '',
})

const loadOrg = async () => {
  if (!isEdit.value) return
  loading.value = true
  try {
    const response = await organizationApi.findById(route.params.id)
    const o = response.data
    form.value = {
      name: o.name || '',
      ogrn: o.ogrn || '',
      codeOKPO: o.codeOKPO || '',
      codeOKUD: o.codeOKUD || '',
      address: o.address || '',
    }
  } catch (err) {
    error.value = 'Ошибка загрузки организации'
  } finally {
    loading.value = false
  }
}

const save = async () => {
  saving.value = true
  error.value = null
  try {
    if (isEdit.value) {
      await organizationApi.update(route.params.id, form.value)
    } else {
      await organizationApi.create(form.value)
    }
    router.push('/organizations')
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка сохранения'
  } finally {
    saving.value = false
  }
}

const cancel = () => {
  router.push('/organizations')
}

onMounted(loadOrg)
</script>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <h1>{{ isEdit ? 'Редактирование организации' : 'Добавление организации' }}</h1>
      
      <div v-if="error" class="alert alert-error">{{ error }}</div>
      
      <div v-if="loading" class="loading">Загрузка...</div>
      
      <form v-else @submit.prevent="save" class="form">
        <div class="form-grid">
          <div class="form-group">
            <label>Название *</label>
            <input v-model="form.name" required />
          </div>
          
          <div class="form-group">
            <label>ОГРН</label>
            <input v-model="form.ogrn" />
          </div>
          
          <div class="form-group">
            <label>Код ОКПО</label>
            <input v-model="form.codeOKPO" />
          </div>
          
          <div class="form-group">
            <label>Код ОКУД</label>
            <input v-model="form.codeOKUD" />
          </div>
          
          <div class="form-group" style="grid-column: 1 / -1">
            <label>Адрес</label>
            <input v-model="form.address" />
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
