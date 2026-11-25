import Book from './Book.jsx';

export default function BookList({
                                     books,
                                     loading,
                                     error,
                                     onDelete,
                                     onUpdate,
                                     onAddToCart,
                                     cartPending,
                                     canManage
                                 }) {
    return (
        <section className="list-section">
            <h2>Book Inventory</h2>

            {loading && <p>Loading books...</p>}

            {error && <p className="error">{error}</p>}

            {!loading && !error && books.length === 0 && (
                <p>No books available.</p>
            )}

            {books.map(book => (
                <Book
                    key={book.id}
                    book={book}
                    onDelete={onDelete}
                    onUpdate={onUpdate}
                    onAddToCart={onAddToCart}
                    cartPending={cartPending}
                    canManage={canManage}
                />
            ))}
        </section>
    );
}