E-commerce Backend
Description
A Spring Boot REST API backend for a simple e-commerce application. 
Implements authentication, 
product and category management, 
shopping cart, orders, payments, 
and address management. 
Designed for developers who need a ready-to-use backend for an e-commerce frontend or testing purposes.

Features
JWT-based authentication (login/register)
Product and category CRUD
Shopping cart and cart item management
Order creation and order item management
Payment request handling (placeholder)
Address management for users
Basic role handling via Spring Security
Tech Stack
Java 21
Spring Boot 3.2.x (Web, Data JPA, Security, Validation)
Maven
PostgreSQL (runtime)
JSON Web Tokens (jjwt)
Lombok (compile-time)
Project Structure
Top-level folders and important packages:

backend - application source
config - security and JWT filter
controller - REST controllers (Auth, Product, Cart, Order, Payment, Category, Address)
model - JPA entities
payload - request/response DTOs
repository - Spring Data JPA repositories
service - business logic services
util - JWT utilities
application.properties - runtime configuration
pom.xml - Maven build file
Installation & Setup
Prerequisites:

Java 21
Maven 3.8+
PostgreSQL (or adjust application.properties for another DB)
Quick start:

Clone the repository
Configure database and secret in application.properties (example keys):
Build the project
Run the application
Usage
The API exposes REST endpoints under /api (see controllers in controller).

Common examples:

Register a user
Authenticate and obtain JWT
Use the returned accessToken in Authorization: Bearer <token> for protected endpoints.



Future Enhancements
Add OpenAPI/Swagger documentation
Add integration tests and CI pipeline
Improve payment integration (real payment gateway)
Add role management UI or admin panel
Add database migration tool support (Flyway/Liquibase)


