import api from './api.js';
import { useState, useEffect, useRef } from 'react';
import { Routes, Route, useNavigate } from 'react-router';
import NavBar from './NavBar.jsx';
import Home from './Home.jsx';
import BookList from './BookList.jsx';
import AddBookForm from './AddBookForm.jsx';
import MagazineList from './MagazineList.jsx';
import AuthProvider from './AuthProvider.jsx';
import useAuth from './useAuth.js';
import Login from './Login.jsx';
import ProtectedRoute from './ProtectedRoute.jsx';
import Cart from './Cart.jsx';
import './App.css';

export default function App() {
    return (
        <AuthProvider>
            <Routes>
                <Route path="/login" element={<Login />} />

                <Route element={<ProtectedRoute />}>
                    <Route path="/*" element={<SignedInBookApp />} />
                </Route>
            </Routes>
        </AuthProvider>
    );
}

function SignedInBookApp() {
    const { token } = useAuth();

    return <BookApp key={token} />;
}

function BookApp() {
    const { token, isAdmin } = useAuth();

    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const [cart, setCart] = useState(null);
    const [cartLoading, setCartLoading] = useState(true);
    const [cartPending, setCartPending] = useState(false);
    const [cartError, setCartError] = useState('');

    const cartBusy = useRef(false);
    const navigate = useNavigate();

    function isCurrent() {
        return localStorage.getItem('token') === token;
    }

    useEffect(() => {
        let active = true;

        api.get('/books')
            .then(response => {
                if (active && localStorage.getItem('token') === token) {
                    setBooks(response.data);
                }
            })
            .catch(err => {
                if (active && localStorage.getItem('token') === token) {
                    setError(err.message);
                }
            })
            .finally(() => {
                if (active && localStorage.getItem('token') === token) {
                    setLoading(false);
                }
            });

        return () => {
            active = false;
        };
    }, [token]);

    useEffect(() => {
        let active = true;

        api.get('/carts/mine')
            .then(response => {
                if (active && localStorage.getItem('token') === token) {
                    setCart(
                        response.status === 204 ? null : response.data
                    );
                }
            })
            .catch(err => {
                if (active && localStorage.getItem('token') === token) {
                    setCartError(err.message);
                }
            })
            .finally(() => {
                if (active && localStorage.getItem('token') === token) {
                    setCartLoading(false);
                }
            });

        return () => {
            active = false;
        };
    }, [token]);

    async function changeCart(productId, remove = false) {
        if (!isCurrent() || cartLoading || cartBusy.current) {
            return;
        }

        cartBusy.current = true;
        setCartPending(true);
        setCartError('');

        try {
            const path = `/carts/mine/products/${productId}`;

            const response = remove
                ? await api.delete(path)
                : await api.post(path);

            if (isCurrent()) {
                setCart(
                    response.status === 204 ? null : response.data
                );
            }
        } catch (err) {
            if (isCurrent()) {
                setCartError(err.message);
            }
        } finally {
            cartBusy.current = false;

            if (isCurrent()) {
                setCartPending(false);
            }
        }
    }

    function handleBookAdded(savedBook) {
        if (!isCurrent()) {
            return;
        }

        setBooks(previous => [...previous, savedBook]);
        navigate('/inventory');
    }

    async function handleDeleteBook(id) {
        if (!isCurrent()) {
            return;
        }

        if (!window.confirm('Delete this book?')) {
            return;
        }

        setError('');

        try {
            await api.delete(`/books/${id}`);

            if (isCurrent()) {
                setBooks(previous =>
                    previous.filter(book => book.id !== id)
                );
            }
        } catch (err) {
            if (isCurrent()) {
                setError(err.message);
            }
        }
    }

    async function handleUpdateBook(id, data) {
        if (!isCurrent()) {
            return;
        }

        const response = await api.put(`/books/${id}`, data);

        if (!isCurrent()) {
            return;
        }

        setBooks(previous =>
            previous.map(book =>
                book.id === id ? response.data : book
            )
        );
    }

    return (
        <div className="container">
            <header>
                <h1>My Bookstore Frontend</h1>
                <p>Book and Magazine Management</p>
                <p>Cart Items: {cart?.products?.length || 0}</p>
            </header>

            <NavBar />

            <main>
                {cartError && (
                    <p className="error" role="alert">
                        {cartError}
                    </p>
                )}

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
                                canManage={isAdmin}
                                onAddToCart={changeCart}
                                cartPending={cartLoading || cartPending}
                            />
                        }
                    />

                    <Route
                        path="/add"
                        element={
                            isAdmin ? (
                                <AddBookForm
                                    onBookAdded={handleBookAdded}
                                />
                            ) : (
                                <p role="alert">
                                    Administrator access is required.
                                </p>
                            )
                        }
                    />

                    <Route
                        path="/magazines"
                        element={
                            <MagazineList
                                canManage={isAdmin}
                                onAddToCart={changeCart}
                                cartPending={cartLoading || cartPending}
                            />
                        }
                    />

                    <Route
                        path="/cart"
                        element={
                            <Cart
                                cart={cart}
                                loading={cartLoading}
                                error={cartError}
                                pending={cartPending}
                                onRemove={id => changeCart(id, true)}
                            />
                        }
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