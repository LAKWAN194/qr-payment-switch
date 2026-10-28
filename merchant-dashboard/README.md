# merchant-dashboard

React + TypeScript (Vite) dashboard for merchants: payments, settlements and webhook
deliveries. Week 7 builds the screens; for now it shows whether it can reach the switch.

```bash
npm install
npm run dev      # http://localhost:8185, with /api forwarded to SWITCH_URL
npm test         # Vitest + Testing Library
npm run lint     # oxlint
npm run build    # type-check and production build
```

| Setting | Default | Meaning |
| --- | --- | --- |
| `SWITCH_URL` | `http://localhost:8180` | Where the dev server forwards `/api/*` |

In Docker, nginx serves the build and forwards `/api/*` to `switch-service:8180`.
