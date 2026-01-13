import React, { useEffect, useState } from "react";
import { BookOpen, Pencil, Trash2, Plus } from "lucide-react";
import { useNavigate } from "react-router-dom";
import SearchBar from "../../../components/admin/SearchBar";

import { getDashboardBooks } from "../../../api/adminApi/dashboardApi";
import { deleteBook } from "../../../api/adminApi/manageBooksApi";
import { toast } from "react-hot-toast";

export default function ManageBooks() {
    const navigate = useNavigate();

    const [books, setBooks] = useState([]);
    const [filteredBooks, setFilteredBooks] = useState([]);
    const [query, setQuery] = useState("");

    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [selectedBook, setSelectedBook] = useState(null);

    // 🔹 1. Lấy toàn bộ sách khi load trang
    useEffect(() => {
        const fetchBooks = async () => {
            try {
                const data = await getDashboardBooks(); // Không truyền status → lấy tất cả
                setBooks(data);
                setFilteredBooks(data);
            } catch (err) {
                console.error("Failed to load books:", err);
            }
        };

        fetchBooks();
    }, []);

    // 🔹 2. Filter theo search
    useEffect(() => {
        const q = query.toLowerCase();

        const result = books.filter(
            (b) =>
                b.title.toLowerCase().includes(q) ||
                b.author.toLowerCase().includes(q)
        );

        setFilteredBooks(result);
    }, [query, books]);

    const handleDeleteBook = async () => {
        try {
            await deleteBook(selectedBook.bookId);

            // Xóa khỏi UI
            setBooks((prev) =>
                prev.filter((b) => b.bookId !== selectedBook.bookId)
            );
            setFilteredBooks((prev) =>
                prev.filter((b) => b.bookId !== selectedBook.bookId)
            );

            // Tắt modal
            setShowDeleteModal(false);

            // Toast thành công
            toast.success("Book deleted successfully!");
        } catch (err) {
            console.error(err);
            toast.error("Failed to delete book");
        }
    };

    return (
        <>
            <div className="p-6 text-white min-h-screen bg-black">

                {/* HEADER */}
                <div className="flex items-center justify-between mb-8">
                    <h1 className="text-3xl font-bold">📘 Manage Books</h1>

                    <button
                        onClick={() => navigate("/manage-books/add")}
                        className="flex items-center gap-2 bg-blue-600 hover:bg-blue-500 px-4 py-2 rounded-xl transition"
                    >
                        <Plus size={18} />
                        Add Book
                    </button>
                </div>

                {/* SEARCH BAR */}
                <SearchBar
                    placeholder="Search books by title or author..."
                    value={query}
                    onChange={setQuery}
                />

                {/* TABLE */}
                <div className="bg-zinc-900 p-6 rounded-2xl border border-zinc-800 shadow-xl overflow-x-auto">
                    <table className="w-full text-left border-collapse">
                        <thead className="bg-zinc-800/60">
                            <tr>
                                <th className="p-4 w-[110px]">Image</th>
                                <th className="p-4 w-[25%] text-left">Title</th>
                                <th className="p-4 w-[20%] text-left">Author</th>
                                <th className="p-4 w-[10%] text-center">Quantity</th>
                                <th className="p-4 w-[12%] text-center">Status</th>
                                <th className="p-4 w-[15%] text-center">Actions</th>
                            </tr>
                        </thead>

                        <tbody className="divide-y divide-zinc-800">
                            {filteredBooks.length === 0 && (
                                <tr>
                                    <td colSpan="6" className="text-center py-6 text-zinc-400">
                                        No books found
                                    </td>
                                </tr>
                            )}

                            {filteredBooks.map((book) => (
                                <tr key={book.bookId} className="hover:bg-zinc-800/40">

                                    {/* IMAGE */}
                                    <td className="p-4">
                                        <img
                                            src={book.imageUrl}
                                            alt={book.title}
                                            className="w-12 h-16 rounded-md border border-zinc-700 object-cover"
                                        />
                                    </td>

                                    {/* TITLE */}
                                    <td className="p-4 font-semibold">{book.title}</td>

                                    {/* AUTHOR */}
                                    <td className="p-4 text-zinc-300">{book.author}</td>

                                    {/* QUANTITY */}
                                    <td className="p-4 text-center">{book.totalCopies}</td>

                                    {/* STATUS */}
                                    <td className="p-4 text-center">
                                        {book.status === "AVAILABLE" ? (
                                            <span className="px-3 py-1 bg-green-600/20 text-green-400 rounded-lg">
                                                Available
                                            </span>
                                        ) : (
                                            <span className="px-3 py-1 bg-orange-600/20 text-orange-400 rounded-lg">
                                                Borrowed
                                            </span>
                                        )}
                                    </td>

                                    {/* ACTIONS */}
                                    <td className="p-4 text-center align-middle">
                                        <div className="flex items-center justify-center gap-4">
                                            <button
                                                className="text-blue-400 hover:text-blue-300"
                                                onClick={() => navigate("/manage-books/detail", { state: { book } })}
                                            >
                                                <BookOpen size={18} />
                                            </button>

                                            <button
                                                className="text-yellow-400 hover:text-yellow-300"
                                                onClick={() => navigate("/manage-books/update", { state: { book } })}
                                            >
                                                <Pencil size={18} />
                                            </button>

                                            <button
                                                className={`p-2 rounded-xl transition ${book.status === "AVAILABLE"
                                                        ? "bg-red-500 hover:bg-red-600"
                                                        : "bg-gray-600 cursor-not-allowed"
                                                    }`}
                                                disabled={book.status !== "AVAILABLE"}
                                                onClick={() => {
                                                    if (book.status === "AVAILABLE") {
                                                        setSelectedBook(book);
                                                        setShowDeleteModal(true);
                                                    }
                                                }}
                                            >
                                                <Trash2 className="w-5 h-5 text-white" />
                                            </button>
                                        </div>
                                    </td>


                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

            </div>

            {showDeleteModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-zinc-900 p-6 rounded-2xl w-[400px] border border-zinc-700">
                        <h2 className="text-xl font-semibold text-red-400 mb-3">
                            Xác nhận xoá sách
                        </h2>

                        <p className="text-zinc-300 mb-4">
                            Bạn có chắc muốn xoá <span className="font-bold">{selectedBook.title}</span>?
                        </p>

                        <div className="flex justify-end gap-3">
                            <button
                                className="px-4 py-2 rounded-xl bg-zinc-700 hover:bg-zinc-600 text-white"
                                onClick={() => setShowDeleteModal(false)}
                            >
                                Hủy
                            </button>

                            <button
                                className="px-4 py-2 rounded-xl bg-red-500 hover:bg-red-600 text-white"
                                onClick={handleDeleteBook}
                            >
                                Xoá
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </>
    );
}
