# Full-Stack Blogging Platform (Spring Boot + React)

A simple full-stack blogging app built with **Spring Boot**, **React + TypeScript**, and **PostgreSQL**, featuring JWT authentication.

## 📦 Tech Used
- **Backend:** Spring Boot, Spring Security, JPA/Hibernate  
- **Frontend:** React, TypeScript, Vite  
- **Database:** PostgreSQL  
- **Auth:** JWT  

## 📁 Project Structure
root/
 ├── client/           # React frontend
 ├── src/main/java/    # Spring Boot backend
 ├── src/main/resources/application.properties
 └── pom.xml

## ⚙️ Setup

### 1. Configure Database  
Create a Postgres database:

CREATE DATABASE blog_app;

Update credentials in application.properties:

spring.datasource.url=jdbc:postgresql://localhost:5432/blog_app
spring.datasource.username=postgres
spring.datasource.password=your_password
jwt.secret=your_secret

## ▶️ Run the App

### Backend  
./mvnw spring-boot:run

### Frontend  
cd client  
npm install  
npm run dev

## 🔐 Authentication
POST /api/v1/auth/login  
POST /api/v1/auth/register  

Header:
Authorization: Bearer <token>

## 📡 Main APIs

### Posts
GET /posts  
POST /posts (auth)  
PUT /posts/{id} (auth)  
DELETE /posts/{id} (auth)

### Categories
GET /categories  
POST /categories (auth)

### Tags
GET /tags  
POST /tags (auth)

## 🏁 Production Build

Backend:  
./mvnw clean package

Frontend:  
npm run build
