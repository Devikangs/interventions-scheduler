# Interventions Scheduler (ACE Demo)

Full-stack demo aligned to EN-ACE: plan interventions (time/location/skills), detect conflicts (pairs, clusters, technician double-bookings), and generate an AI meeting report.

## Stack
- **Backend**: Spring Boot 3 (Web, JPA, Validation, Security-open), OpenAPI, AI chat-completions call
- **DB**: Postgres 16 (Docker)
- **Frontend**: Next.js (TS), Tailwind, Route Handlers proxy (no CORS)
- **Tests**: Unit + WebMvc + Testcontainers (Postgres)
- **Run**: Docker compose

## Quick start

```bash
# 1) Build backend jar
cd backend
./mvnw -DskipTests package
cd ..

# 2) Run DB + backend
docker compose up --build
# Swagger: http://localhost:8080/swagger-ui/index.html

# 3) Run frontend
cd frontend
npm install
npm run dev
# open printed localhost URL (usually http://localhost:3000)
