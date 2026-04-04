# AGENTS.md - Food Delivery Microservices Platform

AI agents working on this codebase should understand:

## Architecture Overview

This is a **Spring Boot 3.5.13 microservices project** using **Java 17** with a Maven multi-module structure (parent POM at root). Four services orchestrate a food delivery platform:

1. **discovery-server** (port 8761): Netflix Eureka server for service registration/discovery
2. **api-gateway**: Spring Cloud Gateway entry point (routes requests to backend services)
3. **order-service** (port 8081): Handles order operations, registers with Eureka
4. **restaurant-service**: Manages restaurant data, registers with Eureka

**Service Topology:**
```
Client → API Gateway → [Order Service / Restaurant Service] 
                              ↓ (register with)
                         Discovery Server (Eureka)
```

## Critical Knowledge

### Service Registration Pattern
- **Discovery Server** uses `@EnableEurekaServer` annotation at `discovery-server/src/main/java/com/codecafe/discovery_server/DiscoveryServerApplication.java`
- Configured with `register-with-eureka: false` and `fetch-registry: false` (it's the server, not a client)
- Listens on port 8761 at `server.port: 8761`
- Other services register via `eureka.client.service-url.defaultZone: http://localhost:8761/eureka/`

**Example:** order-service application.yaml shows the Eureka client pattern that both order-service and restaurant-service follow (see line 10-12 in order-service config)

### Maven Build & Execution
- **Build all modules:** `mvn clean package` from root directory
- **Individual service build:** `mvn clean package -f service-name/pom.xml`
- Each service has `spring-boot-maven-plugin` configured for standalone JAR execution
- Parent POM enforces Spring Boot 3.5.13 parent version across all modules

### Package Naming Convention
- **Important:** Original package naming used hyphens (e.g., `com.code-cafe.food-delivery`) but Maven requires underscores
- All packages use underscores: `com.codecafe.*` with service-specific suffixes
  - `com.codecafe.api_gateway`
  - `com.codecafe.discovery_server`
  - `com.codecafe.order_service`
  - `com.codecafe.restaurent_service` (note: "restaurent" not "restaurant" - matches the actual codebase)

## Common Tasks

### Running Services Locally
Each service runs independently after building:
```bash
# Terminal 1: Start Eureka Discovery Server
mvn spring-boot:run -f discovery-server/pom.xml

# Terminal 2: Start Order Service (registers with Eureka)
mvn spring-boot:run -f order-service/pom.xml

# Terminal 3: Start Restaurant Service (registers with Eureka)
mvn spring-boot:run -f restaurant-service/pom.xml

# Terminal 4: Start API Gateway (routes traffic)
mvn spring-boot:run -f api-gateway/pom.xml
```

### Key Configuration Files
- **Parent POM:** `/pom.xml` (defines Spring Cloud 2025.0.2, Java 17)
- **Service configs:** Each service has `src/main/resources/application.yaml` defining spring.application.name, server.port, and eureka client settings
- **No database configs yet:** Services are foundational; expect DB configurations to be added to order-service and restaurant-service

## Patterns & Conventions

### Application Entry Points
- All services follow Spring Boot `@SpringBootApplication` pattern
- Discovery Server adds `@EnableEurekaServer` annotation (required for Eureka server functionality)
- Microservices typically omit Eureka annotations; client registration happens via classpath dependency + config

### Startup Order Dependency
- **Must start discovery-server first** (other services connect to it on startup)
- API Gateway and microservices have soft dependency on Eureka availability (they'll retry registration)
- No inter-service hard dependencies currently (decoupled design)

### Port Allocation
- Discovery Server: 8761 (Eureka standard)
- Order Service: 8081
- API Gateway: (not explicitly set; uses Spring Cloud Gateway default)
- Restaurant Service: (not explicitly set in config; may default to 8080)

## Adding New Features

### Adding Endpoints to a Microservice
- Create `@RestController` or `@Service` classes in service-specific packages
- Register with `@RequestMapping` or `@GetMapping`/`@PostMapping` annotations
- Service will automatically register with Eureka once running

### Adding New Microservice
1. Create new module directory with same Maven structure as existing services
2. Add module reference to parent `/pom.xml` `<modules>` section
3. Set `spring.application.name` in `application.yaml` (used for Eureka registration)
4. If it needs Eureka discovery: add `spring-cloud-starter-netflix-eureka-client` dependency to pom.xml
5. Services auto-register; API Gateway can route via service name

### Common Dependencies
- Spring Cloud version is managed at parent level: `<spring-cloud.version>2025.0.2</spring-cloud.version>`
- All services inherit from Spring Boot parent: `spring-boot-starter-parent:3.5.13`
- When adding new dependencies, keep them in service-specific pom.xml files (not parent)

## Debugging & Troubleshooting

### Service Not Registering with Eureka
- Verify `eureka.client.service-url.defaultZone` is correct (default: http://localhost:8761/eureka/)
- Check service is running and Eureka server is accessible
- Review service logs for connection errors

### API Gateway Routing Issues
- API Gateway auto-discovers services from Eureka using `spring-cloud-starter-gateway-server-webmvc`
- Routes by application.name by default; verify service names don't have conflicts
- Check gateway logs for routing errors

### Build Failures
- Ensure Java 17 is used: `java -version` should show 17.x.x
- Clean Maven cache if experiencing dependency issues: `mvn clean -DskipTests`
- Each service pom.xml has Spring Cloud BOM import for version alignment

## File Organization Reference
```
food-delivery/
├── pom.xml (parent, defines modules & Spring Cloud version)
├── api-gateway/ (Gateway service, routes requests)
├── discovery-server/ (Eureka server for service discovery)
├── order-service/ (Order microservice, port 8081)
└── restaurant-service/ (Restaurant microservice)
    └── Each service has: pom.xml, src/main/java, src/main/resources/application.yaml, src/test
```

## Spring Cloud & Boot Versions
- **Spring Boot:** 3.5.13 (parent POM)
- **Spring Cloud:** 2025.0.2 (managed dependency)
- **Java:** 17 (enforced across all modules)

