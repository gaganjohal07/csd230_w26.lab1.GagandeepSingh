import api from './api.js';
import { useState, useEffect } from 'react';
import Magazine from './Magazine.jsx';
import MagazineForm from './MagazineForm.jsx';

export default function MagazineList() {
    const [magazines, setMagazines] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        let active = true;

        api.get('/magazines')
            .then(response => {
                if (active) {
                    setMagazines(response.data);
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

    function handleMagazineAdded(saved) {
        setMagazines(previous => [...previous, saved]);
    }

    async function handleDeleteMagazine(id) {
        if (!window.confirm('Delete this magazine?')) {
            return;
        }

        setError('');

        try {
            await api.delete(`/magazines/${id}`);

            setMagazines(previous =>
                previous.filter(magazine => magazine.id !== id)
            );
        } catch (err) {
            setError(err.message);
        }
    }

    async function handleUpdateMagazine(id, data) {
        const response = await api.put(`/magazines/${id}`, data);
        const saved = response.data;

        setMagazines(previous =>
            previous.map(magazine =>
                magazine.id === id ? saved : magazine
            )
        );
    }

    return (
        <section>
            <h2>Magazine Inventory</h2>

            <MagazineForm
                onMagazineAdded={handleMagazineAdded}
            />

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
                />
            ))}
        </section>
    );
}