# 💳 Digital Wallet & Payment System (Spring Boot)

A production-grade **Digital Wallet / Payment System** built using **Java & Spring Boot**, inspired by real-world fintech systems like Paytm, PhonePe, and GPay.

This project focuses on **backend correctness**, **data consistency**, **idempotency**, and **transaction safety**, not just CRUD APIs.

---

## 🧠 Key Concepts Covered

- Wallet architecture
- Payment transaction lifecycle
- Idempotency (duplicate request protection)
- Atomic balance updates
- Database migrations using Flyway
- RESTful API design
- UUID-based primary keys
- Separation of domain logic

---

## 🛠️ Tech Stack

| Layer | Technology |
|-----|-----------|
| Language | Java 17 |
| Framework | Spring Boot |
| Database | MySQL |
| ORM | Spring Data JPA (Hibernate) |
| Migrations | Flyway |
| Build Tool | Maven |
| API Style | REST |
| Auth (Planned) | JWT |
| Testing (Planned) | JUnit, Mockito |

---

## 🧱 Core Domain Model

### 1️⃣ User
Represents a wallet owner.

### 2️⃣ Wallet
- One wallet per user
- Stores current balance
- Balance updated only via transactions

### 3️⃣ Transaction
Represents every money movement:
- Top-up
- Wallet-to-wallet transfer

### 4️⃣ Ledger (Planned)
Immutable record of all balance changes for auditing.

---

## 🗄️ Database Schema (High Level)

- `users`
- `wallets`
- `transactions`
- `ledger_entries` *(planned)*

Database schema is managed using **Flyway migrations** for full version control.

---

## 🔐 Transaction Safety & Idempotency

### Why Idempotency?
Payment systems must handle:
- Network retries
- Duplicate API calls
- Client-side resubmissions

### How it works:
- Each external payment request includes a `referenceId`
- A unique constraint prevents duplicate processing
- If a request is retried, the existing transaction is returned safely

---

## 📡 API Endpoints (Current & Planned)

### User
```http
POST /api/users
GET  /api/users/{id}
