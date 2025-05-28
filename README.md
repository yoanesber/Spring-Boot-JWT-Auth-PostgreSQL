# Netflix Shows REST API with JWT Authentication

## 📖 Overview  

This project is a **REST API** for managing **Netflix Shows**, built using **Spring Boot**. It leverages **PostgreSQL** as the database, **Spring Data JPA** for data management, and **Spring Boot Starter Security** for authentication and authorization. The API is secured using **JWT (JSON Web Token)**, implemented with the open-source **JJWT** library.  

A key aspect of this project is the implementation of **JJWT** to **create and verify JWTs** as an authentication mechanism for accessing NetflixShows resources. JWT is used as a Bearer token, meaning it is included in the Authorization header of HTTP requests to authenticate users. Compared to traditional session-based authentication, JWT provides a **stateless and scalable** approach, reducing the need for server-side session storage. Unlike API keys, JWTs offer **built-in expiration** and **can carry claims**, allowing for **more flexible authorization strategies**.  

### 🔐 JWT Algorithm Flexibility  

This application supports both `HMAC` and `RSA` algorithms for **signing JWTs**, allowing you to choose the desired `cryptographic` method based on your security and deployment needs:  

- **HMAC (symmetric)**: Uses a shared secret key (`HS256`).  
- **RSA (asymmetric)**: Uses a private key to sign and a public key to verify (`RS256`).  

The signing algorithm is configurable via the `application.properties` file using the property:  

```properties
jwt.key-algorithm=HMAC # or RSA
```  

This design provides the flexibility to switch algorithms without changing the application logic.  

This application functions both as a **resource server** and a **custom authorization server**, as it is responsible for **issuing (access and refresh tokens) and validating JWTs** internally for authenticated users. It implements **custom JWT-based authentication**, meaning it does not follow the full OAuth2 protocol. As a result, the login request only requires a username and password—**the `grant_type` parameter is not needed**—because the token issuance (access and refresh tokens) and token refresh are handled via **separate, dedicated endpoints:**  

- `/auth/login` — Handles user authentication. The user provides a username and password, which are authenticated using `UsernamePasswordAuthenticationToken`. Upon successful authentication, the system sets the authentication object in the `SecurityContextHolder`, generates a JWT access token and a refresh token, and updates the user's last login time.  
- `/auth/refresh-token` — Manages refresh tokens using a rotating strategy. Refresh tokens are stored in the `refresh_token` table (fields: `token`, `expiry_date`, and `user_id`). When a request is made to this endpoint, the system verifies the token's existence and expiration, then generates a new JWT access token and a new refresh token, replacing the old one.  

### 🔄 Refresh Token Flow

The **Refresh Token API** is used to **renew** an expired access token without requiring the user to log in again. If the **access token is expired**, but the **refresh token is still valid**, the system will automatically:

- Generate a **new access token**  
- Generate a **new refresh token**  
- Replace the old refresh token in the database

This is a **rotating refresh token strategy** — meaning the old refresh token is replaced after each use to enhance security.

By keeping authentication and token renewal separate, the design promotes clarity, maintainability, and security, all while maintaining full control over the authentication lifecycle.  

---

## 🤖 Tech Stack  

The technology used in this project are:  

- `Spring Boot Starter Web` – Building RESTful APIs or web applications
- `Spring Security` – Provides authentication and authorization mechanisms, ensuring secure access to the application.
- `JJWT (api, impl, jackson)` – Open-source library for creating and verifying JSON Web Tokens (JWTs) used for authentication.
- `PostgreSQL` – Serves as the database for storing Netflix Shows
- `Hibernate` – Simplifying database interactions
- `Lombok` – Reducing boilerplate code
---

## 🧱 Architecture Overview  

The project follows a modular architecture to ensure **separation of concerns**, **testability**, and **maintainability**. Here's a breakdown of each module's responsibility:  

