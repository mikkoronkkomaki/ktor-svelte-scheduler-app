const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export type AppointmentStatus = 'reserved' | 'cancelled' | 'done' | 'no-show';

export interface Appointment {
  id: number;
  description: string;
  startTime: string;
  endTime: string;
  status: AppointmentStatus;
  clientId?: number | null;
  specialistId?: number | null;
  client?: Client | null;
  specialist?: Specialist | null;
}

export interface CreateAppointmentRequest {
  description: string;
  startTime: string;
  endTime: string;
  status: AppointmentStatus;
  clientId?: number | null;
  specialistId?: number | null;
}

export interface Client {
  id: number;
  firstName: string;
  lastName: string;
}

export interface CreateClientRequest {
  firstName: string;
  lastName: string;
}

export interface Specialist {
  id: number;
  firstName: string;
  lastName: string;
  profession: string;
}

export interface CreateSpecialistRequest {
  firstName: string;
  lastName: string;
  profession: string;
}

async function api<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE_URL}${path}`, {
    headers: { 'Content-Type': 'application/json', ...(init?.headers ?? {}) },
    ...init
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(`API ${res.status}: ${text}`);
  }

  if (res.status === 204) return undefined as T;
  return (await res.json()) as T;
}

export const appointmentsApi = {
  list: () => api<Appointment[]>('/appointments'),
  create: (payload: CreateAppointmentRequest) =>
    api<Appointment>('/appointments', {
      method: 'POST',
      body: JSON.stringify(payload)
    })
};

export const clientsApi = {
  list: () => api<Client[]>('/clients'),
  create: (payload: CreateClientRequest) =>
    api<Client>('/clients', {
      method: 'POST',
      body: JSON.stringify(payload)
    })
};

export const specialistsApi = {
  list: () => api<Specialist[]>('/specialists'),
  create: (payload: CreateSpecialistRequest) =>
    api<Specialist>('/specialists', {
      method: 'POST',
      body: JSON.stringify(payload)
    })
};