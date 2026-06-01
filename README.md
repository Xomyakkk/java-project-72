### Hexlet tests and linter status:
[![Actions Status](https://github.com/Xomyakkk/java-project-72/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/Xomyakkk/java-project-72)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-72&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-72)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-72&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-72)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-72&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-72)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-72&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-72)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-72&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-72)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-72&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-72)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-72&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-72)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-72&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-72)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-72&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-72)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-72&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-72)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Xomyakkk_java-project-72&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=Xomyakkk_java-project-72)

# Page Analyzer

Веб-приложение для анализа и мониторинга состояния веб-страниц. Позволяет добавлять URL-адреса, выполнять их проверку и отслеживать историю изменений.

## Функционал

- Добавление URL-адресов для мониторинга
- Проверка URL: получение HTTP-статуса, анализ HTML-контента
- Просмотр списка всех проверенных URL
- История проверок с детальной информацией
- Отображение статуса кодов ответа

## Технологии

| Компонент | Технология |
|-----------|------------|
| Фреймворк | [Javalin](https://javalin.io/) |
| Шаблонизатор | [JTE](https://jte.gg/) |
| База данных | [H2](https://www.h2database.com/) (dev) / [PostgreSQL](https://www.postgresql.org/) (prod) |
| Пул соединений | [HikariCP](https://github.com/brettwooldridge/HikariCP) |
| HTML-парсер | [Jsoup](https://jsoup.org/) |
| HTTP-клиент | [Unirest](https://kong.github.io/unirest-java/) |
| Тестирование | JUnit 5, Javalin Test Tools, MockWebServer |
| Покрытие | JaCoCo |
| CI/CD | GitHub Actions, SonarCloud |

## Требования

- JDK 21
- Gradle 9.x
- PostgreSQL 15+ (для production)

## Сборка

```bash
cd app
gradle build
```

## Запуск

```bash
gradle run
```

Приложение запустится на порту `7070` (или PORT из переменной окружения).

## Docker

```bash
docker build -t page-analyzer .
docker run -p 7070:7070 page-analyzer
```

Проект: https://java-project-72-vkt0.onrender.com
