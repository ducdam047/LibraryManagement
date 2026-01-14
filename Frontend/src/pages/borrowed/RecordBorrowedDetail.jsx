import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getBorrowedorderById, extendBook } from "../../api/userApi/borrowApi";
import toast from "react-hot-toast";

export default function RecordBorrowedDetail() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [order, setorder] = useState(null);

    // Modal state
    const [showExtendModal, setShowExtendModal] = useState(false);
    const [extendDays, setExtendDays] = useState(1);

    // Fetch order
    const fetchorder = async () => {
        try {
            const response = await getBorrowedorderById(id);
            setorder(response);
        } catch (error) {
            console.error("Error fetching order:", error);
        }
    };

    useEffect(() => {
        fetchorder();
    }, [id]);

    if (!order) {
        return (
            <div className="w-full flex justify-center pt-20 text-gray-500">
                Đang tải dữ liệu...
            </div>
        );
    }

    // Format date
    const formatDate = (dateStr) => {
        return new Date(dateStr).toLocaleDateString("vi-VN");
    };

    const getStatusColor = (borrowStatus) => {
        switch (borrowStatus) {
            case "ACTIVE":
                return "bg-blue-100 text-blue-700";
            case "OVERDUE":
                return "bg-red-100 text-red-700";
            case "RETURNED":
                return "bg-green-100 text-green-700";
            default:
                return "bg-gray-200 text-gray-700";
        }
    };

    // --------------------------
    // HANDLE EXTEND ACTION
    // --------------------------
    const handleExtendSubmit = async () => {
        try {
            const res = await extendBook(order.bookId, extendDays);
            toast.success(res.message || "Gia hạn thành công!");
            console.log("order.bookId:", order.bookId);
            setShowExtendModal(false);
            fetchorder(); // reload data
        } catch (err) {
            toast.error(err.response?.data?.message || "Gia hạn thất bại!");
        }
    };

    return (
        <div className="pt-8 px-4 md:px-0 flex justify-center">
            <div className="w-full max-w-xl bg-white shadow-lg rounded-2xl p-8 border border-gray-100">

                {/* Header */}
                <div className="text-center mb-8">
                    <h2 className="text-3xl font-bold text-gray-800 flex items-center justify-center gap-3">
                        📘 Chi Tiết Phiếu Mượn
                    </h2>
                    <p className="text-gray-500 mt-1">
                        Thông tin chi tiết về lượt mượn sách của bạn
                    </p>
                </div>

                {/* Info blocks */}
                <div className="space-y-4">

                    <div className="bg-gray-50 p-4 rounded-xl border border-gray-100">
                        <p className="text-gray-500 text-sm">Người mượn</p>
                        <p className="text-lg font-semibold text-gray-800">{order.fullName}</p>
                    </div>

                    <div className="bg-gray-50 p-4 rounded-xl border border-gray-100">
                        <p className="text-gray-500 text-sm">Tên sách</p>
                        <p className="text-lg font-semibold text-gray-800">{order.title}</p>
                    </div>

                    <div className="flex gap-4">
                        <div className="bg-gray-50 p-4 rounded-xl border border-gray-100 w-1/2">
                            <p className="text-gray-500 text-sm">Ngày mượn</p>
                            <p className="text-lg font-medium text-gray-800">
                                {formatDate(order.borrowDay)}
                            </p>
                        </div>

                        <div className="bg-gray-50 p-4 rounded-xl border border-gray-100 w-1/2">
                            <p className="text-gray-500 text-sm">Hạn trả</p>
                            <p className="text-lg font-medium text-gray-800">
                                {formatDate(order.dueDay)}
                            </p>
                        </div>
                    </div>

                    {/* STATUS + EXTEND COUNT (2 ô cùng hàng) */}
                    <div className="flex gap-4">

                        {/* STATUS */}
                        <div className="w-1/2">
                            <div className="flex items-center justify-between bg-gray-50 p-4 rounded-xl border border-gray-100">
                                <p className="text-gray-500 text-sm">Trạng thái</p>
                                <span
                                    className={`px-3 py-1 rounded-full text-sm font-semibold ${getStatusColor(
                                        order.borrowStatus
                                    )}`}
                                >
                                    {order.borrowStatus}
                                </span>
                            </div>
                        </div>

                        {/* EXTEND COUNT */}
                        <div className="w-1/2">
                            <div className="flex items-center justify-between bg-gray-50 p-4 rounded-xl border border-gray-100">
                                <p className="text-gray-500 text-sm">Số lần gia hạn</p>
                                <span className="text-lg font-semibold text-gray-800">
                                    {order.extendCount}
                                </span>
                            </div>
                        </div>

                    </div>
                </div>

                <div className="mt-8 flex justify-between gap-4">

                    {/* BACK BUTTON — ACTIVE & OVERDUE */}
                    {(order.borrowStatus === "ACTIVE" ||
                        order.borrowStatus === "OVERDUE") && (
                            <button
                                onClick={() => navigate("/borrowed")}
                                className="w-1/2 px-5 py-3 bg-gray-200 text-gray-800 rounded-xl 
                 hover:bg-gray-300 transition font-medium"
                            >
                                ← Quay lại danh sách
                            </button>
                        )}

                    {/* EXTEND BUTTON — ONLY ACTIVE */}
                    {order.borrowStatus === "ACTIVE" && (
                        <button
                            onClick={() => setShowExtendModal(true)}
                            className="w-1/2 px-5 py-3 bg-blue-600 text-white rounded-xl shadow-md 
                 hover:bg-blue-700 transition font-medium"
                        >
                            Gia hạn thêm
                        </button>
                    )}
                </div>

            </div>

            {/* EXTEND MODAL */}
            {showExtendModal && (
                <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
                    <div className="bg-white w-80 rounded-2xl shadow-xl p-6">
                        <h3 className="text-xl font-semibold mb-3 text-gray-800">
                            Gia hạn sách
                        </h3>

                        <label className="text-gray-600 text-sm">Số ngày muốn gia hạn</label>
                        <input
                            type="number"
                            min="1"
                            value={extendDays}
                            onChange={(e) => setExtendDays(Number(e.target.value))}
                            className="w-full mt-2 p-2 border rounded-lg focus:ring focus:ring-blue-300 text-black"
                        />

                        <div className="flex justify-end gap-3 mt-5">
                            <button
                                onClick={() => setShowExtendModal(false)}
                                className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300"
                            >
                                Hủy
                            </button>

                            <button
                                onClick={handleExtendSubmit}
                                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                            >
                                Xác nhận
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
