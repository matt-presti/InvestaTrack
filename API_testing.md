# Investment Portfolio Tracker - REST API Specification & Testing

## API Overview

**Base URL**: `http://localhost:8080/api` (development)
**Authentication**: JWT Bearer Token
**Content-Type**: `application/json`
**API Version**: v1

## External Dependencies

### Yahoo Finance API Integration
- **Purpose**: Real-time stock prices and basic company information
- **Update Frequency**: Stock prices refreshed every 30 minutes via scheduled job
- **Fallback Strategy**: Cached prices used if Yahoo API unavailable
- **Rate Limiting**: 100 requests/hour to Yahoo API (managed internally)
- **Timeout**: 10 second timeout for external API calls

**Error Handling for External APIs**:
- Yahoo API unavailable → Return cached data with `lastUpdated` timestamp
- Invalid stock symbol → Return 404 Not Found
- API timeout → Use cached data and log warning

---

## Authentication Endpoints

### POST /api/auth/register
**Description**: Register a new user account
**Authentication**: None required

**Request Body**:
```json
{
  "username": "string (3-30 chars, unique)",
  "email": "string (valid email format, unique)", 
  "password": "string (8+ chars)",
  "firstName": "string (1-50 chars)",
  "lastName": "string (1-50 chars)"
}
```

