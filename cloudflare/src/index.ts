import { Container, getRandom } from "@cloudflare/containers";
import { env as workerEnv } from "cloudflare:workers";

const DEFAULT_INSTANCE_COUNT = 1;
const CONTAINER_PORT = 8080;
const STARTUP_INSTANCE_GET_TIMEOUT_MS = 30_000;
const STARTUP_PORT_READY_TIMEOUT_MS = 120_000;
const STARTUP_WAIT_INTERVAL_MS = 1_000;

const STATIC_CONTAINER_ENV = {
  SPRING_PROFILES_ACTIVE: "prod",
  TIMEZONE: "UTC",
  TZ: "UTC",
  JAVA_TOOL_OPTIONS: "-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError",
};

const CONTAINER_SECRET_NAMES = ["VAULT_ADDRESS", "VAULT_TOKEN"] as const;

const CONTAINER_VAR_NAMES = [
  "SPRING_PROFILES_ACTIVE",
  "TIMEZONE",
  "TZ",
  "JAVA_TOOL_OPTIONS",
] as const;

const REQUIRED_SECRET_NAMES = ["VAULT_ADDRESS", "VAULT_TOKEN"] as const;

type ContainerSecretName = (typeof CONTAINER_SECRET_NAMES)[number];
type ContainerVarName = (typeof CONTAINER_VAR_NAMES)[number];
type RequiredSecretName = (typeof REQUIRED_SECRET_NAMES)[number];

type SecretBindings = Partial<Record<ContainerSecretName, string>>;
type ConfigBindings = Partial<Record<ContainerVarName, string>>;

export interface Env extends SecretBindings, ConfigBindings {
  SPRING_BOOT_API: DurableObjectNamespace<SpringBootApi>;
  CONTAINER_INSTANCE_COUNT?: string;
}

function buildContainerEnv(envSource: SecretBindings & ConfigBindings): Record<string, string> {
  const containerEnv: Record<string, string> = { ...STATIC_CONTAINER_ENV };

  for (const name of [...CONTAINER_SECRET_NAMES, ...CONTAINER_VAR_NAMES]) {
    const value = envSource[name];

    if (typeof value === "string" && value.length > 0) {
      containerEnv[name] = value;
    }
  }

  return containerEnv;
}

function missingRequiredSecrets(envSource: SecretBindings): RequiredSecretName[] {
  return REQUIRED_SECRET_NAMES.filter((name) => !envSource[name]);
}

function getContainerInstanceCount(envSource: Env): number {
  const configuredCount = Number(envSource.CONTAINER_INSTANCE_COUNT);

  if (Number.isInteger(configuredCount) && configuredCount > 0) {
    return configuredCount;
  }

  return DEFAULT_INSTANCE_COUNT;
}

export class SpringBootApi extends Container {
  defaultPort = CONTAINER_PORT;
  sleepAfter = "5m";
  envVars = buildContainerEnv(workerEnv as unknown as SecretBindings & ConfigBindings);

  override async fetch(request: Request): Promise<Response> {
    const state = await this.getState();

    if (state.status !== "healthy") {
      try {
        await this.startAndWaitForPorts({
          ports: CONTAINER_PORT,
          cancellationOptions: {
            abort: request.signal,
            instanceGetTimeoutMS: STARTUP_INSTANCE_GET_TIMEOUT_MS,
            portReadyTimeoutMS: STARTUP_PORT_READY_TIMEOUT_MS,
            waitInterval: STARTUP_WAIT_INTERVAL_MS,
          },
        });
      } catch (error) {
        console.error("SpringBootApi container failed to become ready", error);

        return new Response(
          `Failed to start container: ${error instanceof Error ? error.message : String(error)}`,
          { status: 500 },
        );
      }
    }

    return this.containerFetch(request, CONTAINER_PORT);
  }

  override onStart() {
    console.log("SpringBootApi container started");
  }

  override onStop({ exitCode, reason }: { exitCode?: number; reason?: string }) {
    console.log("SpringBootApi container stopped", { exitCode, reason });
  }

  override onError(error: unknown) {
    console.error("SpringBootApi container error", error);
    throw error;
  }
}

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    const missing = missingRequiredSecrets(env);

    if (missing.length > 0) {
      return new Response(`Missing Cloudflare Worker secrets: ${missing.join(", ")}`, {
        status: 500,
      });
    }

    const container = await getRandom(env.SPRING_BOOT_API, getContainerInstanceCount(env));
    return container.fetch(request);
  },
};
