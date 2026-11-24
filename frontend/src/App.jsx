import api from './api.js';
import { useState, useEffect } from 'react';
import { Routes, Route, useNavigate } from 'react-router';
import NavBar from './NavBar.jsx';
import Home from './Home.jsx';
import BookList from './BookList.jsx';
import AddBookForm from './AddBookForm.jsx';
import MagazineList from './MagazineList.jsx';
import './App.css';

export default function App() {
    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const navigate = useNavigate();

    useEffect(() => {
        let active = true;

        api.get('/books')
            .then(response => {
                if (active) {
                    setBooks(response.data);
                }
            })
            .catch(err => {
                if (active) {
                    setError(err.message);
                }
            })
            .finally(() => {
                if (active) {
                    setLoading(false);
                }
            });

        return () => {
            active = false;
        };
    }, []);

    function handleBookAdded(savedBook) {
        setBooks(previous => [...previous, savedBook]);
        navigate('/inventory');
    }

    async function handleDeleteBook(id) {
        if (!window.confirm('Delete this book?')) {
            return;
        }

        setError('');

        try {
            await api.delete(`/books/${id}`);

            setBooks(previous =>
                previous.filter(book => book.id !== id)
            );
        } catch (err) {
            setError(err.message);
        }
    }

    async function handleUpdateBook(id, data) {
        const response = await api.put(`/books/${id}`, data);
        const savedBook = response.data;

        setBooks(previous =>
            previous.map(book =>
                book.id === id ? savedBook : book
            )
        );
    }

    return (
        <div className="container">
            <header>
                <h1>My Bookstore Frontend</h1>
                <p>Book and Magazine Management</p>
            </header>

            <NavBar />

            <main>
                <Routes>
                    <Route path="/" element={<Home />} />

                    <Route
                        path="/inventory"
                        element={
                            <BookList
                                books={books}
                                loading={loading}
                                error={error}
                                onDelete={handleDeleteBook}
                                onUpdate={handleUpdateBook}
                            />
                        }
                    />

                    <Route
                        path="/add"
                        element={
                            <AddBookForm
                                onBookAdded={handleBookAdded}
                            />
                        }
                    />

                    <Route
                        path="/magazines"
                        element={<MagazineList />}
                    />

                    <Route
                        path="*"
                        element={<h2>Page not found</h2>}
                    />
                </Routes>
            </main>
        </div>
    );
}