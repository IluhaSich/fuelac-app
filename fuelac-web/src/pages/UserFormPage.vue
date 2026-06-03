<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userApi, organizationApi } from '@/api/index.js'
import AppHeader from '@/components/common/AppHeader.vue'

const route = useRoute()
const router = useRouter()
const isEdit = ref(!!route.params.id)
const loading = ref(false)
const error = ref(null)
const saving = ref(false)

// Roles use Russian values because backend @JsonValue serializes enums as Russian strings
const roles = [
  { value: 'Администратор', label: 'Администратор' },
  { value: 'Менеджер организации', label: 'Менеджер организации' },
  { value: 'Диспетчер', label: 'Диспетчер' },
]

const form = ref({
  lastName: '',
  firstName: '',
  patronymic: '',
  email: '',
  password: '',
  role: 'Диспетчер',
  organizationId: '',
})

const selectedOrganizationName = ref('')

// Organization picker state
const showOrgPicker = ref(false)
const pickerLoading = ref(false)
const pickerError = ref(null)
const pickerItems = ref([])
const pickerSearch = ref('')

const openOrgPicker = async () => {
  showOrgPicker.value = true
  pickerLoading.value = true
  pickerError.value = null
  pickerItems.value = []
  pickerSearch.value = ''
  try {
    const res = await organizationApi.search({ filters: [], sorts: [{ key: 'name', direction: 'ASC' }], page: 0, size: 100 })
    pickerItems.value = res.data.content || []
  } catch (e) {
    pickerError.value = 'Ошибка загрузки организаций'
  } finally {
    pickerLoading.value = false
  }
}

const filteredPickerItems = () => {
  if (!pickerSearch.value) return pickerItems.value
  const s = pickerSearch.value.toLowerCase()
  return pickerItems.value.filter(item => {
    return (item.name || '').toLowerCase().includes(s) ||
           (item.ogrn || '').toLowerCase().includes(s)
  })
}

const selectOrganization = (org) => {
  form.value.organizationId = org.id
  selectedOrganizationName.value = org.name
  showOrgPicker.value = false
}

const loadUser = async () => {
  if (!isEdit.value) return
  loading.value = true
  try {
    const response = await userApi.findById(route.params.id)
    const u = response.data
    form.value = {
      lastName: u.lastName || '',
      firstName: u.firstName || '',
      patronymic: u.patronymic || '',
      email: u.email || '',
      password: '',
      role: u.role || 'Диспетчер',
      organizationId: '',
    }
    selectedOrganizationName.value = ''
  } catch (err) {
    error.value = 'Ошибка загрузки пользователя'
  } finally {
    loading.value = false
  }
}

const save = async () => {
  saving.value = true
  error.value = null
  try {
    if (isEdit.value) {
      await userApi.update(route.params.id, form.value)
    } else {
      await userApi.create(form.value)
    }
    router.push('/users')
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка сохранения'
  } finally {
    saving.value = false
  }
}

const cancel = () => {
  router.push('/users')
}

onMounted(loadUser)
</script>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <h1>{{ isEdit ? 'Редактирование пользователя' : 'Добавление пользователя' }}</h1>
      
      <div v-if="error" class="alert alert-error">{{ error }}</div>
      
      <div v-if="loading" class="loading">Загрузка...</div>
      
      <form v-else @submit.prevent="save" class="form">
        <div class="form-grid">
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
            <label>Email *</label>
            <input v-model="form.email" type="email" required />
          </div>
          
          <div class="form-group">
            <label>Пароль {{ isEdit ? '(оставьте пустым, чтобы не менять)' : '*' }}</label>
            <input v-model="form.password" type="password" :required="!isEdit" />
          </div>
          
          <div class="form-group">
            <label>Роль *</label>
            <select v-model="form.role" required>
              <option v-for="r in roles" :key="r.value" :value="r.value">{{ r.label }}</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>Организация</label>
            <div class="ref-field">
              <span class="ref-value">{{ selectedOrganizationName || 'Не выбрана' }}</span>
              <button type="button" class="ref-btn" @click="openOrgPicker">Выбрать</button>
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

    <!-- Organization Picker Modal -->
    <div v-if="showOrgPicker" class="modal-overlay" @click="showOrgPicker = false">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>Выбор организации</h3>
          <button class="modal-close" @click="showOrgPicker = false">×</button>
        </div>
        <div class="modal-body">
          <input type="text" v-model="pickerSearch" placeholder="Поиск по названию/ОГРН..." class="picker-search" />
          <div v-if="pickerLoading" class="picker-loading">Загрузка...</div>
          <div v-else-if="pickerError" class="picker-error">{{ pickerError }}</div>
          <div v-else class="picker-list">
            <div
              v-for="item in filteredPickerItems()"
              :key="item.id"
              class="picker-item"
              @click="selectOrganization(item)"
            >
              <div class="picker-title">{{ item.name }}</div>
              <div class="picker-sub">ОГРН: {{ item.ogrn }} | ОКПО: {{ item.codeOKPO }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
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

.ref-field {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  min-height: 20px;
}

.ref-value {
  font-size: 14px;
  color: #333;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ref-btn {
  padding: 4px 12px;
  font-size: 12px;
  background: transparent;
  border: 1px solid #1976d2;
  color: #1976d2;
  border-radius: 3px;
  cursor: pointer;
  white-space: nowrap;
}

.ref-btn:hover {
  background: #e3f2fd;
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

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 8px;
  width: 800px;
  max-height: 70vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 20px rgba(0,0,0,0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  font-size: 16px;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.modal-body {
  padding: 16px 20px;
  overflow-y: auto;
  flex: 1;
}

.picker-search {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 12px;
  font-size: 14px;
  box-sizing: border-box;
}

.picker-loading,
.picker-error {
  text-align: center;
  padding: 20px;
  color: #999;
}

.picker-error {
  color: #c62828;
}

.picker-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.picker-item {
  padding: 10px 12px;
  border: 1px solid #eee;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.15s;
}

.picker-item:hover {
  background: #f5f5f5;
  border-color: #1976d2;
}

.picker-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.picker-sub {
  font-size: 12px;
  color: #666;
  margin-top: 2px;
}
</style>
