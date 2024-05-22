# Focus Flow

## Table of Contents

- [Introduction](#introduction)
- [Project Description](#project-description)
- [Features](#features)
- [Languages and Technologies](#languages-and-technologies)
- [Architecture and Design](#architecture-and-design)
- [Installation and Usage](#installation-and-usage)
- [License](#license)
- [Conclusion](#conclusion)

## Introduction

Welcome to the Focus Flow project! This repository contains the source code for a comprehensive educational application designed to enhance the learning experience by providing a distraction-free environment for students and professors.

## Project Description

Focus Flow is a Spring Boot Java-based application that integrates tools for managing documents, taking notes, creating and taking tests, and facilitating real-time communication within an educational setting. It helps in maintaining focus, thus boosting productivity and enhancing educational outcomes.

## Features

- **User Authentication**: Secure login and registration system.
- **Document Viewer**: View and manage educational documents.
- **Note-taking**: Create and manage personal notes.
- **Assignment Management**: Create and distribute assignments.
- **Test Tools**: Integrated tools for test creation and execution.
- **Real-time Chat**: Communication platform for students and faculty.
- **Analytics**: Track and analyze user engagement and performance.

## Languages and Technologies

- **Java**: Primary programming language.
- **Spring Boot**: Framework used for server-side development.
- **MySQL**: Database for storing user data and application data.
- **Maven**: Dependency management.
- **HTML/CSS/JavaScript**: Frontend development.

## Architecture and Design

The application follows a microservices architecture pattern, with services communicating over RESTful APIs. The frontend is designed using responsive web design techniques to ensure compatibility across devices.

## Installation and Usage

### Prerequisites
- **Java Development Kit (JDK 11)**: Included in the provided zip file.
- **MySQL Workbench**: Needed to set up the database.
- **Access to the `focusflow_script.txt`**: Contains the database setup script and is included in the zip file.
- **Basic knowledge of editing configuration files** is advantageous.

### Setup Steps

1. **Install MySQL Workbench**:
   - Download and install MySQL Workbench from [MySQL's official site](https://www.mysql.com/products/workbench/).

2. **Run Database Setup Script**:
   - Open MySQL Workbench.
   - Execute the `focusflow_script.txt` script to set up the required database schema and tables.

3. **Update Application Configuration**:
   - Locate the `application.properties` file within the `FocusFlow_Executable` directory.
   - Open the file with a text editor and update the database connection settings:
     ```
     spring.datasource.url=jdbc:mysql://localhost:3306/focusflow
     spring.datasource.username=your_mysql_username
     spring.datasource.password=your_mysql_password
     ```
   - Adjust the server port if necessary:
     ```
     server.port=8081
     ```
   - Update Hibernate dialect for MySQL 8.0:
     ```
     spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
     ```
   - For older MySQL versions, use the appropriate Hibernate dialect.

4. **Run the Application**:
   - Navigate to the `FocusFlow_Executable` directory.
   - Run the `start_focusflow.bat` batch file by double-clicking on it.

5. **Access the Application**:
   - Once the application starts successfully, open a web browser.
   - Visit [http://localhost:8081](http://localhost:8081) to access the FocusFlow application.

6. **Default Login Credentials**:
   - Log in with the default admin credentials:
     - **Username**: focusflow_admin
     - **Password**: Admin@123

### Import the Project (For Developers)
- Unzip the `focusflow.zip` file, which contains the Spring Boot project.
- Using Spring Tool Suite (STS) or Eclipse IDE:
  - Navigate to `File -> Import -> Existing Maven Projects`.
  - Select the root directory of the unzipped Spring Boot project.
  - Click "Finish" to import the project.

## License

This project is licensed under the MIT License see the LICENSE file for details.


## Conclusion

Focus Flow aims to revolutionize the educational environment by providing tools that assist in managing and enhancing academic engagement. We hope this tool significantly improves your educational productivity and learning outcomes.
