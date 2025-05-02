# OpenAPI Specs Merger Project

This project provides a solution for merging OpenAPI specification files distributed across multiple GitHub repositories. It's built on modern technologies including Quarkus, Swagger, and Docker to create a scalable and maintainable system.

## Project Overview

### Problem Statement

In microservice architectures, API documentation is often distributed across multiple repositories, making it difficult to:
- Generate a unified API documentation for developers
- Maintain consistency across API documentation
- Track changes across all service APIs

### Solution

This project creates a centralized system that:
1. Fetches OpenAPI specs from configured GitHub repositories
2. Merges them into a unified specification
3. Provides a web interface for browsing the combined documentation
4. Offers CI/CD integration for automated updates

## Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  GitHub Repo 1  │     │  GitHub Repo 2  │     │  GitHub Repo N  │
│  (OpenAPI Spec) │     │  (OpenAPI Spec) │     │  (OpenAPI Spec) │
└────────┬────────┘     └────────┬────────┘     └────────┬────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌───────────────────────────────────────────────────────────────┐
│                        OpenAPI Merger                         │
│                                                               │
│  ┌─────────────────┐   ┌─────────────────┐   ┌─────────────┐  │
│  │   Repo Fetcher  │──▶│   Spec Merger   │──▶│  Validator  │  │
│  └─────────────────┘   └─────────────────┘   └─────────────┘  │
│                                │                              │
│                                ▼                              │
│  ┌─────────────────┐   ┌─────────────────┐   ┌─────────────┐  │
│  │  Unified Spec   │◀──│   Transformer   │──▶│ API Console │  │
│  │  (JSON/YAML)    │   │                 │   │             │  │
│  └─────────────────┘   └─────────────────┘   └─────────────┘  │
│                                                               │
└───────────────────────────────────────────────────────────────┘
```

## Technical Stack

- **Backend**: Quarkus (a Kubernetes-native Java framework)
- **OpenAPI Tools**: Swagger Parser, Swagger Merger
- **CI/CD**: GitHub Actions
- **Containerization**: Docker
- **Documentation UI**: Swagger UI or ReDoc
- **Additional**:
  - API management: possible integration with Kong, Apigee, Krakend, ...
  - Validation: OpenAPI linters
  - Testing: Contract testing with tools like Pact

## Implementation

### Project Structure

```
.
├── docker
│   ├── Dockerfile.jvm
│   ├── Dockerfile.legacy-jar
│   ├── Dockerfile.native
│   └── Dockerfile.native-micro
├── k8s
├── mvnw
├── mvnw.cmd
├── pom.xml
├── README.md
└── src
    └── main
        ├── java
        │   └── io
        │       └── flexwiz
        │           └── openapi
        │               ├── api
        │               │   └── ApiDocs.java
        │               ├── client
        │               │   └── GithubClient.java
        │               ├── config
        │               │   ├── RepositoryConfig.java
        │               │   └── repos.yaml
        │               ├── merger
        │               │   ├── MergerService.java
        │               │   └── OpenApiMerger.java
        │               └── OpenApiMergerApplication.java
        └── resources
            └── application.properties
```

### Core Components

#### Repository Configuration
We'll create a YAML configuration to specify which repositories and OpenAPI files to merge:

```yaml
repositories:
  - name: user-service
    url: https://github.com/mycompany/user-service
    branch: main
    specPath: src/main/resources/openapi.yaml
    token: ${GITHUB_TOKEN}
  - name: order-service
    url: https://github.com/mycompany/order-service
    branch: main
    specPath: src/main/resources/openapi.json
    token: ${GITHUB_TOKEN}
```

#### GitHub Client
This component is responsible for fetching OpenAPI specs from GitHub repositories.

#### OpenAPI Merger
The core component that handles merging multiple OpenAPI specs while:
- Resolving naming conflicts
- Maintaining specification validity
- Handling versioning differences

#### API Documentation UI
A web interface that displays the merged OpenAPI documentation.

## Development Roadmap

### Phase 1: Core Functionality
- Set up Quarkus project structure
- Implement GitHub client for fetching OpenAPI specs
- Create basic merger functionality
- Implement simple web UI for viewing merged specs

### Phase 2: Advanced Features
- Add validation and conflict resolution
- Implement caching for improved performance
- Create webhooks for automated updates
- Add authentication and authorization

### Phase 3: CI/CD and Deployment
- Create Docker image
- Set up GitHub Actions for CI/CD
- Create deployment documentation
- Implement monitoring and logging

## Setup Instructions

### Prerequisites
- Java 17 or later
- Maven 3.8+
- Docker
- GitHub personal access token (for accessing repositories)

### Getting Started

1. **Clone the repository**:
   ```bash
   git clone https://github.com/mycompany/openapi-merger.git
   cd openapi-merger
   ```

2. **Configure repositories**:
   Edit `config/repos.yaml` to specify the repositories containing OpenAPI specs.

3. **Build the application**:
   ```bash
   ./mvnw package
   ```

4. **Run locally**:
   ```bash
   ./mvnw quarkus:dev
   ```

5. **Build Docker image**:
   ```bash
   docker build -t openapi-merger .
   ```

6. **Run with Docker**:
   ```bash
   docker run -p 8080:8080 \
     -v $(pwd)/config:/app/config \
     -e GITHUB_TOKEN=your_github_token \
     openapi-merger
   ```

7. **Access the UI**:
   Open `http://localhost:8080` in your browser.

## Key Code Components

### GitHub Client (GitHubClient.java)

This component will fetch OpenAPI specs from GitHub repositories.

### Merger Service (MergerService.java)

The service that handles the merging of multiple OpenAPI specs.

### API Doc Resource (ApiDocResource.java)

RESTful endpoint for accessing the merged OpenAPI specification.

## Deployment Options

### Kubernetes Deployment

Deploy to Kubernetes using the provided manifests:

```bash
kubectl apply -f kubernetes/
```

### GitHub Actions Workflow

Automate the build and deployment process with GitHub Actions.

## Future Enhancements

1. **Version Management**: Track changes in API versions across services
2. **Diff Visualization**: Show differences between API versions
3. **Custom Domain Support**: Host the merged documentation on a custom domain
4. **Multi-environment Support**: Support different environments (dev, staging, prod)
5. **Integration with API Gateways**: Kong, Apigee, AWS API Gateway

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.