<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.js'

const router = useRouter()
const authStore = useAuthStore()

const credentials = ref({
  email: '',
  password: '',
})

const handleSubmit = async () => {
  try {
    await authStore.login(credentials.value)
    router.push('/')
  } catch (err) {
    // ошибка уже в authStore.error
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-form">
      <h1>FuelAC</h1>
      <h2>Вход в систему</h2>
      
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label>Email</label>
          <input 
            v-model="credentials.email" 
            type="email" 
            required
            placeholder="Введите email"
          />
        </div>
        
        <div class="form-group">
          <label>Пароль</label>
          <input 
            v-model="credentials.password" 
            type="password" 
            required
            placeholder="Введите пароль"
          />
        </div>
        
        <div v-if="authStore.error" class="error">
          {{ authStore.error }}
        </div>
        
        <button type="submit" :disabled="authStore.isLoading">
          {{ authStore.isLoading ? 'Вход...' : 'Войти' }}
        </button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e3f2fd;
}

.login-form {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  width: 100%;
  max-width: 400px;
}

h1 {
  text-align: center;
  color: #1976d2;
  margin-bottom: 8px;
}

h2 {
  text-align: center;
  font-weight: normal;
  margin-bottom: 24px;
  color: #666;
}

.form-group {
  margin-bottom: 16px;
}

label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

input:focus {
  outline: none;
  border-color: #1976d2;
}

button {
  width: 100%;
  padding: 12px;
  background: #1976d2;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}

button:hover:not(:disabled) {
  background: #1565c0;
}

button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.error {
  color: #d32f2f;
  margin-bottom: 16px;
  font-size: 14px;
}
</style>
