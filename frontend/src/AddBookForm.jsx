import api from './api.js';
import { useState } from 'react';

export default function AddBookForm({ onBookAdded }) {
    const [title, setTitle] = useState('');
    const [author, setAuthor] = useState('');
    const [isbn, setIsbn] = useState('');
    const [price, setPrice] = useState('');
    const [copies, setCopies] = useState(1);
    const [error, setError] = useState('');
    const [saving, setSaving] = useState(false);

    async function handleSubmit(event) {
        event.preventDefault();
        setError('');
        setSaving(true);

        try {
            const response = await api.post('/books', {
                title,
                author,
                isbn,
                price: Number(price),
                copies: Number(copies)
            });

            const savedBook = response.data;

            onBookAdded(savedBook);

            setTitle('');
            setAuthor('');
            setIsbn('');
            setPrice('');
            setCopies(1);
        } catch (err) {
            setError(err.message);
        } finally {
            setSaving(false);
        }
    }

    return (
        <section className="form-section">
            <h2>Add Book</h2>

            <form onSubmit={handleSubmit}>
                <label>Title</label>
                <input
                    value={title}
                    onChange={e => setTitle(e.target.value)}
                    required
                />

                <label>Author</label>
                <input
                    value={author}
                    onChange={e => setAuthor(e.target.value)}
                    required
                />

                <label>ISBN</label>
                <input
                    value={isbn}
                    onChange={e => setIsbn(e.target.value)}
                    required
                />

                <label>Price</label>
                <input
                    type="number"
                    min="0"
                    step="0.01"
                    value={price}
                    onChange={e => setPrice(e.target.value)}
                    required
                />

                <label>Copies</label>
                <input
                    type="number"
                    min="0"
                    value={copies}
                    onChange={e => setCopies(e.target.value)}
                    required
                />

                <div className="form-buttons">
                    <button disabled={saving}>
                        {saving ? 'Saving...' : 'Add Book'}
                    </button>
                </div>

                {error && <p className="error">{error}</p>}
            </form>
        </section>
    );
}