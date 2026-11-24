import { Link } from 'react-router';

export default function NavBar() {
    return (
        <nav style={{
            display: 'flex',
            gap: '20px',
            padding: '20px',
            backgroundColor: '#e2eaf5',
            borderRadius: '8px',
            marginBottom: '20px'
        }}>
            <Link to="/">Home</Link>
            <Link to="/inventory">Books</Link>
            <Link to="/add">Add Book</Link>
            <Link to="/magazines">Magazines</Link>
        </nav>
    );
}