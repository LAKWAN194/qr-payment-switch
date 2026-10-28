/// <reference types="vitest/config" />
import react from '@vitejs/plugin-react'
import { defineConfig, loadEnv } from 'vite'

// In development, /api/* is forwarded to the switch so the browser never needs CORS.
// In Docker, nginx does the same job (see nginx.conf).
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const switchUrl = env.SWITCH_URL ?? 'http://localhost:8180'

  return {
    plugins: [react()],
    server: {
      port: 8185,
      strictPort: true,
      proxy: {
        '/api': {
          target: switchUrl,
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, ''),
        },
      },
    },
    test: {
      environment: 'jsdom',
      setupFiles: ['./src/test/setup.ts'],
    },
  }
})
