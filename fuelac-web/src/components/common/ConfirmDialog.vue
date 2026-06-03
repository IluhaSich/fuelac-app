<script setup>
const props = defineProps({
  show: Boolean,
  title: String,
  message: String,
})

const emit = defineEmits(['confirm', 'cancel'])
</script>

<template>
  <div v-if="show" class="modal-overlay" @click="emit('cancel')">
    <div class="modal" @click.stop>
      <h3>{{ title || 'Подтверждение' }}</h3>
      <p>{{ message || 'Вы уверены?' }}</p>
      <div class="modal-actions">
        <button class="btn-cancel" @click="emit('cancel')">Отмена</button>
        <button class="btn-confirm" @click="emit('confirm')">Подтвердить</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
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
  padding: 24px;
  border-radius: 8px;
  min-width: 320px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}

.modal h3 {
  margin: 0 0 12px 0;
  font-size: 18px;
}

.modal p {
  margin: 0 0 20px 0;
  color: #666;
}

.modal-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.btn-cancel {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
}

.btn-cancel:hover {
  background: #f5f5f5;
}

.btn-confirm {
  padding: 8px 16px;
  border: none;
  background: #d32f2f;
  color: white;
  border-radius: 4px;
  cursor: pointer;
}

.btn-confirm:hover {
  background: #c62828;
}
</style>