```bash
📂jwt-auth-postgresql/
├── 📂src/
│   ├── 📂main/
│   │   ├── 📂docker/
│   │   │   ├── 📂app/                     # Dockerfile for Spring Boot application (runtime container)
│   │   │   │   └── Dockerfile             # Uses base image, copies JAR/dependencies, defines ENTRYPOINT
│   │   │   └── 📂postgres/                # Custom PostgreSQL Docker image (optional)
│   │   │       ├── Dockerfile             # Extends from postgres:17, useful for init customization
│   │   │       └── init.sql               # SQL script to create database, user, and grant permissions
│   │   ├── 📂java/
│   │   │   ├── 📂config/                  # Spring configuration classes (e.g., security, JWT)
│   │   │   │   └── 📂serializer/          # Custom Jackson serializers/deserializers (e.g., for `Instant`)
│   │   │   ├── 📂controller/              # REST API endpoints (e.g., AuthController, NetflixShowsController)
│   │   │   ├── 📂dto/                     # Data Transfer Objects for requests/responses
│   │   │   ├── 📂entity/                  # JPA entity classes mapped to database tables
│   │   │   ├── 📂handler/                 # Global exception handling and custom error responses
│   │   │   ├── 📂mapper/                  # MapStruct or manual mappers between DTO and entity
│   │   │   ├── 📂repository/              # Spring Data JPA interfaces for database access
│   │   │   ├── 📂service/                 # Business logic layer
│   │   │   │   └── 📂impl/                # Service implementation classes
│   │   │   └── 📂util/                    # Utility/helper classes (e.g., JWT helpers, response builder)
│   │   └── 📂resources/
│   │       ├── application.properties     # Application configuration (DB, JWT, profiles, etc.)
│   │       ├── generate-jwt-keys.sh       # Script to generate RSA key pairs for JWT
│   │       ├── import.sql                 # SQL file for seeding database on startup
│   │       ├── privateKey.pem             # RSA private key for signing JWTs
│   │       └── publicKey.pem              # RSA public key for verifying JWTs
│   └── 📂test/java/                       # Unit and integration test classes
├── 📂target/                              # Maven build output (ignored in version control)
├── .dockerignore                          # Files/directories to exclude from Docker build context
├── .gitignore                             # Files/directories to exclude from Git tracking
├── Makefile                               # Task automation (build/run app, setup DB, etc.)
├── mvnw                                   # Maven wrapper script for Unix-based systems
├── mvnw.cmd                               # Maven wrapper script for Windows
├── pom.xml                                # Maven project configuration (dependencies, plugins)
└── README.md                              # Project documentation and usage guide
```

This clean separation allows the application to **scale well**, supports **test-driven development**, and adheres to best practices in **enterprise application design**.  

---


## 🛠️ Installation & Setup  

Follow these steps to set up and run the project locally:  

### ✅ Prerequisites

Make sure the following tools are installed on your system:

