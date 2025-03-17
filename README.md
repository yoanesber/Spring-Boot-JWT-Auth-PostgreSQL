 # ⚡Netflix Shows REST API with JWT Authentication

## 🚀 Overview
This project is a **REST API** for managing **Netflix Shows**, built using `Spring Boot 3.4.2`. It leverages `PostgreSQL` as the database, `Spring Data JPA` for data management, and `Spring Boot Starter Security` for authentication and authorization. The API is secured using `JWT (JSON Web Token)`, implemented with the open-source JJWT library.

A key aspect of this project is the implementation of **JJWT** to create and verify JWTs as an authentication mechanism for accessing NetflixShows resources. JWT is used as a Bearer token, meaning it is included in the Authorization header of HTTP requests to authenticate users. Compared to traditional session-based authentication, JWT provides a **stateless and scalable** approach, reducing the need for server-side session storage. Unlike API keys, JWTs offer **built-in expiration** and **can carry claims**, allowing for **more flexible authorization strategies**.

---

## ✨Tech Stack
The technology used in this project are:
- `Spring Boot 3.4.2` : Framework for building RESTful APIs
- `Spring Data JPA with Hibernate` : Simplifying database interactions
- `Spring Boot Starter Web` : Building RESTful APIs or web applications
- `PostgreSQL` : Serves as the database for storing Netflix Shows
- `Lombok` : Reducing boilerplate code
- `JJWT (api, impl, jackson)` : Open-source library for creating and verifying JSON Web Tokens (JWTs) used for authentication.
---

## 📋 Project Structure
The project is organized into the following package structure:
```bash
jwt-auth-postgresql/
│── src/main/java/com/yoanesber/spring/security/jwt_auth_postgresql/
│   ├── config/                # Holds configuration classes, including security, CORS, and application properties.
│   │   ├── serializer/        # Contains custom serializers and deserializers for JSON processing.
│   ├── controller/            # Contains REST controllers that handle HTTP requests and return responses.
│   ├── dto/                   # Data Transfer Objects (DTOs) for request/response payloads.
│   ├── entity/                # Contains JPA entity classes representing database tables.
│   ├── handler/               # Manages global exception handling and API error responses.
│   ├── repository/            # Provides database access functionality using Spring Data JPA.
│   ├── service/               # Business logic layer
│   │   ├── impl/              # Implementation of services
```
---

## 📂 Environment Configuration
Configuration values are stored in `.env.development` and referenced in `application.properties`.

Example `.env.development` file content:
```properties
# application
APP_PORT=8081
SPRING_PROFILES_ACTIVE=development

# postgres
SPRING_DATASOURCE_PORT=5432
SPRING_DATASOURCE_USERNAME=myusername
SPRING_DATASOURCE_PASSWORD=mypassword 
SPRING_DATASOURCE_DB=netflix_development

# jwt
JWT_HEADER=Authorization
JWT_PREFIX=Bearer
JWT_TOKEN_NAME=accessToken
JWT_SECRET=<secret_string>
JWT_REFRESH_TOKEN_NAME=refreshToken
JWT_EXPIRATION_MS=900000
JWT_REFRESH_EXPIRATION_MS=1296000000
JWT_COOKIE_NAME=accessToken
JWT_COOKIE_PATH=/api
JWT_COOKIE_MAX_AGE_MS=86400000
JWT_COOKIE_HTTP_ONLY=true
JWT_COOKIE_SECURE=true
JWT_COOKIE_SAME_SITE=Lax
JWT_COOKIE_RESPONSE_ENABLED=false

#cors
CORS_ALLOWED_ORIGINS=http://localhost:8082
CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS
CORS_ALLOWED_HEADERS=Authorization,Cache-Control,Content-Type
CORS_ALLOW_CREDENTIALS=true
CORS_MAX_AGE=3600
CORS_EXPOSED_HEADERS=Authorization
CORS_CONFIGURATION_ENDPOINT=/**

# http security
SECURITY_PERMIT_ALL_REQUEST_URL=/api/v1/auth/**
SECURITY_EXCLUDED_PATHS_FOR_AUTHENTICATION=/api/v1/auth/login,/api/v1/auth/refresh-token
```

