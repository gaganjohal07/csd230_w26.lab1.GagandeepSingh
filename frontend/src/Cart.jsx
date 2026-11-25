export default function Cart({
                                 cart,
                                 loading,
                                 error,
                                 pending,
                                 onRemove
                             }) {
    const products = cart?.products || [];

    const total = products.reduce(
        (sum, product) => sum + Number(product.price), 0
    );

    return (
        <section className="list-section">
            <h2>My Shopping Cart</h2>

            {loading && <p>Loading cart...</p>}

            {error && <p className="error">{error}</p>}

            {!loading && !error && products.length === 0 && (
                <p>Your cart is empty.</p>
            )}

            {products.map(product => (
                <article
                    key={product.id}
                    className="form-section"
                >
                    <h2>{product.title}</h2>
                    <p>Price: ${product.price}</p>

                    <button
                        disabled={loading || pending}
                        onClick={() => onRemove(product.id)}
                    >
                        Remove from Cart
                    </button>
                </article>
            ))}

            {!loading && (
                <h3>Total: ${total.toFixed(2)}</h3>
            )}
        </section>
    );
}