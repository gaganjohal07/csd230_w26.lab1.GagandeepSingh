import { useState } from 'react';
import { Navigate, useNavigate, useSearchParams } from 'react-router';
import api from './api.js';
import useAuth from './useAuth.js';

export default function Login() {
    const { token, login } = useAuth();
    const navigate = useNavigate();
    const [search] = useSearchParams();

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [pending, setPending] = useState(false);

    async function handleSubmit(event) {
        event.preventDefault();
        setError('');
        setPending(true);

        try {
            const response = await api.post('/auth/login', {
                username,
                password
            });

            login(response.data.token);
            navigate('/inventory', { replace: true });
        } catch (err) {
            setError(
                err.response?.data?.message || err.message
            );
        } finally {
            setPending(false);
        }
    }

    if (token) {
        return <Navigate to="/inventory" replace />;
    }

    return (
        <div className="container">
            <h1>Bookstore Login</h1>

            {search.get('reauth') === 'true' && (
                <p role="status">
                    Your session has expired or is invalid.
                    Please sign in again.
                </p>
            )}

            {error && (
                <p className="error" role="alert">
                    {error}
                </p>
            )}

            <form onSubmit={handleSubmit}>
                <label>
                    Username
                    <input
                        type="text"
                        value={username}
                        onChange={e => setUsername(e.target.value)}
                        autoComplete="username"
                        required
                    />
                </label>

                <label>
                    Password
                    <input
                        type="password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        autoComplete="current-password"
                        required
                    />
                </label>

                <button type="submit" disabled={pending}>
                    {pending ? 'Signing in...' : 'Login'}
                </button>
            </form>
        </div>
    );
}