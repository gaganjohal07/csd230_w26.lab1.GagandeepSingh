import axios from 'axios';

const api = axios.create({
    baseURL: '/api/rest'
});

api.interceptors.request.use(config => {
    const token = globalThis.localStorage?.getItem('token');

    config.authToken = config.url === '/auth/login' ? null : token;

    config.headers.delete('Authorization');

    if (config.authToken) {
        config.headers.set(
            'Authorization',
            `Bearer ${config.authToken}`
        );
    }

    return config;
});

let redirecting = false;

api.interceptors.response.use(
    response => response,
    error => {
        const requestToken = error.config?.authToken;

        if (
            error.response?.status === 401 &&
            requestToken &&
            error.config.url !== '/auth/login' &&
            requestToken === globalThis.localStorage?.getItem('token')
        ) {
            localStorage.removeItem('token');

            window.dispatchEvent(new Event('auth:invalid'));

            if (
                !redirecting &&
                window.location.pathname !== '/login'
            ) {
                redirecting = true;

                window.location.replace('/login?reauth=true');
            }
        }

        return Promise.reject(error);
    }
);

export default api;