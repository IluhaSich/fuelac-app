<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { fuelNormApi, vehicleApi } from '@/api/index.js'
import { useSearch } from '@/composables/useSearch.js'
import AppHeader from '@/components/common/AppHeader.vue'
import Pagination from '@/components/common/Pagination.vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'

const router = useRouter()

const {
  items: norms,
  loading,
  error,
  page,
  totalElements,
  totalPages,
  setPage,
  setSort,
  setFilters,
  sortIndicator,
  fetch: loadNorms,
  buildSearchRequest
} = useSearch(fuelNormApi, {
  defaultSort: { key: 'createdAt', direction: 'DESC' }
})

// ─── Filters ──────────────────────────────────────────────────────────────────
// 'all' | 'active' | 'expired'  (default: active)
const activeFilter = ref('active')
const selectedVehicleId = ref('')
const selectedVehicleName = ref('')

const applyAllFilters = () => {
  const f = []
  const today = new Date().toISOString().split('T')[0]
  if (activeFilter.value === 'active') {
    f.push({ key: 'validFrom', operator: 'LTE', fieldType: 'DATE', value: today, valueTo: null, orGroup: null })
    f.push({ key: 'validTo', operator: 'GTE', fieldType: 'DATE', value: today, valueTo: null, orGroup: null })
  } else if (activeFilter.value === 'expired') {
    f.push({ key: 'validTo', operator: 'LT', fieldType: 'DATE', value: today, valueTo: null, orGroup: null })
  }
  if (selectedVehicleId.value) {
    f.push({ key: 'vehicle.id', operator: 'EQUAL', fieldType: 'STRING', value: selectedVehicleId.value, valueTo: null, orGroup: null })
  }
  setFilters(f)
}

const clearVehicleFilter = () => {
  selectedVehicleId.value = ''
  selectedVehicleName.value = ''
  applyAllFilters()
}

// ─── Vehicle picker ───────────────────────────────────────────────────────────
const showVehiclePicker = ref(false)
const pickerLoading = ref(false)
const pickerError = ref(null)
const pickerItems = ref([])
const pickerSearch = ref('')

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
    pickerError.value = 'Ошибка загрузки ТС'
  } finally {
    pickerLoading.value = false
  }
}

const filteredPickerItems = () => {
  if (!pickerSearch.value) return pickerItems.value
  const s = pickerSearch.value.toLowerCase()
  return pickerItems.value.filter(item =>
    `${item.brand} ${item.model} ${item.registrationNumber}`.toLowerCase().includes(s)
  )
}

const selectVehicleFilter = (vehicle) => {
  selectedVehicleId.value = vehicle.id
  selectedVehicleName.value = `${vehicle.brand} ${vehicle.model} (${vehicle.registrationNumber})`
  showVehiclePicker.value = false
  applyAllFilters()
}

// ─── Export ───────────────────────────────────────────────────────────────────
const exportLoading = ref(false)
const exportError = ref(null)

