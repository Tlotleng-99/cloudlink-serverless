# CloudLink — Serverless URL Shortener with Analytics

A serverless URL shortener built with **Java**, **AWS Lambda**, **API Gateway**, and **DynamoDB**.
Built as a cloud computing elective project to demonstrate serverless architecture, managed NoSQL
storage, and infrastructure-as-code on AWS.

## Features

- Shorten any long URL into a compact code
- Redirect from short code to original URL
- Track click analytics (count, timestamps) per short link
- Fully serverless — no servers to manage, scales to zero
- Infrastructure defined as code (AWS CDK, Java)

## Architecture

```
Client
  |
  v
API Gateway (REST API)
  |
  +--> POST /shorten   --> Lambda: CreateShortUrlHandler --> DynamoDB
  +--> GET  /{code}    --> Lambda: RedirectHandler        --> DynamoDB
  +--> GET  /stats/{code} --> Lambda: StatsHandler         --> DynamoDB
```

*(Architecture diagram to be added in later section)*

## Tech Stack

| Layer          | Technology                        |
|----------------|------------------------------------|
| Language       | Java 17                            |
| Compute        | AWS Lambda                         |
| API Layer      | Amazon API Gateway                 |
| Database       | Amazon DynamoDB                    |
| IaC            | AWS CDK (Java)                     |
| Build Tool     | Maven                              |
| Testing        | JUnit 5, Mockito                   |
| CI/CD          | GitHub Actions                     |

## Project Structure

```
cloudlink/
├── src/
│   ├── main/java/com/cloudlink/
│   │   ├── handlers/       # Lambda function handlers
│   │   ├── model/          # Data models (UrlMapping, etc.)
│   │   ├── repository/     # DynamoDB access layer
│   │   └── util/           # Helpers (short code generation, etc.)
│   └── test/java/com/cloudlink/
├── infra/                  # AWS CDK infrastructure code
├── pom.xml
└── README.md
```

## Status

🚧 Work in progress — being built incrementally as part of a cloud computing elective.

- [x] Project setup & repo structure
- [ ] DynamoDB schema + data model
- [ ] Lambda: Create Short URL
- [ ] Lambda: Redirect
- [ ] Lambda: Stats
- [ ] AWS CDK stack
- [ ] Tests
- [ ] CI/CD pipeline
- [ ] Architecture diagram

## Getting Started

_Deployment instructions will be added once the CDK stack is complete._

## License

MIT
