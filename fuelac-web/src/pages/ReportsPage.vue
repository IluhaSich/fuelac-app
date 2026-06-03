<script setup>
import { ref, onMounted } from 'vue'
import { vehicleApi, waybillApi, SearchRequest } from '@/api/index.js'
import AppHeader from '@/components/common/AppHeader.vue'

const reportType = ref('vehicle-work-report')
const dateFrom = ref('')
const dateTo = ref('')
const selectedVehicleId = ref('')
const vehicles = ref([])
const loading = ref(false)
const error = ref(null)
const reportData = ref([])
const showReport = ref(false)

const loadVehicles = async () => {
  try {
    const res = await vehicleApi.search(SearchRequest.create({
      sorts: [SearchRequest.sort('brand', 'ASC')],
      page: 0,
      size: 1000
    }))
    vehicles.value = res.data.content || []
  } catch (e) {
    console.error('Failed to load vehicles', e)
  }
}

const generateReport = async () => {
  if (!dateFrom.value || !dateTo.value || !selectedVehicleId.value) {
    error.value = 'Заполните период и выберите транспортное средство'
    return
  }
  error.value = null
  loading.value = true
  try {
    // Fetch all waybills and filter client-side for now
    // In the future, backend should support filtering by vehicleId and date range
    let allWaybills = []
    let page = 0
    let totalPages = 1
    while (page < totalPages) {
      const res = await waybillApi.search(SearchRequest.create({
        sorts: [SearchRequest.sort('workStartDate', 'DESC')],
        page: page,
        size: 100
      }))
      const data = res.data
      allWaybills = allWaybills.concat(data.content || [])
      totalPages = data.totalPages || 1
      page++
    }

    const fromDate = new Date(dateFrom.value)
    const toDate = new Date(dateTo.value)
    toDate.setHours(23, 59, 59, 999)

    reportData.value = allWaybills.filter(w => {
      if (w.vehicle?.id !== selectedVehicleId.value) return false
      if (!w.workStartDate) return false
      const d = new Date(w.workStartDate)
      return d >= fromDate && d <= toDate
    })

    showReport.value = true
  } catch (e) {
    error.value = 'Ошибка формирования отчета'
  } finally {
    loading.value = false
  }
}

