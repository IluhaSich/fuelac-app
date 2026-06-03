<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { waybillApi, driverApi, vehicleApi, fuelNormApi } from '@/api/index.js'
import AppHeader from '@/components/common/AppHeader.vue'

const route = useRoute()
const router = useRouter()
const isEdit = ref(!!route.params.id)
const isClosing = ref(route.query.action === 'close')
const loading = ref(false)
const error = ref(null)
const saving = ref(false)

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

// Form for creation/opening
const form = ref({
  number: '',
  vehicleType: 'Легковой',
  messageType: 'Городские перевозки',
  transportationType: 'Пассажирские перевозки',
  driverId: '',
  vehicleId: '',
  workStartDate: '',
  fuelStart: 0,
  fuelRefilled: 0,
  odometerStart: 0,
  machineHoursStart: null,
  engineHoursStart: null,
})

const selectedDriverName = ref('')
const selectedVehicleName = ref('')

// Form for closing
const closeForm = ref({
  workEndDate: '',
  fuelEnd: 0,
  odometerEnd: 0,
  machineHoursEnd: null,
  engineHoursEnd: null,
  idleTime: null,
  engineWarmUpTime: null,
  airConditionerTime: null,
  appliedFuelNormId: '',
})

// Picker state
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

const filteredPickerItems = () => {
  if (!pickerSearch.value) return pickerItems.value
  const s = pickerSearch.value.toLowerCase()
  return pickerItems.value.filter(item => {
    if (item.fullName) return item.fullName.toLowerCase().includes(s)
    if (item.brand || item.model) return `${item.brand} ${item.model} ${item.registrationNumber}`.toLowerCase().includes(s)
    return false
  })
}

const selectDriver = (driver) => {
  form.value.driverId = driver.id
  selectedDriverName.value = driver.fullName
  showDriverPicker.value = false
}

const selectVehicle = async (vehicle) => {
  form.value.vehicleId = vehicle.id
  selectedVehicleName.value = `${vehicle.brand} ${vehicle.model} (${vehicle.registrationNumber})`
  showVehiclePicker.value = false

  try {
    const typeRes = await vehicleApi.getType(vehicle.id)
    form.value.vehicleType = typeRes.data
  } catch (e) {
    console.error('Failed to get vehicle type', e)
  }

  try {
    const res = await vehicleApi.getLastReadings(vehicle.id)
    if (res.data) {
      form.value.odometerStart = res.data.odometer || 0
      form.value.fuelStart = res.data.fuel || 0
    }
  } catch (e) {
    console.error('Failed to load last readings', e)
  }
}

// Fuel Norm picker state
const showFuelNormPicker = ref(false)
const selectedFuelNormName = ref('')

const openFuelNormPicker = async () => {
  if (!form.value.vehicleId) {
    error.value = 'Сначала выберите транспортное средство'
    return
  }
  showFuelNormPicker.value = true
  pickerLoading.value = true
  pickerError.value = null
  pickerItems.value = []
  pickerSearch.value = ''
  try {
    const res = await fuelNormApi.search({
      filters: [{ key: 'vehicle.id', operator: 'EQUAL', fieldType: 'STRING', value: form.value.vehicleId }],
      sorts: [{ key: 'validFrom', direction: 'DESC' }],
      page: 0,
      size: 100
    })
    pickerItems.value = res.data.content || []
  } catch (e) {
    pickerError.value = 'Ошибка загрузки норм расхода'
  } finally {
    pickerLoading.value = false
  }
}

const selectFuelNorm = (norm) => {
  closeForm.value.appliedFuelNormId = norm.id
  selectedFuelNormName.value = norm.description || `Норма с ${norm.validFrom}`
  showFuelNormPicker.value = false
}

