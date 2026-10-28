// A smoke test, not the real load test: it proves k6 can reach the switch.
// Week 7 replaces this with payment traffic and the thresholds from docs/brief.md.
import http from 'k6/http'
import { check, sleep } from 'k6'

export const options = {
  vus: 5,
  duration: '30s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
  },
}

const BASE = __ENV.SWITCH_URL || 'http://localhost:8180'

export default function () {
  const res = http.get(`${BASE}/actuator/health`)
  check(res, { 'status is 200': (r) => r.status === 200 })
  sleep(0.1)
}