const exportExcel = async () => {
  exportLoading.value = true
  exportError.value = null
  try {
    const response = await fuelNormApi.export(buildSearchRequest())
    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = 'Нормы_расхода_топлива.xlsx'
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

const goToDetail = (id) => router.push(`/fuel-norms/${id}`)
const goToCreate = () => router.push('/fuel-norms/new')

const goToEdit = (id, event) => {
  event.stopPropagation()
  router.push(`/fuel-norms/${id}/edit`)
}

const confirmDelete = (id, event) => {
  event.stopPropagation()
  itemToDelete.value = id
  showDeleteConfirm.value = true
}

const handleDelete = async () => {
  if (!itemToDelete.value) return
  try {
    await fuelNormApi.deleteById(itemToDelete.value)
    loadNorms()
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка удаления нормы расхода'
  } finally {
    showDeleteConfirm.value = false
    itemToDelete.value = null
  }
}

onMounted(applyAllFilters)
</script>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <div class="page-header">
        <h1>Нормы расхода топлива</h1>
        <div class="actions">
          <select v-model="activeFilter" @change="applyAllFilters" class="filter-select">
            <option value="all">Все нормы</option>
            <option value="active">Действующие</option>
            <option value="expired">Недействующие</option>
          </select>
          <div class="vehicle-filter">
            <span class="filter-label">ТС:</span>
            <div class="filter-ref">
              <span class="filter-ref-value">{{ selectedVehicleName || 'Все' }}</span>
              <button class="filter-ref-btn" @click="openVehiclePicker">▾</button>
              <button v-if="selectedVehicleId" class="filter-clear-btn" @click="clearVehicleFilter">✕</button>
            </div>
          </div>
          <button @click="loadNorms" :disabled="loading" class="btn-secondary">
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
        <table v-if="norms.length > 0">
          <thead>
            <tr>
              <th>Описание</th>
              <th>ТС</th>
              <th @click="setSort('validFrom')" class="sortable">Действует с {{ sortIndicator('validFrom') }}</th>
              <th @click="setSort('validTo')" class="sortable">Действует до {{ sortIndicator('validTo') }}</th>
              <th @click="setSort('fuelNormPerKm')" class="sortable">л/100км {{ sortIndicator('fuelNormPerKm') }}</th>
              <th style="width: 100px">Действия</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="n in norms" :key="n.id" @click="goToDetail(n.id)" class="clickable">
              <td>{{ n.description || '—' }}</td>
              <td>{{ n.vehicle?.brand }} {{ n.vehicle?.model }}</td>
              <td>{{ n.validFrom }}</td>
              <td>{{ n.validTo || '—' }}</td>
              <td>{{ n.fuelNormPerKm?.toFixed(2) || '—' }}</td>
              <td class="actions-cell">
                <button class="btn-icon" @click="goToEdit(n.id, $event)">✎</button>
                <button class="btn-icon btn-delete" @click="confirmDelete(n.id, $event)">🗑</button>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-else-if="!loading" class="empty">Нет норм расхода</div>
      </div>

      <Pagination
        :current-page="page"
        :total-pages="totalPages"
        :total-elements="totalElements"
        @page-change="setPage"
      />
    </main>

    <!-- Vehicle picker modal -->
    <div v-if="showVehiclePicker" class="modal-overlay" @click="showVehiclePicker = false">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>Выбор транспортного средства</h3>
          <button class="modal-close" @click="showVehiclePicker = false">×</button>
        </div>
        <div class="modal-body">
          <input type="text" v-model="pickerSearch" placeholder="Поиск по марке/модели/номеру..." class="picker-search" />
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
      title="Удаление нормы расхода"
      message="Вы уверены, что хотите удалить эту норму расхода топлива?"
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
  flex-wrap: wrap;
}

.filter-select {
  padding: 7px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  font-size: 13px;
  color: #333;
  cursor: pointer;
}

.vehicle-filter {
  display: flex;
  align-items: center;
  gap: 6px;
}

.filter-label {
  font-size: 13px;
  color: #555;
  white-space: nowrap;
}

.filter-ref {
  display: flex;
  align-items: center;
  gap: 4px;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 6px 10px;
  background: white;
  min-width: 180px;
  max-width: 240px;
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

.filter-ref-btn:hover { color: #1976d2; }

.filter-clear-btn {
  padding: 0 4px;
  background: transparent;
  border: none;
  cursor: pointer;
  color: #999;
  font-size: 12px;
}

.filter-clear-btn:hover { color: #d32f2f; }

button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-primary { background: #1976d2; color: white; }
.btn-primary:hover { background: #1565c0; }
.btn-secondary { background: #f5f5f5; color: #333; border: 1px solid #ddd; }
.btn-secondary:hover:not(:disabled) { background: #e0e0e0; }
button:disabled { opacity: 0.7; cursor: not-allowed; }

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  overflow-x: auto;
}

table { width: 100%; border-collapse: collapse; font-size: 14px; }

th, td { padding: 12px; text-align: left; border-bottom: 1px solid #eee; }

th { background: #f5f5f5; font-weight: 600; color: #555; }

th.sortable { cursor: pointer; user-select: none; }
th.sortable:hover { background: #e8e8e8; }

.clickable { cursor: pointer; }
.clickable:hover { background: #f9f9f9; }

.actions-cell { display: flex; gap: 4px; align-items: center; }

.btn-icon { padding: 4px 8px; background: transparent; border: 1px solid transparent; border-radius: 4px; cursor: pointer; font-size: 14px; color: #666; line-height: 1; }
.btn-icon:hover { background: #e3f2fd; border-color: #1976d2; color: #1976d2; }
.btn-delete:hover { background: #ffebee; border-color: #d32f2f; color: #d32f2f; }

.empty { padding: 40px; text-align: center; color: #999; }

.alert-error { background: #ffebee; color: #c62828; padding: 12px 16px; border-radius: 4px; margin-bottom: 16px; border: 1px solid #ef9a9a; }

/* Modal */
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal { background: white; border-radius: 8px; width: 600px; max-height: 70vh; display: flex; flex-direction: column; box-shadow: 0 4px 20px rgba(0,0,0,0.2); }
.modal-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid #eee; }
.modal-header h3 { margin: 0; font-size: 16px; }
.modal-close { background: none; border: none; font-size: 24px; cursor: pointer; color: #999; }
.modal-body { padding: 16px 20px; overflow-y: auto; flex: 1; }
.picker-search { width: 100%; padding: 8px 12px; border: 1px solid #ddd; border-radius: 4px; margin-bottom: 12px; font-size: 14px; box-sizing: border-box; }
.picker-loading, .picker-error { text-align: center; padding: 20px; color: #999; }
.picker-error { color: #c62828; }
.picker-list { display: flex; flex-direction: column; gap: 4px; }
.picker-item { padding: 10px 12px; border: 1px solid #eee; border-radius: 4px; cursor: pointer; transition: background 0.15s; }
.picker-item:hover { background: #f5f5f5; border-color: #1976d2; }
.picker-title { font-size: 14px; font-weight: 500; color: #333; }
.picker-sub { font-size: 12px; color: #666; margin-top: 2px; }
</style>
