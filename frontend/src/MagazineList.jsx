import api from './api.js';
import { useState, useEffect } from 'react';
import Magazine from './Magazine.jsx';
import MagazineForm from './MagazineForm.jsx';
import useAuth from './useAuth.js';

export default function MagazineList({
                                         onAddToCart,
                                         cartPending,
                                         canManage
                                     }) {
    const { token } = useAuth();

    const [magazines, setMagazines] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    function isCurrent() {
        return localStorage.getItem('token') === token;
    }

    useEffect(() => {
        let active = true;

        api.get('/magazines')
            .then(response => {
                if (
                    active &&
                    localStorage.getItem('token') === token
                ) {
                    setMagazines(response.data);
                }
            })
            .catch(err => {
                if (
                    active &&
                    localStorage.getItem('token') === token
                ) {
                    setError(
                    err.response?.status === 403
                        ? 'Administrator permission is required for this action.'
                        : err.message
                );
                }
            })
            .finally(() => {
                if (
                    active &&
                    localStorage.getItem('token') === token
                ) {
                    setLoading(false);
                }
            });

        return () => {
            active = false;
        };
    }, [token]);

    function handleMagazineAdded(saved) {
        if (isCurrent()) {
            setMagazines(previous => [...previous, saved]);
        }
    }

    async function handleDeleteMagazine(id) {
        if (!isCurrent()) {
            return;
        }

        if (!window.confirm('Delete this magazine?')) {
            return;
        }

        setError('');

        try {
            await api.delete(`/magazines/${id}`);

            if (isCurrent()) {
                setMagazines(previous =>
                    previous.filter(magazine =>
                        magazine.id !== id
                    )
                );
            }
        } catch (err) {
            if (isCurrent()) {
                setError(
                    err.response?.status === 403
                        ? 'Administrator permission is required for this action.'
                        : err.message
                );
            }
        }
    }

    async function handleUpdateMagazine(id, data) {
        if (!isCurrent()) {
            return;
        }

        const response = await api.put(
            `/magazines/${id}`, data
        );

        if (isCurrent()) {
            setMagazines(previous =>
                previous.map(magazine =>
                    magazine.id === id
                        ? response.data
                        : magazine
                )
            );
        }
    }

    return (
        <section>
            <h2>Magazine Inventory</h2>

            {canManage && (
                <MagazineForm
                    onMagazineAdded={handleMagazineAdded}
                />
            )}

            {loading && <p>Loading magazines...</p>}

            {error && <p className="error">{error}</p>}

            {!loading && !error && magazines.length === 0 && (
                <p>No magazines available.</p>
            )}

            {magazines.map(magazine => (
                <Magazine
                    key={magazine.id}
                    {...magazine}
                    onUpdate={handleUpdateMagazine}
                    onDelete={handleDeleteMagazine}
                    onAddToCart={onAddToCart}
                    cartPending={cartPending}
                    canManage={canManage}
                />
            ))}
        </section>
    );
}