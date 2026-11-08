import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { GoogleOAuthProvider } from '@react-oauth/google'
import './index.css'
import App from './App.jsx'
import { FsfDialogProvider } from './components/FsfDialogProvider.jsx'

// Fallback client ID ensures GoogleOAuthProvider does not throw in dev/test environments
// where VITE_GOOGLE_CLIENT_ID is not configured in .env
const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID || 'test-client-id.apps.googleusercontent.com';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    {/* GoogleOAuthProvider kept for production / testing. Set VITE_GOOGLE_CLIENT_ID in .env when testing real OAuth */}
    <GoogleOAuthProvider clientId={googleClientId}>
      <FsfDialogProvider>
        <App />
      </FsfDialogProvider>
    </GoogleOAuthProvider>
  </StrictMode>,
)
