# Bank-microservice

## Project Description
A microservice architecture for managing banking operations including account creation, transactions, and more.

## Microservices Description
The project consists of the following microservices:

### 1. Account Service
- **Description**: Manages user accounts, including creating, updating, and deleting accounts.
- **Technology**: Java, Spring Boot, PostgreSQL

### 2. Transaction Service
- **Description**: Handles financial transactions such as deposits, withdrawals, and transfers.
- **Technology**: Java, Spring Boot, PostgreSQL

### 3. Customer Service
- **Description**: Manages customer information, including creating, updating, and deleting customers.
- **Technology**: Java, Spring Boot, PostgreSQL

### 3. Notification Service
- **Description**: Sends notifications to users for various events such as transaction alerts and account updates.
- **Technology**: Java, Spring Boot, MongoDB


## Features
- Account management
- Transaction processing
- RESTful APIs
- Database integration with PostgreSQL

## Technologies Used
- Java
- Spring Boot
- PostgreSQL

## Setup Instructions
1. Clone the repository: `git clone https://github.com/tumelosizani/Bank-microservice.git`
2. Navigate to the project directory: `cd Bank-microservice`
3. Build the project: `./mvnw clean install`
4. Run the application: `./mvnw spring-boot:run`

## Usage
- Access the application at `http://localhost:8080`
- Use the provided RESTful APIs for managing accounts and transactions

## Contributing
Contributions are welcome! Please follow these steps to contribute:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Commit your changes (`git commit -m 'Add new feature'`).
4. Push to the branch (`git push origin feature-branch`).
5. Open a pull request.

## License
This project is licensed under the MIT License.
