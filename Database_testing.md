# Investment Portfolio Tracker - Database Design & Testing

## Database Tables

### Table: User
**Table Description**
The User table stores information about registered users of the investment portfolio tracker application.

**Attributes and Description**
- `userId` (BIGINT, Primary Key, Auto Increment): Unique identifier for each user
- `username` (VARCHAR(50), Not Null, Unique): User's login username
- `email` (VARCHAR(100), Not Null, Unique): User's email address
- `passwordHash` (VARCHAR(255), Not Null): Encrypted password
- `firstName` (VARCHAR(50), Not Null): User's first name
- `lastName` (VARCHAR(50), Not Null): User's last name
- `createdAt` (TIMESTAMP, Not Null): Account creation timestamp
- `lastLoginAt` (TIMESTAMP, Nullable): Last login timestamp
- `isActive` (BOOLEAN, Not Null, Default: true): Account status

**Tests**
- **Test: Insert Valid User**
  - Description: Verify that a new user can be successfully registered
  - Steps: Insert user with valid data for all required fields
  - Expected: User successfully created and retrievable
  - Status: Pass/Fail

- **Test: Enforce Unique Username**
  - Description: Verify username uniqueness constraint
  - Steps: Attempt to create two users with same username
  - Expected: Second insert fails with constraint violation
  - Status: Pass/Fail

- **Test: Enforce Unique Email**
  - Description: Verify email uniqueness constraint
  - Steps: Attempt to create two users with same email
  - Expected: Second insert fails with constraint violation
  - Status: Pass/Fail

- **Test: Password Encryption**
  - Description: Verify passwords are encrypted before storage
  - Steps: Create user and verify password is hashed in database
  - Expected: Raw password never stored, only BCrypt hash
  - Status: Pass/Fail

### Table: Portfolio
**Table Description**
The Portfolio table stores information about user investment portfolios.

**Attributes and Description**
- `portfolioId` (BIGINT, Primary Key, Auto Increment): Unique portfolio identifier
- `userId` (BIGINT, Foreign Key to User.userId, Not Null): Portfolio owner
- `name` (VARCHAR(100), Not Null): Portfolio name
- `description` (TEXT, Nullable): Portfolio description
- `totalValue` (DECIMAL(15,2), Not Null, Default: 0.00): Current total value
- `totalCost` (DECIMAL(15,2), Not Null, Default: 0.00): Total cost basis
- `createdAt` (TIMESTAMP, Not Null): Portfolio creation date
- `updatedAt` (TIMESTAMP, Not Null): Last update timestamp

**Tests**
- **Test: Insert Valid Portfolio**
  - Description: Verify portfolio creation with valid data
  - Steps: Create portfolio with existing userId
  - Expected: Portfolio successfully created
  - Status: Pass/Fail

- **Test: Enforce Foreign Key Constraint**
  - Description: Verify userId foreign key constraint
  - Steps: Attempt to create portfolio with non-existent userId
  - Expected: Insert fails with foreign key violation
  - Status: Pass/Fail

- **Test: Multiple Portfolios Per User**
  - Description: Verify user can have multiple portfolios
  - Steps: Create multiple portfolios for same user
  - Expected: All portfolios created successfully
  - Status: Pass/Fail

### Table: Stock
**Table Description**
The Stock table stores information about tradeable securities.

**Attributes and Description**
- `stockId` (BIGINT, Primary Key, Auto Increment): Unique stock identifier
- `symbol` (VARCHAR(10), Not Null, Unique): Stock ticker symbol
- `companyName` (VARCHAR(255), Not Null): Company name
- `currentPrice` (DECIMAL(10,2), Nullable): Current stock price
- `lastUpdated` (TIMESTAMP, Nullable): Price last update time
- `sector` (VARCHAR(100), Nullable): Business sector
- `marketCap` (BIGINT, Nullable): Market capitalization

**Tests**
- **Test: Insert Valid Stock**
  - Description: Verify stock information can be added
  - Steps: Insert stock with valid symbol and company name
  - Expected: Stock successfully added to database
  - Status: Pass/Fail

- **Test: Enforce Unique Symbol**
  - Description: Verify stock symbols are unique
  - Steps: Attempt to insert duplicate symbol
  - Expected: Insert fails with unique constraint violation
  - Status: Pass/Fail

- **Test: Price Update**
  - Description: Verify stock prices can be updated
  - Steps: Update existing stock price and timestamp
  - Expected: Price and timestamp updated successfully
  - Status: Pass/Fail

### Table: Transaction
**Table Description**
The Transaction table records all buy/sell transactions for portfolio tracking.

