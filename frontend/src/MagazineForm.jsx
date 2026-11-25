import api from './api.js';
import { useState } from 'react';
import useAuth from './useAuth.js';

export default function MagazineForm({ onMagazineAdded }) {
    const { token } = useAuth();

    const [title, setTitle] = useState('');
    const [price, setPrice] = useState('');
    const [copies, setCopies] = useState(1);
    const [orderQty, setOrderQty] = useState(1);
    const [currentIssue, setCurrentIssue] = useState('');
    const [error, setError] = useState('');
    const [saving, setSaving] = useState(false);

    function isCurrent() {
        return localStorage.getItem('token') === token;
    }

    async function handleSubmit(event) {
        event.preventDefault();

        if (!isCurrent() || saving) {
            return;
        }

        setError('');
        setSaving(true);

        try {
            const response = await api.post('/magazines', {
                title,
                price: Number(price),
                copies: Number(copies),
                orderQty: Number(orderQty),
                currentIssue
            });

            if (!isCurrent()) {
                return;
            }

            setTitle('');
            setPrice('');
            setCopies(1);
            setOrderQty(1);
            setCurrentIssue('');

            onMagazineAdded(response.data);
        } catch (err) {
            if (isCurrent()) {
                setError(err.message);
            }
        } finally {
            if (isCurrent()) {
                setSaving(false);
            }
        }
    }

    return (
        <section className="form-section">
            <h2>Add Magazine</h2>

            <form onSubmit={handleSubmit}>
                <label>Title</label>
                <input
                    value={title}
                    onChange={e => setTitle(e.target.value)}
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

                <label>Order Quantity</label>
                <input
                    type="number"
                    min="0"
                    value={orderQty}
                    onChange={e => setOrderQty(e.target.value)}
                    required
                />

                <label>Current Issue</label>
                <input
                    type="datetime-local"
                    step="1"
                    value={currentIssue}
                    onChange={e => setCurrentIssue(e.target.value)}
                    required
                />

                <div className="form-buttons">
                    <button disabled={saving}>
                        {saving ? 'Saving...' : 'Add Magazine'}
                    </button>
                </div>

                {error && (
                    <p className="error" role="alert">
                        {error}
                    </p>
                )}
            </form>
        </section>
    );
}