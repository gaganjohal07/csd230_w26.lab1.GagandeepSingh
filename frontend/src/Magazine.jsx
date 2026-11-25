import { useState } from 'react';
import useAuth from './useAuth.js';

export default function Magazine({
                                     id,
                                     title,
                                     price,
                                     copies,
                                     orderQty,
                                     currentIssue,
                                     onUpdate,
                                     onDelete,
                                     onAddToCart,
                                     cartPending
                                 }) {
    const { token } = useAuth();

    const [editing, setEditing] = useState(false);
    const [draft, setDraft] = useState({});
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');

    function isCurrent() {
        return localStorage.getItem('token') === token;
    }

    function beginEdit() {
        setDraft({
            title,
            price,
            copies,
            orderQty,
            currentIssue
        });

        setError('');
        setEditing(true);
    }

    function cancelEdit() {
        setEditing(false);
        setError('');
    }

    async function handleSave(event) {
        event.preventDefault();

        if (!isCurrent()) {
            return;
        }

        setSaving(true);
        setError('');

        try {
            await onUpdate(id, {
                ...draft,
                price: Number(draft.price),
                copies: Number(draft.copies),
                orderQty: Number(draft.orderQty)
            });

            if (isCurrent()) {
                setEditing(false);
            }
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

    if (editing) {
        return (
            <article className="form-section">
                <form onSubmit={handleSave}>
                    <label>Title</label>
                    <input
                        value={draft.title}
                        onChange={e => setDraft({
                            ...draft,
                            title: e.target.value
                        })}
                        required
                    />

                    <label>Price</label>
                    <input
                        type="number"
                        min="0"
                        step="0.01"
                        value={draft.price}
                        onChange={e => setDraft({
                            ...draft,
                            price: e.target.value
                        })}
                        required
                    />

                    <label>Copies</label>
                    <input
                        type="number"
                        min="0"
                        value={draft.copies}
                        onChange={e => setDraft({
                            ...draft,
                            copies: e.target.value
                        })}
                        required
                    />

                    <label>Order Quantity</label>
                    <input
                        type="number"
                        min="0"
                        value={draft.orderQty}
                        onChange={e => setDraft({
                            ...draft,
                            orderQty: e.target.value
                        })}
                        required
                    />

                    <label>Current Issue</label>
                    <input
                        type="datetime-local"
                        step="1"
                        value={draft.currentIssue}
                        onChange={e => setDraft({
                            ...draft,
                            currentIssue: e.target.value
                        })}
                        required
                    />

                    <div className="form-buttons">
                        <button disabled={saving}>
                            {saving ? 'Saving...' : 'Save'}
                        </button>

                        <button
                            type="button"
                            disabled={saving}
                            onClick={cancelEdit}
                        >
                            Cancel
                        </button>
                    </div>

                    {error && (
                        <p className="error">{error}</p>
                    )}
                </form>
            </article>
        );
    }

    return (
        <article className="form-section">
            <h2>{title}</h2>
            <p>ID: {id}</p>
            <p>Price: ${price}</p>
            <p>Copies: {copies}</p>
            <p>Order Quantity: {orderQty}</p>
            <p>Current Issue: {currentIssue}</p>

            <button
                disabled={cartPending}
                onClick={() => onAddToCart(id)}
            >
                Add to Cart
            </button>

            <button onClick={beginEdit}>
                Edit
            </button>

            <button onClick={() => onDelete(id)}>
                Delete
            </button>
        </article>
    );
}