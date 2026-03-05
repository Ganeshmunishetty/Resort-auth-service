import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests if available
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Auth endpoints
export const register = (userData) => {
  const { role, ...data } = userData;
  const endpoint = role === 'USER' ? '/auth/register/user' : '/auth/register/owner';
  return api.post(endpoint, data);
};

export const registerAdmin = (userData) => {
  return api.post('/auth/register/admin', userData);
};

export const login = (credentials, role) => {
  // Map role to correct endpoint
  const roleMap = {
    'user': 'user',
    'owner': 'owner',
    'admin': 'admin',
    'USER': 'user',
    'OWNER': 'owner',
    'ADMIN': 'admin'
  };
  
  const endpoint = roleMap[role] || 'user';
  return api.post(`/auth/login/${endpoint}`, credentials);
};

export const forgotPassword = (email) => {
  return api.post('/auth/forgot-password', { email });
};

export const resetPassword = (data) => {
  return api.post('/auth/reset-password', data);
};

// Admin endpoints
export const getPendingUsers = () => {
  return api.get('/admin/pending-users');
};

export const getAllUsers = () => {
  return api.get('/admin/users');
};

export const approveUser = (userId) => {
  return api.post(`/admin/approve/${userId}`);
};

export const rejectUser = (userId) => {
  return api.post(`/admin/reject/${userId}`);
};

export const unlockAccount = (userId) => {
  return api.post(`/admin/unlock/${userId}`);
};

// Backward compatibility - export authService object for Login.js and ForgotPassword.js
export const authService = {
  login,
  forgotPassword,
  resetPassword,
  register,
  registerAdmin
};

export default api;