Example `application.properties` file content:
```properties
## application
spring.application.name=jwt-auth-postgresql
server.port=${APP_PORT}
spring.profiles.active=${SPRING_PROFILES_ACTIVE}

## datasource 
spring.datasource.url=jdbc:postgresql://localhost:${SPRING_DATASOURCE_PORT}/${SPRING_DATASOURCE_DB}?currentSchema=netflix
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

## hibernate 
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.hibernate.naming.implicit-strategy=org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=true

## jwt configuration
jwt.header=${JWT_HEADER}
jwt.prefix=${JWT_PREFIX}
jwt.tokenName=${JWT_TOKEN_NAME}
jwt.secret=${JWT_SECRET}
jwt.expirationMs=${JWT_EXPIRATION_MS}
jwt.refreshTokenName=${JWT_REFRESH_TOKEN_NAME}
jwt.refreshTokenExpirationMs=${JWT_REFRESH_EXPIRATION_MS}
jwt.cookieName=${JWT_COOKIE_NAME}
jwt.cookiePath=${JWT_COOKIE_PATH}
jwt.cookieMaxAgeMs=${JWT_COOKIE_MAX_AGE_MS}
jwt.cookieSecure=${JWT_COOKIE_SECURE}
jwt.cookieHttpOnly=${JWT_COOKIE_HTTP_ONLY}
jwt.cookieSameSite=${JWT_COOKIE_SAME_SITE}
jwt.cookieResponseEnabled=${JWT_COOKIE_RESPONSE_ENABLED}

## cors configuration
cors-allowed-origins=${CORS_ALLOWED_ORIGINS}
cors-allowed-methods=${CORS_ALLOWED_METHODS}
cors-allowed-headers=${CORS_ALLOWED_HEADERS}
cors-allow-credentials=${CORS_ALLOW_CREDENTIALS}
cors-max-age=${CORS_MAX_AGE}
cors-exposed-headers=${CORS_EXPOSED_HEADERS}
cors-configuration-endpoint=${CORS_CONFIGURATION_ENDPOINT}

## http security
permit-all-request-url=${SECURITY_PERMIT_ALL_REQUEST_URL}
excluded-paths-for-authentication=${SECURITY_EXCLUDED_PATHS_FOR_AUTHENTICATION}
```
---

## 💾 Database Schema (DDL – PostgreSQL)
The following is the database schema for the PostgreSQL database used in this project:

