import { useState } from 'react';

export default function Book({ book, onDelete, onUpdate }) {
    const [editing, setEditing] = useState(false);
    const [draft, setDraft] = useState(book);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState('');

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
        setSaving(true);
        setError('');

        try {
            await onUpdate(book.id, {
                ...draft,
                price: Number(draft.price),
                copies: Number(draft.copies)
            });

            setEditing(false);
        } catch (err) {
            setError(err.message);
        } finally {
            setSaving(false);
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
                            ...draft, title: e.target.value
                        })}
                        required
                    />

                    <label>Author</label>
                    <input
                        value={draft.author}
                        onChange={e => setDraft({
                            ...draft, author: e.target.value
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
                            ...draft, price: e.target.value
                        })}
                        required
                    />

                    <label>Copies</label>
                    <input
                        type="number"
                        min="0"
                        value={draft.copies}
                        onChange={e => setDraft({
                            ...draft, copies: e.target.value
                        })}
                        required
                    />

                    <div className="form-buttons">
                        <button disabled={saving}>Save</button>

                        <button
                            type="button"
                            disabled={saving}
                            onClick={cancelEdit}
                        >
                            Cancel
                        </button>
                    </div>

                    {error && <p role="alert">{error}</p>}
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

            <button onClick={beginEdit}>Edit</button>
            <button onClick={() => onDelete(book.id)}>
                Delete
            </button>
        </article>
    );
}