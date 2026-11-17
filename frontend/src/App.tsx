
import { useEffect, useState, type FormEvent } from 'react'
import './App.css'

type Magazine = {
  id: number
  title: string
  price: number
  copies: number
  orderQty: number
  currentIssue: string
}

const API_URL = '/api/rest/magazines'

function App() {
  const [magazines, setMagazines] = useState<Magazine[]>([])
  const [editingId, setEditingId] = useState<number | null>(null)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  const [title, setTitle] = useState('')
  const [price, setPrice] = useState('')
  const [copies, setCopies] = useState('')
  const [orderQty, setOrderQty] = useState('')
  const [currentIssue, setCurrentIssue] = useState('')

  async function loadMagazines() {
    try {
      const response = await fetch(API_URL)

      if (!response.ok) {
        throw new Error('Failed to load magazines')
      }

      const data: Magazine[] = await response.json()
      setMagazines(data)
      setError('')
    } catch {
      setError('Could not load magazines. Please check the backend.')
    }
  }

  useEffect(() => {
    let cancelled = false

    async function fetchMagazines() {
      try {
        const response = await fetch(API_URL)

        if (!response.ok) {
          throw new Error('Failed to load magazines')
        }

        const data: Magazine[] = await response.json()

        if (!cancelled) {
          setMagazines(data)
          setError('')
        }
      } catch {
        if (!cancelled) {
          setError('Could not load magazines. Please check the backend.')
        }
      }
    }

    void fetchMagazines()

    return () => {
      cancelled = true
    }
  }, [])

  function clearForm() {
    setTitle('')
    setPrice('')
    setCopies('')
    setOrderQty('')
    setCurrentIssue('')
    setEditingId(null)
  }

  async function saveMagazine(e: FormEvent<HTMLFormElement>) {
    e.preventDefault()

    const magazine = {
      title,
      price: Number(price),
      copies: Number(copies),
      orderQty: Number(orderQty),
      currentIssue: currentIssue + ':00'
    }

    const isEditing = editingId !== null

    const url = isEditing
        ? `${API_URL}/${editingId}`
        : API_URL

    try {
      const response = await fetch(url, {
        method: isEditing ? 'PUT' : 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(magazine)
      })

      if (!response.ok) {
        throw new Error('Failed to save magazine')
      }

      setMessage(
          isEditing
              ? 'Magazine updated successfully!'
              : 'Magazine added successfully!'
      )

      setError('')
      clearForm()
      await loadMagazines()
    } catch {
      setError('Could not save magazine.')
      setMessage('')
    }
  }

  function editMagazine(magazine: Magazine) {
    setEditingId(magazine.id)
    setTitle(magazine.title)
    setPrice(String(magazine.price))
    setCopies(String(magazine.copies))
    setOrderQty(String(magazine.orderQty))
    setCurrentIssue(magazine.currentIssue?.slice(0, 16) || '')
    setMessage('')
    setError('')

    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    })
  }

  async function deleteMagazine(id: number) {
    const confirmed = window.confirm(
        'Are you sure you want to delete this magazine?'
    )

    if (!confirmed) return

    try {
      const response = await fetch(`${API_URL}/${id}`, {
        method: 'DELETE'
      })

      if (!response.ok) {
        throw new Error('Failed to delete magazine')
      }

      setMessage('Magazine deleted successfully!')
      setError('')
      await loadMagazines()
    } catch {
      setError('Could not delete magazine.')
      setMessage('')
    }
  }

  return (
      <div className="container">
        <header>
          <h1>Bookstore Management</h1>
          <p>Lab 5 - React Magazine CRUD</p>
        </header>

        <main>
          <h2>Magazine Management</h2>

          {message && (
              <p className="message">{message}</p>
          )}

          {error && (
              <p className="error">{error}</p>
          )}

          <section className="form-section">
            <h2>
              {editingId === null
                  ? 'Add New Magazine'
                  : 'Edit Magazine'}
            </h2>

            <form onSubmit={saveMagazine}>
              <label htmlFor="title">Magazine Title</label>
              <input
                  id="title"
                  type="text"
                  placeholder="Enter magazine title"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  required
              />

              <label htmlFor="price">Price</label>
              <input
                  id="price"
                  type="number"
                  placeholder="Enter price"
                  value={price}
                  onChange={(e) => setPrice(e.target.value)}
                  min="0"
                  step="0.01"
                  required
              />

              <label htmlFor="copies">Copies</label>
              <input
                  id="copies"
                  type="number"
                  placeholder="Enter number of copies"
                  value={copies}
                  onChange={(e) => setCopies(e.target.value)}
                  min="0"
                  step="1"
                  required
              />

              <label htmlFor="orderQty">Order Quantity</label>
              <input
                  id="orderQty"
                  type="number"
                  placeholder="Enter order quantity"
                  value={orderQty}
                  onChange={(e) => setOrderQty(e.target.value)}
                  min="0"
                  step="1"
                  required
              />

              <label htmlFor="currentIssue">Current Issue</label>
              <input
                  id="currentIssue"
                  type="datetime-local"
                  value={currentIssue}
                  onChange={(e) => setCurrentIssue(e.target.value)}
                  required
              />

              <div className="form-buttons">
                <button type="submit">
                  {editingId === null
                      ? 'Add Magazine'
                      : 'Update Magazine'}
                </button>

                {editingId !== null && (
                    <button
                        type="button"
                        onClick={clearForm}
                    >
                      Cancel
                    </button>
                )}
              </div>
            </form>
          </section>

          <section className="list-section">
            <h2>Magazine List</h2>

            <button
                type="button"
                onClick={() => void loadMagazines()}
            >
              Refresh Magazines
            </button>

            <table>
              <thead>
              <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Price</th>
                <th>Copies</th>
                <th>Order Qty</th>
                <th>Current Issue</th>
                <th>Actions</th>
              </tr>
              </thead>

              <tbody>
              {magazines.length === 0 ? (
                  <tr>
                    <td colSpan={7}>
                      No magazines available.
                    </td>
                  </tr>
              ) : (
                  magazines.map((magazine) => (
                      <tr key={magazine.id}>
                        <td>{magazine.id}</td>
                        <td>{magazine.title}</td>
                        <td>${magazine.price.toFixed(2)}</td>
                        <td>{magazine.copies}</td>
                        <td>{magazine.orderQty}</td>
                        <td>
                          {magazine.currentIssue
                              ? magazine.currentIssue.replace('T', ' ')
                              : 'N/A'}
                        </td>
                        <td>
                          <button
                              type="button"
                              onClick={() => editMagazine(magazine)}
                          >
                            Edit
                          </button>

                          <button
                              type="button"
                              onClick={() => void deleteMagazine(magazine.id)}
                          >
                            Delete
                          </button>
                        </td>
                      </tr>
                  ))
              )}
              </tbody>
            </table>
          </section>
        </main>
      </div>
  )
}

export default App
