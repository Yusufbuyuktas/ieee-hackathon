/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        ergene: {
          dark: '#0f172a',
          surface: '#1e293b',
          border: '#334155',
          danger: '#ef4444',
          warning: '#f59e0b',
          success: '#10b981',
          cyan: '#06b6d4',
        }
      }
    },
  },
  plugins: [],
}