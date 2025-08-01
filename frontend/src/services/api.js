// Correct API service for connecting to Spring Boot backend
import axios from 'axios';

// Create axios instance pointing to our Spring Boot server
const api = axios.create({
    baseURL: 'http://localhost:8080',
});

// Portfolio functions - Use /api/portfolios (from PortfolioController)
export const getPortfolios = () => api.get('/api/portfolios');

// Transaction functions - Use /api/transactions (from TransactionController)
export const getTransactions = () => api.get('/test/transactions');
export const createBuyTransaction = (data) => api.post('/api/transactions/buy', data);
export const createSellTransaction = (data) => api.post('/api/transactions/sell', data);

// Stock functions - Use /api/stocks (from StockController)
export const getStocks = () => api.get('/api/stocks');

// Test data functions - Use /test (from TestDataController)
export const loadTestData = () => api.get('/test/load');
export const clearTestData = () => api.delete('/test/clear');