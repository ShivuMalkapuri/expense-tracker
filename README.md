# 💰 Dynamic Expense Tracker & Financial Analytics Web Application

A full-stack financial management web application built with **Java 21**, **Spring Boot 3**, **Spring Data JPA**, **H2 Embedded Database**, and **Thymeleaf + Chart.js**.

---

## ✨ Features

- 📊 **Financial Analytics Dashboard**: Visual charts for category spending distribution (Doughnut Chart) and 6-month spending trends (Bar Chart).
- 🎯 **Budget Health Tracker**: Dynamic monthly budget limit setting with color-coded budget progress indicator.
- 📝 **Expense CRUD Operations**: Add, list, search by keyword, filter by category & date range, and delete transactions.
- 📑 **Data Export**: One-click **CSV Report Export** for filtered or all expenses.
- 💾 **Zero-Setup Database**: Embedded file-based H2 database requiring 0 database installation. Includes built-in H2 Web Console.
- 📱 **Responsive UI**: Built with Bootstrap 5 and FontAwesome 6 icons.

---

## 🛠️ Technology Stack

- **Backend**: Java 21, Spring Boot 3.3 (Spring Web, Spring Data JPA)
- **Database**: H2 Database (Embedded)
- **Frontend**: HTML5, Bootstrap 5, FontAwesome 6, Thymeleaf, Chart.js 4
- **Build Tool**: Apache Maven

---

## 🚀 Getting Started

### Prerequisites
- JDK 21 or higher
- Apache Maven 3.8+

### Running the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/expense-tracker.git
   cd expense-tracker
   ```

2. Build and run using Maven:
   ```bash
   mvn spring-boot:run
   ```

3. Open in your browser:
   - **Main Web Application**: [http://localhost:8080](http://localhost:8080)
   - **H2 DB Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
     - *JDBC URL*: `jdbc:h2:file:./data/expensedb`
     - *Username*: `sa`
     - *Password*: *(leave blank)*

---

## 🏗️ Architecture & Project Structure

```
expense-tracker/
├── src/main/java/com/example/expensetracker/
│   ├── ExpenseTrackerApplication.java  # Application Entry Point
│   ├── config/DataInitializer.java    # Sample Demo Data Initializer
│   ├── controller/ExpenseController.java# Spring MVC Web Routes
│   ├── model/                          # JPA Entities (Expense, Budget, Category)
│   ├── repository/                     # Spring Data JPA Repositories
│   └── service/                        # Business Logic & Analytics
└── src/main/resources/
    ├── application.properties          # App & H2 DB Configuration
    └── templates/                      # Thymeleaf Views (dashboard, expenses, etc.)
```
