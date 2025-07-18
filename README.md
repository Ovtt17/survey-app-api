# 📝 Survey App – Spring Boot + OAuth2 + MySQL

A full-featured **Survey Application** built with **Spring Boot**, integrating **OAuth2.0 (Google & Facebook Login)** and modular functionalities including:

- 🗳️ Survey creation & participation
- 🌟 Review system
- 📧 Email notifications
- 🖼️ Image uploads using **Imgur API**
- 📊 Survey reports generation
- 👤 User profile with all created surveys

---

## 🚀 Features

- ✅ OAuth2.0 authentication with Google and Facebook
- ✅ Dynamic CORS configuration for frontend integration
- ✅ Modular architecture (Survey, Review, Email, Image, Reports, Profile)
- ✅ Imgur integration for image hosting
- ✅ RESTful API built with Spring Boot
- ✅ MySQL as the primary relational database

---

## 🛠️ Requirements

- **Java 17+**
- **Maven 3.6.0+**
- **MySQL Server**

---

## 📦 Installation

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/Ovtt17/survey-app-api.git
cd survey-app-api
```

---

### 2️⃣ Configure the Database

Create a MySQL database and update the following fields in:

```properties
src/main/resources/application.properties
```

---

### 3️⃣ Configure application.properties

All configuration (database, OAuth2, email, Imgur, JWT, etc.) is managed via `src/main/resources/application.properties`. You must set the required environment variables or values for your environment. Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
application.front-end.url=http://localhost:3000
# ...and other required properties (see below for details)
```

Below is a summary of the main configuration options used in `application.properties` and their purpose:

### 1. Application Name
- `spring.application.name=survey-app`: Sets the name of the Spring Boot application.

### 2. Database Configuration
- `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`, `spring.datasource.driver-class-name`: Configure the connection to the MySQL database. It is recommended to use environment variables for sensitive data.

### 3. JPA (Java Persistence API)
- `spring.jpa.*`: Controls how Hibernate interacts with the database, shows SQL queries, updates the schema automatically, and uses the correct dialect.

### 4. Email
- `spring.mail.*`: Configures the SMTP server for sending emails (account activation, password recovery, etc.).

### 5. Security and JWT
- `application.security.jwt.*`: Defines the secret key, expiration, and URLs related to JWT-based authentication.

### 6. Frontend and CORS
- `application.front-end.url`: URL allowed for requests from the frontend (CORS).

### 7. Server
- `server.servlet.context-path`, `server.port`: Sets the API route prefix and the port where the app runs.

### 8. File Uploads
- `spring.servlet.multipart.*`: Limits the size of uploaded files and requests.

### 9. Imgur
- `imgur.*`: Configuration for uploading images to Imgur.

### 10. OAuth2 (Google and Facebook)
- `spring.security.oauth2.client.registration.*` and `spring.security.oauth2.client.provider.*`: Enable login with Google and Facebook, using environment variables for credentials and endpoint URLs.

> **Recommendation:** Use environment variables for sensitive data and make sure all are defined before starting the app.

---

---

### 4️⃣ Build the Project

```bash
mvn clean install
```

---

### 5️⃣ Run the Application

```bash
mvn spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

---

## 🧪 Modules

This project is divided into the following modules:

- **Survey**: Create, edit, and participate in surveys
- **Review**: Allow users to leave feedback
- **Email**: Notification system (SMTP-based)
- **Image**: Upload and retrieve images via **Imgur API**
- **Reports**: Generate aggregated data reports for each survey
- **Profile**: View user profile and all surveys created by that user

---

## 📊 Survey Reports

Admins and survey creators can generate detailed reports with aggregated answers and metadata for each survey.

Example data included:

- Total responses per question
- Answer distribution (bar/pie charts if integrated on frontend)
- Submission timestamps
- User participation stats

---

## 👤 User Profile

Each authenticated user has access to their **profile**, where they can:

- View and manage all their created surveys
- Edit or delete surveys
- Access report summaries per survey

---

## 🤝 Contributing

Want to help improve the app? Here's how:

1. Fork the repository
2. Create a new branch:
   ```bash
   git checkout -b feature/your-feature
   ```
3. Commit your changes:
   ```bash
   git commit -am 'Add new feature'
   ```
4. Push to your fork:
   ```bash
   git push origin feature/your-feature
   ```
5. Open a **Pull Request**

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

## 📬 Contact

For questions, ideas, or feedback, feel free to [open an issue](https://github.com/Ovtt17/survey-app-api/issues).

---

##  Survey App UI

You can find and try the Survey App UI (frontend) here:

[Go to Survey App UI](https://github.com/Ovtt17/survey-app-ui)

