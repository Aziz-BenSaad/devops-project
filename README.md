# Student Management – DevOps Project

A Spring Boot REST API for managing students, departments, courses and enrollments, with a full CI/CD chain: **Jenkins → Maven tests → SonarQube → Docker Hub → Kubernetes**.

## Tech stack

| Layer | Tools |
|---|---|
| Backend | Java 17, Spring Boot 3.5, Spring Data JPA, Lombok |
| Database | MySQL (H2 in-memory for tests) |
| API docs | springdoc-openapi (Swagger UI) |
| Build & tests | Maven, JUnit 5, Mockito, JaCoCo |
| Code quality | SonarQube |
| Containerization | Docker, Docker Hub |
| CI/CD | Jenkins |
| Orchestration | Kubernetes |

## Architecture

```
GitHub ──(poll every 2 min)──► Jenkins
                                 ├─ mvn test        (JUnit + Mockito, H2, JaCoCo coverage)
                                 ├─ SonarQube       (code quality + coverage)
                                 ├─ mvn package     (JAR)
                                 ├─ docker build    (image tagged with build number + latest)
                                 ├─ docker push     (Docker Hub)
                                 └─ kubectl apply   (namespace devops: MySQL + Spring app)
```

## Project structure

```
├── src/
│   ├── main/java/tn/esprit/studentmanagement/
│   │   ├── controllers/    # REST endpoints (Student, Department, Enrollment)
│   │   ├── entities/       # JPA entities (Student, Department, Course, Enrollment, Status)
│   │   ├── repositories/   # Spring Data repositories
│   │   └── services/       # Business logic + interfaces
│   ├── main/resources/application.properties   # MySQL config, port 8089
│   └── test/
│       ├── java/.../services/                   # Mockito unit tests
│       └── resources/application.properties     # H2 config for tests
├── Dockerfile
├── Jenkinsfile
└── pom.xml
```

## Domain model

- **Department**: has many students
- **Student**: belongs to a department, has many enrollments
- **Course**: has many enrollments
- **Enrollment**: links a student to a course, with a date, a grade and a **Status** (`ACTIVE`, `COMPLETED`, `DROPPED`, `FAILED`, `WITHDRAWN`)

## API

Base URL: `http://localhost:8089/student`

| Resource | Endpoints |
|---|---|
| Students | `GET /students/getAllStudents` · `GET /students/getStudent/{id}` · `POST /students/createStudent` · `PUT /students/updateStudent` · `DELETE /students/deleteStudent/{id}` |
| Departments | `GET /Department/getAllDepartment` · `GET /Department/getDepartment/{id}` · `POST /Department/createDepartment` · `PUT /Department/updateDepartment` · `DELETE /Department/deleteDepartment/{id}` |
| Enrollments | `GET /Enrollment/getAllEnrollment` · `GET /Enrollment/getEnrollment/{id}` · `POST /Enrollment/createEnrollment` · `PUT /Enrollment/updateEnrollment` · `DELETE /Enrollment/deleteEnrollment/{id}` |

Swagger UI: `http://localhost:8089/student/swagger-ui/index.html`

## Run locally

Prerequisites: Java 17 and a MySQL server on `localhost:3306` (user `root`, empty password by default; the `studentdb` database is created automatically).

```bash
git clone https://github.com/Aziz-BenSaad/devops-project.git
cd devops-project
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
```

Run the tests (no MySQL needed):

```bash
./mvnw test
```

The coverage report is generated in `target/site/jacoco/index.html`.

## Docker

```bash
./mvnw clean package -DskipTests
docker build -t student-management .
docker run -p 8089:8089 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://<mysql-host>:3306/studentdb?createDatabaseIfNotExist=true \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=<password> \
  student-management
```

## CI/CD pipeline (Jenkinsfile)

| # | Stage | What it does |
|---|---|---|
| 1 | Checkout | Pulls `main` from GitHub (polled every 2 minutes) |
| 2 | Unit tests | `mvn clean test` with H2, publishes JUnit results, generates JaCoCo coverage |
| 3 | SonarQube Analysis | Sends code and coverage to SonarQube |
| 4 | Package | Builds the JAR |
| 5 | Build Docker image | Tags the image with the build number and `latest` |
| 6 | Push Docker image | Pushes both tags to Docker Hub |
| 7 | Archive artifact | Archives the JAR in Jenkins |
| 8 | Deploy on Kubernetes | Applies the namespace, MySQL, ConfigMap, Secret, Deployment and Service in the `devops` namespace, then restarts the deployment |

### Jenkins credentials required

| ID | Type | Used for |
|---|---|---|
| GitHub credentials | Username + token | Checkout |
| `sonar-token` | Secret text | SonarQube |
| Docker Hub credentials | Username + password/token | Image push |

## Kubernetes

The app is deployed in the `devops` namespace with:
- a **MySQL** deployment
- a **ConfigMap** for the database URL
- a **Secret** for the database credentials
- the **Spring Boot** deployment and its **Service** (container port `8089`)

## Done by

- Aziz Ben Saad
