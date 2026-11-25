import { Link, useNavigate } from 'react-router';
import useAuth from './useAuth.js';

export default function NavBar() {
    const { logout, isAdmin } = useAuth();
    const navigate = useNavigate();

    function handleLogout() {
        logout();
        navigate('/login', { replace: true });
    }

    return (
        <nav style={{
            display: 'flex',
            gap: '20px',
            padding: '20px',
            backgroundColor: '#e2eaf5',
            borderRadius: '8px',
            marginBottom: '20px',
            alignItems: 'center',
            flexWrap: 'wrap'
        }}>
            <Link to="/">Home</Link>
            <Link to="/inventory">Books</Link>

            {isAdmin && <Link to="/add">Add Book</Link>}

            <Link to="/magazines">Magazines</Link>
            <Link to="/cart">My Cart</Link>

            <button onClick={handleLogout}>
                Logout
            </button>
        </nav>
    );
}