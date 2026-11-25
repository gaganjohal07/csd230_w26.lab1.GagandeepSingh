import { useState } from 'react';
import { AuthContext } from './AuthContext.js';

export default function AuthProvider({ children }) {
    const [token, setToken] = useState(
        () => localStorage.getItem('token')
    );

    function login(newToken) {
        if (typeof newToken !== 'string' || !newToken.trim()) {
            throw new Error('Invalid token');
        }

        localStorage.setItem('token', newToken);
        setToken(newToken);
    }

    function logout() {
        localStorage.removeItem('token');
        setToken(null);
    }

    return (
        <AuthContext.Provider value={{ token, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}