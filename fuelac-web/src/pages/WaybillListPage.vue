<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { waybillApi, driverApi, vehicleApi } from '@/api/index.js'
import { useSearch } from '@/composables/useSearch.js'
import AppHeader from '@/components/common/AppHeader.vue'
import Pagination from '@/components/common/Pagination.vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'

const router = useRouter()

const {
  items: waybills,
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
  fetch: loadWaybills
} = useSearch(waybillApi, {
  defaultSort: { key: 'workStartDate', direction: 'DESC' },
  searchFields: ['number', 'vehicle.registrationNumber', 'driver.lastName', 'driver.firstName']
})

// ─── Filter state ───────────────────────────────────────────────────────────
const statusFilter = ref('')
const dateFrom = ref('')
const dateTo = ref('')
const selectedDriverId = ref('')
const selectedDriverName = ref('')
const selectedVehicleId = ref('')
const selectedVehicleName = ref('')

const statusOptions = [
  { value: '', label: 'Все статусы' },
  { value: 'OPEN', label: 'Открытые' },
  { value: 'CLOSED', label: 'Закрытые' },
]

const applyAllFilters = () => {
  const f = []
  if (statusFilter.value) {
    f.push({ key: 'status', operator: 'EQUAL', fieldType: 'ENUM', value: statusFilter.value, valueTo: null, orGroup: null })
  }
  if (dateFrom.value && dateTo.value) {
    f.push({ key: 'workStartDate', operator: 'BETWEEN', fieldType: 'DATE', value: dateFrom.value + 'T00:00:00', valueTo: dateTo.value + 'T23:59:59', orGroup: null })
  } else if (dateFrom.value) {
    f.push({ key: 'workStartDate', operator: 'GTE', fieldType: 'DATE', value: dateFrom.value + 'T00:00:00', valueTo: null, orGroup: null })
  } else if (dateTo.value) {
    f.push({ key: 'workStartDate', operator: 'LTE', fieldType: 'DATE', value: dateTo.value + 'T23:59:59', valueTo: null, orGroup: null })
  }
  if (selectedDriverId.value) {
    f.push({ key: 'driver.id', operator: 'EQUAL', fieldType: 'STRING', value: selectedDriverId.value, valueTo: null, orGroup: null })
  }
  if (selectedVehicleId.value) {
    f.push({ key: 'vehicle.id', operator: 'EQUAL', fieldType: 'STRING', value: selectedVehicleId.value, valueTo: null, orGroup: null })
  }
  setFilters(f)
}

const hasActiveFilters = () =>
  statusFilter.value || dateFrom.value || dateTo.value || selectedDriverId.value || selectedVehicleId.value

const resetAllFilters = () => {
  statusFilter.value = ''
  dateFrom.value = ''
  dateTo.value = ''
  selectedDriverId.value = ''
  selectedDriverName.value = ''
  selectedVehicleId.value = ''
  selectedVehicleName.value = ''
  setFilters([])
}

// ─── Filter pickers ──────────────────────────────────────────────────────────
const showDriverPicker = ref(false)
const showVehiclePicker = ref(false)
const pickerLoading = ref(false)
const pickerError = ref(null)
const pickerItems = ref([])
const pickerSearch = ref('')

const openDriverPicker = async () => {
  showDriverPicker.value = true
  pickerLoading.value = true
  pickerError.value = null
  pickerItems.value = []
  pickerSearch.value = ''
  try {
    const res = await driverApi.search({ filters: [], sorts: [{ key: 'lastName', direction: 'ASC' }], page: 0, size: 200 })
    pickerItems.value = res.data.content || []
  } catch {
    pickerError.value = 'Ошибка загрузки'
  } finally {
    pickerLoading.value = false
  }
}

const openVehiclePicker = async () => {
  showVehiclePicker.value = true
  pickerLoading.value = true
  pickerError.value = null
  pickerItems.value = []
  pickerSearch.value = ''
  try {
    const res = await vehicleApi.search({ filters: [], sorts: [{ key: 'brand', direction: 'ASC' }], page: 0, size: 200 })
    pickerItems.value = res.data.content || []
  } catch {
    pickerError.value = 'Ошибка загрузки'
  } finally {
    pickerLoading.value = false
  }
}

const filteredPickerItems = () => {
  if (!pickerSearch.value) return pickerItems.value
  const s = pickerSearch.value.toLowerCase()
  return pickerItems.value.filter(item => {
    if (item.fullName) return item.fullName.toLowerCase().includes(s)
    if (item.brand || item.model) return `${item.brand} ${item.model} ${item.registrationNumber}`.toLowerCase().includes(s)
    return false
  })
}

const selectDriverFilter = (driver) => {
  selectedDriverId.value = driver.id
  selectedDriverName.value = driver.fullName
  showDriverPicker.value = false
  applyAllFilters()
}

const clearDriverFilter = () => {
  selectedDriverId.value = ''
  selectedDriverName.value = ''
  applyAllFilters()
}

