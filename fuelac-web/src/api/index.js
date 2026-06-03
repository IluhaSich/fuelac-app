import apiClient from './client.js'

// Search request builder helper
export const SearchRequest = {
  create: (options = {}) => {
    const { filters = [], sorts = [], page = 0, size = 20 } = options
    return { filters, sorts, page, size }
  },

  filter: (key, operator, value, fieldType = 'STRING', valueTo = null, orGroup = null) => ({
    key, operator, fieldType, value, valueTo, orGroup
  }),

  sort: (key, direction = 'ASC') => ({ key, direction }),

  eq: (key, value, fieldType = 'STRING') => SearchRequest.filter(key, 'EQUAL', value, fieldType),
  like: (key, value, orGroup = null) => SearchRequest.filter(key, 'LIKE', value, 'STRING', null, orGroup),
  gt: (key, value, fieldType = 'DOUBLE') => SearchRequest.filter(key, 'GT', value, fieldType),
  gte: (key, value, fieldType = 'DOUBLE') => SearchRequest.filter(key, 'GTE', value, fieldType),
  lt: (key, value, fieldType = 'DOUBLE') => SearchRequest.filter(key, 'LT', value, fieldType),
  lte: (key, value, fieldType = 'DOUBLE') => SearchRequest.filter(key, 'LTE', value, fieldType),
  between: (key, from, to, fieldType = 'DATE') => SearchRequest.filter(key, 'BETWEEN', from, fieldType, to),
  in: (key, values, fieldType = 'STRING') => SearchRequest.filter(key, 'IN', values, fieldType),
}

export const waybillApi = {
  findById: (id) => apiClient.get(`/waybills/${id}`),
  create: (data) => apiClient.post('/waybills', data),
  update: (id, data) => apiClient.put(`/waybills/${id}`, data),
  deleteById: (id) => apiClient.delete(`/waybills/${id}`),
  close: (id) => apiClient.post(`/waybills/${id}/close`),
  search: (searchRequest) => apiClient.post('/waybills/search', searchRequest),
  generateReport: (id, from, to) => apiClient.get(`/waybills/${id}/report`, { params: { from, to } }),
  downloadExcelReport: (from, to) => apiClient.get('/waybills/report/excel', {
    params: { from, to },
    responseType: 'blob'
  }),
}

export const driverApi = {
  findById: (id) => apiClient.get(`/drivers/${id}`),
  create: (data) => apiClient.post('/drivers', data),
  update: (id, data) => apiClient.put(`/drivers/${id}`, data),
  deleteById: (id) => apiClient.delete(`/drivers/${id}`),
  search: (searchRequest) => apiClient.post('/drivers/search', searchRequest),
  export: (searchRequest) => apiClient.post('/drivers/export', searchRequest, { responseType: 'blob' }),
}

export const vehicleApi = {
  findById: (id) => apiClient.get(`/vehicles/${id}`),
  create: (data) => apiClient.post('/vehicles', data),
  update: (id, data) => apiClient.put(`/vehicles/${id}`, data),
  deleteById: (id) => apiClient.delete(`/vehicles/${id}`),
  search: (searchRequest) => apiClient.post('/vehicles/search', searchRequest),
  export: (searchRequest) => apiClient.post('/vehicles/export', searchRequest, { responseType: 'blob' }),
  getLastReadings: (id) => apiClient.get(`/vehicles/${id}/last-readings`),
  getType: (id) => apiClient.get(`/vehicles/${id}/type`),
  getExcelReport: (id, from, to) => apiClient.get(`/vehicles/${id}/excel-report`, { 
    params: { from, to },
    responseType: 'blob'
  }),
}

export const fuelNormApi = {
  findById: (id) => apiClient.get(`/fuel-norms/${id}`),
  create: (data) => apiClient.post('/fuel-norms', data),
  update: (id, data) => apiClient.put(`/fuel-norms/${id}`, data),
  deleteById: (id) => apiClient.delete(`/fuel-norms/${id}`),
  search: (searchRequest) => apiClient.post('/fuel-norms/search', searchRequest),
  export: (searchRequest) => apiClient.post('/fuel-norms/export', searchRequest, { responseType: 'blob' }),
  getActive: (vehicleId, date) => apiClient.get('/fuel-norms/active', { params: { vehicleId, date } }),
}

export const organizationApi = {
  findById: (id) => apiClient.get(`/admin/organizations/${id}`),
  create: (data) => apiClient.post('/admin/organizations', data),
  update: (id, data) => apiClient.put(`/admin/organizations/${id}`, data),
  deleteById: (id) => apiClient.delete(`/admin/organizations/${id}`),
  search: (searchRequest) => apiClient.post('/admin/organizations/search', searchRequest),
}

export const userApi = {
  findById: (id) => apiClient.get(`/admin/users/${id}`),
  create: (data) => apiClient.post('/admin/users', data),
  update: (id, data) => apiClient.put(`/admin/users/${id}`, data),
  deleteById: (id) => apiClient.delete(`/admin/users/${id}`),
  search: (searchRequest) => apiClient.post('/admin/users/search', searchRequest),
}

export const authApi = {
  login: (email, password) => apiClient.post('/auth/login', { email, password }),
}
