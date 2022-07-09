# Augeil Admin Portal
*Administration portal for Augeil*

## Environment Configuration

Define and set the following environment variables:
```bash
# Basic authentication
SPRING_SECURITY_USERNAME=...
SPRING_SECURITY_PASSWORD=...

DATABASE_USER=<your album database username>
DATABASE_PASSWORD=<your album database password>
SFTP_USER=<your sftp server username>
SFTP_PASSWORD=<your sftp server password>

# Spring active profile (local or prod)
spring.profiles.active=...
```

## Local development
First, run the Spring Boot backend application. After that follow the steps below to run the frontend:
```bash
# Download dependencies of frontend
cd frontend
npm install

# Run react application in development mode :
npm start
```

## Build and run with docker
```bash
docker build -t augeil .
# Assumes there is a file called "env" on the root project directory containing the defined environment variables
docker run -p 8080:8080 --env-file env augeil
```