const selectVehicleFilter = (vehicle) => {
  selectedVehicleId.value = vehicle.id
  selectedVehicleName.value = `${vehicle.brand} ${vehicle.model} (${vehicle.registrationNumber})`
  showVehiclePicker.value = false
  applyAllFilters()
}

const clearVehicleFilter = () => {
  selectedVehicleId.value = ''
  selectedVehicleName.value = ''
  applyAllFilters()
}

// ─── CRUD ─────────────────────────────────────────────────────────────────────
const showDeleteConfirm = ref(false)
const itemToDelete = ref(null)

const goToDetail = (id) => router.push(`/waybills/${id}`)
const goToCreate = () => router.push('/waybills/new')
const goToReports = () => router.push('/reports')

const goToEdit = (id, event) => {
  event.stopPropagation()
  router.push(`/waybills/${id}/edit`)
}

const confirmDelete = (id, event) => {
  event.stopPropagation()
  itemToDelete.value = id
  showDeleteConfirm.value = true
}

const handleDelete = async () => {
  if (!itemToDelete.value) return
  try {
    await waybillApi.deleteById(itemToDelete.value)
    loadWaybills()
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка удаления путевого листа'
  } finally {
    showDeleteConfirm.value = false
    itemToDelete.value = null
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleString('ru-RU')
}

const getStatusClass = (status) => status === 'Открыт' ? 'status-open' : 'status-closed'

onMounted(loadWaybills)
</script>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <div class="page-header">
        <h1>Путевые листы</h1>
        <div class="actions">
          <input
            type="text"
            placeholder="Поиск по номеру, гос.номеру, водителю..."
            @input="search($event.target.value)"
            class="search-input"
          />
          <select v-model="statusFilter" @change="applyAllFilters">
            <option v-for="s in statusOptions" :key="s.value" :value="s.value">{{ s.label }}</option>
          </select>
          <button @click="loadWaybills" :disabled="loading" class="btn-secondary">
            {{ loading ? 'Загрузка...' : 'Обновить' }}
          </button>
          <button @click="goToReports" class="btn-secondary">Отчёты</button>
          <button @click="goToCreate" class="btn-primary">+ Создать</button>
        </div>
      </div>

      <!-- Строка дополнительных фильтров -->
      <div class="filter-row">
        <div class="filter-group">
          <span class="filter-label">Дата с:</span>
          <input type="date" v-model="dateFrom" @change="applyAllFilters" class="date-input" />
        </div>
        <div class="filter-group">
          <span class="filter-label">по:</span>
          <input type="date" v-model="dateTo" @change="applyAllFilters" class="date-input" />
        </div>
        <div class="filter-group">
          <span class="filter-label">Водитель:</span>
          <div class="filter-ref">
            <span class="filter-ref-value">{{ selectedDriverName || 'Все' }}</span>
            <button class="filter-ref-btn" @click="openDriverPicker">▾</button>
            <button v-if="selectedDriverId" class="filter-clear-btn" @click="clearDriverFilter">✕</button>
          </div>
        </div>
        <div class="filter-group">
          <span class="filter-label">ТС:</span>
          <div class="filter-ref">
            <span class="filter-ref-value">{{ selectedVehicleName || 'Все' }}</span>
            <button class="filter-ref-btn" @click="openVehiclePicker">▾</button>
            <button v-if="selectedVehicleId" class="filter-clear-btn" @click="clearVehicleFilter">✕</button>
          </div>
        </div>
        <button v-if="hasActiveFilters()" class="btn-link" @click="resetAllFilters">Сбросить фильтры</button>
      </div>

      <div v-if="error" class="alert alert-error">{{ error }}</div>

      <div class="table-container">
        <table v-if="waybills.length > 0">
          <thead>
            <tr>
              <th @click="setSort('number')" class="sortable">Номер {{ sortIndicator('number') }}</th>
              <th @click="setSort('status')" class="sortable">Статус {{ sortIndicator('status') }}</th>
              <th>ТС</th>
              <th>Водитель</th>
              <th @click="setSort('workStartDate')" class="sortable">Начало {{ sortIndicator('workStartDate') }}</th>
              <th @click="setSort('workEndDate')" class="sortable">Окончание {{ sortIndicator('workEndDate') }}</th>
              <th @click="setSort('calculatedNormativeFuel')" class="sortable">Норма, л {{ sortIndicator('calculatedNormativeFuel') }}</th>
              <th @click="setSort('calculatedActualFuel')" class="sortable">Факт, л {{ sortIndicator('calculatedActualFuel') }}</th>
              <th @click="setSort('calculatedDeviation')" class="sortable">Откл., л {{ sortIndicator('calculatedDeviation') }}</th>
              <th style="width: 100px">Действия</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="w in waybills" :key="w.id" @click="goToDetail(w.id)" class="clickable">
              <td>{{ w.number }}</td>
              <td><span :class="getStatusClass(w.status)">{{ w.status }}</span></td>
              <td>{{ w.vehicle?.brand }} {{ w.vehicle?.model }}</td>
              <td>{{ w.driver?.fullName }}</td>
              <td>{{ formatDate(w.workStartDate) }}</td>
              <td>{{ formatDate(w.workEndDate) }}</td>
              <td>{{ w.calculatedNormativeFuel?.toFixed(2) ?? '—' }}</td>
              <td>{{ w.calculatedActualFuel?.toFixed(2) ?? '—' }}</td>
              <td :class="w.calculatedDeviation > 0 ? 'text-danger' : w.calculatedDeviation < 0 ? 'text-success' : ''">
                {{ w.calculatedDeviation != null ? w.calculatedDeviation.toFixed(2) : '—' }}
              </td>
              <td class="actions-cell">
                <div class="actions-wrap">
                  <button v-if="w.status === 'Открыт'" class="btn-icon" @click="goToEdit(w.id, $event)">✎</button>
                  <button v-if="w.status === 'Открыт'" class="btn-icon btn-delete" @click="confirmDelete(w.id, $event)">🗑</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-else-if="!loading" class="empty">Нет путевых листов</div>
      </div>

      <Pagination
        :current-page="page"
        :total-pages="totalPages"
        :total-elements="totalElements"
        @page-change="setPage"
      />
    </main>

    <!-- Picker: Водитель -->
    <div v-if="showDriverPicker" class="modal-overlay" @click="showDriverPicker = false">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>Выбор водителя</h3>
          <button class="modal-close" @click="showDriverPicker = false">×</button>
        </div>
        <div class="modal-body">
          <input type="text" v-model="pickerSearch" placeholder="Поиск..." class="picker-search" />
          <div v-if="pickerLoading" class="picker-loading">Загрузка...</div>
          <div v-else-if="pickerError" class="picker-error">{{ pickerError }}</div>
          <div v-else class="picker-list">
            <div
              v-for="item in filteredPickerItems()"
              :key="item.id"
              class="picker-item"
              @click="selectDriverFilter(item)"
            >
              <div class="picker-title">{{ item.fullName }}</div>
              <div class="picker-sub">Таб. №: {{ item.personnelNumber }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Picker: ТС -->
    <div v-if="showVehiclePicker" class="modal-overlay" @click="showVehiclePicker = false">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>Выбор транспортного средства</h3>
          <button class="modal-close" @click="showVehiclePicker = false">×</button>
        </div>
        <div class="modal-body">
          <input type="text" v-model="pickerSearch" placeholder="Поиск..." class="picker-search" />
          <div v-if="pickerLoading" class="picker-loading">Загрузка...</div>
          <div v-else-if="pickerError" class="picker-error">{{ pickerError }}</div>
          <div v-else class="picker-list">
            <div
              v-for="item in filteredPickerItems()"
              :key="item.id"
              class="picker-item"
              @click="selectVehicleFilter(item)"
            >
              <div class="picker-title">{{ item.brand }} {{ item.model }}</div>
              <div class="picker-sub">{{ item.registrationNumber }} / {{ item.garageNumber }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <ConfirmDialog
      :show="showDeleteConfirm"
      title="Удаление путевого листа"
      message="Вы уверены, что хотите удалить этот путевой лист?"
      @confirm="handleDelete"
      @cancel="showDeleteConfirm = false; itemToDelete = null"
    />
  </div>
</template>

<style scoped>
.main-content {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
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
  width: 280px;
}

select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
}

/* Filter row */
.filter-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  background: #f9f9f9;
  border: 1px solid #eee;
  border-radius: 6px;
  padding: 10px 14px;
  margin-bottom: 16px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 6px;
}

.filter-label {
  font-size: 13px;
  color: #666;
  white-space: nowrap;
}

.date-input {
  padding: 6px 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 13px;
}

.filter-ref {
  display: flex;
  align-items: center;
  gap: 4px;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 4px 8px;
  background: white;
  min-width: 160px;
  max-width: 220px;
}

.filter-ref-value {
  font-size: 13px;
  color: #333;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.filter-ref-btn {
  padding: 0 4px;
  background: transparent;
  border: none;
  cursor: pointer;
  color: #666;
  font-size: 12px;
}

.filter-ref-btn:hover {
  color: #1976d2;
}

.filter-clear-btn {
  padding: 0 4px;
  background: transparent;
  border: none;
  cursor: pointer;
  color: #999;
  font-size: 12px;
}

.filter-clear-btn:hover {
  color: #d32f2f;
}

.btn-link {
  background: none;
  border: none;
  color: #1976d2;
  font-size: 13px;
  cursor: pointer;
  padding: 4px 8px;
  text-decoration: underline;
}

.btn-link:hover {
  color: #1565c0;
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
  padding: 10px 12px;
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

.actions-cell .actions-wrap {
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

.status-open {
  background: #e3f2fd;
  color: #1565c0;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-closed {
  background: #e8f5e9;
  color: #2e7d32;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.text-danger {
  color: #d32f2f;
  font-weight: 600;
}

.text-success {
  color: #2e7d32;
  font-weight: 600;
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

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 8px;
  width: 600px;
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

.picker-loading, .picker-error {
  text-align: center;
  padding: 20px;
  color: #999;
}

.picker-error { color: #c62828; }

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
