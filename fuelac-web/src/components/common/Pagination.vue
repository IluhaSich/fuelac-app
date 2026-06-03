<script setup>
const props = defineProps({
  currentPage: Number,
  totalPages: Number,
  totalElements: Number,
})

const emit = defineEmits(['page-change'])

const goToPage = (page) => {
  if (page >= 0 && page < props.totalPages && page !== props.currentPage) {
    emit('page-change', page)
  }
}

const pages = () => {
  const pages = []
  const start = Math.max(0, props.currentPage - 2)
  const end = Math.min(props.totalPages, start + 5)
  for (let i = start; i < end; i++) {
    pages.push(i)
  }
  return pages
}
</script>

<template>
  <div class="pagination" v-if="totalPages > 1">
    <button 
      :disabled="currentPage === 0" 
      @click="goToPage(currentPage - 1)"
      class="page-btn"
    >
      ←
    </button>
    
    <button 
      v-for="page in pages()" 
      :key="page"
      @click="goToPage(page)"
      :class="['page-btn', { active: page === currentPage }]"
    >
      {{ page + 1 }}
    </button>
    
    <button 
      :disabled="currentPage === totalPages - 1" 
      @click="goToPage(currentPage + 1)"
      class="page-btn"
    >
      →
    </button>
    
    <span class="info">Всего: {{ totalElements }}</span>
  </div>
</template>

<style scoped>
.pagination {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 16px;
  padding: 12px;
  justify-content: center;
}

.page-btn {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  border-radius: 4px;
  font-size: 14px;
}

.page-btn:hover:not(:disabled) {
  background: #e3f2fd;
  border-color: #1976d2;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-btn.active {
  background: #1976d2;
  color: white;
  border-color: #1976d2;
}

.info {
  margin-left: 12px;
  color: #666;
  font-size: 13px;
}
</style>
