# SafetyNet Alert

## Description

This application aims to send information to emergency service systems.  
This application is implemented as an API using Spring Boot. The datas ares stored in a JSON file.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing
purposes.

### Prerequisites

What things you need to install the software and how to install them

- Java 21
- Maven 3.9.9

### Installing

A step by step series of examples that tell you how to get a development env running:

1.Install Java:

https://docs.oracle.com/en/java/javase/21/install/overview-jdk-installation.html

2.Install Maven:

https://maven.apache.org/install.html

### Running App

Import the code into an IDE of your choice and run the SafetyNetApplication.java to launch the application.

### Testing

The app has unit tests and integration tests written.  
To run the tests from maven, go to the folder that contains the pom.xml file and execute the below command.

For unit tests : `mvn test`  
For unit and integration tests :  `mvn verify`      
For generating reporting : `mvn clean site`  
Surefire, JaCoCo, JavaDoc reporting are available in the directory : /target/site/index.html

## Swagger

Swagger interface : http://localhost:8080/swagger-ui/index.html
