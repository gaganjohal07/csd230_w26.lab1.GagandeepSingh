import { useEffect, useState } from 'react';
import { hasRole } from './tokenRoles.js';
import { AuthContext } from './AuthContext.js';

export default function AuthProvider({ children }) {
    const [token, setToken] = useState(
        () => localStorage.getItem('token')
    );

    useEffect(() => {
        function invalidSession() {
            setToken(null);
        }

        window.addEventListener('auth:invalid', invalidSession);

        return () => {
            window.removeEventListener('auth:invalid', invalidSession);
        };
    }, []);

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
        <AuthContext.Provider
            value={{
                token,
                login,
                logout,
                isAdmin: hasRole(token, 'ROLE_ADMIN')
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}