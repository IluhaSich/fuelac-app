<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fuelNormApi } from '@/api/index.js'
import AppHeader from '@/components/common/AppHeader.vue'

const route = useRoute()
const router = useRouter()
const norm = ref(null)
const loading = ref(false)
const error = ref(null)

const loadNorm = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await fuelNormApi.findById(route.params.id)
    norm.value = response.data
  } catch (err) {
    error.value = 'Ошибка загрузки нормы расхода'
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/fuel-norms')
}

const goToEdit = () => {
  router.push(`/fuel-norms/${route.params.id}/edit`)
}

onMounted(loadNorm)
</script>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <button class="back-btn" @click="goBack">← Назад к списку</button>
      
      <div v-if="loading" class="loading">Загрузка...</div>
      <div v-else-if="error" class="alert alert-error">{{ error }}</div>
      
      <div v-else-if="norm" class="detail-card">
        <div class="detail-header">
          <h1>Норма расхода топлива</h1>
          <button class="btn-edit" @click="goToEdit">Редактировать</button>
        </div>

        <div class="detail-grid">
          <div class="detail-section">
            <h3>Информация</h3>
            <div class="field"><label>ID ТС:</label> <span>{{ norm.vehicleId }}</span></div>
            <div class="field"><label>Описание:</label> <span>{{ norm.description }}</span></div>
            <div class="field"><label>Действует с:</label> <span>{{ norm.validFrom }}</span></div>
            <div class="field"><label>Действует до:</label> <span>{{ norm.validTo || '—' }}</span></div>
          </div>

          <div class="detail-section">
            <h3>Нормы расхода</h3>
            <div class="field"><label>На пробег (л/100км):</label> <span>{{ norm.fuelNormPerKm?.toFixed(2) || '—' }}</span></div>
            <div class="field"><label>На машино-час (л/маш-час):</label> <span>{{ norm.fuelNormPerMachineHour?.toFixed(2) || '—' }}</span></div>
            <div class="field"><label>На моточас (л/моточас):</label> <span>{{ norm.fuelNormPerEngineHour?.toFixed(2) || '—' }}</span></div>
            <div class="field"><label>Холостой ход (л/час):</label> <span>{{ norm.fuelNormIdle?.toFixed(2) || '—' }}</span></div>
            <div class="field"><label>Прогрев (л/час):</label> <span>{{ norm.fuelNormWarmUp?.toFixed(2) || '—' }}</span></div>
            <div class="field"><label>Кондиционер (л/час):</label> <span>{{ norm.fuelNormAirConditioner?.toFixed(2) || '—' }}</span></div>
          </div>

          <div class="detail-section">
            <h3>Системная информация</h3>
            <div class="field"><label>Создана:</label> <span>{{ new Date(norm.createdAt).toLocaleString('ru-RU') }}</span></div>
            <div class="field"><label>Обновлена:</label> <span>{{ new Date(norm.updatedAt).toLocaleString('ru-RU') }}</span></div>
          </div>
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
  padding: 12px 16px;
  border-radius: 4px;
  border: 1px solid #ef9a9a;
}

.detail-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  padding: 24px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.detail-header h1 {
  margin: 0;
  font-size: 20px;
}

.btn-edit {
  padding: 8px 16px;
  background: #1976d2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-edit:hover {
  background: #1565c0;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.detail-section {
  background: #fafafa;
  padding: 16px;
  border-radius: 6px;
}

.detail-section h3 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #555;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.field {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  border-bottom: 1px solid #eee;
  font-size: 14px;
}

.field:last-child {
  border-bottom: none;
}

.field label {
  color: #666;
  font-weight: 500;
}
</style>