**Attributes and Description**
- `transactionId` (BIGINT, Primary Key, Auto Increment): Unique transaction identifier
- `portfolioId` (BIGINT, Foreign Key to Portfolio.portfolioId, Not Null): Associated portfolio
- `stockId` (BIGINT, Foreign Key to Stock.stockId, Not Null): Stock involved in transaction
- `transactionType` (ENUM('BUY', 'SELL'), Not Null): Transaction type
- `quantity` (INT, Not Null): Number of shares
- `pricePerShare` (DECIMAL(10,2), Not Null): Price per share at transaction
- `totalAmount` (DECIMAL(15,2), Not Null): Total transaction amount
- `transactionDate` (TIMESTAMP, Not Null): Transaction date
- `fees` (DECIMAL(8,2), Default: 0.00): Transaction fees

**Tests**
- **Test: Insert Valid Transaction**
  - Description: Verify transactions can be recorded
  - Steps: Insert buy/sell transaction with valid data
  - Expected: Transaction successfully recorded
  - Status: Pass/Fail

- **Test: Enforce Foreign Key Constraints**
  - Description: Verify portfolio and stock foreign keys
  - Steps: Attempt transaction with invalid portfolioId or stockId
  - Expected: Insert fails with foreign key violation
  - Status: Pass/Fail

- **Test: Calculate Total Amount**
  - Description: Verify total amount calculation
  - Steps: Insert transaction and verify totalAmount = quantity * pricePerShare + fees
  - Expected: Total amount correctly calculated
  - Status: Pass/Fail

- **Test: Positive Quantity Constraint**
  - Description: Verify quantity must be positive
  - Steps: Attempt to insert transaction with zero or negative quantity
  - Expected: Insert fails with check constraint violation
  - Status: Pass/Fail

### Table: Position
**Table Description**
The Position table tracks current holdings for each portfolio.

**Attributes and Description**
- `positionId` (BIGINT, Primary Key, Auto Increment): Unique position identifier
- `portfolioId` (BIGINT, Foreign Key to Portfolio.portfolioId, Not Null): Associated portfolio
- `stockId` (BIGINT, Foreign Key to Stock.stockId, Not Null): Stock position
- `quantity` (INT, Not Null): Current shares owned
- `averageCost` (DECIMAL(10,2), Not Null): Average cost per share
- `totalCost` (DECIMAL(15,2), Not Null): Total cost basis
- `currentValue` (DECIMAL(15,2), Nullable): Current market value
- `updatedAt` (TIMESTAMP, Not Null): Last update timestamp

**Unique Constraint**: (portfolioId, stockId) - One position per stock per portfolio

**Tests**
- **Test: Insert Valid Position**
  - Description: Verify position can be created
  - Steps: Insert position with valid portfolio and stock
  - Expected: Position successfully created
  - Status: Pass/Fail

- **Test: Enforce Unique Portfolio-Stock Combination**
  - Description: Verify one position per stock per portfolio
  - Steps: Attempt to create duplicate portfolio-stock position
  - Expected: Insert fails with unique constraint violation
  - Status: Pass/Fail

- **Test: Update Position on Transaction**
  - Description: Verify position updates when transactions occur
  - Steps: Execute buy transaction and verify position quantity/cost updates
  - Expected: Position reflects new quantity and average cost
  - Status: Pass/Fail

## Data Access Methods

### getUserPortfolios
**Description**: Retrieves all portfolios for a specific user
**Parameters**: userId (Long)
**Returns**: List of Portfolio objects with basic information
**Tests**:
- Valid user with portfolios: Should return all user's portfolios
- Valid user with no portfolios: Should return empty list
- Invalid userId: Should return empty list or throw exception

### getPortfolioDetails
**Description**: Retrieves detailed portfolio information including positions
**Parameters**: portfolioId (Long), userId (Long) 
**Returns**: Portfolio object with positions and current values
**Tests**:
- Valid portfolio owned by user: Should return complete portfolio details
- Valid portfolio not owned by user: Should throw unauthorized exception
- Invalid portfolioId: Should throw not found exception

### addTransaction
**Description**: Records a new buy/sell transaction and updates positions
**Parameters**: Transaction object
**Returns**: Transaction ID if successful, error message if failed
**Tests**:
- Valid buy transaction: Should create transaction and update/create position
- Valid sell transaction: Should create transaction and update position
- Sell more than owned: Should throw insufficient shares exception
- Invalid portfolio/stock: Should throw validation exception

### getPortfolioPerformance
**Description**: Calculates portfolio performance metrics
**Parameters**: portfolioId (Long), dateRange (String)
**Returns**: Performance metrics (total value, gain/loss, percentage return)
**Tests**:
- Portfolio with positions: Should return accurate performance calculations
- Empty portfolio: Should return zero values
- Invalid date range: Should throw validation exception

### updateStockPrices
**Description**: Updates current stock prices from external API
**Parameters**: List of stock symbols (or all stocks)
**Returns**: Number of stocks updated successfully
**Tests**:
- Valid symbols: Should update prices and timestamps
- Invalid symbols: Should log errors but not fail completely
- API unavailable: Should handle gracefully with cached data
