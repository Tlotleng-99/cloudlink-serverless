# CloudLink — Serverless URL Shortener with Analytics

A serverless URL shortener built with **Java**, **OCI Functions**, **OCI API Gateway**, and
**Oracle NoSQL Database**. Built as a cloud computing elective project to demonstrate serverless
architecture, managed NoSQL storage, and infrastructure-as-code on Oracle Cloud Infrastructure (OCI).

## Features

- Shorten any long URL into a compact code
- Redirect from short code to original URL
- Track click analytics (count, timestamps) per short link
- Fully serverless — no servers to manage, scales to zero
- Infrastructure defined as code (Terraform, OCI provider)

## Architecture

```
Client
  |
  v
OCI API Gateway
  |
  +--> POST /shorten      --> OCI Function: CreateShortUrlHandler --> Oracle NoSQL Database
  +--> GET  /{code}       --> OCI Function: RedirectHandler        --> Oracle NoSQL Database
  +--> GET  /stats/{code} --> OCI Function: StatsHandler           --> Oracle NoSQL Database
```

*(Architecture diagram to be added in later section)*

## Tech Stack

| Layer          | Technology                          |
|----------------|--------------------------------------|
| Language       | Java 17                              |
| Compute        | OCI Functions (Fn Project)           |
| API Layer      | OCI API Gateway                      |
| Database       | Oracle NoSQL Database Cloud Service  |
| IaC            | Terraform (OCI provider)             |
| Build Tool     | Maven                                |
| Testing        | JUnit 5, Mockito                     |
| CI/CD          | GitHub Actions                       |

## Project Structure

```
cloudlink/
├── src/
│   ├── main/java/com/cloudlink/
│   │   ├── handlers/       # OCI Function handlers
│   │   ├── model/          # Data models (UrlMapping, etc.)
│   │   ├── repository/     # Oracle NoSQL Database access layer
│   │   └── util/           # Helpers (short code generation, etc.)
│   └── test/java/com/cloudlink/
├── infra/                  # Terraform (OCI) infrastructure code + table schema
├── pom.xml
└── README.md
```

## Status

🚧 Work in progress — being built incrementally as part of a cloud computing elective.

- [x] Project setup & repo structure
- [x] Oracle NoSQL schema + data model
- [ ] OCI Function: Create Short URL
- [ ] OCI Function: Redirect
- [ ] OCI Function: Stats
- [ ] Terraform (OCI) infrastructure stack
- [ ] Tests
- [ ] CI/CD pipeline
- [ ] Architecture diagram

## Getting Started

_Deployment instructions will be added once the Terraform stack is complete._

## License

MIT