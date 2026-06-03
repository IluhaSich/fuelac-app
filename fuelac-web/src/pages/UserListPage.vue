<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '@/api/index.js'
import { useSearch } from '@/composables/useSearch.js'
import AppHeader from '@/components/common/AppHeader.vue'
import Pagination from '@/components/common/Pagination.vue'
import ConfirmDialog from '@/components/common/ConfirmDialog.vue'

const router = useRouter()

const {
  items: users,
  loading,
  error,
  page,
  totalElements,
  totalPages,
  filters,
  setPage,
  setSort,
  addFilter,
  removeFilter,
  search,
  sortIndicator,
  fetch: loadUsers
} = useSearch(userApi, {
  defaultSort: { key: 'createdAt', direction: 'DESC' },
  searchFields: ['lastName', 'firstName', 'patronymic', 'email']
})

// Role filter
const roleOptions = [
  { value: '', label: 'Все' },
  { value: 'ADMIN', label: 'Администратор' },
  { value: 'ORGANIZATION_MANAGER', label: 'Менеджер организации' },
  { value: 'DISPATCHER', label: 'Диспетчер' },
]

const selectedRole = ref('')

const handleRoleChange = () => {
  const existingIndex = filters.value.findIndex(f => f.key === 'role')
  if (existingIndex >= 0) removeFilter(existingIndex)
  
  if (selectedRole.value) {
    addFilter('role', 'EQUAL', selectedRole.value, 'STRING')
  }
}

// Delete confirmation
const showDeleteConfirm = ref(false)
const itemToDelete = ref(null)

const goToDetail = (id) => {
  router.push(`/users/${id}`)
}

const goToCreate = () => {
  router.push('/users/new')
}

const goToEdit = (id, event) => {
  event.stopPropagation()
  router.push(`/users/${id}/edit`)
}

const confirmDelete = (id, event) => {
  event.stopPropagation()
  itemToDelete.value = id
  showDeleteConfirm.value = true
}

const handleDelete = async () => {
  if (!itemToDelete.value) return
  try {
    await userApi.deleteById(itemToDelete.value)
    loadUsers()
  } catch (err) {
    error.value = err.response?.data?.message || 'Ошибка удаления пользователя'
  } finally {
    showDeleteConfirm.value = false
    itemToDelete.value = null
  }
}

const getRoleLabel = (role) => {
  const map = {
    ADMIN: 'Администратор',
    ORGANIZATION_MANAGER: 'Менеджер',
    DISPATCHER: 'Диспетчер',
  }
  return map[role] || role
}

onMounted(loadUsers)
</script>

<template>
  <div class="layout">
    <AppHeader />
    <main class="main-content">
      <div class="page-header">
        <h1>Пользователи</h1>
        <div class="actions">
          <input 
            type="text" 
            placeholder="Поиск..." 
            @input="search($event.target.value)"
            class="search-input"
          />
          <select v-model="selectedRole" @change="handleRoleChange">
            <option v-for="r in roleOptions" :key="r.value" :value="r.value">{{ r.label }}</option>
          </select>
          <button @click="loadUsers" :disabled="loading" class="btn-secondary">
            {{ loading ? 'Загрузка...' : 'Обновить' }}
          </button>
          <button @click="goToCreate" class="btn-primary">+ Добавить</button>
        </div>
      </div>

      <div v-if="error" class="alert alert-error">{{ error }}</div>

      <div class="table-container">
        <table v-if="users.length > 0">
          <thead>
            <tr>
              <th @click="setSort('email')" class="sortable">Email {{ sortIndicator('email') }}</th>
               <th @click="setSort('lastName')" class="sortable">ФИО {{ sortIndicator('lastName') }}</th>
              <th>Роль</th>
              <th>Организация</th>
              <th style="width: 100px">Действия</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="u in users" :key="u.id" @click="goToDetail(u.id)" class="clickable">
              <td>{{ u.email }}</td>
              <td>{{ u.fullName }}</td>
              <td><span class="role-badge">{{ getRoleLabel(u.role) }}</span></td>
              <td>{{ u.organization?.name || '—' }}</td>
              <td class="actions-cell">
                <button class="btn-icon" @click="goToEdit(u.id, $event)">✎</button>
                <button class="btn-icon btn-delete" @click="confirmDelete(u.id, $event)">🗑</button>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-else-if="!loading" class="empty">Нет пользователей</div>
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
      title="Удаление пользователя"
      message="Вы уверены, что хотите удалить этого пользователя?"
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

.role-badge {
  background: #e3f2fd;
  color: #1565c0;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
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