const downloadExcel = async () => {
  if (!dateFrom.value || !dateTo.value) {
    error.value = 'Выберите период для скачивания отчёта'
    return
  }
  if (!selectedVehicleId.value) {
    error.value = 'Выберите транспортное средство для скачивания отчёта'
    return
  }
  error.value = null
  loading.value = true
  try {
    const response = await vehicleApi.getExcelReport(selectedVehicleId.value, dateFrom.value, dateTo.value)
    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `Отчет_по_ТС_${dateFrom.value}_${dateTo.value}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (e) {
    if (e.response?.data instanceof Blob) {
      const text = await e.response.data.text()
      try {
        const json = JSON.parse(text)
        error.value = 'Ошибка скачивания: ' + (json.message || text)
      } catch {
        error.value = 'Ошибка скачивания: ' + text
      }
    } else {
      error.value = 'Ошибка скачивания: ' + (e.response?.status || e.message)
    }
    console.error(e)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleString('ru-RU')
}

onMounted(loadVehicles)
</script>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <h1>Отчёты</h1>

      <div class="report-form">
        <div class="form-row">
          <div class="form-group">
            <label>Тип отчёта</label>
            <select v-model="reportType">
              <option value="vehicle-work-report">Отчёт по учёту работы транспортного средства</option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Период с</label>
            <input type="date" v-model="dateFrom" />
          </div>
          <div class="form-group">
            <label>Период по</label>
            <input type="date" v-model="dateTo" />
          </div>
          <div class="form-group">
            <label>Транспортное средство</label>
            <select v-model="selectedVehicleId">
              <option value="">Выберите ТС</option>
              <option v-for="v in vehicles" :key="v.id" :value="v.id">
                {{ v.brand }} {{ v.model }} ({{ v.registrationNumber }})
              </option>
            </select>
          </div>
        </div>

        <div v-if="error" class="alert alert-error">{{ error }}</div>

        <div class="form-actions">
          <button class="btn-primary" @click="generateReport" :disabled="loading">
            {{ loading ? 'Формирование...' : 'Сформировать отчёт' }}
          </button>
        </div>
      </div>

      <!-- Report Table -->
      <div v-if="showReport" class="report-result">
        <div class="report-header">
          <h2>Отчёт по учёту работы транспортного средства</h2>
          <button class="btn-secondary" @click="downloadExcel">
            Скачать Excel
          </button>
        </div>

        <div v-if="reportData.length === 0" class="empty">
          Нет данных за выбранный период
        </div>

        <div v-else class="table-wrapper">
          <table class="data-table">
            <thead>
              <tr>
                <th>№ п/л</th>
                <th>Водитель</th>
                <th>Начало<br>работы</th>
                <th>Окончание<br>работы</th>
                <th>Остаток в баке<br>на начало смены, л</th>
                <th>Остаток в баке<br>на конец смены, л</th>
                <th>Заправка<br>по п/л, л</th>
                <th>Одометр<br>на начало, км</th>
                <th>Одометр<br>на конец, км</th>
                <th>Пробег<br>ТС, км</th>
                <th>Машиночасы<br>на начало, м/ч</th>
                <th>Машиночасы<br>на конец, м/ч</th>
                <th>Машино-<br>часы, м/ч</th>
                <th>Моточасы<br>на начало, м/ч</th>
                <th>Моточасы<br>на конец, м/ч</th>
                <th>Нормативный<br>расход, л</th>
                <th>Фактический<br>расход, л</th>
                <th>Отклонение,<br>л</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="w in reportData" :key="w.id">
                <td>{{ w.number }}</td>
                <td>{{ w.driver?.fullName || '' }}</td>
                <td>{{ formatDate(w.workStartDate) }}</td>
                <td>{{ formatDate(w.workEndDate) }}</td>
                <td>{{ w.fuelStart }}</td>
                <td>{{ w.fuelEnd }}</td>
                <td>{{ w.fuelRefilled }}</td>
                <td>{{ w.odometerStart }}</td>
                <td>{{ w.odometerEnd }}</td>
                <td>{{ (w.odometerEnd != null && w.odometerStart != null) ? (w.odometerEnd - w.odometerStart).toFixed(1) : '' }}</td>
                <td>{{ w.machineHoursStart }}</td>
                <td>{{ w.machineHoursEnd }}</td>
                <td>{{ (w.machineHoursEnd != null && w.machineHoursStart != null) ? (w.machineHoursEnd - w.machineHoursStart).toFixed(1) : '' }}</td>
                <td>{{ w.engineHoursStart }}</td>
                <td>{{ w.engineHoursEnd }}</td>
                <td>{{ w.calculatedNormativeFuel?.toFixed(2) || '' }}</td>
                <td>{{ w.calculatedActualFuel?.toFixed(2) || '' }}</td>
                <td :class="w.calculatedDeviation > 0 ? 'text-danger' : w.calculatedDeviation < 0 ? 'text-success' : ''">
                  {{ w.calculatedDeviation?.toFixed(2) || '' }}
                </td>
              </tr>
            </tbody>
          </table>
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

h1 {
  margin-bottom: 24px;
}

.report-form {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  margin-bottom: 24px;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
}

.form-group {
  flex: 1;
  min-width: 200px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
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
  width: 100%;
  box-sizing: border-box;
}

input:focus, select:focus {
  outline: none;
  border-color: #1976d2;
}

.form-actions {
  margin-top: 16px;
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

.alert-error {
  background: #ffebee;
  color: #c62828;
  padding: 10px 14px;
  border-radius: 4px;
  border: 1px solid #ef9a9a;
  margin-bottom: 12px;
}

.report-result {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.report-header h2 {
  margin: 0;
  font-size: 16px;
}

.table-wrapper {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
  min-width: 1200px;
}

.data-table th {
  background: #f5f5f5;
  padding: 6px 4px;
  text-align: center;
  font-weight: 600;
  border: 1px solid #ddd;
  font-size: 11px;
  line-height: 1.3;
  vertical-align: bottom;
}

.data-table td {
  padding: 6px 4px;
  border: 1px solid #ddd;
  text-align: center;
  font-size: 12px;
}

.data-table tr:nth-child(even) {
  background: #fafafa;
}

.empty {
  text-align: center;
  padding: 40px;
  color: #999;
}

.text-danger {
  color: #d32f2f;
}

.text-success {
  color: #2e7d32;
}
</style>
