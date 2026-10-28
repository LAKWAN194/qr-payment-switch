import { SwitchStatus } from './components/SwitchStatus'

export default function App() {
  return (
    <main className="page">
      <h1>Merchant Dashboard</h1>
      <p className="lede">
        Payments, settlements and webhook deliveries for one merchant. The screens arrive in week
        7; this page only checks that the dashboard can reach the switch.
      </p>
      <SwitchStatus />
    </main>
  )
}
