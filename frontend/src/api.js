import axios from 'axios';

const api = axios.create({
    baseURL: '/api/rest'
});

api.interceptors.request.use(config => {
    const token = globalThis.localStorage?.getItem('token');

    config.authToken = config.url === '/auth/login'
        ? null
        : token;

    config.headers.delete('Authorization');

    if (config.authToken) {
        config.headers.set(
            'Authorization',
            `Bearer ${config.authToken}`
        );
    }

    return config;
});

export default api;