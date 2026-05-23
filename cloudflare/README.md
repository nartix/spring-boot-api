# Cloudflare Containers Deployment

This folder contains the Cloudflare-only deployment surface for the Spring Boot API. The existing Docker Compose deployment remains available for future use.

## What Is Here

- `wrangler.jsonc`: Worker, Container image, Durable Object binding, production custom domain, and staging `workers.dev` config.
- `src/index.ts`: Worker entrypoint that proxies requests to the Spring Boot container and injects runtime env vars/secrets.
- `scripts/sync-vault-secrets.mjs`: Cloudflare Worker secret sync script.
- `secrets.manifest.json`: shell-provided secret names and required Worker secret names.
- `package.json`: local Wrangler scripts for manual sync and deploy.

## Environments

| Environment | Worker name | URL |
| --- | --- | --- |
| Staging | `spring-boot-api-worker-staging` | `https://spring-boot-api-worker-staging.<your-workers-subdomain>.workers.dev` |
| Production | `spring-boot-api-worker` | `https://spring-boot-api.ferozfaiz.com` |

Staging uses the current Vault backend, matching production for now. Cloudflare stores staging and production Worker secrets separately, so run the sync command for each environment before deploying it.

## Prerequisites

Install Cloudflare tooling:

```bash
cd cloudflare
npm install --no-package-lock
```

Export Cloudflare credentials, or put them in the repository root `.env` file:

```bash
export CLOUDFLARE_API_TOKEN=...
export CLOUDFLARE_ACCOUNT_ID=...
```

`CLOUDFLARE_API_TOKEN` is required for `npm run secrets:sync:*` and `npm run deploy:*`. The token needs permission to edit Workers, Worker secrets, Containers, Durable Objects, `workers.dev`, and the production custom domain.

Export the Vault connection values used by the Spring Boot container, or keep them in the repository root `.env` file:

```bash
export VAULT_ADDR=...
export VAULT_TOKEN=...
```

`VAULT_ADDR` is synced into Cloudflare as `VAULT_ADDRESS`. If `VAULT_ADDRESS` is already set in your shell, it takes precedence over `VAULT_ADDR`.

The sync script loads `../.env` automatically when run from this folder. Already-exported shell variables win over `.env` values. To use a different file, set `ENV_FILE=/path/to/file`.

## Runtime Environment

The Cloudflare container receives the same runtime values as `docker-compose.prod.yml`:

- `SPRING_PROFILES_ACTIVE`
- `VAULT_ADDRESS`
- `VAULT_TOKEN`
- `TIMEZONE`
- `TZ`
- `JAVA_TOOL_OPTIONS`

`SPRING_PROFILES_ACTIVE`, `TIMEZONE`, `TZ`, and `JAVA_TOOL_OPTIONS` are regular Wrangler vars in `wrangler.jsonc`. `VAULT_ADDRESS` and `VAULT_TOKEN` are Cloudflare Worker secrets.

Spring Boot uses `VAULT_ADDRESS` and `VAULT_TOKEN` to load PostgreSQL, OAuth, JWT, and other application secrets from Vault internally at runtime.

## Sync Secrets

The sync script reads these shell values:

- `VAULT_ADDRESS` or `VAULT_ADDR`
- `VAULT_TOKEN`

It writes them into Cloudflare Worker secrets using Wrangler.

Dry-run staging:

```bash
cd cloudflare
DRY_RUN=true npm run secrets:sync:staging
```

Sync staging:

```bash
cd cloudflare
npm run secrets:sync:staging
```

Sync production:

```bash
cd cloudflare
npm run secrets:sync:production
```

## Deploy Staging

```bash
cd cloudflare
npm run secrets:sync:staging
npm run deploy:staging -- --non-interactive
```

Smoke checks:

```bash
curl https://spring-boot-api-worker-staging.<your-workers-subdomain>.workers.dev/actuator/health
curl https://spring-boot-api-worker-staging.<your-workers-subdomain>.workers.dev/swagger-ui.html
```

## Deploy Production

```bash
cd cloudflare
npm run secrets:sync:production
npm run deploy:production -- --non-interactive
```

Smoke checks:

```bash
curl https://spring-boot-api.ferozfaiz.com/actuator/health
curl https://spring-boot-api.ferozfaiz.com/swagger-ui.html
```

## Required Secrets

`secrets.manifest.json` currently treats these as required:

- `VAULT_ADDRESS`
- `VAULT_TOKEN`

The script warns if either value is missing. It will still sync any values it can find.

## Operational Notes

- No GitHub Actions workflow is included; deployment is manual from this folder.
- `docker-compose.prod.yml` is intentionally untouched.
- `cloudflare/` is ignored by Docker builds through `.dockerignore` so local Wrangler dependencies do not enter the Java image context.
- The Worker waits up to 120 seconds for Spring Boot to start listening on port `8080`. The default Cloudflare Containers helper waits about 20 seconds, which can be too short for Spring Boot startup with Vault/JPA/Liquibase.
- `CONTAINER_INSTANCE_COUNT` and `max_instances` are set to `1` because the OAuth authorization service is currently in-memory. Increase them only after auth/session behavior is confirmed for multiple instances.
- If the Java process needs more memory than Cloudflare's `basic` container type provides, change `instance_type` in `wrangler.jsonc`.
