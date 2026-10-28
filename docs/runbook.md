# Runbook

How to run, test and debug everything locally.

## Prerequisites

| Tool | Version | Check |
| --- | --- | --- |
| JDK | 21 | `javac -version` (Ubuntu: `sudo apt install openjdk-21-jdk`) |
| Docker with Compose | recent | `docker compose version` |
| Python | 3.12 | `python3 --version`, plus `python3-venv` on Ubuntu |
| Node.js | 22 | `node -v` |

Maven itself is not needed: `./mvnw` downloads the pinned version (3.9.16).

## Ports

This project keeps to its own block so it can run next to the other portfolio
projects. Change any of them in `.env` (copy `.env.example`).

| Service | Port |
| --- | --- |
| switch-service | 8180 |
| mock issuer BANK_A | 8181 |
| mock issuer BANK_B | 8182 |
| fraud-scorer | 8183 |
| merchant-dashboard (Docker) | 8184 |
| merchant-dashboard (Vite dev server) | 8185 |
| Redpanda Console | 8186 |
| PostgreSQL | 5481 |
| Redis | 6381 |
| Kafka API (Redpanda) | 19181 |

## Everyday commands

The `Makefile` wraps the common ones (`make help` lists them).

```bash
make infra-up            # PostgreSQL, Redpanda, Redpanda Console, Redis
make run-switch          # switch-service on :8180, against the infra above
make run-issuer-a        # BANK_A on :8181 (run-issuer-b for BANK_B on :8182)
make test                # every test in every language
make up                  # the whole system in Docker
make down                # stop everything (data is kept in a volume)
make clean-data          # stop everything and delete the database volume
```

## Checking it works

```bash
curl localhost:8180/actuator/health        # {"status":"UP",...}
curl localhost:8181/actuator/info          # {"issuer":"BANK_A"}
curl localhost:8183/health                 # {"status":"ok","model_loaded":false}
open http://localhost:8184                 # dashboard (Docker), shows "Switch service: UP"
open http://localhost:8186                 # Redpanda Console: topics and messages
```

## Troubleshooting

- **Integration tests fail with "Could not find a valid Docker environment".**
  Docker is not running, or your user cannot reach it (`docker ps` should work
  without `sudo`).
- **Port already in use.** Another project is on that port. Change it in `.env`,
  or find the owner with `ss -ltnp | grep <port>`.
- **Flyway reports a checksum mismatch.** An applied migration was edited. Put
  the change in a new migration; locally you can reset with `make clean-data`.
- **The dashboard shows "unreachable".** The switch is not running on the port
  the proxy expects (`SWITCH_URL`, default `http://localhost:8180`).
