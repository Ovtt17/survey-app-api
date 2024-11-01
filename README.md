# Survey App

This project is a survey application built with Spring Boot and Maven.

## Requirements

- **Java 17** or higher
- **Maven 3.6.0** or higher
- **MySQL**

## Installation

### Step 1: Clone the repository

```bash
git clone https://github.com/Ovtt17/survey-app-backend.git
cd survey-app
```

### Step 2: Configure the database
### Create a database in MySQL and update the following properties in the src/main/resources/application.properties file:

```bash
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Step 3: Configure the frontend URL
### Update the application.front-end.url property in the src/main/resources/application.properties file:

```bash
application.front-end.url=http://localhost:3000
```

### Step 4: Build the project

```bash
mvn clean install
```

### Step 5: Run the application

```bash
mvn spring-boot:run
```

### The application will be available at http://localhost:8080.

### CORS Configuration
The project is already configured to allow CORS requests from the frontend specified in application.properties.  
### Contributing
1. Fork the project.
2. Create a new branch (git checkout -b feature/new-feature).
3. Make your changes and commit them (git commit -am 'Add new feature').
4. Push to the branch (git push origin feature/new-feature).
5. Open a Pull Request.

### License
This project is licensed under the MIT License.
