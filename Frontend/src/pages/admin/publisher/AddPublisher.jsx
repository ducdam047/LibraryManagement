import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Plus, ArrowLeft } from "lucide-react";
import { createPublisher } from "../../../api/adminApi/managePublishersApi";

export default function AddPublisher() {
    const navigate = useNavigate();

    const [form, setForm] = useState({
        publisherId: "",
        publisherName: "",
        address: "",
    });

    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            await createPublisher(form);
            navigate("/manage-publishers");
        } catch (err) {
            console.error("Error creating publisher:", err);
            alert("Không thể thêm nhà xuất bản!");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="p-6 max-w-2xl mx-auto">
            <div className="flex items-center mb-6">
                <button
                    onClick={() => navigate("/manage-publishers")}
                    className="flex items-center gap-2 text-zinc-300 hover:text-white"
                >
                    <ArrowLeft size={18} />
                    Back
                </button>
            </div>

            <div className="bg-zinc-900 p-6 rounded-2xl border border-zinc-800 shadow-xl">
                <h1 className="text-2xl font-semibold mb-6 text-white">Add New Publisher</h1>

                <form onSubmit={handleSubmit} className="space-y-5">

                    {/* Publisher ID */}
                    <div>
                        <label className="block text-sm mb-2 text-zinc-300">Publisher ID</label>
                        <input
                            type="text"
                            name="publisherId"
                            value={form.publisherId}
                            onChange={handleChange}
                            className="w-full p-3 rounded-lg bg-zinc-800 border border-zinc-700 text-white 
                                       focus:ring-2 focus:ring-blue-500 outline-none"
                            placeholder="VD: NXB01, NXB-TRE..."
                            required
                        />
                    </div>

                    {/* Publisher Name */}
                    <div>
                        <label className="block text-sm mb-2 text-zinc-300">Publisher Name</label>
                        <input
                            type="text"
                            name="publisherName"
                            value={form.publisherName}
                            onChange={handleChange}
                            className="w-full p-3 rounded-lg bg-zinc-800 border border-zinc-700 text-white 
                                       focus:ring-2 focus:ring-blue-500 outline-none"
                            placeholder="Nhập tên nhà xuất bản"
                            required
                        />
                    </div>

                    {/* Address */}
                    <div>
                        <label className="block text-sm mb-2 text-zinc-300">Address</label>
                        <textarea
                            name="address"
                            rows="3"
                            value={form.address}
                            onChange={handleChange}
                            className="w-full p-3 rounded-lg bg-zinc-800 border border-zinc-700 text-white 
                                       focus:ring-2 focus:ring-blue-500 outline-none"
                            placeholder="Địa chỉ (không bắt buộc)"
                        ></textarea>
                    </div>

                    {/* Buttons */}
                    <div className="flex justify-end gap-3 mt-6">
                        <button
                            type="button"
                            onClick={() => navigate("/manage-publishers")}
                            className="px-4 py-2 rounded-lg bg-zinc-700 hover:bg-zinc-600 text-white"
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            disabled={loading}
                            className="flex items-center gap-2 px-5 py-2 bg-blue-600 hover:bg-blue-700 
                                       text-white rounded-lg shadow disabled:opacity-60"
                        >
                            <Plus size={18} />
                            {loading ? "Adding..." : "Add Publisher"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