```sql
CREATE SCHEMA netflix;

-- create table roles
CREATE TABLE IF NOT EXISTS netflix.roles
(
	id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
	name character varying(20) COLLATE pg_catalog."default" NOT NULL,
	CONSTRAINT roles_pkey PRIMARY KEY (id)
);

-- feed data roles
INSERT INTO netflix.roles ("name") VALUES
	 ('ROLE_USER'),
	 ('ROLE_MODERATOR'),
	 ('ROLE_ADMIN');


-- create table users
CREATE TABLE IF NOT EXISTS netflix.users
(
	id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
	username character varying(20) COLLATE pg_catalog."default" NOT NULL,
    password character varying(150) COLLATE pg_catalog."default" NOT NULL,
    email character varying(100) COLLATE pg_catalog."default" NOT NULL,
    firstname character varying(20) COLLATE pg_catalog."default" NOT NULL,
    lastname character varying(20) COLLATE pg_catalog."default",
    is_enabled boolean NOT NULL DEFAULT false,
    is_account_non_expired boolean NOT NULL DEFAULT false,
    is_account_non_locked boolean NOT NULL DEFAULT false,
    is_credentials_non_expired boolean NOT NULL DEFAULT false,
    is_deleted boolean NOT NULL DEFAULT false,
	account_expiration_date timestamp with time zone,
    credentials_expiration_date timestamp with time zone,
	last_login timestamp with time zone,
	user_type character varying(15) COLLATE pg_catalog."default" NOT NULL,
	created_by character varying(20) NOT NULL,
	created_date timestamp with time zone NOT NULL DEFAULT now(),
	updated_by character varying(20) NOT NULL,
	updated_date timestamp with time zone NOT NULL DEFAULT now(),
	CONSTRAINT users_pkey PRIMARY KEY (id),
	CONSTRAINT users_unique_username UNIQUE (username),
	CONSTRAINT users_unique_email UNIQUE (email),
	CONSTRAINT users_user_type_check CHECK (user_type::text = ANY (ARRAY['SERVICE_ACCOUNT'::character varying, 'USER_ACCOUNT'::character varying]::text[]))
);

-- feed data users
-- both superadmin and channel1 password is `P@ssw0rd`
INSERT INTO netflix.users (username,"password",email,firstname,lastname,is_enabled,is_account_non_expired,is_account_non_locked,is_credentials_non_expired,is_deleted,account_expiration_date,credentials_expiration_date,last_login,user_type,created_by,created_date,updated_by,updated_date) VALUES
	 ('superadmin','$2a$10$71wrLlzlkJ/54ZWDwA6KiegFX0naXg.T2zvKB2EbyqdS1Yl7Cwt1W','superadmin@youremail.com','Super','Admin',true,true,true,true,false,'2025-04-23 21:52:38+07','2025-02-28 01:58:35.835127+07','2025-02-11 22:54:32.816+07','USER_ACCOUNT','system','2024-09-04 03:42:58.847+07','system','2024-11-28 01:58:35.835+07'),
	 ('channel1','$2a$10$eP5Sddi7Q5Jv6seppeF93.XsWGY8r4PnsqprWGb5AxsZ9TpwULIGa','channel1@youremail.com','Channel','One',true,true,true,true,false,'2025-07-14 19:50:56.880054+07','2025-05-11 22:57:25.611336+07','2025-02-10 14:53:04.704+07','SERVICE_ACCOUNT','superadmin','2024-09-04 03:44:48.827+07','superadmin','2025-02-11 22:57:25.609+07');

-- create table user_roles
CREATE TABLE IF NOT EXISTS netflix.user_roles
(
    user_id bigint NOT NULL,
    role_id integer NOT NULL,
    CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id),
    CONSTRAINT user_roles_fkey1 FOREIGN KEY (role_id)
        REFERENCES roles (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT user_roles_fkey2 FOREIGN KEY (user_id)
        REFERENCES users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)

-- feed data user_roles
INSERT INTO netflix.user_roles (user_id,role_id) VALUES
	 (1,1),
	 (1,2),
	 (1,3),
	 (2,3);


-- create table netflix_shows
CREATE TABLE IF NOT EXISTS netflix.netflix_shows (
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
	"type" varchar(7) NOT NULL,
	title text NOT NULL,
	director text NULL,
	cast_members text NULL,
	country varchar(60) NOT NULL,
	date_added date NOT NULL,
	release_year int4 NOT NULL,
	rating int4 NULL,
	duration_in_minute int4 NULL,
	listed_in text NULL,
	description text NULL,
	CONSTRAINT netflix_shows_pkey PRIMARY KEY (id),
	CONSTRAINT netflix_shows_type_check CHECK (((type)::text = ANY (ARRAY[('MOVIE'::character varying)::text, ('TV_SHOW'::character varying)::text])))
);
```
---

## 🛠 Installation & Setup
A step by step series of examples that tell you how to get a development env running.
1. Clone the repository
```bash
git clone https://github.com/yoanesber/Spring-Boot-JWT-Auth-PostgreSQL.git
cd Spring-Boot-JWT-Auth-PostgreSQL
```

2. Set up PostgreSQL
- Run the provided DDL script to set up the database schema
- Configure the connection in `.env.development` file:
```properties
# postgres
SPRING_DATASOURCE_PORT=5432
SPRING_DATASOURCE_USERNAME=myusername
SPRING_DATASOURCE_PASSWORD=mypassword
SPRING_DATASOURCE_DB=netflix_development
```

3. Run the application locally
- Make sure PostgreSQL is running, then execute: 
```bash
mvn spring-boot:run
```

- The API will be available at http://localhost:8081/ 
- You can test the API using:
    - Postman (Desktop/Web version)
    - cURL
---

## 🔗 API Endpoints
The REST API provides a set of endpoints to manage Netflix shows, allowing clients to perform CRUD operations (Create, Read, Update, Delete). Each endpoint follows RESTful principles and accepts/returns JSON data. Authentication is handled using JWT Bearer tokens, ensuring secure access to protected resources. Below is a list of available endpoints along with sample requests.

### Authentication
#### Login
`POST` http://localhost:8081/api/v1/auth/login

Login API allows users to authenticate by providing valid credentials. Upon successful authentication, the server responds with an access token and a refresh token. The access token is used for making authorized requests, while the refresh token is used to obtain a new access token when the previous one expires.

**Request Body:**
```json
{
    "username": "channel1",
    "password": "P@ssw0rd"
}
```

