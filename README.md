# eFormer

eFormer is a simple eCommerce dashboard application that consists of a frontend and a backend. The backend is built using Spring Boot, Hibernate, and Spring Security, providing REST APIs for interaction. The frontend is developed using JavaFX and relies on the APIs provided by the backend.

## Features

- User authentication and authorization using Spring Security.
- RESTful APIs for managing products, orders, and user information.
- JavaFX interface to demonstrate the APIs

## Technologies Used

- Backend:
  - Spring Boot
  - Hibernate
  - Spring Security

- Frontend:
  - JavaFX

## Requirements

Before running the project, ensure you have the following installed:

- JDK: v20
- JavaFX-SDK: v20
- Maven: v3.6.3

## Getting Started

Make sure there are no applications using ports 8080 & 3306.
Navigate to the root directory then:

1. Backend:
  - Navigate to the Backend directory: `cd Backend`
  - Run the following commands:
    - `mvn clean package`
    - `java -jar target/eformer.backend.jar`
  - Now the server is running & the APIs should function properly

2. Frontend:

