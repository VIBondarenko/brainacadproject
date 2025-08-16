---
mode: agent
name: GenerateTests
description: Generate comprehensive JUnit5 tests for the specified class.
---
Проанализируй текущий Java-класс сервиса и сгенерируй исчерпывающие unit-тесты на JUnit5.
Требования:
- Структура AAA (arrange-act-assert), читаемые названия тестов.
- Моки через Mockito + @ExtendWith(MockitoExtension.class).
- Проверяй позитивные, негативные и граничные сценарии; покрытие затронутых веток ≥80%.
- Отдельно протестируй выбрасываемые исключения (assertThrows).
- Используй Testcontainers, если сервис обращается к БД.
- В конце выведи список сценариев, которые осознанно не тестируешь и почему.
Выведи готовые файлы тестов и краткий отчёт по покрытию логики.