const loadWaybill = async () => {
  if (!isEdit.value) return
  loading.value = true
  try {
    const response = await waybillApi.findById(route.params.id)
    const w = response.data
    form.value = {
      number: w.number || '',
      vehicleType: w.vehicleType || 'Легковой',
      messageType: w.messageType || 'Городские перевозки',
      transportationType: w.transportationType || 'Пассажирские перевозки',
      driverId: w.driver?.id || '',
      vehicleId: w.vehicle?.id || '',
      workStartDate: w.workStartDate || '',
      fuelStart: w.fuelStart || 0,
      fuelRefilled: w.fuelRefilled || 0,
      odometerStart: w.odometerStart || 0,
      machineHoursStart: w.machineHoursStart,
      engineHoursStart: w.engineHoursStart,
    }
    selectedDriverName.value = w.driver?.fullName || ''
    selectedVehicleName.value = w.vehicle ? `${w.vehicle.brand} ${w.vehicle.model} (${w.vehicle.registrationNumber})` : ''
    
    closeForm.value = {
      workEndDate: w.workEndDate || '',
      fuelEnd: w.fuelEnd || 0,
      odometerEnd: w.odometerEnd || 0,
      machineHoursEnd: w.machineHoursEnd,
      engineHoursEnd: w.engineHoursEnd,
      idleTime: w.idleTime,
      engineWarmUpTime: w.engineWarmUpTime,
      airConditionerTime: w.airConditionerTime,
      appliedFuelNormId: '',
    }
  } catch (err) {
    error.value = 'Ошибка загрузки путевого листа'
  } finally {
    loading.value = false
  }
}

const save = async () => {
  saving.value = true
  error.value = null
  try {
    if (isClosing.value) {
      const closePayload = { ...form.value, ...closeForm.value, status: 'Закрыт' }
      await waybillApi.close(route.params.id, closePayload)
    } else if (isEdit.value) {
      await waybillApi.update(route.params.id, form.value)
    } else {
      await waybillApi.create(form.value)
    }
    router.push('/waybills')
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка сохранения'
  } finally {
    saving.value = false
  }
}

const cancel = () => {
  router.push('/waybills')
}

