// Every call goes through /api, which the dev server and nginx forward to the switch.
const BASE = '/api'

export type HealthStatus = 'UP' | 'DOWN' | 'OUT_OF_SERVICE' | 'UNKNOWN'

export async function getSwitchHealth(signal?: AbortSignal): Promise<HealthStatus> {
  const response = await fetch(`${BASE}/actuator/health`, { signal })
  // Actuator answers 503 with a JSON body when the service is DOWN, so read the body either way.
  const body = (await response.json()) as { status?: HealthStatus }
  return body.status ?? 'UNKNOWN'
}
