---
applyTo: '**'
---
# Workspace Instructions — Java / Spring Boot

## General
- Follow the [Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html).
- Use meaningful names for variables, methods, and classes.
- Keep methods small and focused on a single task.
- Write Javadoc comments for public APIs.

## Goals
- Write a maintainable and observable service on Java 21 / Spring Boot 3.x.
- Minimize regressions through tests, static analysis and CI.
- Automate routine tasks via Copilot Agent within clear boundaries.

## Technology stack
- Java 21, Spring Boot 3.x, Maven.
- Data: Spring Data JPA, PostgreSQL, MySQL, MariaDB.
- API documentation: springdoc-openapi.
- Tests: JUnit 5, Mockito, Testcontainers, WebTestClient.
- Logging: Simple Logging Facade for Java (SLF4J).
- Containerization: Dockerfile + docker-compose for local development.

## Code style and quality
- Formatting: Google Java Style (Spotless).
- Static analysis: Error Prone.
- Null contracts: @NonNull/@Nullable annotations (JetBrains).
- Names: English, clear; avoid magic numbers.
- Public contracts are stable; changes via semantic versioning.

## Architecture
- Thin controllers: validation and orchestration only.
- Business logic in services; data access in repositories.
- DTO ≠ Entity; mapping via MapStruct.
- Exceptions: hierarchy of business/technical exceptions; global error handler.
- Configuration via application.yaml; secrets only via environment variables.

## Security
- Spring Security without WebSecurityConfigurerAdapter; method-level @PreAuthorize.
- Do not expose sensitive data in logs and responses.
- SQL only parameterized; validate input data.
- Do not commit secrets; example in .env.example.

## Logging and observability
- JSON format; fields: timestamp, level, logger, message, traceId.
- Propagate correlation-id via MDC.
- Micrometer metrics; health checks enabled.

## Testing (Definition of Done)
- Unit tests for key logic; integration tests for critical paths.
- Branch coverage for affected code at least 80% for modified modules.
- Contract tests for public endpoints (statuses/schemas).
- Testcontainers for DB interactions.
- Do not merge with failing tests/linters.

## DB migrations
- Only via Liquibase; each changeSet must have a correct rollback.
- Coordinate destructive operations (DROP/ALTER) via ADR.

## Git process
- Branches feature/…, fix/…, chore/…; PRs small and atomic.
- Conventional Commits.
- Code review mandatory; force-push prohibited on protected branches.
- the commit message must be without line breaks, in a single line

## Documentation
- OpenAPI up to date; README briefly describes how to run and environment variables.
- ADR (MADR) for architectural decisions in docs/adr/.

## Language
- Copilot responses: Russian.
- Comments and identifiers — English.
- All messages in code - English.

## Rules for Copilot Agent
- Allowed: read/write files (Editor), run tests, build the project, generate migrations (without applying to production), suggest commits/PRs.
- Require confirmation before: terminal commands, CI changes, migrations with destructive actions.
- Commits should be small batches with meaningful single-line messages.
- To open pages, you must use the browser through the MCP server Playwright.
- Everything you suggest to improve or do, write it down in todo.md. And then, once it’s done, remove it from todo.md. 
- Project compilation: mvn clean compile
- Run application: mvn clean spring-boot:run