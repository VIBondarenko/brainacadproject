---
mode: agent
name: GenerateTests
description: Generate comprehensive JUnit5 tests for the specified class.
---
Analyze the current Java class and generate comprehensive unit tests using JUnit5.
Requirements:
- AAA structure (arrange–act–assert), readable test names.
- Mocks via Mockito + @ExtendWith(MockitoExtension.class).
- Cover positive, negative, and boundary scenarios; branch coverage ≥80%.
- Separately test thrown exceptions (assertThrows).
- Use Testcontainers if the service interacts with a database.
- At the end, output a list of scenarios you consciously do not test and explain why.
- Output ready-to-use test files and a brief report on logic coverage.