<p align="center">
  <h1 align="center">TemplarMail</h1>
  <p align="center"><em>Information about project</em></p>
</p>

![last-commit](https://img.shields.io/github/last-commit/NgvLanh/TemplarMail?style=flat&logo=git&logoColor=white&color=0080ff)
![repo-top-language](https://img.shields.io/github/languages/top/NgvLanh/TemplarMail?style=flat&color=0080ff)
![repo-language-count](https://img.shields.io/github/languages/count/NgvLanh/TemplarMail?style=flat&color=0080ff)

**Built with the tools and technologies:**  
![Docker](https://img.shields.io/badge/Docker-2496ED.svg?style=flat&logo=Docker&logoColor=white)
![XML](https://img.shields.io/badge/XML-005FAD.svg?style=flat&logo=XML&logoColor=white)
![YAML](https://img.shields.io/badge/YAML-CB171E.svg?style=flat&logo=YAML&logoColor=white)
![Spring MVC](https://img.shields.io/badge/Spring%20MVC-6DB33F.svg?style=flat&logo=Spring&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F.svg?style=flat&logo=Thymeleaf&logoColor=white)
![jQuery](https://img.shields.io/badge/jQuery-0769AD.svg?style=flat&logo=jquery&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-7952B3.svg?style=flat&logo=bootstrap&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-3ECF8E.svg?style=flat&logo=supabase&logoColor=white)

---

## Table of Contents
- [Overview](#overview)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Usage](#usage)
  - [Testing](#testing)

---

## Overview
TemplarMail is a versatile backend framework built on Spring Boot, designed to simplify the development of automated email workflows and scheduling systems. It integrates core functionalities like data persistence, web interfaces, security, and scheduled tasks into a cohesive architecture.

**Why TemplarMail?**  
This project helps developers rapidly build scalable, secure, and maintainable backend services for email automation. The core features include:

- 🧩 **🔧** *Containerized Deployment:* Uses Docker and Docker Compose for consistent, environment-agnostic setup.
- 🚀 **⏰** *Advanced Scheduling:* Leverages Quartz to automate email dispatches based on flexible cron expressions.
- 🔐 **🛡️** *Secure Authentication:* Integrates OAuth2 login via Google for seamless user management.
- 📄 **📝** *API-Driven Management:* Provides endpoints for managing email templates and schedules.
- 🌐 **🎨** *Web UI:* Offers intuitive interfaces for editing templates and configuring schedules.
- 📧 **📅** *Reliable Email Workflow:* Supports dynamic email content and automated delivery, boosting communication efficiency.

---

## Getting Started

### Prerequisites
This project requires the following dependencies:
- **Programming Language:** Java
- **Package Manager:** Maven
- **Container Runtime:** Docker

### Installation
Build TemplarMail from the source and install dependencies:

1. **Clone the repository:**
   ```sh
   git clone https://github.com/NgvLanh/TemplarMail
   ```

2. **Navigate to the project directory:**
   ```sh
   cd TemplarMail
   ```

3. **Install the dependencies:**

**Using [docker](https://www.docker.com/):**
```sh
docker build -t NgvLanh/TemplarMail .
```

**Using [maven](https://maven.apache.org/):**
```sh
mvn install
```

### Usage
Run the project with:

**Using [docker](https://www.docker.com/):**
```sh
docker run -it {image_name}
```

**Using [maven](https://maven.apache.org/):**
```sh
mvn exec:java
```

### Testing
TemplarMail uses the **{test_framework}** test framework. Run the test suite with:

**Using [docker](https://www.docker.com/):**
```sh
echo 'INSERT-TEST-COMMAND-HERE'
```

**Using [maven](https://maven.apache.org/):**
```sh
mvn test
```

---

⬆ [Return to Top](#top)
