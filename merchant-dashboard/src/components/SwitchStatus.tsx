import { useEffect, useState } from 'react'
import { getSwitchHealth, type HealthStatus } from '../api/client'

type State = { kind: 'loading' } | { kind: 'ok'; status: HealthStatus } | { kind: 'unreachable' }

export function SwitchStatus() {
  const [state, setState] = useState<State>({ kind: 'loading' })

  useEffect(() => {
    const controller = new AbortController()
    getSwitchHealth(controller.signal)
      .then((status) => setState({ kind: 'ok', status }))
      .catch((error: unknown) => {
        if (!controller.signal.aborted) {
          console.warn('Switch health check failed', error)
          setState({ kind: 'unreachable' })
        }
      })
    return () => controller.abort()
  }, [])

  const label =
    state.kind === 'loading' ? 'checking…' : state.kind === 'ok' ? state.status : 'unreachable'
  const tone = state.kind === 'ok' && state.status === 'UP' ? 'good' : state.kind === 'loading' ? 'idle' : 'bad'

  return (
    <p className="status">
      Switch service: <span className={`badge badge-${tone}`}>{label}</span>
    </p>
  )
}
