/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/main/webapp/**/*.{html,ts}"],
  safelist: [
    'underline',
    'bg-gray-100'
  ],
  theme: {
    extend: {},
    container: {
      center: true,
    }
  },
  plugins: [
    require('@tailwindcss/forms')
  ]
}
