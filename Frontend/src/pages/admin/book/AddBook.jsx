import React, { useEffect, useState } from "react";
import { addBook } from "../../../api/adminApi/manageBooksApi";
import { getAllCategories, getAllPublishers } from "../../../api/adminApi/manageBooksApi";
import { useNavigate } from "react-router-dom";
import { FileText, Image } from "lucide-react";
import toast from "react-hot-toast";

export default function AddBook() {
    const navigate = useNavigate();

    // FORM STATE
    const [title, setTitle] = useState("");
    const [author, setAuthor] = useState("");
    const [categoryName, setCategoryName] = useState("");
    const [publisherName, setPublisherName] = useState("");
    const [imageFile, setImageFile] = useState(null);
    const [pdfFile, setPdfFile] = useState(null);

    const [categories, setCategories] = useState([]);
    const [publishers, setPublishers] = useState([]);

    // LOAD CATEGORY + PUBLISHER
    useEffect(() => {
        fetchDropdownData();
    }, []);

    const fetchDropdownData = async () => {
        try {
            const cateRes = await getAllCategories();
            const pubRes = await getAllPublishers();

            setCategories(cateRes.data);
            setPublishers(pubRes.data);
        } catch (err) {
            console.error("Failed to load dropdowns:", err);
        }
    };

    // SUBMIT FORM
    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!imageFile) {
            alert("Please upload a book image.");
            return;
        }

        // JSON bạn gửi vào bookData
        const bookData = {
            title,
            author,
            categoryName,
            publisherName
        };

        const formData = new FormData();
        formData.append("bookData", JSON.stringify(bookData)); // <-- QUAN TRỌNG
        formData.append("imageFile", imageFile);
        if (pdfFile) formData.append("pdfFile", pdfFile);

        try {
            const res = await addBook(formData);
            toast.success("Book added successfully!", {
                duration: 3000,
            });
        } catch (err) {
            console.error(err);
            alert("Failed to add book!");
        }
    };


    return (
        <div className="p-6 text-white min-h-screen bg-black">
            <h1 className="text-3xl font-bold mb-6">📘 Add New Book</h1>

            <form
                onSubmit={handleSubmit}
                className="bg-zinc-900 p-8 rounded-2xl border border-zinc-800 shadow-xl grid grid-cols-2 gap-10"
            >

                {/* LEFT SIDE */}
                <div className="space-y-6">
                    {/* TITLE */}
                    <div>
                        <label className="block mb-2 text-zinc-300">Book Title</label>
                        <input
                            type="text"
                            className="w-full p-3 rounded-xl bg-zinc-800 border border-zinc-700 outline-none"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            required
                        />
                    </div>

                    {/* AUTHOR */}
                    <div>
                        <label className="block mb-2 text-zinc-300">Author</label>
                        <input
                            type="text"
                            className="w-full p-3 rounded-xl bg-zinc-800 border border-zinc-700 outline-none"
                            value={author}
                            onChange={(e) => setAuthor(e.target.value)}
                            required
                        />
                    </div>

                    {/* CATEGORY */}
                    <div>
                        <label className="block mb-2 text-zinc-300">Category</label>
                        <select
                            className="w-full p-3 bg-zinc-800 rounded-xl border border-zinc-700 outline-none"
                            value={categoryName}
                            onChange={(e) => setCategoryName(e.target.value)}
                            required
                        >
                            <option value="">Select category</option>
                            {categories.map((c) => (
                                <option key={c.categoryId} value={c.categoryName}>
                                    {c.categoryName}
                                </option>
                            ))}
                        </select>
                    </div>

                    {/* PUBLISHER */}
                    <div>
                        <label className="block mb-2 text-zinc-300">Publisher</label>
                        <select
                            className="w-full p-3 bg-zinc-800 rounded-xl border border-zinc-700 outline-none"
                            value={publisherName}
                            onChange={(e) => setPublisherName(e.target.value)}
                            required
                        >
                            <option value="">Select publisher</option>
                            {publishers.map((p) => (
                                <option key={p.publisherId} value={p.publisherName}>
                                    {p.publisherName}
                                </option>
                            ))}
                        </select>
                    </div>
                </div>

                {/* RIGHT SIDE */}
                <div className="space-y-6">

                    {/* IMAGE */}
                    <div>
                        <label className="block mb-2 text-zinc-300">Book Cover Image</label>

                        <div className="p-6 border border-zinc-700 rounded-xl bg-zinc-800 text-center cursor-pointer hover:bg-zinc-700 transition">
                            <label className="cursor-pointer flex flex-col items-center gap-2">
                                <Image size={40} className="text-zinc-400" />
                                <span className="text-zinc-400">
                                    {imageFile ? imageFile.name : "Upload Image"}
                                </span>
                                <input
                                    type="file"
                                    accept="image/*"
                                    className="hidden"
                                    onChange={(e) => setImageFile(e.target.files[0])}
                                />
                            </label>
                        </div>
                    </div>

                    {/* PDF */}
                    <div>
                        <label className="block mb-2 text-zinc-300">PDF (optional)</label>

                        <div className="p-6 border border-zinc-700 rounded-xl bg-zinc-800 text-center cursor-pointer hover:bg-zinc-700 transition">
                            <label className="cursor-pointer flex flex-col items-center gap-2">
                                <FileText size={40} className="text-zinc-400" />
                                <span className="text-zinc-400">
                                    {pdfFile ? pdfFile.name : "Upload PDF"}
                                </span>
                                <input
                                    type="file"
                                    accept="application/pdf"
                                    className="hidden"
                                    onChange={(e) => setPdfFile(e.target.files[0])}
                                />
                            </label>
                        </div>
                    </div>
                </div>

                {/* BUTTONS */}
                <div className="col-span-2 flex justify-end gap-4 mt-6">
                    <button
                        type="button"
                        onClick={() => navigate("/manage-books")}
                        className="px-5 py-2 rounded-xl bg-zinc-700 hover:bg-zinc-600 transition"
                    >
                        Cancel
                    </button>

                    <button
                        type="submit"
                        className="px-5 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 transition font-semibold"
                    >
                        Add Book
                    </button>
                </div>
            </form>
        </div>
    );
}
