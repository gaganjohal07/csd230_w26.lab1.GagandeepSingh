import axios from 'axios';

const api = axios.create({
    baseURL: '/api/rest'
});

export default api;
