# Esoft ordering service

Esoft Ordering Service is a service built for ordering system using Spring Boot with microservices architecture and Docker

## Architecture

Esoft ordering service system consists of the following modules:
- **eureka-server** - a module containing embedded Eureka Server used for microservices to register themselves and discover other services to communicate with
- **api-gateway** - a module that Spring Cloud Gateway for running Spring Boot application that acts as a gateway/routing in our architecture.
- **order-service** - a module responsible for handling operations related to orders.
- **report-service** - a module responsible for handling summary reports of orders. 
- **common-config** - a module stores common configuration like Security, Exception handler, common entity,...

## Deployment guide
### Requirements
1. `JDK 17`
2. `Apache Maven` - >= 3.8.6
3. `Docker` - > 25.0.2

### Dockerfiles for build
1. `docker-compose.yaml`
2. `eureka-server/Dockerfile`
3. `api-gateway/Dockerfile`
4. `order-service/Dockerfile`
5. `report-service/Dockerfile`

### Usage
1. Build Maven project with using command: `mvn clean install -Pprod`
2. Build Docker compose with using command: `docker compose build --no-cache`
3. Run Docker compose with using command: `docker compose up -d`