**Successful Response:**
```json
{
    "statusCode": 200,
    "timestamp": "2024-01-10T07:56:25.520985300Z",
    "message": "Login successful",
    "data": {
        "accessToken": "<JWT Token>",
        "refreshToken": "<Refresh Token>",
        "expirationDate": "2025-03-09T02:58:50.000Z",
        "tokenType": "Bearer"
    }
}
```

**Invalid Credentials Response:**
```json
{
    "statusCode": 400,
    "timestamp": "2024-01-10T07:56:25.520985300Z",
    "message": "Invalid username or password: Bad credentials",
    "data": null
}
```

**Invalid JWT token Response:**
```json
{
    "statusCode": 401,
    "timestamp": "2024-01-10T07:56:25.520985300Z",
    "message": "Unauthorized request",
    "data": "Invalid JWT token"
}
```


#### Refresh Token API
`POST` http://localhost:8081/api/v1/auth/refresh-token

Refresh Token API is used to renew an expired access token without requiring the user to log in again. Clients send a valid refresh token, and the server issues a new access token and a new refresh token.

**Request Body:**
```json
{
    "refreshToken": "<Refresh Token>"
}
```

**Successful Response:**
```json
{
    "statusCode": 200,
    "timestamp": "2024-01-10T07:56:25.520985300Z",
    "message": "Refresh token successful",
    "data": {
        "accessToken": "<JWT Token>",
        "refreshToken": "<Refresh Token>",
        "expirationDate": "2025-03-09T04:01:56.000Z",
        "tokenType": "Bearer"
    }
}
```

**Invalid refresh token Response:**
```json
{
    "statusCode": 400,
    "timestamp": "2024-01-10T07:56:25.520985300Z",
    "message": "Invalid refresh token",
    "data": null
}
```

**Expired refresh token Response:**
```json
{
    "statusCode": 401,
    "timestamp": "2024-01-10T07:56:25.520985300Z",
    "message": "Failed to verify expiration of refresh token: Refresh token has expired. Please make a new signin request",
    "data": null
}
```


### Netflix Shows API
Netflix Show API allows users to perform CRUD operations on Netflix Shows. Users can create, retrieve, update, and delete show records. Access to these endpoints requires authentication via JWT.

#### Create a Netflix Show
`POST` http://localhost:8081/api/v1/netflix-shows

This endpoint allows users to create a new Netflix show by providing relevant details in the request body. Ensure that a valid JWT token is included in the Authorization header.

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
    "rating": 10,
    "durationInMinute": 90,
    "listedIn": "Drama",
    "description": "A woman adjusting to life after a loss contends with a feisty bird that's taken over her garden — and a husband who's struggling to find a way forward."
}
```

**Successful Response:**
```json
{
    "statusCode": 201,
    "timestamp": "2024-01-10T07:56:25.520985300Z",
    "message": "NetflixShows created successfully",
    "data": {
        "id": 1, 
        "showType": "MOVIE",
        "title": "Sankofa",
        "director": "Haile Gerima",
        "castMembers": "Kofi Ghanaba, Oyafunmike Ogunlano, Alexandra Duah, Nick Medley, Mutabaruka, Afemo Omilami, Reggie Carter, Mzuri, Oliver",
        "country": "United States",
        "dateAdded": "2021-09-24",
        "releaseYear": 2024,
        "rating": 10,
        "durationInMinute": 90,
        "listedIn": "Drama",
        "description": "A woman adjusting to life after a loss contends with a feisty bird that's taken over her garden — and a husband who's struggling to find a way forward."
    }
}
```

**Invalid/expired JWT Token Response:**
```json
{
    "statusCode": 401,
    "timestamp": "2024-01-10T07:56:25.520985300Z",
    "message": "Unauthorized request",
    "data": "Invalid JWT token"
}
```

#### Get All Netflix Shows
`GET` http://localhost:8081/api/v1/netflix-shows

Retrieves a list of all Netflix shows stored in the database.

**Successful Response:**
```json
{
    "statusCode": 200,
    "timestamp": "2024-01-10T07:56:25.520985300Z",
    "message": "NetflixShows retrieved successfully",
    "data": [
        {
            "id": 1, 
            "showType": "MOVIE",
            "title": "Sankofa",
            "director": "Haile Gerima",
            "castMembers": "Kofi Ghanaba, Oyafunmike Ogunlano, Alexandra Duah, Nick Medley, Mutabaruka, Afemo Omilami, Reggie Carter, Mzuri, Oliver",
            "country": "United States",
            "dateAdded": "2021-09-24",
            "releaseYear": 2024,
            "rating": 10,
            "durationInMinute": 90,
            "listedIn": "Comedy",
            "description": "A woman adjusting to life after a loss contends with a feisty bird that's taken over her garden — and a husband who's struggling to find a way forward."
        }
    ]
}
```

#### Get Netflix Show by ID
`GET` http://localhost:8081/api/v1/netflix-shows/{id}

Fetches the details of a specific Netflix show using its unique ID.

**Successful Response:**
```json
{
    "statusCode": 200,
    "timestamp": "2024-01-10T07:56:25.520985300Z",
    "message": "NetflixShows retrieved successfully",
    "data": {
        "id": 1, 
        "showType": "MOVIE",
        "title": "Sankofa",
        "director": "Haile Gerima",
        "castMembers": "Kofi Ghanaba, Oyafunmike Ogunlano, Alexandra Duah, Nick Medley, Mutabaruka, Afemo Omilami, Reggie Carter, Mzuri, Oliver",
        "country": "United States",
        "dateAdded": "2021-09-24",
        "releaseYear": 2024,
        "rating": 10,
        "durationInMinute": 90,
        "listedIn": "Comedy",
        "description": "A woman adjusting to life after a loss contends with a feisty bird that's taken over her garden — and a husband who's struggling to find a way forward."
    }
}
```

#### Update a Netflix Show
`PUT` http://localhost:8081/api/v1/netflix-shows/{id}

Allows updating the details of an existing Netflix show.

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
    "rating": 10,
    "durationInMinute": 90,
    "listedIn": "Comedy",
    "description": "A woman adjusting to life after a loss contends with a feisty bird that's taken over her garden — and a husband who's struggling to find a way forward."
}
```

