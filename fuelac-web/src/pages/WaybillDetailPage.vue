<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { waybillApi, driverApi, vehicleApi, fuelNormApi } from '@/api/index.js'
import AppHeader from '@/components/common/AppHeader.vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'

const route = useRoute()
const router = useRouter()
const waybill = ref(null)
const loading = ref(false)
const error = ref(null)
const editing = ref(false)
const editData = ref({})
const saveError = ref(null)
const closeError = ref(null)
const showCloseConfirm = ref(false)

// Picker state
const showDriverPicker = ref(false)
const showVehiclePicker = ref(false)
const showNormPicker = ref(false)
const pickerLoading = ref(false)
const pickerError = ref(null)
const pickerItems = ref([])
const pickerSearch = ref('')
const selectedNormName = ref('')
const selectedDriverName = ref('')
const selectedVehicleDisplayName = ref('')

const loadWaybill = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await waybillApi.findById(route.params.id)
    waybill.value = response.data
  } catch (err) {
    error.value = 'Ошибка загрузки путевого листа'
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/waybills')
}

const formatDate = (dateStr) => {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleString('ru-RU')
}

const formatDateShort = (dateStr) => {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleString('ru-RU', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const isOpen = (status) => status === 'Открыт'

const vehicleTypes = [
  { value: 'Легковой', label: 'Легковой' },
  { value: 'Грузовой', label: 'Грузовой' },
  { value: 'Специальный', label: 'Специальный' },
]

const messageTypes = [
  { value: 'Городские перевозки', label: 'Городские перевозки' },
  { value: 'Пригородные перевозки', label: 'Пригородные перевозки' },
  { value: 'Междугородние перевозки', label: 'Междугородние перевозки' },
  { value: 'Международные перевозки', label: 'Международные перевозки' },
]

const transportationTypes = [
  { value: 'Пассажирские перевозки', label: 'Пассажирские перевозки' },
  { value: 'Грузовые перевозки', label: 'Грузовые перевозки' },
  { value: 'Специальные перевозки', label: 'Специальные перевозки' },
]

const toIsoLocal = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day}T${hours}:${minutes}`
}

const startEdit = () => {
  if (!waybill.value) return
  editing.value = true
  saveError.value = null
  selectedDriverName.value = waybill.value.driver?.fullName || ''
  selectedVehicleDisplayName.value = waybill.value.vehicle
    ? `${waybill.value.vehicle.brand} ${waybill.value.vehicle.model}`
    : ''
  selectedNormName.value = waybill.value.appliedFuelNorm?.description || ''
  editData.value = {
    number: waybill.value.number,
    vehicleType: waybill.value.vehicleType,
    messageType: waybill.value.messageType,
    transportationType: waybill.value.transportationType,
    driverId: waybill.value.driver?.id,
    vehicleId: waybill.value.vehicle?.id,
    workStartDate: toIsoLocal(waybill.value.workStartDate),
    workEndDate: toIsoLocal(waybill.value.workEndDate),
    odometerStart: waybill.value.odometerStart ?? '',
    odometerEnd: waybill.value.odometerEnd ?? '',
    fuelStart: waybill.value.fuelStart ?? '',
    fuelEnd: waybill.value.fuelEnd ?? '',
    fuelRefilled: waybill.value.fuelRefilled ?? '',
    machineHoursStart: waybill.value.machineHoursStart ?? '',
    machineHoursEnd: waybill.value.machineHoursEnd ?? '',
    engineHoursStart: waybill.value.engineHoursStart ?? '',
    engineHoursEnd: waybill.value.engineHoursEnd ?? '',
    idleTime: waybill.value.idleTime ?? '',
    engineWarmUpTime: waybill.value.engineWarmUpTime ?? '',
    airConditionerTime: waybill.value.airConditionerTime ?? '',
    appliedFuelNormId: waybill.value.appliedFuelNorm?.id ?? null,
  }
}

const cancelEdit = () => {
  editing.value = false
  saveError.value = null
}

const val = (v) => (v === '' || v === null || v === undefined) ? null : parseFloat(v)

const saveEdit = async () => {
  saveError.value = null
  try {
    const payload = {
      number: editData.value.number,
      status: waybill.value.status,
      vehicleType: editData.value.vehicleType,
      messageType: editData.value.messageType,
      transportationType: editData.value.transportationType,
      driverId: editData.value.driverId,
      vehicleId: editData.value.vehicleId,
      appliedFuelNormId: editData.value.appliedFuelNormId || null,
      workStartDate: editData.value.workStartDate ? editData.value.workStartDate + ':00' : null,
      workEndDate: editData.value.workEndDate ? editData.value.workEndDate + ':00' : null,
      fuelStart: val(editData.value.fuelStart),
      fuelEnd: val(editData.value.fuelEnd),
      fuelRefilled: val(editData.value.fuelRefilled),
      odometerStart: val(editData.value.odometerStart),
      odometerEnd: val(editData.value.odometerEnd),
      machineHoursStart: val(editData.value.machineHoursStart),
      machineHoursEnd: val(editData.value.machineHoursEnd),
      engineHoursStart: val(editData.value.engineHoursStart),
      engineHoursEnd: val(editData.value.engineHoursEnd),
      idleTime: val(editData.value.idleTime),
      engineWarmUpTime: val(editData.value.engineWarmUpTime),
      airConditionerTime: val(editData.value.airConditionerTime),
    }
    await waybillApi.update(waybill.value.id, payload)
    editing.value = false
    await loadWaybill()
  } catch (err) {
    saveError.value = 'Ошибка сохранения: ' + (err.response?.data?.message || err.message)
  }
}

const confirmClose = async () => {
  closeError.value = null
  showCloseConfirm.value = false
  try {
    await waybillApi.close(waybill.value.id)
    await loadWaybill()
  } catch (err) {
    closeError.value = 'Ошибка закрытия: ' + (err.response?.data?.message || err.message)
  }
}

// Picker helpers
const openDriverPicker = async () => {
  showDriverPicker.value = true
  pickerLoading.value = true
  pickerError.value = null
  pickerItems.value = []
  pickerSearch.value = ''
  try {
    const res = await driverApi.search({ filters: [], sorts: [{ key: 'lastName', direction: 'ASC' }], page: 0, size: 100 })
    pickerItems.value = res.data.content || []
  } catch (e) {
    pickerError.value = 'Ошибка загрузки водителей'
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
    const res = await vehicleApi.search({ filters: [], sorts: [{ key: 'brand', direction: 'ASC' }], page: 0, size: 100 })
    pickerItems.value = res.data.content || []
  } catch (e) {
    pickerError.value = 'Ошибка загрузки ТС'
  } finally {
    pickerLoading.value = false
  }
}

const openNormPicker = async () => {
  showNormPicker.value = true
  pickerLoading.value = true
  pickerError.value = null
  pickerItems.value = []
  pickerSearch.value = ''
  try {
    const vehicleId = editData.value.vehicleId || waybill.value?.vehicle?.id
    const filters = vehicleId
      ? [{ key: 'vehicle.id', operator: 'EQUAL', fieldType: 'STRING', value: vehicleId }]
      : []
    const res = await fuelNormApi.search({ filters, sorts: [{ key: 'validFrom', direction: 'DESC' }], page: 0, size: 100 })
    pickerItems.value = res.data.content || []
  } catch (e) {
    pickerError.value = 'Ошибка загрузки норм'
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
    if (item.description) return item.description.toLowerCase().includes(s)
    return false
  })
}

const selectDriver = (driver) => {
  editData.value.driverId = driver.id
  selectedDriverName.value = driver.fullName
  showDriverPicker.value = false
}

const selectVehicle = (vehicle) => {
  editData.value.vehicleId = vehicle.id
  editData.value.vehicleType = vehicle.type
  selectedVehicleDisplayName.value = `${vehicle.brand} ${vehicle.model}`
  showVehiclePicker.value = false
}

const selectNorm = (norm) => {
  editData.value.appliedFuelNormId = norm.id
  selectedNormName.value = norm.description || `Норма с ${norm.validFrom}`
  showNormPicker.value = false
}

onMounted(loadWaybill)
</script>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <button class="back-btn" @click="goBack">← Назад к списку</button>

      <div v-if="loading" class="loading">Загрузка...</div>
      <div v-else-if="error" class="alert alert-error">{{ error }}</div>

      <div v-else-if="waybill" class="waybill-form">
        <!-- Заголовок -->
        <div class="form-header">
          <div class="header-left">
            <template v-if="!editing">
              <h1>Путевой лист № {{ waybill.number }}</h1>
            </template>
            <template v-else>
              <input type="text" v-model="editData.number" class="header-input" />
            </template>
            <span :class="isOpen(waybill.status) ? 'status-open' : 'status-closed'">
              {{ waybill.status }}
            </span>
          </div>
          <div class="header-actions" v-if="!editing">
            <button v-if="isOpen(waybill.status)" class="btn-danger" @click="showCloseConfirm = true">Закрыть ПТ</button>
            <button class="btn-secondary" @click="startEdit">Редактировать</button>
          </div>
          <div class="header-actions" v-else>
            <button class="btn-primary" @click="saveEdit">Сохранить</button>
            <button class="btn-secondary" @click="cancelEdit">Отмена</button>
          </div>
        </div>
        <div v-if="saveError" class="alert alert-error" style="margin-bottom: 16px;">{{ saveError }}</div>
        <div v-if="closeError" class="alert alert-error" style="margin-bottom: 16px;">{{ closeError }}</div>

        <!-- Общая информация -->
        <div class="form-section">
          <h2 class="section-title">Общая информация</h2>
          <div class="form-row">
            <div class="form-group">
              <label>Тип ТС:</label>
              <template v-if="!editing"><div class="value">{{ waybill.vehicleType }}</div></template>
              <template v-else>
                <select v-model="editData.vehicleType" class="field-input">
                  <option v-for="t in vehicleTypes" :key="t.value" :value="t.value">{{ t.label }}</option>
                </select>
              </template>
            </div>
            <div class="form-group">
              <label>Вид сообщения:</label>
              <template v-if="!editing"><div class="value">{{ waybill.messageType }}</div></template>
              <template v-else>
                <select v-model="editData.messageType" class="field-input">
                  <option v-for="t in messageTypes" :key="t.value" :value="t.value">{{ t.label }}</option>
                </select>
              </template>
            </div>
            <div class="form-group">
              <label>Тип перевозки:</label>
              <template v-if="!editing"><div class="value">{{ waybill.transportationType }}</div></template>
              <template v-else>
                <select v-model="editData.transportationType" class="field-input">
                  <option v-for="t in transportationTypes" :key="t.value" :value="t.value">{{ t.label }}</option>
                </select>
              </template>
            </div>
          </div>
          <div class="form-row mt-16">
            <div class="form-group"><label>Создан:</label><div class="value">{{ formatDate(waybill.createdAt) }}</div></div>
            <div class="form-group"><label>Кем создан:</label><div class="value">{{ waybill.createdBy?.fullName || waybill.createdBy?.email || '—' }}</div></div>
            <div class="form-group"><label>Обновлён:</label><div class="value">{{ formatDate(waybill.updatedAt) }}</div></div>
          </div>
          <div class="form-row mt-16" v-if="!isOpen(waybill.status)">
            <div class="form-group"><label>Закрыт:</label><div class="value">{{ formatDate(waybill.closedAt) }}</div></div>
            <div class="form-group"><label>Кем закрыт:</label><div class="value">{{ waybill.closedBy?.fullName || waybill.closedBy?.email || '—' }}</div></div>
          </div>
        </div>

        <!-- Транспортное средство -->
        <div class="form-section">
          <h2 class="section-title">Транспортное средство</h2>
          <div class="form-row">
            <div class="form-group wide">
              <label>Марка и модель:</label>
              <template v-if="!editing"><div class="value">{{ waybill.vehicle?.brand }} {{ waybill.vehicle?.model }}</div></template>
              <template v-else>
                <div class="ref-field">
                  <span class="ref-value">{{ selectedVehicleDisplayName || waybill.vehicle?.brand + ' ' + waybill.vehicle?.model }}</span>
                  <button class="ref-btn" @click="openVehiclePicker">Изменить</button>
                </div>
              </template>
            </div>
            <div class="form-group">
              <label>Рег. номер:</label>
              <div class="value">{{ waybill.vehicle?.registrationNumber || '—' }}</div>
            </div>
            <div class="form-group">
              <label>Гаражный номер:</label>
              <div class="value">{{ waybill.vehicle?.garageNumber || '—' }}</div>
            </div>
          </div>
        </div>

        <!-- Водитель -->
        <div class="form-section">
          <h2 class="section-title">Водитель</h2>
          <div class="form-row">
            <div class="form-group wide">
              <label>ФИО:</label>
              <template v-if="!editing"><div class="value">{{ waybill.driver?.fullName || '—' }}</div></template>
              <template v-else>
                <div class="ref-field">
                  <span class="ref-value">{{ selectedDriverName || waybill.driver?.fullName || '—' }}</span>
                  <button class="ref-btn" @click="openDriverPicker">Изменить</button>
                </div>
              </template>
            </div>
            <div class="form-group">
              <label>Табельный номер:</label>
              <div class="value">{{ waybill.driver?.personnelNumber || '—' }}</div>
            </div>
          </div>
        </div>

        <!-- Таблица показаний -->
        <div class="form-section">
          <h2 class="section-title">Показания счётчиков и расход топлива</h2>

          <table class="data-table main-table">
            <thead>
              <tr>
                <th style="width: 80px">Операция</th>
                <th>Дата и время</th>
                <th>Одометр, км</th>
                <th>Топливо, л</th>
                <th>Заправка, л</th>
                <th>Моточасы</th>
                <th>Двигатель, ч</th>
              </tr>
            </thead>
            <tbody>
              <!-- Выезд -->
              <tr>
                <td class="op-cell">Выезд</td>
                <td>
                  <template v-if="!editing">{{ formatDateShort(waybill.workStartDate) }}</template>
                  <template v-else><input type="datetime-local" v-model="editData.workStartDate" class="table-input" /></template>
                </td>
                <td>
                  <template v-if="!editing">{{ waybill.odometerStart ?? '—' }}</template>
                  <template v-else><input type="number" v-model="editData.odometerStart" class="table-input" step="0.1" /></template>
                </td>
                <td>
                  <template v-if="!editing">{{ waybill.fuelStart ?? '—' }}</template>
                  <template v-else><input type="number" v-model="editData.fuelStart" class="table-input" step="0.1" /></template>
                </td>
                <td>
                  <template v-if="!editing">{{ waybill.fuelRefilled ?? '—' }}</template>
                  <template v-else><input type="number" v-model="editData.fuelRefilled" class="table-input" step="0.1" /></template>
                </td>
                <td>
                  <template v-if="!editing">{{ waybill.machineHoursStart ?? '—' }}</template>
                  <template v-else><input type="number" v-model="editData.machineHoursStart" class="table-input" step="0.1" /></template>
                </td>
                <td>
                  <template v-if="!editing">{{ waybill.engineHoursStart ?? '—' }}</template>
                  <template v-else><input type="number" v-model="editData.engineHoursStart" class="table-input" step="0.1" /></template>
                </td>
              </tr>
              <!-- Возвращение -->
              <tr>
                <td class="op-cell">Возвращение</td>
                <td>
                  <template v-if="!editing">{{ formatDateShort(waybill.workEndDate) }}</template>
                  <template v-else><input type="datetime-local" v-model="editData.workEndDate" class="table-input" /></template>
                </td>
                <td>
                  <template v-if="!editing">{{ waybill.odometerEnd ?? '—' }}</template>
                  <template v-else><input type="number" v-model="editData.odometerEnd" class="table-input" step="0.1" /></template>
                </td>
                <td>
                  <template v-if="!editing">{{ waybill.fuelEnd ?? '—' }}</template>
                  <template v-else><input type="number" v-model="editData.fuelEnd" class="table-input" step="0.1" /></template>
                </td>
                <td class="muted">—</td>
                <td>
                  <template v-if="!editing">{{ waybill.machineHoursEnd ?? '—' }}</template>
                  <template v-else><input type="number" v-model="editData.machineHoursEnd" class="table-input" step="0.1" /></template>
                </td>
                <td>
                  <template v-if="!editing">{{ waybill.engineHoursEnd ?? '—' }}</template>
                  <template v-else><input type="number" v-model="editData.engineHoursEnd" class="table-input" step="0.1" /></template>
                </td>
              </tr>
              <!-- Итог -->
              <tr class="total-row">
                <td class="op-cell">Итог</td>
                <td>—</td>
                <td>{{ (waybill.odometerEnd != null && waybill.odometerStart != null) ? (waybill.odometerEnd - waybill.odometerStart).toFixed(1) : '—' }}</td>
                <td>{{ (waybill.fuelStart != null && waybill.fuelEnd != null && waybill.fuelRefilled != null) ? (waybill.fuelStart + waybill.fuelRefilled - waybill.fuelEnd).toFixed(1) : '—' }}</td>
                <td>{{ waybill.fuelRefilled ?? '—' }}</td>
                <td>{{ (waybill.machineHoursEnd != null && waybill.machineHoursStart != null) ? (waybill.machineHoursEnd - waybill.machineHoursStart).toFixed(1) : '—' }}</td>
                <td>{{ (waybill.engineHoursEnd != null && waybill.engineHoursStart != null) ? (waybill.engineHoursEnd - waybill.engineHoursStart).toFixed(1) : '—' }}</td>
              </tr>
            </tbody>
          </table>

          <!-- Дополнительные параметры -->
          <div v-if="!isOpen(waybill.status) || editing" style="margin-top: 16px;">
            <h3 class="subsection-title">Дополнительные параметры</h3>
            <table class="data-table">
              <thead>
                <tr>
                  <th>Холостой ход, ч</th>
                  <th>Прогрев, ч</th>
                  <th>Кондиционер, ч</th>
                  <th>Норма расхода</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>
                    <template v-if="!editing">{{ waybill.idleTime ?? '—' }}</template>
                    <template v-else><input type="number" v-model="editData.idleTime" class="table-input" step="0.01" /></template>
                  </td>
                  <td>
                    <template v-if="!editing">{{ waybill.engineWarmUpTime ?? '—' }}</template>
                    <template v-else><input type="number" v-model="editData.engineWarmUpTime" class="table-input" step="0.01" /></template>
                  </td>
                  <td>
                    <template v-if="!editing">{{ waybill.airConditionerTime ?? '—' }}</template>
                    <template v-else><input type="number" v-model="editData.airConditionerTime" class="table-input" step="0.01" /></template>
                  </td>
                  <td>
                    <template v-if="!editing">{{ waybill.appliedFuelNorm?.description || '—' }}</template>
                    <template v-else>
                      <div class="ref-field-inline">
                        <span class="ref-value-sm">{{ selectedNormName || waybill.appliedFuelNorm?.description || 'Не выбрана' }}</span>
                        <button class="ref-btn" @click="openNormPicker">Выбрать</button>
                      </div>
                    </template>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Расчёты -->
        <div class="form-section" v-if="!isOpen(waybill.status)">
          <h2 class="section-title">Расчёт расхода топлива</h2>
          <div class="calc-grid">
            <div class="calc-item">
              <label>Нормативный расход:</label>
              <div class="calc-value">{{ waybill.calculatedNormativeFuel?.toFixed(2) || '—' }} л</div>
            </div>
            <div class="calc-item">
              <label>Фактический расход:</label>
              <div class="calc-value">{{ waybill.calculatedActualFuel?.toFixed(2) || '—' }} л</div>
            </div>
            <div class="calc-item">
              <label>Отклонение:</label>
              <div class="calc-value" :class="waybill.calculatedDeviation > 0 ? 'text-danger' : waybill.calculatedDeviation < 0 ? 'text-success' : ''">
                {{ waybill.calculatedDeviation?.toFixed(2) || '—' }} л
              </div>
            </div>
          </div>
        </div>

        <!-- Применённая норма -->
        <div class="form-section" v-if="waybill.appliedFuelNorm">
          <h2 class="section-title">Применённая норма расхода топлива</h2>
          <div class="norm-grid">
            <div class="norm-item">
              <label>Описание:</label>
              <div class="norm-value">{{ waybill.appliedFuelNorm.description || '—' }}</div>
            </div>
            <div class="norm-item">
              <label>На 1 км:</label>
              <div class="norm-value">{{ waybill.appliedFuelNorm.fuelNormPerKm }} л</div>
            </div>
            <div class="norm-item" v-if="waybill.appliedFuelNorm.fuelNormPerMachineHour != null">
              <label>На 1 моточас:</label>
              <div class="norm-value">{{ waybill.appliedFuelNorm.fuelNormPerMachineHour }} л</div>
            </div>
            <div class="norm-item" v-if="waybill.appliedFuelNorm.fuelNormPerEngineHour != null">
              <label>На 1 час двигателя:</label>
              <div class="norm-value">{{ waybill.appliedFuelNorm.fuelNormPerEngineHour }} л</div>
            </div>
            <div class="norm-item" v-if="waybill.appliedFuelNorm.fuelNormIdle != null">
              <label>Холостой ход:</label>
              <div class="norm-value">{{ waybill.appliedFuelNorm.fuelNormIdle }} л/ч</div>
            </div>
            <div class="norm-item" v-if="waybill.appliedFuelNorm.fuelNormWarmUp != null">
              <label>Прогрев:</label>
              <div class="norm-value">{{ waybill.appliedFuelNorm.fuelNormWarmUp }} л/ч</div>
            </div>
            <div class="norm-item" v-if="waybill.appliedFuelNorm.fuelNormAirConditioner != null">
              <label>Кондиционер:</label>
              <div class="norm-value">{{ waybill.appliedFuelNorm.fuelNormAirConditioner }} л/ч</div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Picker Modal -->
    <div v-if="showDriverPicker || showVehiclePicker || showNormPicker" class="modal-overlay" @click="showDriverPicker = showVehiclePicker = showNormPicker = false">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>
            {{ showDriverPicker ? 'Выбор водителя' : showVehiclePicker ? 'Выбор транспортного средства' : 'Выбор нормы расхода' }}
          </h3>
          <button class="modal-close" @click="showDriverPicker = showVehiclePicker = showNormPicker = false">×</button>
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
              @click="showDriverPicker ? selectDriver(item) : showVehiclePicker ? selectVehicle(item) : selectNorm(item)"
            >
              <template v-if="showDriverPicker">
                <div class="picker-title">{{ item.fullName }}</div>
                <div class="picker-sub">Таб. №: {{ item.personnelNumber }}</div>
              </template>
              <template v-else-if="showVehiclePicker">
                <div class="picker-title">{{ item.brand }} {{ item.model }}</div>
                <div class="picker-sub">{{ item.registrationNumber }} / {{ item.garageNumber }}</div>
              </template>
              <template v-else>
                <div class="picker-title">{{ item.description || 'Норма без описания' }}</div>
                <div class="picker-sub">{{ item.fuelNormPerKm }} л/км | {{ item.validFrom }} - {{ item.validTo }}</div>
              </template>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Подтверждение закрытия -->
    <ConfirmDialog
      :show="showCloseConfirm"
      title="Закрытие путевого листа"
      message="Вы уверены, что хотите закрыть этот путевой лист? Действие необратимо."
      @confirm="confirmClose"
      @cancel="showCloseConfirm = false"
    />
  </div>
</template>

<style scoped>
.main-content {
  padding: 24px;
  max-width: 1000px;
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
  padding: 10px 14px;
  border-radius: 4px;
  border: 1px solid #ef9a9a;
  font-size: 13px;
}

.waybill-form {
  background: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 24px;
  font-family: 'Times New Roman', Times, serif;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #333;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.form-header h1 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
}

.header-input {
  font-size: 18px;
  font-weight: bold;
  border: 1px solid #1976d2;
  border-radius: 4px;
  padding: 4px 8px;
  width: 200px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.btn-primary {
  padding: 6px 16px;
  background: #1976d2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-primary:hover {
  background: #1565c0;
}

.btn-secondary {
  padding: 6px 16px;
  background: transparent;
  color: #555;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-secondary:hover {
  background: #f5f5f5;
}

.btn-danger {
  padding: 6px 16px;
  background: #d32f2f;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-danger:hover {
  background: #b71c1c;
}

.status-open {
  background: #e3f2fd;
  color: #1565c0;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 13px;
}

.status-closed {
  background: #e8f5e9;
  color: #2e7d32;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 13px;
}

.form-section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 13px;
  font-weight: bold;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: #333;
  margin: 0 0 10px 0;
  padding-bottom: 6px;
  border-bottom: 1px solid #ddd;
}

.subsection-title {
  font-size: 12px;
  font-weight: bold;
  color: #555;
  margin: 12px 0 8px 0;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.form-group {
  flex: 1;
  min-width: 200px;
}

.form-group.wide {
  flex: 2;
  min-width: 300px;
}

.form-group label {
  display: block;
  font-size: 11px;
  color: #666;
  margin-bottom: 2px;
}

.form-group .value {
  font-size: 14px;
  color: #000;
  font-weight: 500;
  padding: 4px 0;
  border-bottom: 1px solid #ccc;
  min-height: 24px;
}

.field-input {
  width: 100%;
  padding: 4px 6px;
  border: 1px solid #1976d2;
  border-radius: 3px;
  font-size: 14px;
  font-family: inherit;
  box-sizing: border-box;
}

.ref-field {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 0;
  border-bottom: 1px solid #ccc;
  min-height: 24px;
}

.ref-field-inline {
  display: flex;
  align-items: center;
  gap: 6px;
  justify-content: center;
}

.ref-value {
  font-size: 14px;
  color: #000;
  font-weight: 500;
  flex: 1;
}

.ref-value-sm {
  font-size: 12px;
  color: #333;
}

.ref-btn {
  padding: 2px 8px;
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

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
  margin-bottom: 12px;
}

.data-table th {
  background: #f5f5f5;
  padding: 8px 10px;
  text-align: center;
  font-weight: 600;
  border: 1px solid #bbb;
  font-size: 11px;
  text-transform: uppercase;
  color: #555;
}

.data-table td {
  padding: 8px 10px;
  border: 1px solid #bbb;
  font-size: 13px;
  text-align: center;
  vertical-align: middle;
  height: 36px;
}

.data-table .op-cell {
  font-weight: 600;
  background: #fafafa;
  text-align: left;
  padding-left: 12px;
}

.data-table .muted {
  color: #999;
}

.data-table .total-row {
  background: #f0f0f0;
  font-weight: 600;
}

.table-input {
  width: 100%;
  padding: 4px 6px;
  border: 1px solid #1976d2;
  border-radius: 3px;
  font-size: 13px;
  font-family: inherit;
  text-align: center;
  box-sizing: border-box;
}

.table-input:focus {
  outline: none;
  border-color: #1565c0;
  box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.15);
}

.calc-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.calc-item {
  background: #fafafa;
  padding: 12px;
  border: 1px solid #eee;
  border-radius: 4px;
}

.calc-item label {
  display: block;
  font-size: 11px;
  color: #666;
  margin-bottom: 4px;
}

.calc-value {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.norm-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.norm-item {
  background: #fafafa;
  padding: 10px 12px;
  border: 1px solid #eee;
  border-radius: 4px;
}

.norm-item label {
  display: block;
  font-size: 11px;
  color: #666;
  margin-bottom: 2px;
}

.norm-value {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.mt-16 {
  margin-top: 16px;
}

.text-danger {
  color: #d32f2f;
}

.text-success {
  color: #2e7d32;
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
