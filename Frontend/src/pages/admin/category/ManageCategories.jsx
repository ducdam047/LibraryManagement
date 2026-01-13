import React, { useEffect, useState } from "react";
import { getAllCategories } from "../../../api/adminApi/manageCategoriesApi";
import { useNavigate } from "react-router-dom";
import { Plus } from "lucide-react";

export default function ManageCategories() {
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        fetchCategories();
    }, []);

    const fetchCategories = async () => {
        try {
            const data = await getAllCategories();
            setCategories(Array.isArray(data) ? data : []);
        } catch (err) {
            console.error("Error loading categories:", err);
            setCategories([]);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="p-6 w-full">

            {/* --- HEADER + BUTTON --- */}
            <div className="flex justify-between items-center mb-4">
                <h1 className="text-2xl font-semibold">Manage Categories</h1>

                <button
                    onClick={() => navigate("/manage-categories/add")}
                    className="flex items-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 
                   text-white rounded-lg shadow"
                >
                    <Plus size={18} />
                    Add Category
                </button>
            </div>

            {loading ? (
                <p>Đang tải...</p>
            ) : (
                <div className="bg-zinc-900 p-6 rounded-2xl border border-zinc-800 shadow-xl overflow-x-auto">
                    <table className="min-w-full text-sm">
                        <thead>
                            <tr className="bg-zinc-800/60 text-blue-300">
                                <th className="p-3 text-left pl-60">Category Name</th>
                                <th className="p-3 text-left">Description</th>
                            </tr>
                        </thead>

                        <tbody className="divide-y divide-zinc-800 text-zinc-300">
                            {categories.map((cat) => (
                                <tr key={cat.categoryId} className="hover:bg-zinc-800/40 transition">
                                    <td className="p-3 pl-60">{cat.categoryName}</td>

                                    <td className="p-3 text-zinc-500">
                                        {cat.description ?? "—"}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}
