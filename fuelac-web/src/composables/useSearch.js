import { ref, computed } from 'vue'
import { SearchRequest } from '@/api/index.js'

export function useSearch(api, options = {}) {
  const { defaultSort = { key: 'createdAt', direction: 'DESC' }, defaultSize = 20 } = options
  
  const items = ref([])
  const loading = ref(false)
  const error = ref(null)
  const page = ref(0)
  const size = ref(defaultSize)
  const totalElements = ref(0)
  const totalPages = ref(0)
  const filters = ref([])
  const sorts = ref([defaultSort])
  const searchQuery = ref('')
  
  const buildSearchRequest = () => {
    const activeFilters = [...filters.value]

    if (searchQuery.value) {
      if (options.searchFields && options.searchFields.length > 0) {
        // OR-поиск по нескольким полям через orGroup
        options.searchFields.forEach(field => {
          activeFilters.push(SearchRequest.like(field, searchQuery.value, 'nameSearch'))
        })
      } else if (options.searchField) {
        activeFilters.push(SearchRequest.like(options.searchField, searchQuery.value))
      }
    }
    
    return SearchRequest.create({
      filters: activeFilters,
      sorts: sorts.value,
      page: page.value,
      size: size.value
    })
  }
  
  const fetch = async () => {
    loading.value = true
    error.value = null
    try {
      const response = await api.search(buildSearchRequest())
      const data = response.data
      items.value = data.content || []
      totalElements.value = data.totalElements || 0
      totalPages.value = data.totalPages || 0
    } catch (err) {
      error.value = 'Ошибка загрузки данных'
      console.error(err)
    } finally {
      loading.value = false
    }
  }
  
  const setPage = (newPage) => {
    page.value = newPage
    fetch()
  }
  
  const setSort = (key) => {
    const existing = sorts.value.find(s => s.key === key)
    if (existing) {
      existing.direction = existing.direction === 'ASC' ? 'DESC' : 'ASC'
    } else {
      sorts.value = [{ key, direction: 'ASC' }]
    }
    page.value = 0
    fetch()
  }
  
  const addFilter = (key, operator, value, fieldType = 'STRING', valueTo = null) => {
    filters.value.push(SearchRequest.filter(key, operator, value, fieldType, valueTo))
    page.value = 0
    fetch()
  }
  
  const removeFilter = (index) => {
    filters.value.splice(index, 1)
    page.value = 0
    fetch()
  }
  
  const clearFilters = () => {
    filters.value = []
    searchQuery.value = ''
    page.value = 0
    fetch()
  }
  
  const search = (query) => {
    searchQuery.value = query
    page.value = 0
    fetch()
  }
  
  const setFilters = (newFilters) => {
    filters.value = newFilters.slice()
    page.value = 0
    fetch()
  }

  const sortIndicator = (key) => {
    const sort = sorts.value.find(s => s.key === key)
    if (!sort) return '↕'
    return sort.direction === 'ASC' ? '↑' : '↓'
  }

  return {
    items,
    loading,
    error,
    page,
    size,
    totalElements,
    totalPages,
    filters,
    sorts,
    searchQuery,
    fetch,
    setPage,
    setSort,
    addFilter,
    removeFilter,
    clearFilters,
    setFilters,
    search,
    sortIndicator,
    buildSearchRequest
  }
}
