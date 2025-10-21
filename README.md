# 🔐 MicroPay - Security Service

**MicroPay-Security** is the **security** microservice of the MicroPay — a distributed, event-driven **Wallet Application**.

This service manages **user identity**, **role-based access control**, and **token lifecycle management** for the entire MicroPay ecosystem.

---

## 🧩 System Context

**MicroPay** consists of six independent microservices:

| Service | Description |
|----------|-------------|
| Gateway | Central API gateway for routing and load balancing |
| Security | Handles authentication and authorization |
| **Payment** | Orchestrates the payment lifecycle |
| Wallet | Maintains wallet balance and reservations |
| Transaction | Records and tracks transaction states |
| Notification | Sends notifications to users |

---

## 🔑 Core Responsibilities

The **Security Service** is responsible for:

- Authenticating users via **phone number** and **PIN**  
- Managing **access** and **refresh tokens**  
- Enforcing **role-based access control (RBAC)**  
- Handling **user data management** (create, suspend, activate, block)  
- Integrating seamlessly with the Gateway and other services for secure communication  

---

## 🧠 Authentication Design

Authentication in MicroPay is designed around **simplicity and security**.

Users authenticate using:
The service defines two custom Spring Security components:
- **`PhonePinAuthenticationFilter`** – intercepts login requests, extracting phone and PIN.
- **`PhonePinAuthenticationProvider`** – performs actual authentication logic against the database.

These are implemented directly from **low-level Spring Security interfaces**, ensuring deep control over the authentication process.

---

## 🔒 Token Management

Upon successful registration or login, users receive **two JWT tokens**:

| Token Type | Lifetime | Description |
|-------------|-----------|-------------|
| Access Token | 15 minutes | Used for authorized API requests |
| Refresh Token | 7 days | Used to obtain new access tokens |

When an access token expires:
- The user calls the `/auth/refresh-access-token` endpoint  
- A **new access and refresh token pair** is issued  
- The **old refresh token is rotated and blacklisted**

---

## 🧍 Role & Permissions

By default, all users are assigned the `USER` role.  
Specific trusted clients can be promoted to `ADMIN` manually in the database.

| Role | Permissions |
|------|--------------|
| USER | Access to standard wallet and transaction features |
| ADMIN | Manage, block, suspend, and activate users |

Only developers or the admin team can modify roles, ensuring strict control.

---

## 🚀 Deployment
All MicroPay services are:
- Built with Gradle
- Containerized using Docker
- Deployed and tested in Google Cloud Platform (GCP)
- Designed for Kubernetes orchestration
## 🧰 Tech Stack
- Java 21
- Spring Boot & Spring Security
- PostgreSQL
- Redis
- Docker & Kubernetes
- Gradle
- JUnit & Mockito
- Prometheus & Grafana(later)

---

<p align="center">
  <b>Omar Ismailov</b><br>
  <i>Software Engineer • Backend & System Design Enthusiast</i><br>
  Building reliable systems with simplicity and architecture in mind.
</p>

