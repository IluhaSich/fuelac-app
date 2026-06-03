<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { vehicleApi } from '@/api/index.js'
import { useSearch } from '@/composables/useSearch.js'
import AppHeader from '@/components/common/AppHeader.vue'
import Pagination from '@/components/common/Pagination.vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'

const router = useRouter()

const {
  items: vehicles,
  loading,
  error,
  page,
  totalElements,
  totalPages,
  setPage,
  setSort,
  setFilters,
  search,
  sortIndicator,
  fetch: loadVehicles,
  buildSearchRequest
} = useSearch(vehicleApi, {
  defaultSort: { key: 'createdAt', direction: 'DESC' },
  searchFields: ['registrationNumber', 'brand', 'model']
})

// Type filter
const typeOptions = [
  { value: '', label: 'Все типы' },
  { value: 'LIGHT', label: 'Легковой' },
  { value: 'TRUCK', label: 'Грузовой' },
  { value: 'SPECIAL', label: 'Спецтехника' },
]

const selectedType = ref('')

const applyAllFilters = () => {
  const f = []
  if (selectedType.value) {
    f.push({ key: 'type', operator: 'EQUAL', fieldType: 'ENUM', value: selectedType.value, valueTo: null, orGroup: null })
  }
  setFilters(f)
}

const exportLoading = ref(false)
const exportError = ref(null)

const exportExcel = async () => {
  exportLoading.value = true
  exportError.value = null
  try {
    const response = await vehicleApi.export(buildSearchRequest())
    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = 'Транспортные_средства.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (e) {
    exportError.value = 'Ошибка экспорта'
    console.error(e)
  } finally {
    exportLoading.value = false
  }
}

// Delete confirmation
const showDeleteConfirm = ref(false)
const itemToDelete = ref(null)

const goToDetail = (id) => {
  router.push(`/vehicles/${id}`)
}

const goToCreate = () => {
  router.push('/vehicles/new')
}

const goToEdit = (id, event) => {
  event.stopPropagation()
  router.push(`/vehicles/${id}/edit`)
}

const confirmDelete = (id, event) => {
  event.stopPropagation()
  itemToDelete.value = id
  showDeleteConfirm.value = true
}

const handleDelete = async () => {
  if (!itemToDelete.value) return
  try {
    await vehicleApi.deleteById(itemToDelete.value)
    loadVehicles()
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка удаления транспортного средства'
  } finally {
    showDeleteConfirm.value = false
    itemToDelete.value = null
  }
}

const getTypeLabel = (type) => {
  const map = { LIGHT: 'Легковой', TRUCK: 'Грузовой', SPECIAL: 'Спецтехника' }
  return map[type] || type
}

onMounted(loadVehicles)
</script>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <div class="page-header">
        <h1>Транспортные средства</h1>
        <div class="actions">
          <input
            type="text"
            placeholder="Поиск по марке, модели, рег. номеру..."
            @input="search($event.target.value)"
            class="search-input"
          />
          <select v-model="selectedType" @change="applyAllFilters">
            <option v-for="t in typeOptions" :key="t.value" :value="t.value">{{ t.label }}</option>
          </select>
          <button @click="loadVehicles" :disabled="loading" class="btn-secondary">
            {{ loading ? 'Загрузка...' : 'Обновить' }}
          </button>
          <button @click="exportExcel" :disabled="exportLoading" class="btn-secondary">
            {{ exportLoading ? 'Экспорт...' : '↓ Excel' }}
          </button>
          <button @click="goToCreate" class="btn-primary">+ Добавить</button>
        </div>
      </div>

      <div v-if="error" class="alert alert-error">{{ error }}</div>
      <div v-if="exportError" class="alert alert-error">{{ exportError }}</div>

      <div class="table-container">
        <table v-if="vehicles.length > 0">
          <thead>
            <tr>
              <th>ТС</th>
              <th @click="setSort('registrationNumber')" class="sortable">Рег. номер {{ sortIndicator('registrationNumber') }}</th>
              <th>Гаражный №</th>
              <th @click="setSort('type')" class="sortable">Тип {{ sortIndicator('type') }}</th>
              <th @click="setSort('year')" class="sortable">Год {{ sortIndicator('year') }}</th>
              <th style="width: 100px">Действия</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="v in vehicles" :key="v.id" @click="goToDetail(v.id)" class="clickable">
              <td>{{ v.brand }} {{ v.model }}</td>
              <td>{{ v.registrationNumber }}</td>
              <td>{{ v.garageNumber }}</td>
              <td>{{ getTypeLabel(v.type) }}</td>
              <td>{{ v.year }}</td>
              <td class="actions-cell">
                <button class="btn-icon" @click="goToEdit(v.id, $event)">✎</button>
                <button class="btn-icon btn-delete" @click="confirmDelete(v.id, $event)">🗑</button>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-else-if="!loading" class="empty">Нет транспортных средств</div>
      </div>

      <Pagination 
        :current-page="page" 
        :total-pages="totalPages" 
        :total-elements="totalElements"
        @page-change="setPage"
      />
    </main>

    <ConfirmDialog
      :show="showDeleteConfirm"
      title="Удаление ТС"
      message="Вы уверены, что хотите удалить это транспортное средство?"
      @confirm="handleDelete"
      @cancel="showDeleteConfirm = false; itemToDelete = null"
    />
  </div>
</template>

<style scoped>
.main-content {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.search-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 200px;
}

select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
}

button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-primary {
  background: #1976d2;
  color: white;
}

.btn-primary:hover {
  background: #1565c0;
}

.btn-secondary {
  background: #f5f5f5;
  color: #333;
  border: 1px solid #ddd;
}

.btn-secondary:hover:not(:disabled) {
  background: #e0e0e0;
}

button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  background: #f5f5f5;
  font-weight: 600;
  color: #555;
}

th.sortable {
  cursor: pointer;
  user-select: none;
}

th.sortable:hover {
  background: #e8e8e8;
}

.clickable {
  cursor: pointer;
}

.clickable:hover {
  background: #f9f9f9;
}

.actions-cell {
  display: flex;
  gap: 4px;
  align-items: center;
}

.btn-icon {
  padding: 4px 8px;
  background: transparent;
  border: 1px solid transparent;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  line-height: 1;
}

.btn-icon:hover {
  background: #e3f2fd;
  border-color: #1976d2;
  color: #1976d2;
}

.btn-delete:hover {
  background: #ffebee;
  border-color: #d32f2f;
  color: #d32f2f;
}

.empty {
  padding: 40px;
  text-align: center;
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
</style>
