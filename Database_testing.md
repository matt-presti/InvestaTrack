# Investment Portfolio Tracker - Database Design & Testing

## Database Tables

### Table: User
**Table Description**
The User table stores information about registered users of the investment portfolio tracker application. Passwords are hashed using BCrypt for security.

**Attributes and Description**
- `id` (BIGINT, Primary Key, Auto Increment): Unique user identifier
- `username` (VARCHAR(50), Not Null, Unique): User's login name
- `email` (VARCHAR(100), Not Null, Unique): User's email address
- `password` (VARCHAR(255), Not Null): BCrypt hashed password
- `confirm_password` (VARCHAR(255), Not Null): BCrypt hashed confirmation password
- `firstName` (VARCHAR(50), Not Null): User's first name
- `lastName` (VARCHAR(50), Not Null): User's last name
- `createdAt` (TIMESTAMP, Not Null, Default: CURRENT_TIMESTAMP): Account creation date
- `lastLoginAt` (TIMESTAMP, Nullable): Last successful login
- `isActive` (BOOLEAN, Not Null, Default: true): Account status
- `resetPasswordToken` (VARCHAR(255), Nullable): Token for password reset

**Tests**
- **Test: Insert Valid User**
  - Description: Verify that a new user can be successfully registered
  - Steps: Insert user with valid data meeting password requirements
  - Expected: User successfully created with BCrypt hashed passwords
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
  - Steps: Create user and verify both password fields are BCrypt hashed
  - Expected: Raw passwords never stored, only BCrypt hashes
  - Status: Pass/Fail

- **Test: Password Validation**
  - Description: Verify password meets complexity requirements
  - Steps: Attempt registration with weak password
  - Expected: Registration fails with validation error
  - Status: Pass/Fail

### Table: UserRoleJoin
**Table Description**
The UserRoleJoin table manages user roles and permissions for authorization.

**Attributes and Description**
- `id` (BIGINT, Primary Key, Auto Increment): Unique join record identifier
- `users_id` (BIGINT, Foreign Key to User.id, Not Null): Reference to user
- `authority` (VARCHAR(50), Not Null): Role name (e.g., "USER", "ADMIN")

**Tests**
- **Test: Insert Valid User Role**
  - Description: Verify user role assignment
  - Steps: Create user role join record with valid user and authority
  - Expected: Role successfully assigned to user
  - Status: Pass/Fail

- **Test: Enforce Foreign Key Constraint**
  - Description: Verify users_id foreign key constraint
  - Steps: Attempt to create role with non-existent user ID
  - Expected: Insert fails with foreign key violation
  - Status: Pass/Fail

- **Test: Multiple Roles Per User**
  - Description: Verify user can have multiple roles
  - Steps: Assign multiple authorities to same user
  - Expected: All role assignments created successfully
  - Status: Pass/Fail

- **Test: Role-based Access Control**
  - Description: Verify Spring Security recognizes user roles
  - Steps: Authenticate user and check hasAuthority() works
  - Expected: User can access role-appropriate endpoints
  - Status: Pass/Fail

### Table: Portfolio
**Table Description**
The Portfolio table stores information about user investment portfolios.

**Attributes and Description**
- `portfolioId` (BIGINT, Primary Key, Auto Increment): Unique portfolio identifier
- `userId` (BIGINT, Foreign Key to User.id, Not Null): Portfolio owner
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

- **Test: Portfolio Ownership**
  - Description: Verify user can only access their own portfolios
  - Steps: User A attempts to access User B's portfolio
  - Expected: Access denied with 403 Forbidden
  - Status: Pass/Fail

### Table: Stock
**Table Description**
The Stock table stores information about tradeable securities.

**Attributes and Description**
- `stockId` (BIGINT, Primary Key, Auto Increment)
- `symbol` (VARCHAR(10), Not Null, Unique)
- `companyName` (VARCHAR(255), Not Null)
- `currentPrice` (DECIMAL(10,2), Nullable)
- `lastUpdated` (TIMESTAMP, Nullable)
- `sector` (VARCHAR(100), Nullable)
- `marketCap` (BIGINT, Nullable)

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

### Table: Transaction
**Table Description**
The Transaction table records all buy/sell transactions for portfolio tracking.

**Attributes and Description**
- `transactionId` (BIGINT, Primary Key, Auto Increment)
- `portfolioId` (BIGINT, Foreign Key to Portfolio.portfolioId, Not Null)
- `stockId` (BIGINT, Foreign Key to Stock.stockId, Not Null)
- `transactionType` (ENUM('BUY', 'SELL'), Not Null)
- `quantity` (INT, Not Null)
- `pricePerShare` (DECIMAL(10,2), Not Null)
- `totalAmount` (DECIMAL(15,2), Not Null)
- `transactionDate` (TIMESTAMP, Not Null)
- `fees` (DECIMAL(8,2), Default: 0.00)

