# Variables for the application containers
APP_CONTAINER_IMAGE=my-spring-app
APP_CONTAINER_NAME=spring-app
APP_DOCKER_CONTEXT=.
APP_DOCKERFILE=./src/main/docker/app/Dockerfile
APP_PORT=8081

# Variables for the PostgreSQL container
POSTGRES_CONTAINER_IMAGE=my-postgres-server
POSTGRES_CONTAINER_NAME=postgres-server
POSTGRES_DOCKER_CONTEXT=.
POSTGRES_DOCKERFILE=./src/main/docker/postgres/Dockerfile
POSTGRES_PORT=5432
POSTGRES_USER=postgres
POSTGRES_PASSWORD=P@ssw0rd
POSTGRES_DB=netflix

# Network for the application and RabbitMQ containers
NETWORK=app-network

# Generating JWT keys
# This will generate the keys and store them in src/main/resources
generate-jwt-keys:
	@echo "Generating JWT keys..."
	@./src/main/resources/generate-jwt-keys.sh
	@echo "JWT keys generated successfully."

# Running in development mode
dev:
	@echo "Running in development mode..."
	./mvnw spring-boot:run

# Cleaning the project
clean:
	@echo "Cleaning the project..."
	./mvnw clean

# Building the application as a JAR file
# This will run Maven Lifecycle phase "package": clean → validate → compile → test → package, 
# which cleans the target directory, compiles the code, runs tests, and packages the application into a JAR file.
package:
	@echo "Building the application as a JAR file..."
	./mvnw clean package -DskipTests


# Docker related targets
# Create a Docker network if it does not exist
docker-create-network:
	docker network inspect $(NETWORK) >NUL 2>&1 || docker network create $(NETWORK)

# Remove the Docker network if it exists
docker-remove-network:
	docker network rm $(NETWORK)

# Build PostgreSQL
docker-build-postgres:
	docker build -f $(POSTGRES_DOCKERFILE) -t $(POSTGRES_CONTAINER_IMAGE) $(POSTGRES_DOCKER_CONTEXT)

# Run PostgreSQL
docker-run-postgres:
	docker run --name $(POSTGRES_CONTAINER_NAME) --network $(NETWORK) -p $(POSTGRES_PORT):$(POSTGRES_PORT) \
	-e POSTGRES_DB=$(POSTGRES_DB) \
	-e POSTGRES_USER=$(POSTGRES_USER) \
	-e POSTGRES_PASSWORD=$(POSTGRES_PASSWORD) \
	-d $(POSTGRES_CONTAINER_IMAGE)

# Build and run PostgreSQL container
docker-build-run-postgres: docker-build-postgres docker-run-postgres

# Remove PostgreSQL container
docker-remove-postgres:
	docker stop $(POSTGRES_CONTAINER_NAME)
	docker rm $(POSTGRES_CONTAINER_NAME)

# Build the application in Docker
docker-build-app:
	docker build -f $(APP_DOCKERFILE) -t $(APP_CONTAINER_IMAGE) $(APP_DOCKER_CONTEXT)

# Run the application in Docker
docker-run-app: 
	docker run --name $(APP_CONTAINER_NAME) --network $(NETWORK) -p $(APP_PORT):$(APP_PORT) \
	-e SERVER_PORT=$(APP_PORT) \
	-d $(APP_CONTAINER_IMAGE)

docker-build-run-app: docker-build-app docker-run-app

# Remove the application container
docker-remove-app:
	docker stop $(APP_CONTAINER_NAME)
	docker rm $(APP_CONTAINER_NAME)

# Start all services: PostgreSQL and the application
docker-start-all: docker-create-network docker-build-run-postgres docker-build-run-app

# Stop all services: PostgreSQL and the application
docker-stop-all: docker-remove-app docker-remove-postgres docker-remove-network

.PHONY: generate-jwt-keys dev clean package \
	docker-create-network docker-remove-network \
	docker-build-postgres docker-run-postgres docker-build-run-postgres docker-remove-postgres \
	docker-build-app docker-run-app docker-build-run-app docker-remove-app \
	docker-start-all docker-stop-all