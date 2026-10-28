import '@testing-library/jest-dom/vitest'
import { cleanup } from '@testing-library/react'
import { afterEach } from 'vitest'

// Unmount whatever each test rendered. Testing Library only does this automatically when
// Vitest globals are on, and this project keeps them off.
afterEach(() => cleanup())
