# InvestaTrack

**Investment Portfolio Tracker Built with Spring Boot**

InvestaTrack is an investment portfolio management web application that allows users to track their stock investments, monitor portfolio performance, and manage buy/sell transactions. This project represents a focused exploration into backend development using Spring Boot and enterprise Java technologies, demonstrating proficiency in building scalable REST APIs, implementing clean architecture patterns, and following industry-standard development practices.

The application features a robust backend API with comprehensive portfolio management capabilities, transaction processing, and stock information tracking. A functional React frontend interface has been integrated to showcase core functionality with test data, while the well-documented API architecture provides extensive capabilities for future expansion and development.

---

## Live Demo

- **Frontend**: [http://localhost:3000](http://localhost:3000)
- **Backend API**: [http://localhost:8080](http://localhost:8080)
- **Interactive API Documentation**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Application Features

### Portfolio Management Dashboard
![Portfolio Dashboard](path/to/dashboard-screenshot.png)
*Clean, intuitive dashboard showing portfolio performance and key metrics*

### Transaction History
![Transaction History](path/to/transactions-screenshot.png)
*Comprehensive transaction tracking with filtering and search capabilities*

### Professional API Documentation
![Swagger API Documentation](path/to/swagger-screenshot.png)
*Interactive OpenAPI documentation for all endpoints*

---

## Technology Stack

### Backend
- **Java 17** - Modern Java development
- **Spring Boot 3.5.3** - Enterprise application framework
- **Spring Data JPA** - Database abstraction and ORM
- **Spring Security** - Authentication and authorization
- **H2 Database** - In-memory database for development
- **OpenAPI/Swagger** - API documentation and testing
- **Maven** - Dependency management and build automation

### Frontend
- **React.js** - Modern UI library
- **JavaScript ES6+** - Client-side functionality
- **CSS3** - Responsive styling
- **Fetch API** - RESTful API communication

### Development Tools
- **Spring Boot DevTools** - Hot reloading and development utilities
- **Spring Boot Actuator** - Application monitoring and health checks
- **IntelliJ IDEA** - Professional IDE

---

## Getting Started

### Prerequisites
- **Java 17+**
- **Node.js 16+**
- **Maven 3.6+**
- **Git**

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/matt-presti/InvestaTrack.git
   cd InvestaTrack
   ```

2. **Set up the backend**
   ```bash
   # Install dependencies and run Spring Boot
   mvn clean install
   mvn spring-boot:run
   ```

3. **Set up the frontend**
   ```bash
   # Navigate to frontend directory
   cd frontend
   
   # Install dependencies
   npm install
   
   # Start development server
   npm start
   ```

4. **Load sample data**
   - Navigate to [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - Find **Test Data Management** section
   - Execute `GET /test/load` to populate with sample data

---

## Core Features

### Portfolio Management
- Create and manage multiple investment portfolios
- Track portfolio performance with real-time calculations
- View gain/loss metrics and percentage returns
- Portfolio summary with detailed analytics

### Transaction Processing
- Record buy and sell transactions
- Automatic position calculations and updates
- Transaction history with filtering capabilities
- Fee tracking and net amount calculations

### Stock Information
- Stock lookup by symbol or company name
- Price tracking and updates
- Sector-based organization

### Professional API
- **RESTful endpoints** with proper HTTP methods
- **OpenAPI documentation** with interactive testing
- **DTO pattern** for clean, secure responses
- **Error handling** with meaningful status codes

---

## API Documentation

### Interactive Documentation
Access the complete API documentation at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Portfolio Management API
![Portfolio Management Endpoints](path/to/portfolio-api-screenshot.png)
*Clean portfolio endpoints with professional documentation*

### Transaction Management API
![Transaction Management Endpoints](path/to/transaction-api-screenshot.png)
*Comprehensive transaction tracking with DTOs*

### Stock Information API
![Stock Management Endpoints](path/to/stock-api-screenshot.png)
*Stock lookup and management capabilities*

### API Response Examples
![API Response Sample](path/to/api-response-screenshot.png)
*Clean DTO responses with proper data structure*

---

## Development Goals Achieved

### Learning Objectives
- **Java & Spring Boot Mastery** - Enterprise Java development
- **RESTful API Design** - Professional backend development
- **Database Integration** - JPA/Hibernate with relational design
- **Security Implementation** - Authentication and data protection
- **API Documentation** - OpenAPI/Swagger professional documentation

### Industry-Ready Features
- **Professional API Documentation** - Interactive Swagger UI
- **Clean Data Transfer** - DTOs for secure API responses
- **Error Handling** - Comprehensive exception management
- **CORS Configuration** - Production-ready security setup
- **Transaction Management** - Database consistency and integrity
- **Responsive Frontend** - Modern React-based user interface for demonstration

---

## Future Enhancements

### Planned Features
- **Real-time Stock Data** - Integration with financial APIs (Alpha Vantage/Yahoo Finance)
- **User Authentication**
- **Advanced Analytics** - Portfolio performance charts and metrics
- **Cloud Deployment** - AWS/Heroku production deployment

### Technical Improvements
- **PostgreSQL Integration** - Production database setup
- **Caching Layer** - Redis for improved performance
- **CI/CD Pipeline** - Automated testing and deployment
- **Docker Containerization** - Simplified deployment process

---

## About the Developer

This project was built as a comprehensive demonstration of enterprise Java development skills, focusing on:

- **Backend Development** - Spring Boot, REST APIs, Database Design
- **Frontend Integration** - React.js and modern JavaScript
- **Professional Practices** - Clean code, documentation
- **Industry Standards** - Security, error handling, API design

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Links

- **Portfolio Website**: [https://matt-presti.github.io/](https://matt-presti.github.io/)
- **LinkedIn**: [Your LinkedIn Profile](https://www.linkedin.com/in/matthew-presti-6531aa361/)
- **GitHub**: [https://github.com/matt-presti](https://github.com/matt-presti)

---

*Built for professional portfolio demonstration*