**Successful Response:**
```json
{
    "statusCode": 200,
    "timestamp": "2024-01-10T07:56:25.520985300Z",
    "message": "NetflixShows updated successfully",
    "data": {
        "id": 1, 
        "showType": "MOVIE",
        "title": "Sankofa",
        "director": "Haile Gerima",
        "castMembers": "Kofi Ghanaba, Oyafunmike Ogunlano, Alexandra Duah, Nick Medley, Mutabaruka, Afemo Omilami, Reggie Carter, Mzuri, Oliver",
        "country": "United States",
        "dateAdded": "2021-09-24",
        "releaseYear": 2024,
        "rating": 10,
        "durationInMinute": 90,
        "listedIn": "Comedy",
        "description": "A woman adjusting to life after a loss contends with a feisty bird that's taken over her garden — and a husband who's struggling to find a way forward."
    }
}
```

**Not found Response:**
```json
{
    "statusCode": 404,
    "timestamp": "2024-01-10T07:56:25.520985300Z",
    "message": "NetflixShows not found",
    "data": null
}
```

#### Delete a Netflix Show
`DELETE` http://localhost:8081/api/v1/netflix-shows/{id}

Deletes a specific Netflix show from the database.

**Successful Response:**
```json
{
    "statusCode": 200,
    "timestamp": "2024-01-10T07:56:25.520985300Z",
    "message": "NetflixShows deleted successfully",
    "data": null
}
```

**Not found Response:**
```json
{
    "statusCode": 404,
    "timestamp": "2024-01-10T07:56:25.520985300Z",
    "message": "NetflixShows not found",
    "data": null
}
```
---

## 🗒️ Notes
- **JWT Expiration**: The access token has a limited validity period. Clients should use the refresh token to obtain a new access token when expired.
- **Authorization**: Every API request must include a valid JWT token in the Authorization header (Bearer <JWT Token>).
- **Data Validation**: Requests with missing or invalid fields will return a 400 Bad Request response.
- **Security Considerations**:
    - Never expose JWT tokens in frontend code or logs.
    - Use HTTPS to protect tokens in transit.
    - Implement role-based access control (RBAC) to restrict API actions based on user roles, ensuring that only authorized users can perform specific operations. Assign different roles such as 'ADMIN' and 'USER' to enforce proper access levels.
- **Database Schema**: Ensure all necessary tables (users, roles, netflix_shows) are created and populated correctly.
- **Error Handling**: The API provides meaningful error messages with appropriate HTTP status codes (400, 401, 403, 404, 500).