| Tool                                      | Description                                                                 | Required      |
|-------------------------------------------|-----------------------------------------------------------------------------|---------------|
| [Java 17+](https://adoptium.net/)         | Java Development Kit (JDK) to run the Quarkus application                   | ✅            |
| [PostgreSQL](https://www.postgresql.org/) | Relational database to persist application data                             | ✅            |
| [Make](https://www.gnu.org/software/make/)| Automation tool for tasks like `make run-app`                               | ✅            |
| [Docker](https://www.docker.com/)         | To run services like Kafka/PostgreSQL in isolated containers                | ⚠️ *optional* |

### ☕ 1. Install Java 17  

1. Ensure **Java 17** is installed on your system. You can verify this with:  

```bash
java --version
```  

2. If Java is not installed, follow one of the methods below based on your operating system:  

#### 🐧 Linux  

**Using apt (Ubuntu/Debian-based)**:  

```bash
sudo apt update
sudo apt install openjdk-17-jdk
```  

#### 🪟 Windows  
1. Use [https://adoptium.net](https://adoptium.net) to download and install **Java 17 (Temurin distribution recommended)**.  

2. After installation, ensure `JAVA_HOME` is set correctly and added to the `PATH`.  

3. You can check this with:  

```bash
echo $JAVA_HOME
```  

### 🐘 2. Install PostgreSQL  
1. Install PostgreSQL if it’s not already available on your machine:  
    - Use [https://www.postgresql.org/download/](https://www.postgresql.org/download/) to download PostgreSQL.  

2. Once installed, create the following databases:  
```sql
CREATE DATABASE netflix;  
```  

These databases are used for development and automated testing, respectively.  

### 🧰 3. Install `make` (Optional but Recommended)  
This project uses a `Makefile` to streamline common tasks.  

Install `make` if not already available:  

#### 🐧 Linux  

Install `make` using **APT**  

```bash
sudo apt update
sudo apt install make
```  

You can verify installation with:   
```bash
make --version
```  

#### 🪟 Windows  

If you're using **PowerShell**:  

- Install [Chocolatey](https://chocolatey.org/install) (if not installed):  
```bash
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
```  

- Verify `Chocolatey` installation:  
```bash
choco --version
```  

- Install `make` via `Chocolatey`:  
```bash
choco install make
```  

After installation, **restart your terminal** or ensure `make` is available in your `PATH`.  

### 🔁 4. Clone the Project  

Clone the repository:  

```bash
git clone https://github.com/yoanesber/Spring-Boot-JWT-Auth-PostgreSQL.git
cd Spring-Boot-JWT-Auth-PostgreSQL
```  

### ⚙️ 5. Configure Application Properties  

Set up your `application.properties` in `src/main/resources`:  

```properties
# application configuration
spring.application.name=jwt-auth-postgresql
server.port=8080
spring.profiles.active=development

## datasource configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/netflix
spring.datasource.username=appuser
spring.datasource.password=app@123
spring.datasource.driver-class-name=org.postgresql.Driver
spring.sql.init.mode=always

## hibernate configuration
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.hibernate.naming.implicit-strategy=org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=true


## jwt configuration
jwt.header=Authorization
jwt.token.type=Bearer
jwt.issuer=http://localhost:8080/realms/jwt-auth
jwt.expiration-ms=900000
jwt.refresh-token.expiration-ms=1296000000
jwt.cookie.name=accessToken
jwt.cookie.path=/api
jwt.cookie.max-age-ms=86400000
jwt.cookie.secure=true
jwt.cookie.http-only=true
jwt.cookie.same-site=Lax
jwt.cookie.response-enabled=true
jwt.key-algorithm=HMAC
# optional: if you want to use asymmetric encryption (RSA)
jwt.private-key-file=./src/main/resources/privateKey.pem
jwt.public-key-file=./src/main/resources/publicKey.pem
jwt.keySize=2048
# optional: if you want to use symmetric encryption (HMAC)
jwt.key-secret=qwertyuiopasdfghjklzxcvbnm1234567890abcd

## cors configuration
cors-allowed-origins=http://localhost:8080
cors-allowed-methods=GET,POST,PUT,DELETE,OPTIONS
cors-allowed-headers=Authorization,Cache-Control,Content-Type
cors-allow-credentials=true
cors-max-age=3600
cors-exposed-headers=Authorization
cors-configuration-endpoint=/**

## http security
permit-all-request-url=/api/v1/auth/**
excluded-paths-for-authentication=/api/v1/auth/login,/api/v1/auth/refresh-token
```

- **🔐 Notes**:  Ensure that:  
  - Database URLs, username, and password are correct.  
  - JWT keys (path to `.pem` files) are set correctly.
  - `spring.datasource.username=appuser`, `spring.datasource.password=app@123`: It's strongly recommended to create a dedicated database user instead of using the default postgres superuser.



### 🔐 6. Generate JWT RSA Key Pair  

Generate a `private` and `public key` pair to **sign** and **verify** JWT tokens:  

**Using `make`:**  

```bash
make generate-jwt-keys
```  

**Or manually:**  

```bash
bash generate-jwt-keys.sh
```  

This will generate `privateKey.pem` and `publicKey.pem` in the `src/main/resources/` directory. And the files will be referenced by your `application.properties`:
```properties
jwt.private-key-file=./src/main/resources/privateKey.pem
jwt.public-key-file=./src/main/resources/publicKey.pem
```

**⚠️ Security Note:**  
The `privateKey.pem` file is included in `.gitignore` to **prevent accidental commits to the repository**, especially since this project will be made **public**.  
**Never expose your private key** in version control to protect your JWT signing mechanism. You **must generate** your own `private` and `public key` pair.  


### 👤 7. Create Dedicated PostgreSQL User (Recommended)

For security reasons, it's recommended to avoid using the default postgres superuser. Use the following SQL script to create a dedicated user (`appuser`) and assign permissions:

```sql
-- Create appuser and database
CREATE USER appuser WITH PASSWORD 'app@123';

-- Allow user to connect to database
GRANT CONNECT ON DATABASE netflix TO appuser;

-- Grant permissions on public schema
GRANT USAGE, CREATE ON SCHEMA public TO appuser;

-- Grant all permissions on existing tables
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO appuser;

-- Grant all permissions on sequences (if using SERIAL/BIGSERIAL ids)
GRANT USAGE, SELECT, UPDATE ON ALL SEQUENCES IN SCHEMA public TO appuser;

-- Ensure future tables/sequences will be accessible too
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO appuser;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO appuser;
```

Update your `application.properties` accordingly:
```properties
spring.datasource.username=appuser
spring.datasource.password=app@123
```

---

## 🚀 8. Running the Application  

This section provides step-by-step instructions to run the application either **locally** or via **Docker containers**.

- **Notes**:  
  - All commands are defined in the `Makefile`.
  - To run using `make`, ensure that `make` is installed on your system.
  - To run the application in containers, make sure `Docker` is installed and running.


### 🔧 Run Locally (Non-containerized)

Ensure PostgreSQL and Kafka are running locally, then:

```bash
make dev
```

### 🐳 Run Using Docker

To build and run all services (PostgreSQL, Apache Kafka, Quarkus app):

```bash
make docker-start-all
```

To stop and remove all containers:

```bash
make docker-stop-all
```

- **Notes**:  
  - Before running the application inside Docker, make sure to update your `application.properties`
    - Replace `localhost` with the appropriate **container name** for services like PostgreSQL.  
    - For example:
      - Change `localhost:5432` to `jwt-auth-postgres:5432`

### 🟢 Application is Running

Now your application is accessible at:
```bash
http://localhost:8080
```

---


## 🧪 Testing Scenarios  

The REST API provides a set of endpoints to manage Netflix shows, allowing clients to perform CRUD operations (Create, Read, Update, Delete). Each endpoint follows RESTful principles and accepts/returns JSON data. Authentication is handled using JWT Bearer tokens, ensuring secure access to protected resources. Below is a list of available endpoints along with sample requests.  

### 🔐 Authentication  

#### 1. Login API Testing Scenarios

Login API allows users to authenticate by providing valid credentials. Upon successful authentication, the server responds with an access token and a refresh token. The access token is used for making authorized requests, while the refresh token is used to obtain a new access token when the previous one expires.  

**Endpoint:**  

```bash
POST http://localhost:8080/auth/login
Content-Type: application/json
```  

##### Scenario 1: Successful Login

**Request Body:**  

```json
{
    "username":"userone",
    "password":"P@ssw0rd"
}
```

**Successful Response:**  

```json
{
    "message": "Login successful",
    "error": null,
    "path": "/api/v1/auth/login",
    "status": 200,
    "data": {
        "accessToken": "<JWT_TOKEN>",
        "refreshToken": "<UUID_REFRESH_TOKEN>",
        "expirationDate": "2025-05-28T12:29:51.000Z",
        "tokenType": "Bearer"
    },
    "timestamp": "2025-05-28T12:14:51.684714Z"
}
```


##### Scenario 2: Invalid Credentials  

**Request Body:**  

```json
{
    "username":"invalid_user",
    "password":"P@ssw0rd"
}
```  

**Expected Response (`401 Unauthorized`):**  

```json
{
    "message": "Authentication Failed",
    "error": "Invalid username or password",
    "path": "/auth/login",
    "status": 401,
    "data": null,
    "timestamp": "2025-05-28T15:26:37.289781300Z"
}
```  

##### Scenario 3: Disabled User  

**Precondition:**  

```sql
UPDATE users SET is_enabled = false WHERE id = 2;
```  

**Request Body:**  

```json
{
    "username":"userone",
    "password":"P@ssw0rd"
}
```  

**Expected Response (`401 Unauthorized`):**  

```json
{
    "message": "Authentication Failed",
    "error": "User is disabled",
    "path": "/auth/login",
    "status": 401,
    "data": null,
    "timestamp": "2025-05-28T15:27:59.157329700Z"
}
```  

##### Scenario 4: Expired User Account  

**Precondition:**  

```sql
UPDATE users SET is_account_non_expired = false WHERE id = 2;
```  

**Request Body:**  

```json
{
    "username":"userone",
    "password":"P@ssw0rd"
}
```  

**Expected Response (`401 Unauthorized`):**  

```json
{
    "message": "Authentication Failed",
    "error": "User account has expired",
    "path": "/auth/login",
    "status": 401,
    "data": null,
    "timestamp": "2025-05-28T15:29:01.301251900Z"
}
```  

#### 🔄 Refresh Token API  

Refresh Token API is used to **renew** an expired access token without requiring the user to log in again. Clients send a valid refresh token, and the server issues a new access token and a new refresh token.  

**Endpoint:**  

```bash
POST http://localhost:8080/auth/refresh-token
Content-Type: application/json
```  

##### Scenario 1: Successful Refresh Token

**Request Body:**  

```json
{
    "refreshToken": "<UUID_REFRESH_TOKEN>"
}
```

**Successful Response:**  

```json
{
    "message": "Refresh token successful",
    "error": null,
    "path": "/auth/refresh-token",
    "status": 200,
    "data": {
        "accessToken": "<JWT_TOKEN>",
        "refreshToken": "<UUID_REFRESH_TOKEN>",
        "expirationDate": "2025-05-28T12:30:07.000Z",
        "tokenType": "Bearer"
    },
    "timestamp": "2025-05-28T12:15:07.486443700Z"
}
```

##### Scenario 2: Invalid Refresh Token

**Invalid refresh token Response:**  

```json
{
    "message": "Invalid Refresh Token",
    "error": "The provided refresh token is invalid or does not exist",
    "path": "/auth/refresh-token",
    "status": 400,
    "data": null,
    "timestamp": "2025-05-28T15:33:20.291114800Z"
}
```


### 🎬 Netflix Shows API  

Netflix Show API allows users to perform CRUD operations on Netflix Shows. Users can create, retrieve, update, and delete show records. Access to these endpoints requires authentication via JWT.  

#### Create a Netflix Show  

This endpoint allows users to create a new Netflix show by providing relevant details in the request body. Ensure that a valid JWT token is included in the Authorization header.  

**Endpoint:**  

```bash
POST http://localhost:8080/api/v1/netflix-shows
Content-Type: application/json
```  

##### Scenario 1: Successful Create Record

**Request Body:**  

```json
{
    "showType": "MOVIE",
    "title": "Sankofa",
    "director": "Haile Gerima",
    "castMembers": "Kofi Ghanaba, Oyafunmike Ogunlano, Alexandra Duah, Nick Medley, Mutabaruka, Afemo Omilami, Reggie Carter, Mzuri, Oliver",
    "country": "United States",
    "dateAdded": "2021-09-24",
    "releaseYear": 2024,
    "rating": "TV-MA",
    "duration": "90 min",
    "listedIn": "Drama",
    "description": "A woman adjusting to life after a loss contends with a feisty bird that's taken over her garden — and a husband who's struggling to find a way forward."
}
```

**Successful Response:**  

```json
{
    "message": "Record created successfully",
    "error": null,
    "path": "/api/v1/netflix-shows",
    "status": 200,
    "data": {
        "id": 21,
        "showType": "MOVIE",
        "title": "Sankofa",
        "director": "Haile Gerima",
        "castMembers": "Kofi Ghanaba, Oyafunmike Ogunlano, Alexandra Duah, Nick Medley, Mutabaruka, Afemo Omilami, Reggie Carter, Mzuri, Oliver",
        "country": "United States",
        "dateAdded": "2021-09-24",
        "releaseYear": 2024,
        "rating": "TV-MA",
        "duration": "90 min",
        "listedIn": "Drama",
        "description": "A woman adjusting to life after a loss contends with a feisty bird that's taken over her garden — and a husband who's struggling to find a way forward."
    },
    "timestamp": "2025-05-28T15:40:08.232535600Z"
}
```

##### Scenario 2: Expired Token

**Expired JWT Token Response:**  

```json
{
    "message": "Unauthorized request",
    "error": "Token has expired",
    "path": "/api/v1/netflix-shows",
    "status": 401,
    "data": null,
    "timestamp": "2025-05-28T15:37:01.115671900Z"
}
```

##### Scenario 2: Invalid Token

**Invalid JWT Token Response:**  

```json
{
    "message": "Unauthorized request",
    "error": "Invalid token signature",
    "path": "/api/v1/netflix-shows",
    "status": 401,
    "data": null,
    "timestamp": "2025-05-28T15:38:21.516094300Z"
}
```

##### Scenario 3: Malformed Token

**Malformed JWT Token Response:**  

```json
{
    "message": "Unauthorized request",
    "error": "Malformed or unsupported JWT token",
    "path": "/api/v1/netflix-shows",
    "status": 401,
    "data": null,
    "timestamp": "2025-05-28T15:38:34.345061600Z"
}
```

#### Get All Netflix Shows  

Retrieves a list of all Netflix shows stored in the database.  

**Endpoint:**  

```bash
GET http://localhost:8080/api/v1/netflix-shows
```  

**Successful Response:**  

```json
{
    "message": "Record retrieved successfully",
    "error": null,
    "path": "/api/v1/netflix-shows",
    "status": 200,
    "data": [
        {
            "id": 1,
            "showType": "MOVIE",
            "title": "Dick Johnson Is Dead",
            "director": "Kirsten Johnson",
            "castMembers": null,
            "country": "United States",
            "dateAdded": "2021-09-25",
            "releaseYear": 2020,
            "rating": "PG-13",
            "duration": "90 min",
            "listedIn": "Documentaries",
            "description": "As her father nears the end of his life, filmmaker Kirsten Johnson stages his death in inventive and comical ways to help them both face the inevitable."
        },
        ...
    ],
    "timestamp": "2025-05-28T15:42:21.627460400Z"
}
```

#### Get Netflix Show by ID  

Fetches the details of a specific Netflix show using its unique ID.  

**Endpoint:**  

```bash
GET http://localhost:8080/api/v1/netflix-shows/{id}  
```  

**Successful Response:**  

```json
{
    "message": "Record retrieved successfully",
    "error": null,
    "path": "/api/v1/netflix-shows/21",
    "status": 200,
    "data": {
        "id": 21,
        "showType": "MOVIE",
        "title": "Sankofa",
        "director": "Haile Gerima",
        "castMembers": "Kofi Ghanaba, Oyafunmike Ogunlano, Alexandra Duah, Nick Medley, Mutabaruka, Afemo Omilami, Reggie Carter, Mzuri, Oliver",
        "country": "United States",
        "dateAdded": "2021-09-24",
        "releaseYear": 2024,
        "rating": "TV-MA",
        "duration": "90 min",
        "listedIn": "Drama",
        "description": "A woman adjusting to life after a loss contends with a feisty bird that's taken over her garden — and a husband who's struggling to find a way forward."
    },
    "timestamp": "2025-05-28T15:43:40.840925500Z"
}
```

**Not found Response:**  

```json
{
    "message": "Record not found",
    "error": "NetflixShows not found with ID: 22",
    "path": "/api/v1/netflix-shows/22",
    "status": 404,
    "data": null,
    "timestamp": "2025-05-28T15:46:07.050397400Z"
}
```

#### Update a Netflix Show  

Allows updating the details of an existing Netflix show.  

**Endpoint:**  

```bash
PUT http://localhost:8080/api/v1/netflix-shows/{id}  
```  

**Request Body:**  

```json
{
    "showType": "MOVIE",
    "title": "Sankofa",
    "director": "Haile Gerima",
    "castMembers": "Kofi Ghanaba, Oyafunmike Ogunlano, Alexandra Duah, Nick Medley, Mutabaruka, Afemo Omilami, Reggie Carter, Mzuri, Oliver",
    "country": "United States",
    "dateAdded": "2021-09-24",
    "releaseYear": 2024,
    "rating": "TV-MB",
    "duration": "120 min",
    "listedIn": "Comedy",
    "description": "A woman adjusting to life after a loss contends with a feisty bird that's taken over her garden — and a husband who's struggling to find a way forward."
}
```

**Successful Response:**  

```json
{
    "message": "Record updated successfully",
    "error": null,
    "path": "/api/v1/netflix-shows/21",
    "status": 200,
    "data": {
        "id": 21,
        "showType": "MOVIE",
        "title": "Sankofa",
        "director": "Haile Gerima",
        "castMembers": "Kofi Ghanaba, Oyafunmike Ogunlano, Alexandra Duah, Nick Medley, Mutabaruka, Afemo Omilami, Reggie Carter, Mzuri, Oliver",
        "country": "United States",
        "dateAdded": "2021-09-24",
        "releaseYear": 2024,
        "rating": "TV-MB",
        "duration": "120 min",
        "listedIn": "Comedy",
        "description": "A woman adjusting to life after a loss contends with a feisty bird that's taken over her garden — and a husband who's struggling to find a way forward."
    },
    "timestamp": "2025-05-28T12:15:49.740526700Z"
}
```

**Not found Response:**  

```json
{
    "message": "Record not found",
    "error": "NetflixShows not found with ID: 22",
    "path": "/api/v1/netflix-shows/22",
    "status": 404,
    "data": null,
    "timestamp": "2025-05-28T15:45:31.568490900Z"
}
```

#### Delete a Netflix Show  

Deletes a specific Netflix show from the database.  

**Note**:  
This operation performs a soft delete, meaning the record is not permanently removed from the database. Instead, it updates the following fields in the database:  
- `is_deleted` → set to true
- `deleted_by` → set to the current authenticated user ID
- `deleted_at` → set to the current timestamp

These flags allow the record to be excluded from retrieval operations (e.g., Get All, Get By ID), but still retained in the database for audit or recovery purposes.
All `GET` endpoints are designed to exclude records where `is_deleted = true`.

**Endpoint:**  

```bash
DELETE http://localhost:8080/api/v1/netflix-shows/{id}  
```  

**Successful Response:**  

```json
{
    "message": "Record deleted successfully",
    "error": null,
    "path": "/api/v1/netflix-shows/21",
    "status": 200,
    "data": null,
    "timestamp": "2025-05-28T12:15:56.640090800Z"
}
```
---

## 📝 Notes & Future Enhancements  

- **JWT Expiration** – The access token has a limited validity period. Clients should use the refresh token to obtain a new access token when expired.
- **Authorization** – Every API request must include a valid JWT token in the Authorization header (`Bearer <JWT Token>`).
- **Stateless or Stateful Authentication**: When implementing authentication with JWT, it's important to consider whether to use a stateless or stateful approach based on the application's needs.
- **Data Validation** – Requests with missing or invalid fields will return a `400 Bad Request` response.
- **Security Considerations**:
    - Never expose JWT tokens in frontend code or logs.
    - Use HTTPS to protect tokens in transit.
    - Implement `role-based access control (RBAC)` to restrict API actions based on user roles, ensuring that only authorized users can perform specific operations. Assign different roles such as 'ADMIN' and 'USER' to enforce proper access levels.
- **Database Schema** – Ensure all necessary tables (`users, roles, netflix_shows`) are created and populated correctly.
- **Error Handling** – The API provides meaningful error messages with appropriate HTTP status codes (`400, 401, 403, 404, 500`).

---

## 🔗 Related Repositories  

- JWT Authentication with Kong GitHub Repository, check out [Spring Boot Department API with Kong JWT Authentication (DB-Backed Mode)](https://github.com/yoanesber/Spring-Boot-JWT-Auth-Kong).  
- Form-Based Authentication Repository, check out [Spring Web Application with JDBC Session](https://github.com/yoanesber/Spring-Boot-JDBC-Session).