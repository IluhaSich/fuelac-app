<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { driverApi } from '@/api/index.js'
import { useSearch } from '@/composables/useSearch.js'
import AppHeader from '@/components/common/AppHeader.vue'
import Pagination from '@/components/common/Pagination.vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'

const router = useRouter()

const {
  items: drivers,
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
  fetch: loadDrivers,
  buildSearchRequest
} = useSearch(driverApi, {
  defaultSort: { key: 'createdAt', direction: 'DESC' },
  searchFields: ['lastName', 'firstName', 'patronymic']
})

// ─── Category filter ─────────────────────────────────────────────────────────
const categorySearch = ref('')

const applyAllFilters = () => {
  const f = []
  if (categorySearch.value.trim()) {
    f.push({ key: 'license.category', operator: 'LIKE', fieldType: 'STRING', value: categorySearch.value.trim(), valueTo: null, orGroup: null })
  }
  setFilters(f)
}

// ─── Export ──────────────────────────────────────────────────────────────────
const exportLoading = ref(false)
const exportError = ref(null)

const exportExcel = async () => {
  exportLoading.value = true
  exportError.value = null
  try {
    const response = await driverApi.export(buildSearchRequest())
    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = 'Водители.xlsx'
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

// ─── CRUD ─────────────────────────────────────────────────────────────────────
const showDeleteConfirm = ref(false)
const itemToDelete = ref(null)

const goToDetail = (id) => router.push(`/drivers/${id}`)
const goToCreate = () => router.push('/drivers/new')

const goToEdit = (id, event) => {
  event.stopPropagation()
  router.push(`/drivers/${id}/edit`)
}

const confirmDelete = (id, event) => {
  event.stopPropagation()
  itemToDelete.value = id
  showDeleteConfirm.value = true
}

const handleDelete = async () => {
  if (!itemToDelete.value) return
  try {
    await driverApi.deleteById(itemToDelete.value)
    loadDrivers()
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка удаления водителя'
  } finally {
    showDeleteConfirm.value = false
    itemToDelete.value = null
  }
}

const getLicenseStatus = (driver) => {
  if (!driver.licenseExpirationDate) return '—'
  const exp = new Date(driver.licenseExpirationDate)
  return exp > new Date() ? 'Действителен' : 'Просрочено'
}

const getLicenseStatusClass = (driver) => {
  if (!driver.licenseExpirationDate) return ''
  return new Date(driver.licenseExpirationDate) > new Date() ? 'text-success' : 'text-danger'
}

onMounted(loadDrivers)
</script>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <div class="page-header">
        <h1>Водители</h1>
        <div class="actions">
          <input
            type="text"
            placeholder="Поиск по ФИО..."
            @input="search($event.target.value)"
            class="search-input"
          />
          <input
            type="text"
            placeholder="Категория ВУ (B, C, CE...)"
            v-model="categorySearch"
            @input="applyAllFilters"
            class="search-input category-input"
          />
          <button @click="loadDrivers" :disabled="loading" class="btn-secondary">
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
        <table v-if="drivers.length > 0">
          <thead>
            <tr>
              <th @click="setSort('personnelNumber')" class="sortable">Таб. номер {{ sortIndicator('personnelNumber') }}</th>
              <th @click="setSort('lastName')" class="sortable">ФИО {{ sortIndicator('lastName') }}</th>
              <th>СНИЛС</th>
              <th>Удостоверение</th>
              <th>Категория</th>
              <th>Удостоверение действительно</th>
              <th style="width: 100px">Действия</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="d in drivers" :key="d.id" @click="goToDetail(d.id)" class="clickable">
              <td>{{ d.personnelNumber }}</td>
              <td>{{ d.fullName }}</td>
              <td>{{ d.snils }}</td>
              <td>{{ d.licenseNumber }}</td>
              <td>{{ d.licenseCategory }}</td>
              <td :class="getLicenseStatusClass(d)">{{ getLicenseStatus(d) }}</td>
              <td class="actions-cell">
                <button class="btn-icon" @click="goToEdit(d.id, $event)">✎</button>
                <button class="btn-icon btn-delete" @click="confirmDelete(d.id, $event)">🗑</button>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-else-if="!loading" class="empty">Нет водителей</div>
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
      title="Удаление водителя"
      message="Вы уверены, что хотите удалить этого водителя?"
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
  align-items: flex-start;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.actions {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 200px;
}

.category-input {
  width: 160px;
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

.text-success {
  color: #2e7d32;
}

.text-danger {
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
