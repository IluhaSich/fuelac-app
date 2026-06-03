/**
 * Enum mappings for backend communication.
 * Backend expects English enum names for deserialization.
 * Backend returns Russian labels via @JsonValue.
 */

export const WaybillStatus = {
  OPEN: { value: 'OPEN', label: 'Открыт' },
  CLOSED: { value: 'CLOSED', label: 'Закрыт' },
}

export const VehicleType = {
  LIGHT: { value: 'LIGHT', label: 'Легковой' },
  TRUCK: { value: 'TRUCK', label: 'Грузовой' },
  SPECIAL: { value: 'SPECIAL', label: 'Специальный' },
}

export const MessageType = {
  URBAN: { value: 'URBAN', label: 'Городские перевозки' },
  SUBURBAN: { value: 'SUBURBAN', label: 'Пригородные перевозки' },
  INTERCITY: { value: 'INTERCITY', label: 'Междугородние перевозки' },
  INTERNATIONAL: { value: 'INTERNATIONAL', label: 'Международные перевозки' },
}

export const TransportationType = {
  PASSENGER: { value: 'PASSENGER', label: 'Пассажирские перевозки' },
  CARGO: { value: 'CARGO', label: 'Грузовые перевозки' },
  SPECIAL: { value: 'SPECIAL', label: 'Специальные перевозки' },
}

export const UserRole = {
  ADMIN: { value: 'ADMIN', label: 'Администратор' },
  ORGANIZATION_MANAGER: { value: 'ORGANIZATION_MANAGER', label: 'Менеджер организации' },
  DISPATCHER: { value: 'DISPATCHER', label: 'Диспетчер' },
}

// Helper to get label by English value
export const getLabel = (enumMap, value) => {
  const entry = Object.values(enumMap).find(e => e.value === value)
  return entry?.label || value
}

// Helper to get English value by label
export const getValue = (enumMap, label) => {
  const entry = Object.values(enumMap).find(e => e.label === label)
  return entry?.value || label
}

// Array helpers for select options
export const toOptions = (enumMap) => Object.values(enumMap)
