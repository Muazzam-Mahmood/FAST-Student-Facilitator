/**
 * FSF API Client
 * 
 * Centralizes all backend calls and automatically injects security headers
 * (X-User-Email, X-User-Role) from localStorage.
 */
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

    const response = await fetch(url, {
        ...options,
        headers
    });

    return response;
}
