import { render, screen } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { SwitchStatus } from './SwitchStatus'

afterEach(() => {
  vi.unstubAllGlobals()
  vi.restoreAllMocks()
})

describe('SwitchStatus', () => {
  it('shows the status the switch reports', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(new Response(JSON.stringify({ status: 'UP' }), { status: 200 })),
    )
    render(<SwitchStatus />)
    expect(await screen.findByText('UP')).toBeInTheDocument()
    expect(fetch).toHaveBeenCalledWith('/api/actuator/health', expect.anything())
  })

  it('reads DOWN from a 503 response', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(new Response(JSON.stringify({ status: 'DOWN' }), { status: 503 })),
    )
    render(<SwitchStatus />)
    expect(await screen.findByText('DOWN')).toBeInTheDocument()
  })

  it('says unreachable when the request fails', async () => {
    vi.spyOn(console, 'warn').mockImplementation(() => {})
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new TypeError('Failed to fetch')))
    render(<SwitchStatus />)
    expect(await screen.findByText('unreachable')).toBeInTheDocument()
  })
})
