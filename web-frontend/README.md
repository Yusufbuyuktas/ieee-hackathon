# React + Vite

## API and session configuration

The frontend uses `/api` by default. Vite proxies this path to the local backend at
`http://localhost:8080`; the Vercel rewrite proxies it to the deployed backend. This
keeps browser requests same-origin, so Vercel does not need to be added to the CORS
allowlist for that setup. Leave `VITE_API_BASE_URL` unset or set it to `/api` in Vercel.
For the HTTPS Vercel deployment, set `SESSION_COOKIE_SECURE=true` on the backend;
`SameSite=Lax` can remain in place with this same-origin proxy setup.

If the browser calls the backend directly from another origin, configure the backend's
`CORS_ALLOWED_ORIGINS` environment variable with exact comma-separated origins (for
example, `https://your-dashboard.vercel.app`). Do not use `*` with credentials.
For direct cross-site session cookies, serve the backend over HTTPS and set
`SESSION_COOKIE_SAME_SITE=none` and `SESSION_COOKIE_SECURE=true`. The defaults
(`lax` and `false`) retain local HTTP development behavior.

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Oxc](https://oxc.rs)
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/)

## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.