onMounted(loadWaybill)
</script>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <h1 v-if="isClosing">Закрытие путевого листа</h1>
      <h1 v-else-if="isEdit">Редактирование путевого листа</h1>
      <h1 v-else>Добавление путевого листа</h1>
      
      <div v-if="error" class="alert alert-error">{{ error }}</div>
      
      <div v-if="loading" class="loading">Загрузка...</div>
      
      <!-- Closing Form -->
      <form v-else-if="isClosing" @submit.prevent="save" class="form">
        <div class="form-grid">
          <div class="form-group">
            <label>Окончание работы *</label>
            <input v-model="closeForm.workEndDate" type="datetime-local" required />
          </div>
          
          <div class="form-group">
            <label>Топливо (конец) *</label>
            <input v-model="closeForm.fuelEnd" type="number" step="0.1" required />
          </div>
          
          <div class="form-group">
            <label>Одометр (конец) *</label>
            <input v-model="closeForm.odometerEnd" type="number" step="0.1" required />
          </div>
          
          <div class="form-group">
            <label>Моточасы (конец)</label>
            <input v-model="closeForm.machineHoursEnd" type="number" step="0.1" />
          </div>
          
          <div class="form-group">
            <label>Двигатель (конец)</label>
            <input v-model="closeForm.engineHoursEnd" type="number" step="0.1" />
          </div>
          
          <div class="form-group">
            <label>Холостой ход (ч)</label>
            <input v-model="closeForm.idleTime" type="number" step="0.1" />
          </div>
          
          <div class="form-group">
            <label>Прогрев (ч)</label>
            <input v-model="closeForm.engineWarmUpTime" type="number" step="0.1" />
          </div>
          
          <div class="form-group">
            <label>Кондиционер (ч)</label>
            <input v-model="closeForm.airConditionerTime" type="number" step="0.1" />
          </div>
          
          <div class="form-group">
            <label>Норма расхода</label>
            <div class="ref-field">
              <span class="ref-value">{{ selectedFuelNormName || 'Не выбрана' }}</span>
              <button type="button" class="ref-btn" @click="openFuelNormPicker">Выбрать</button>
            </div>
          </div>
        </div>
        
        <div class="form-actions">
          <button type="button" @click="cancel" class="btn-secondary">Отмена</button>
          <button type="submit" :disabled="saving" class="btn-primary">
            {{ saving ? 'Закрытие...' : 'Закрыть путевой лист' }}
          </button>
        </div>
      </form>
      
      <!-- Create/Edit Form -->
      <form v-else @submit.prevent="save" class="form">
        <div class="form-grid">
          <div class="form-group">
            <label>Номер *</label>
            <input v-model="form.number" required />
          </div>
          
          <div class="form-group">
            <label>Тип ТС *</label>
            <select v-model="form.vehicleType" required>
              <option v-for="t in vehicleTypes" :key="t.value" :value="t.value">{{ t.label }}</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>Вид сообщения</label>
            <select v-model="form.messageType">
              <option v-for="t in messageTypes" :key="t.value" :value="t.value">{{ t.label }}</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>Тип перевозки</label>
            <select v-model="form.transportationType">
              <option v-for="t in transportationTypes" :key="t.value" :value="t.value">{{ t.label }}</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>Водитель</label>
            <div class="ref-field">
              <span class="ref-value">{{ selectedDriverName || 'Не выбран' }}</span>
              <button type="button" class="ref-btn" @click="openDriverPicker">Выбрать</button>
            </div>
          </div>
          
          <div class="form-group">
            <label>Транспортное средство</label>
            <div class="ref-field">
              <span class="ref-value">{{ selectedVehicleName || 'Не выбрано' }}</span>
              <button type="button" class="ref-btn" @click="openVehiclePicker">Выбрать</button>
            </div>
          </div>
          
          <div class="form-group">
            <label>Начало работы</label>
            <input v-model="form.workStartDate" type="datetime-local" />
          </div>
          
          <div class="form-group">
            <label>Топливо (начало)</label>
            <input v-model="form.fuelStart" type="number" step="0.1" />
          </div>
          
          <div class="form-group">
            <label>Заправлено</label>
            <input v-model="form.fuelRefilled" type="number" step="0.1" />
          </div>
          
          <div class="form-group">
            <label>Одометр (начало)</label>
            <input v-model="form.odometerStart" type="number" step="0.1" />
          </div>
          
          <div class="form-group">
            <label>Моточасы (начало)</label>
            <input v-model="form.machineHoursStart" type="number" step="0.1" />
          </div>
          
          <div class="form-group">
            <label>Двигатель (начало)</label>
            <input v-model="form.engineHoursStart" type="number" step="0.1" />
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

    <!-- Driver Picker Modal -->
    <div v-if="showDriverPicker" class="modal-overlay" @click="showDriverPicker = false">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>Выбор водителя</h3>
          <button class="modal-close" @click="showDriverPicker = false">×</button>
        </div>
        <div class="modal-body">
          <input type="text" v-model="pickerSearch" placeholder="Поиск по ФИО..." class="picker-search" />
          <div v-if="pickerLoading" class="picker-loading">Загрузка...</div>
          <div v-else-if="pickerError" class="picker-error">{{ pickerError }}</div>
          <div v-else class="picker-list">
            <div
              v-for="item in filteredPickerItems()"
              :key="item.id"
              class="picker-item"
              @click="selectDriver(item)"
            >
              <div class="picker-title">{{ item.fullName }}</div>
              <div class="picker-sub">Таб. №: {{ item.personnelNumber }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Vehicle Picker Modal -->
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
              @click="selectVehicle(item)"
            >
              <div class="picker-title">{{ item.brand }} {{ item.model }}</div>
              <div class="picker-sub">{{ item.registrationNumber }} / {{ item.garageNumber }} | {{ item.vehicleType }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Fuel Norm Picker Modal -->
    <div v-if="showFuelNormPicker" class="modal-overlay" @click="showFuelNormPicker = false">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>Выбор нормы расхода</h3>
          <button class="modal-close" @click="showFuelNormPicker = false">×</button>
        </div>
        <div class="modal-body">
          <div v-if="pickerLoading" class="picker-loading">Загрузка...</div>
          <div v-else-if="pickerError" class="picker-error">{{ pickerError }}</div>
          <div v-else class="picker-list">
            <div
              v-for="item in pickerItems"
              :key="item.id"
              class="picker-item"
              @click="selectFuelNorm(item)"
            >
              <div class="picker-title">{{ item.description || `Норма с ${item.validFrom}` }}</div>
              <div class="picker-sub">{{ item.vehicleBrand }} {{ item.vehicleModel }} | Л/100км: {{ item.fuelNormPerKm }} | С {{ item.validFrom }} по {{ item.validTo }}</div>
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
  max-width: 900px;
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
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
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
