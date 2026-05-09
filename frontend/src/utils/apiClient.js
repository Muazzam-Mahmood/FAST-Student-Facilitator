/**
 * FSF API Client
 * 
 * Centralizes all backend calls and automatically injects security headers
 * (X-User-Email, X-User-Role) from localStorage.
 */

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export async function fsfFetch(url, options = {}) {
    const userJson = localStorage.getItem('fsf-user');
    const user = userJson ? JSON.parse(userJson) : null;

    const headers = { ...options.headers };
    
    // Auto-inject JSON content type only if not already set and NOT a FormData upload
    if (!(options.body instanceof FormData) && !headers['Content-Type']) {
        headers['Content-Type'] = 'application/json';
    }

    if (user && user.email) {
        headers['X-User-Email'] = user.email;
        headers['X-User-Role'] = user.role || 'STUDENT';
    }

    // Convert relative URL to absolute using API_BASE_URL if needed
    let finalUrl = url.toString();
    if (!finalUrl.startsWith('http')) {
        const base = API_BASE_URL.endsWith('/') ? API_BASE_URL.slice(0, -1) : API_BASE_URL;
        const path = finalUrl.startsWith('/') ? finalUrl : `/${finalUrl}`;
        finalUrl = `${base}${path}`;
    }

    const response = await fetch(finalUrl, {
        ...options,
        headers
    });

    return response;
}
