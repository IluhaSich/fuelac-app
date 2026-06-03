<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { organizationApi } from '@/api/index.js'
import { useSearch } from '@/composables/useSearch.js'
import AppHeader from '@/components/common/AppHeader.vue'
import Pagination from '@/components/common/Pagination.vue'

const router = useRouter()

const {
  items: organizations,
  loading,
  error,
  page,
  totalElements,
  totalPages,
  setPage,
  setSort,
  search,
  sortIndicator,
  fetch: loadOrganizations
} = useSearch(organizationApi, {
  defaultSort: { key: 'createdAt', direction: 'DESC' },
  searchFields: ['name', 'ogrn']
})

const goToDetail = (id) => {
  router.push(`/organizations/${id}`)
}

const goToCreate = () => {
  router.push('/organizations/new')
}

const goToEdit = (id, event) => {
  event.stopPropagation()
  router.push(`/organizations/${id}/edit`)
}

onMounted(loadOrganizations)
</script>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <div class="page-header">
        <h1>Организации</h1>
        <div class="actions">
          <input 
            type="text" 
            placeholder="Поиск..." 
            @input="search($event.target.value)"
            class="search-input"
          />
          <button @click="loadOrganizations" :disabled="loading" class="btn-secondary">
            {{ loading ? 'Загрузка...' : 'Обновить' }}
          </button>
          <button @click="goToCreate" class="btn-primary">+ Добавить</button>
        </div>
      </div>

      <div v-if="error" class="alert alert-error">{{ error }}</div>

      <div class="table-container">
        <table v-if="organizations.length > 0">
          <thead>
            <tr>
              <th @click="setSort('name')" class="sortable">Название {{ sortIndicator('name') }}</th>
              <th>ОГРН</th>
              <th>Код ОКПО</th>
              <th>Адрес</th>
              <th style="width: 60px">Действия</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="o in organizations" :key="o.id" @click="goToDetail(o.id)" class="clickable">
               <td>{{ o.name }}</td>
               <td>{{ o.ogrn }}</td>
               <td>{{ o.codeOKPO }}</td>
               <td>{{ o.address }}</td>
               <td class="actions-cell">
                <button class="btn-icon" @click="goToEdit(o.id, $event)">✎</button>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-else-if="!loading" class="empty">Нет организаций</div>
      </div>

      <Pagination 
        :current-page="page" 
        :total-pages="totalPages" 
        :total-elements="totalElements"
        @page-change="setPage"
      />
    </main>
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
}

.btn-icon {
  padding: 4px 8px;
  background: transparent;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-icon:hover {
  background: #e3f2fd;
  border-color: #1976d2;
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