**Success Response (201 Created)**:
```json
{
  "userId": 123,
  "username": "johndoe",
  "firstName": "John",
  "lastName": "Doe",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Test Cases**:
- [PASS] Valid registration data → Should return 201 with user and JWT
- [FAIL] Duplicate username → Should return 409 Conflict
- [FAIL] Invalid email format → Should return 400 Bad Request
- [FAIL] Password too short → Should return 400 Bad Request

### POST /api/auth/login
**Description**: Authenticate user and return JWT token
**Authentication**: None required

**Request Body**:
```json
{
  "username": "string",
  "password": "string"
}
```

**Success Response (200 OK)**:
```json
{
  "userId": 123,
  "username": "johndoe",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Test Cases**:
- [PASS] Valid credentials → Should return 200 with JWT token
- [FAIL] Invalid credentials → Should return 401 Unauthorized

---

## Portfolio Management Endpoints

### GET /api/portfolios
**Description**: Get all portfolios for authenticated user
**Authentication**: JWT Bearer Token required

**Success Response (200 OK)**:
```json
[
  {
    "portfolioId": 1,
    "name": "My Growth Portfolio",
    "description": "Long-term growth stocks",
    "totalValue": 25000.50,
    "totalCost": 22000.00,
    "gainLoss": 3000.50,
    "gainLossPercent": 13.64,
    "positionCount": 5
  }
]
```

**Test Cases**:
- [PASS] Valid token with portfolios → Should return array of portfolios
- [PASS] Valid token, no portfolios → Should return empty array
- [FAIL] Invalid token → Should return 401 Unauthorized

### POST /api/portfolios
**Description**: Create a new portfolio
**Authentication**: JWT Bearer Token required

**Request Body**:
```json
{
  "name": "string (1-100 chars, required)",
  "description": "string (optional, max 255 chars)"
}
```

**Success Response (201 Created)**: Returns created portfolio object

**Test Cases**:
- [PASS] Valid portfolio data → Should create and return portfolio (201)
- [FAIL] Missing name → Should return 400 Bad Request
- [FAIL] Name too long → Should return 400 Bad Request

### GET /api/portfolios/{id}
**Description**: Get detailed portfolio information including positions
**Authentication**: JWT Bearer Token required

**Success Response (200 OK)**:
```json
{
  "portfolioId": 1,
  "name": "My Growth Portfolio",
  "description": "Long-term growth stocks",
  "totalValue": 25000.50,
  "totalCost": 22000.00,
  "gainLoss": 3000.50,
  "gainLossPercent": 13.64,
  "positions": [
    {
      "positionId": 101,
      "symbol": "AAPL",
      "companyName": "Apple Inc.",
      "quantity": 50,
      "averageCost": 165.20,
      "currentPrice": 185.50,
      "totalCost": 8260.00,
      "currentValue": 9275.00,
      "gainLoss": 1015.00,
      "gainLossPercent": 12.29
    }
  ]
}
```

**Test Cases**:
- [PASS] Valid portfolio owned by user → Should return portfolio details (200)
- [FAIL] Portfolio not owned by user → Should return 403 Forbidden
- [FAIL] Non-existent portfolio → Should return 404 Not Found

### PUT /api/portfolios/{id}
**Description**: Update portfolio name and description
**Authentication**: JWT Bearer Token required

**Request Body**:
```json
{
  "name": "string (1-100 chars)",
  "description": "string (max 255 chars)"
}
```

**Test Cases**:
- [PASS] Valid update data → Should update and return portfolio (200)
- [FAIL] Portfolio not owned by user → Should return 403 Forbidden

### DELETE /api/portfolios/{id}
**Description**: Delete a portfolio (only if no positions exist)
**Authentication**: JWT Bearer Token required

**Test Cases**:
- [PASS] Empty portfolio owned by user → Should delete portfolio (204)
- [FAIL] Portfolio with positions → Should return 409 Conflict
- [FAIL] Portfolio not owned by user → Should return 403 Forbidden

---

## Transaction Endpoints

### POST /api/portfolios/{portfolioId}/transactions
**Description**: Add a new buy or sell transaction
**Authentication**: JWT Bearer Token required

**Request Body**:
```json
{
  "stockSymbol": "string (1-10 chars, required)",
  "transactionType": "BUY|SELL (required)",
  "quantity": "integer (positive, required)",
  "pricePerShare": "decimal (positive, required)",
  "transactionDate": "ISO 8601 date (required, not future)"
}
```

**Success Response (201 Created)**:
```json
{
  "transactionId": 501,
  "portfolioId": 1,
  "symbol": "AAPL",
  "companyName": "Apple Inc.",
  "transactionType": "BUY",
  "quantity": 10,
  "pricePerShare": 185.50,
  "totalAmount": 1855.00,
  "transactionDate": "2024-06-12T14:30:00Z"
}
```

**Test Cases**:
- [PASS] Valid buy transaction → Should create transaction and update position (201)
- [PASS] Valid sell transaction → Should create transaction and update position (201)
- [FAIL] Sell more than owned → Should return 400 Bad Request
- [FAIL] Invalid stock symbol → Should return 400 Bad Request
- [FAIL] Future transaction date → Should return 400 Bad Request
- [FAIL] Portfolio not owned → Should return 403 Forbidden

### GET /api/portfolios/{portfolioId}/transactions
**Description**: Get transaction history for portfolio (simple pagination)
**Authentication**: JWT Bearer Token required

**Query Parameters**: 
- `page` (integer, optional, default: 0): Page number (0-based)
- `size` (integer, optional, default: 20): Page size (max: 50)

**Success Response (200 OK)**:
```json
{
  "transactions": [
    {
      "transactionId": 501,
      "symbol": "AAPL",
      "companyName": "Apple Inc.",
      "transactionType": "BUY",
      "quantity": 10,
      "pricePerShare": 185.50,
      "totalAmount": 1855.00,
      "transactionDate": "2024-06-12T14:30:00Z"
    }
  ],
  "totalPages": 2,
  "currentPage": 0,
  "totalTransactions": 25
}
```

**Test Cases**:
- [PASS] Valid request → Should return paginated transactions (200)
- [FAIL] Portfolio not owned → Should return 403 Forbidden

---

## Stock Information Endpoints

### GET /api/stocks/search
**Description**: Search for stocks by symbol or company name
**Authentication**: JWT Bearer Token required

**Query Parameters**: 
- `q` (string, required): Search term (min 2 chars)
- `limit` (integer, optional, default: 10): Max results (max: 20)

**Success Response (200 OK)**:
```json
[
  {
    "symbol": "AAPL",
    "companyName": "Apple Inc.",
    "currentPrice": 185.50,
    "sector": "Technology"
  }
]
```

**Test Cases**:
- [PASS] Valid search term → Should return matching stocks (200)
- [FAIL] Search term too short → Should return 400 Bad Request
- [PASS] No matches → Should return empty array (200)
- [FAIL] Yahoo API unavailable → Should return 503 Service Unavailable

### GET /api/stocks/{symbol}
**Description**: Get current information for a specific stock
**Authentication**: JWT Bearer Token required

**Success Response (200 OK)**:
```json
{
  "symbol": "AAPL",
  "companyName": "Apple Inc.",
  "currentPrice": 185.50,
  "dayChange": 1.30,
  "dayChangePercent": 0.71,
  "sector": "Technology",
  "lastUpdated": "2024-06-12T20:00:00Z"
}
```

**Test Cases**:
- [PASS] Valid stock symbol → Should return stock information (200)
- [FAIL] Invalid symbol → Should return 404 Not Found
- [PASS] Yahoo API unavailable → Should return cached data with lastUpdated timestamp

---

## Portfolio Analytics Endpoints

### GET /api/portfolios/{id}/performance
**Description**: Get basic portfolio performance metrics
**Authentication**: JWT Bearer Token required

**Query Parameters**:
- `period` (string, optional, default: "1mo"): Time period (1w, 1m, 3m, 6m, 1y)

**Success Response (200 OK)**:
```json
{
  "portfolioId": 1,
  "period": "1mo",
  "totalValue": 25000.50,
  "totalCost": 22000.00,
  "totalReturn": 3000.50,
  "totalReturnPercent": 13.64,
  "topPerformer": {
    "symbol": "AAPL",
    "gainPercent": 15.20
  },
  "worstPerformer": {
    "symbol": "MSFT",
    "gainPercent": -2.30
  }
}
```

**Test Cases**:
- [PASS] Valid portfolio with positions → Should return performance data (200)
- [PASS] Empty portfolio → Should return zero performance metrics (200)
- [FAIL] Portfolio not owned → Should return 403 Forbidden

---

## Background Services

### Stock Price Update Service
- **Schedule**: Every 30 minutes during market hours (9:30 AM - 4:00 PM ET)
- **Process**: Updates prices for all stocks in user portfolios
- **Failure Handling**: Continues with cached data, logs errors
- **Performance**: Recalculates portfolio values after price updates

### Manual Price Refresh
**POST /api/stocks/refresh** (Optional endpoint for immediate updates)
- Triggers immediate price update for user's portfolio stocks
- Rate limited to 1 request per minute per user

---

## Error Handling

### Standard HTTP Status Codes
- `200 OK`: Successful requests
- `201 Created`: Successful creation
- `204 No Content`: Successful deletion
- `400 Bad Request`: Invalid input data
- `401 Unauthorized`: Authentication required
- `403 Forbidden`: Access denied
- `404 Not Found`: Resource not found
- `409 Conflict`: Business rule violation
- `503 Service Unavailable`: External service unavailable

### Error Response Format
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "timestamp": "2024-06-12T15:30:00Z",
    "path": "/api/portfolios"
  }
}
```

### External API Error Handling
```json
{
  "error": {
    "code": "EXTERNAL_SERVICE_UNAVAILABLE",
    "message": "Stock price data temporarily unavailable",
    "timestamp": "2024-06-12T15:30:00Z",
    "path": "/api/stocks/AAPL",
    "lastKnownPrice": 185.50,
    "lastUpdated": "2024-06-12T14:30:00Z"
  }
}
```

---

## Implementation Strategy

### Phase 1: Core Functionality (Weeks 1-6)
1. User authentication (register/login)
2. Portfolio CRUD operations
3. Basic transaction entry
4. Simple position tracking

### Phase 2: External Integration (Weeks 7-9)
1. Yahoo Finance API integration
2. Stock search functionality
3. Real-time price updates
4. Basic performance calculations

### Phase 3: Polish & Testing (Weeks 10-12)
1. Comprehensive error handling
2. Background price update service
3. Performance metrics
4. Complete test coverage

## Security & Performance Notes

- **JWT Tokens**: 7-day expiry 
- **Password Hashing**: BCrypt with default strength
- **Input Validation**: Spring Boot validation annotations
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries
- **Rate Limiting**: Basic rate limiting for external API calls
- **Caching**: Simple in-memory cache for stock prices (30-minute TTL)

