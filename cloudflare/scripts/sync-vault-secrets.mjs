#!/usr/bin/env node
import { spawnSync } from "node:child_process";
import { existsSync, readFileSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const scriptDir = dirname(fileURLToPath(import.meta.url));
const repoRoot = resolve(scriptDir, "../..");
const cloudflareDir = resolve(repoRoot, "cloudflare");
const manifestPath =
  process.env.CLOUDFLARE_SECRETS_MANIFEST ??
  resolve(cloudflareDir, "secrets.manifest.json");
const wranglerConfig =
  process.env.WRANGLER_CONFIG ?? resolve(cloudflareDir, "wrangler.jsonc");
const cloudflareEnv = process.env.CLOUDFLARE_ENV?.trim();
const dryRun = process.env.DRY_RUN === "true";
const envFilePath = process.env.ENV_FILE ?? resolve(repoRoot, ".env");

const secretNamePattern = /^[A-Za-z_][A-Za-z0-9_]*$/;

function readPositiveInteger(value, fallback) {
  const parsed = Number.parseInt(value ?? "", 10);
  return Number.isInteger(parsed) && parsed > 0 ? parsed : fallback;
}

const secretPutDelayMs = readPositiveInteger(process.env.SECRET_SYNC_DELAY_MS, 1500);
const secretPutMaxAttempts = readPositiveInteger(
  process.env.SECRET_SYNC_MAX_ATTEMPTS,
  5,
);

function readJson(path) {
  return JSON.parse(readFileSync(path, "utf8"));
}

function stripInlineComment(value) {
  let quote = "";

  for (let index = 0; index < value.length; index += 1) {
    const char = value[index];

    if ((char === "\"" || char === "'") && value[index - 1] !== "\\") {
      quote = quote === char ? "" : quote || char;
      continue;
    }

    if (char === "#" && !quote && (index === 0 || /\s/.test(value[index - 1]))) {
      return value.slice(0, index).trimEnd();
    }
  }

  return value.trimEnd();
}

function unquoteEnvValue(value) {
  const trimmedValue = stripInlineComment(value.trim());
  const quote = trimmedValue[0];

  if (
    (quote === "\"" || quote === "'") &&
    trimmedValue.endsWith(quote) &&
    trimmedValue.length >= 2
  ) {
    const unquoted = trimmedValue.slice(1, -1);

    if (quote === "\"") {
      return unquoted
        .replaceAll("\\n", "\n")
        .replaceAll("\\r", "\r")
        .replaceAll("\\t", "\t")
        .replaceAll("\\\"", "\"")
        .replaceAll("\\\\", "\\");
    }

    return unquoted;
  }

  return trimmedValue;
}

function loadEnvFile(path) {
  if (!existsSync(path)) {
    return;
  }

  const lines = readFileSync(path, "utf8").split(/\r?\n/);

  for (const line of lines) {
    const trimmedLine = line.trim();

    if (!trimmedLine || trimmedLine.startsWith("#")) {
      continue;
    }

    const normalizedLine = trimmedLine.startsWith("export ")
      ? trimmedLine.slice("export ".length).trimStart()
      : trimmedLine;
    const separatorIndex = normalizedLine.indexOf("=");

    if (separatorIndex <= 0) {
      continue;
    }

    const name = normalizedLine.slice(0, separatorIndex).trim();
    const value = unquoteEnvValue(normalizedLine.slice(separatorIndex + 1));

    if (!secretNamePattern.test(name) || process.env[name] !== undefined) {
      continue;
    }

    process.env[name] = value;
  }
}

function run(command, args, options = {}) {
  const result = spawnSync(command, args, {
    encoding: "utf8",
    ...options,
  });

  if (result.status !== 0) {
    const stderr = result.stderr?.trim();
    const stdout = result.stdout?.trim();
    const spawnError = result.error?.message;
    const signal = result.signal ? `signal ${result.signal}` : undefined;
    const exitCode =
      result.status === null || result.status === undefined
        ? undefined
        : `exit code ${result.status}`;
    const detail =
      [spawnError, stderr, stdout, signal, exitCode].filter(Boolean).join("; ") ||
      "unknown error";
    throw new Error(`${command} ${args.join(" ")} failed: ${detail}`);
  }

  return result.stdout;
}

function sleep(ms) {
  if (ms <= 0) {
    return;
  }

  Atomics.wait(new Int32Array(new SharedArrayBuffer(4)), 0, 0, ms);
}

function isThrottleError(error) {
  const message = error instanceof Error ? error.message : String(error);

  return (
    message.includes("[code: 971]") ||
    message.includes("Please wait and consider throttling") ||
    message.includes("[code: 429]") ||
    message.includes("Too Many Requests")
  );
}

function assertVaultCliAvailable(manifest) {
  if ((manifest.vaultPaths ?? []).length === 0) {
    return;
  }

  try {
    run("vault", ["version"]);
  } catch (error) {
    throw new Error(
      [
        "Vault CLI is required to sync Worker secrets.",
        "Install HashiCorp Vault and make sure `vault` is on PATH, then retry.",
        `Original error: ${error.message}`,
      ].join(" "),
    );
  }
}

function assertCloudflareCredentialsAvailable() {
  if (dryRun) {
    return;
  }

  if (!process.env.CLOUDFLARE_API_TOKEN?.trim()) {
    throw new Error(
      [
        "CLOUDFLARE_API_TOKEN is required to sync Cloudflare Worker secrets.",
        "Export a Cloudflare API token in this shell, then retry.",
        "Example: export CLOUDFLARE_API_TOKEN=...",
      ].join(" "),
    );
  }

  if (!process.env.CLOUDFLARE_ACCOUNT_ID?.trim()) {
    console.warn(
      "Warning: CLOUDFLARE_ACCOUNT_ID is not set. Wrangler may fail if it cannot infer the account.",
    );
  }
}

function toVaultKvCliPath(path) {
  const kvV2ApiPath = path.match(/^([^/]+)\/data\/(.+)$/);
  return kvV2ApiPath ? `${kvV2ApiPath[1]}/${kvV2ApiPath[2]}` : path;
}

function vaultKvGet(path) {
  const output = run("vault", ["kv", "get", "-format=json", toVaultKvCliPath(path)]);
  const parsed = JSON.parse(output);
  const data = parsed?.data?.data;

  if (!data || typeof data !== "object" || Array.isArray(data)) {
    throw new Error(`Vault path ${path} did not return a KV v2 data object`);
  }

  return data;
}

function normalizeSecretValue(value) {
  return typeof value === "string" ? value : JSON.stringify(value);
}

function assertValidSecretName(name) {
  if (!secretNamePattern.test(name)) {
    throw new Error(`Invalid Worker secret binding name: ${name}`);
  }
}

function addSecret(secrets, name, value) {
  assertValidSecretName(name);

  if (value === undefined || value === null || value === "") {
    return;
  }

  secrets.set(name, normalizeSecretValue(value));
}

function putWorkerSecret(name, value) {
  const envArgs = cloudflareEnv ? ["--env", cloudflareEnv] : [];

  if (dryRun) {
    const envLabel = cloudflareEnv ? ` to ${cloudflareEnv}` : "";
    console.log(`[dry-run] would sync ${name}${envLabel}`);
    return;
  }

  const args = [
    "--prefix",
    cloudflareDir,
    "exec",
    "--",
    "wrangler",
    "secret",
    "put",
    name,
    "--config",
    wranglerConfig,
    ...envArgs,
  ];

  for (let attempt = 1; attempt <= secretPutMaxAttempts; attempt += 1) {
    try {
      run("npm", args, { input: value });

      const envLabel = cloudflareEnv ? ` to ${cloudflareEnv}` : "";
      console.log(`Synced ${name}${envLabel}`);
      sleep(secretPutDelayMs);
      return;
    } catch (error) {
      const canRetry = attempt < secretPutMaxAttempts && isThrottleError(error);

      if (!canRetry) {
        throw error;
      }

      const delayMs = Math.min(secretPutDelayMs * 2 ** attempt, 60_000);
      console.warn(
        `Cloudflare throttled secret ${name}; retrying in ${Math.round(
          delayMs / 1000,
        )}s (${attempt}/${secretPutMaxAttempts})`,
      );
      sleep(delayMs);
    }
  }
}

function collectEnvironmentSecrets(manifest, secrets) {
  for (const entry of manifest.environmentSecrets ?? []) {
    const source = entry.source;
    const fallbackSource = entry.fallbackSource;
    const target = entry.target ?? source;
    const value = process.env[source] ?? process.env[fallbackSource];

    addSecret(secrets, target, value);
  }
}

function collectVaultSecrets(manifest, secrets) {
  for (const vaultPath of manifest.vaultPaths ?? []) {
    const data = vaultKvGet(vaultPath.path);

    if (vaultPath.copyAll) {
      for (const [name, value] of Object.entries(data)) {
        addSecret(secrets, name, value);
      }
    }

    for (const [sourceName, targetName] of Object.entries(vaultPath.aliases ?? {})) {
      if (Object.hasOwn(data, sourceName)) {
        addSecret(secrets, targetName, data[sourceName]);
      }
    }
  }
}

function collectSecrets(manifest) {
  const secrets = new Map();

  collectVaultSecrets(manifest, secrets);
  collectEnvironmentSecrets(manifest, secrets);

  return secrets;
}

function warnForMissingRequiredSecrets(manifest, secrets) {
  const missing = (manifest.requiredWorkerSecrets ?? []).filter(
    (name) => !secrets.has(name),
  );

  if (missing.length > 0) {
    console.warn(
      `Warning: required Worker secrets were not found in environment or Vault data: ${missing.join(", ")}`,
    );
  }
}

loadEnvFile(envFilePath);
const manifest = readJson(manifestPath);
assertCloudflareCredentialsAvailable();
assertVaultCliAvailable(manifest);
const secrets = collectSecrets(manifest);

warnForMissingRequiredSecrets(manifest, secrets);

for (const [name, value] of [...secrets.entries()].sort(([a], [b]) =>
  a.localeCompare(b),
)) {
  putWorkerSecret(name, value);
}