**Tests**
- **Test: Insert Valid Transaction**
  - Description: Verify transactions can be recorded
  - Steps: Insert buy/sell transaction with valid data
  - Expected: Transaction successfully recorded
  - Status: Pass/Fail

- **Test: Transaction Authorization**
  - Description: Verify user can only create transactions in their portfolios
  - Steps: User attempts transaction in another user's portfolio
  - Expected: Operation denied with 403 Forbidden
  - Status: Pass/Fail

### Table: Position
**Table Description**
The Position table tracks current holdings for each portfolio.

**Attributes and Description**
- `positionId` (BIGINT, Primary Key, Auto Increment)
- `portfolioId` (BIGINT, Foreign Key to Portfolio.portfolioId, Not Null)
- `stockId` (BIGINT, Foreign Key to Stock.stockId, Not Null)
- `quantity` (INT, Not Null)
- `averageCost` (DECIMAL(10,2), Not Null)
- `totalCost` (DECIMAL(15,2), Not Null)
- `currentValue` (DECIMAL(15,2), Nullable)
- `updatedAt` (TIMESTAMP, Not Null)

**Unique Constraint**: (portfolioId, stockId) - One position per stock per portfolio

**Tests**
- **Test: Position Access Control**
  - Description: Verify user can only view positions in their portfolios
  - Steps: User attempts to view another user's positions
  - Expected: Access denied or empty results
  - Status: Pass/Fail

## Data Access Methods

### Authentication & Authorization Methods

### authenticateUser
**Description**: Validates user credentials and establishes session
**Parameters**: username (String), password (String)
**Returns**: UserDetails object if successful, throws exception if failed
**Implementation**: Uses Spring Security UserDetailsService
**Tests**:
- Valid credentials: Should authenticate and create session
- Invalid username: Should throw UsernameNotFoundException
- Invalid password: Should throw BadCredentialsException
- Account disabled: Should throw DisabledException

### loadUserByUsername
**Description**: Loads user details for Spring Security authentication
**Parameters**: username (String)
**Returns**: UserDetails implementation with authorities
**Tests**:
- Valid username: Should return user with correct roles
- Invalid username: Should throw UsernameNotFoundException
- User with multiple roles: Should return all authorities

### hasAuthority
**Description**: Checks if authenticated user has specific role
**Parameters**: authority (String)
**Returns**: Boolean indicating permission
**Tests**:
- Admin user accessing admin endpoint: Should return true
- Regular user accessing admin endpoint: Should return false
- Unauthenticated user: Should return false

### Portfolio Access Methods

### getUserPortfolios
**Description**: Retrieves all portfolios for authenticated user only
**Parameters**: Authentication object (from Spring Security context)
**Returns**: List of Portfolio objects belonging to authenticated user
**Tests**:
- Authenticated user with portfolios: Should return only user's portfolios
- Authenticated user with no portfolios: Should return empty list
- Cross-user access prevention: Should never return other users' portfolios

### getPortfolioDetails
**Description**: Retrieves detailed portfolio information with ownership validation
**Parameters**: portfolioId (Long), Authentication object
**Returns**: Portfolio object with positions if user owns it
**Tests**:
- Valid portfolio owned by user: Should return complete portfolio details
- Valid portfolio not owned by user: Should throw AccessDeniedException
- Invalid portfolioId: Should throw NotFoundException

### addTransaction
**Description**: Records transaction with authorization check
**Parameters**: Transaction object, Authentication object
**Returns**: Transaction ID if successful, throws exception if unauthorized
**Tests**:
- Valid transaction in user's portfolio: Should create transaction and update position
- Transaction in another user's portfolio: Should throw AccessDeniedException
- Invalid portfolio/stock: Should throw ValidationException

## Security Testing

### Session Management Tests
- **Test: Session Creation**: Login creates valid session
- **Test: Session Expiry**: Expired sessions require re-authentication
- **Test: Concurrent Sessions**: Multiple sessions handled correctly
- **Test: Session Invalidation**: Logout properly destroys session

### Authorization Tests
- **Test: Endpoint Protection**: Protected endpoints require authentication
- **Test: Role-based Access**: ADMIN endpoints reject USER role
- **Test: Resource Ownership**: Users cannot access others' resources
- **Test: CSRF Protection**: State-changing operations require CSRF token

### Password Security Tests
- **Test: BCrypt Hashing**: Passwords properly hashed with salt
- **Test: Password Validation**: Complex password requirements enforced
- **Test: Password Confirmation**: Registration requires matching passwords
- **Test: Password Reset**: Reset token functionality works securely
