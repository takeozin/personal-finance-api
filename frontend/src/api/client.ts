import axios from 'axios';

// The base URL points to our Spring Boot backend
const API_URL = 'http://localhost:8080/api';

export const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to attach the JWT token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle global errors (like 401 Unauthorized)
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      // If we get a 401, the token is likely invalid or expired.
      // We can clear it and let the user log in again.
      localStorage.removeItem('token');
      // If not already on login page, redirect or trigger state update
      // For now, we'll just remove the token. The UI should react accordingly.
    }
    return Promise.reject(error);
  }
);

// Auth Service
export const authService = {
  login: async (credentials: any) => {
    const response = await apiClient.post('/auth/login', credentials);
    if (response.data && response.data.token) {
      localStorage.setItem('token', response.data.token);
    }
    return response.data;
  },
  register: async (userData: any) => {
    const response = await apiClient.post('/auth/register', userData);
    if (response.data && response.data.token) {
      localStorage.setItem('token', response.data.token);
    }
    return response.data;
  },
  logout: () => {
    localStorage.removeItem('token');
  },
  isAuthenticated: () => {
    return !!localStorage.getItem('token');
  }
};

// Account Service
export const accountService = {
  getAccounts: async () => {
    const response = await apiClient.get('/accounts');
    return response.data;
  }
};

// Category Service
export const categoryService = {
  getCategories: async () => {
    const response = await apiClient.get('/categories');
    return response.data;
  }
};

// Transaction Service
export const transactionService = {
  getTransactions: async (startDate: string, endDate: string) => {
    const response = await apiClient.get('/transactions', {
      params: { startDate, endDate }
    });
    return response.data;
  },
  createTransaction: async (data: any) => {
    const response = await apiClient.post('/transactions', data);
    return response.data;
  }
};
