import { useState } from 'react';
import useAuth from './useAuth.js';

export default function Book({
                                 book,
                                 onDelete,
                                 onUpdate,
                                 onAddToCart,
                                 cartPending,
                                 canManage
                             }) {
    const { token } = useAuth();

    const [editing, setEditing] = useState(false);
    const [draft, setDraft] = useState(book);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');

    function isCurrent() {
        return localStorage.getItem('token') === token;
    }

    function beginEdit() {
        setDraft({ ...book });
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
            await onUpdate(book.id, {
                ...draft,
                price: Number(draft.price),
                copies: Number(draft.copies)
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

    if (editing && canManage) {
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

                    <label>Author</label>
                    <input
                        value={draft.author}
                        onChange={e => setDraft({
                            ...draft,
                            author: e.target.value
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
            <h2>{book.title}</h2>
            <p>Author: {book.author}</p>
            <p>ISBN: {book.isbn}</p>
            <p>Price: ${book.price}</p>
            <p>Copies: {book.copies}</p>

            <button
                disabled={cartPending}
                onClick={() => onAddToCart(book.id)}
            >
                Add to Cart
            </button>

            {canManage && (
                <>
                    <button onClick={beginEdit}>
                        Edit
                    </button>

                    <button onClick={() => onDelete(book.id)}>
                        Delete
                    </button>
                </>
            )}
        </article>
    );
}