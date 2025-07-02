# Investment Portfolio Tracker - REST API Specification & Testing

## API Overview

**Base URL**: `http://localhost:8080` (development)
**Authentication**: Session-based with Spring Security
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

### POST /register
**Description**: Register a new user account
**Authentication**: None required

**Request Body**:
```json
{
  "username": "string (3-30 chars, unique)",
  "email": "string (valid email format, unique)", 
  "password": "string (8+ chars, must contain 1 upper, 1 lower, 1 digit, 1 special)",
  "confirm_password": "string (must match password)",
  "firstName": "string (1-50 chars)",
  "lastName": "string (1-50 chars)"
}
```

**Request Parameters**:
- `role_name` (String): User role (e.g., "USER", "ADMIN")

**Success Response (201 Created)**:
```json
{
  "id": 123,
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Test Cases**:
- [PASS] Valid registration data → Should return 201 with user data and create session
- [FAIL] Duplicate username → Should return 400 Bad Request
- [FAIL] Duplicate email → Should return 400 Bad Request
- [FAIL] Password validation failure → Should return 400 Bad Request
- [FAIL] Passwords don't match → Should return 400 Bad Request

### GET /login
**Description**: Authenticate user and establish session
**Authentication**: HTTP Basic Auth or form-based login

**Success Response (200 OK)**:
```json
"Log In Success"
```

**Error Response (400 Bad Request)**:
```json
"Login Failure"
```

**Test Cases**:
- [PASS] Valid credentials → Should return 200 and establish session
- [FAIL] Invalid credentials → Should return 400 Bad Request
- [PASS] Already authenticated → Should return 200

### POST /logout
**Description**: End user session
**Authentication**: Active session required

**Success Response (200 OK)**:
```json
"Logout successful"
```

---

## User Management Endpoints

### GET /users
**Description**: Get all users (Admin only)
**Authentication**: ADMIN role required

**Success Response (200 OK)**:
```json
[
  {
    "Name": "johndoe",
    "ID": 123
  }
]
```

**Test Cases**:
- [PASS] Admin user → Should return list of all users
- [FAIL] Non-admin user → Should return 403 Forbidden
- [FAIL] Not authenticated → Should return 401 Unauthorized

---

## Portfolio Management Endpoints

### GET /api/portfolios
**Description**: Get all portfolios for authenticated user
**Authentication**: Active session required

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
- [PASS] Authenticated user with portfolios → Should return array of portfolios
- [PASS] Authenticated user, no portfolios → Should return empty array
- [FAIL] Not authenticated → Should return 401 Unauthorized

### POST /api/portfolios
**Description**: Create a new portfolio
**Authentication**: Active session required

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
- [FAIL] Not authenticated → Should return 401 Unauthorized

### GET /api/portfolios/{id}
**Description**: Get detailed portfolio information including positions
**Authentication**: Active session required

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
- [FAIL] Not authenticated → Should return 401 Unauthorized

---

## Transaction Endpoints

### POST /api/portfolios/{portfolioId}/transactions
**Description**: Add a new buy or sell transaction
**Authentication**: Active session required

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
- [FAIL] Not authenticated → Should return 401 Unauthorized

---

## Stock Information Endpoints

### GET /api/stocks/search
**Description**: Search for stocks by symbol or company name
**Authentication**: Active session required

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
- [FAIL] Not authenticated → Should return 401 Unauthorized

---

## Error Handling

### Standard HTTP Status Codes
- `200 OK`: Successful requests
- `201 Created`: Successful creation
- `204 No Content`: Successful deletion
- `400 Bad Request`: Invalid input data or validation failure
- `401 Unauthorized`: Authentication required
- `403 Forbidden`: Access denied (wrong role or not resource owner)
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

---

## Security & Performance Notes

- **Session Management**: Spring Security handles session creation/management
- **Password Hashing**: BCrypt with salt for secure password storage
- **Input Validation**: Spring Boot validation annotations + custom password rules
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries
- **Role-based Access**: User roles stored in UserRoleJoin table
- **Authentication Flow**: Form-based login with session cookies
- **Session Timeout**: Configurable session timeout (default: 30 minutes)
- **CSRF Protection**: Enabled for state-changing operations
