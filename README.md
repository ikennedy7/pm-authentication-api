## JAF API Project v4.5
# One Project to Rule Them All

This is a template project built with the H-E-B standard technology stack published by the IS Architecture team. It has some standard configurations built in to make it easier to start writing useful code.

# Open in an IDE

If you are using IntelliJ IDEA < 14.1.7, skip to the instructions below.

1 Open command prompt/terminal and navigate to root folder of the project
2 Run the command 'gradlew idea' or 'gradlew eclipse', depending on your IDE
3 After you see 'BUILD SUCCESSFUL' you can open the project in your IDE

## IntelliJ 14.1.7

More recent versions of IntelliJ IDEA have better support for importing Gradle projects. Simply use the `Import Project` menu option in IntelliJ and you will be good to go.

## The Gradle Wrapper

The [Gradle wrapper](gradle.org/docs/current/userguide/gradle_wrapper.html) is the recommended method for running a Gradle build. A Gradle wrapper script (`gradlew.bat/sh`) is already included in your project. See the docs for info about upgrading the Gradle version that your project uses.

You can also install [Gradle](gradle.org) on your machine for use outside of your project.

## Run Instructions in IntelliJ

To run it inside IntelliJ IDEA:

Run as Application with the following VM Argument: -Dspring.profiles.active=local

API will be running on 8080

## Build Java classes, Docker image and run unit tests

gradlew build docker test

## Run container

docker run -d --rm -p 8080:8080 --name pm-authentication-api com.heb/pm-authentication-api

docker stop <container ID>

REST Endpoint
http://localhost:8080/pm_auth/login

POST Request Body (fill in user and pw)
{"username":"","password":""}

# Other things to know

There are a few other things to be aware of...

## Java Config

The template is written in Java Config. That means no more XML! (except in special cases like Spring Integration).

You will find these configuration classes at the root of the example package.

## Spring Boot

The template is running on Spring Boot. Many of the configurations have been delegated to Spring Boot but you can customize these by adding your beans/configurations.

## Spring profiles

We have identified 4 profiles pertaining to environments. You can customize this further to suit your needs (such as separating your data sources by platform).

## Webservice

WSImport Commands:
Generate code in command line: wsimport -extension wsdlUrl
Jar up Generated Code: jar cvf ExampleName.jar com(file root)

See gradle script for how to import jar dependencies from libs folder

# Maintainers

This is a JAF 4.5 API template modified by Jared Martinez, originated by Jason Roberts. Please provide feedback.

## Technologies

- Spring (http://spring.io)
  - Spring Boot
  - Spring Web
  - Spring Security
  - Spring LDAP
  - Spring Data
- EH Cache
- Hibernate
