import React, { useEffect, useState } from "react";
import SearchBar from "../../../components/admin/SearchBar";
import { getAllUsers } from "../../../api/adminApi/manageUsersApi";
import { getLoanList } from "../../../api/adminApi/manageUsersApi";
import { FaHistory } from "react-icons/fa";

export default function ManageUsers() {
    const [users, setUsers] = useState([]);
    const [filteredUsers, setFilteredUsers] = useState([]);
    const [query, setQuery] = useState("");

    const [historyModal, setHistoryModal] = useState(false);
    const [historyData, setHistoryData] = useState([]);

    // Fetch users
    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const data = await getAllUsers();
                setUsers(data);
                setFilteredUsers(data);
            } catch (err) {
                console.error("Failed to load users:", err);
            }
        };
        fetchUsers();
    }, []);

    // Search filter
    useEffect(() => {
        const q = query.toLowerCase();
        setFilteredUsers(
            users.filter(
                (u) =>
                    u.fullName?.toLowerCase().includes(q) ||
                    u.email?.toLowerCase().includes(q) ||
                    u.username?.toLowerCase().includes(q)
            )
        );
    }, [query, users]);

    // Status color
    const getStatusColor = (status) => {
        switch (status) {
            case "ACTIVE":
                return "bg-green-600/20 text-green-400";
            case "BORROWING":
                return "bg-yellow-600/20 text-yellow-400";
            case "BANNED":
                return "bg-red-600/20 text-red-400";
            case "MANAGER":
                return "bg-purple-600/20 text-purple-400";
            default:
                return "bg-zinc-600/20 text-zinc-400";
        }
    };

    // Open history modal
    const openHistory = async (userId) => {
        try {
            const list = await getLoanList(userId);
            setHistoryData(list);
            setHistoryModal(true);
        } catch (err) {
            console.error("Load history failed:", err);
        }
    };

    const calcDaysOverdue = (dueDay, returnedDay) => {
        if (!dueDay) return 0;

        const due = new Date(dueDay);
        const ref = returnedDay ? new Date(returnedDay) : new Date(); // nếu đã trả thì so ngày trả

        const diff = ref - due;
        const days = Math.floor(diff / (1000 * 60 * 60 * 24));

        return days > 0 ? days : 0;
    };

    return (
        <div className="p-6 text-white min-h-screen bg-black">
            <div className="flex items-center justify-between mb-8">
                <h1 className="text-3xl font-bold">👥 Manage Users</h1>
            </div>

            <SearchBar
                placeholder="Search users by name, email or username..."
                value={query}
                onChange={setQuery}
            />

            <div className="bg-zinc-900 p-6 rounded-2xl border border-zinc-800 shadow-xl overflow-x-auto">
                <table className="min-w-full border-collapse text-sm">
                    <thead>
                        <tr>
                            <th className="p-3 text-left font-semibold">Full name</th>
                            <th className="p-3 text-left font-semibold">Email</th>
                            <th className="p-3 text-center font-semibold">Role</th>
                            <th className="p-3 text-center font-semibold">Borrowing</th>
                            <th className="p-3 text-center font-semibold">Status</th>
                            <th className="p-3 text-center font-semibold">Ban until</th>
                            <th className="p-3 text-center font-semibold">History</th>
                        </tr>
                    </thead>

                    <tbody className="divide-y divide-zinc-800">
                        {filteredUsers.length === 0 && (
                            <tr>
                                <td colSpan="6" className="text-center py-6 text-zinc-400">
                                    No users found
                                </td>
                            </tr>
                        )}

                        {filteredUsers.map((user) => (
                            <tr key={user.userId} className="hover:bg-zinc-800/40 transition">
                                <td className="p-3 font-semibold">{user.fullName ?? "(No name)"}</td>
                                <td className="p-3 text-zinc-300">{user.email}</td>

                                <td className="p-3 text-center">
                                    {user.role === "ADMIN" ? (
                                        <span className="px-3 py-1 bg-purple-600/20 text-purple-400 rounded-lg">
                                            Admin
                                        </span>
                                    ) : (
                                        <span className="px-3 py-1 bg-blue-600/20 text-blue-400 rounded-lg">
                                            User
                                        </span>
                                    )}
                                </td>

                                <td className="p-3 text-center font-bold">{user.bookBorrowing}</td>

                                <td className="p-3 text-center">
                                    <span className={`px-3 py-1 rounded-lg font-medium ${getStatusColor(user.status)}`}>
                                        {user.status}
                                    </span>
                                </td>

                                <td className="p-3 text-center">
                                    {user.banUtil ? (
                                        <span className="text-red-400 font-semibold">
                                            {user.banUtil}
                                        </span>
                                    ) : (
                                        <span className="text-zinc-500">—</span>
                                    )}
                                </td>

                                <td className="p-3 text-center">
                                    <button
                                        onClick={() => openHistory(user.userId)}
                                        className="text-blue-500 hover:text-blue-700"
                                    >
                                        <FaHistory size={18} />
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* 🔹 MODAL lịch sử */}
            {historyModal && (
                <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50">

                    <div className="bg-zinc-900 p-6 rounded-2xl w-[650px] border border-zinc-700 shadow-2xl relative max-h-[85vh] overflow-auto">

                        {/* Nút X thoát */}
                        <button
                            onClick={() => setHistoryModal(false)}
                            className="absolute top-3 right-3 text-zinc-400 hover:text-red-400 transition"
                        >
                            ✕
                        </button>

                        {/* Header */}
                        <h2 className="text-2xl font-semibold mb-5 text-blue-400 flex items-center gap-2">
                            📜 Lịch sử mượn sách
                        </h2>

                        <div className="space-y-4">
                            {historyData.length === 0 && (
                                <div className="text-center text-zinc-400 py-10">
                                    Người dùng chưa có lịch sử mượn sách
                                </div>
                            )}

                            {historyData.map((r) => {
                                const overdueDays =
                                    r.status === "OVERDUE"
                                        ? calcDaysOverdue(r.dueDay, r.returnedDay)
                                        : 0;

                                return (
                                    <div
                                        key={r.loanId}
                                        className="flex gap-4 bg-zinc-800/40 hover:bg-zinc-800/70 transition rounded-xl p-4"
                                    >
                                        {/* Ảnh sách */}
                                        <img
                                            src={r.imageUrl}
                                            alt={r.title ?? "Book"}
                                            className="w-16 h-20 object-cover rounded-lg border border-zinc-700"
                                        />

                                        {/* Nội dung */}
                                        <div className="flex-1">
                                            <h3 className="font-semibold text-lg text-white">
                                                {r.title ?? "(Không xác định)"}
                                            </h3>

                                            <p className="text-sm text-zinc-400 mb-2">
                                                {r.author}
                                            </p>

                                            <div className="text-sm text-zinc-300 space-y-1">
                                                <div>📅 Mượn: {r.borrowDay}</div>
                                                <div>📤 Trả: {r.returnedDay ?? "Chưa trả"}</div>
                                                <div>⏰ Hạn: {r.dueDay}</div>

                                                {r.status === "OVERDUE" && (
                                                    <div className="text-red-400 font-semibold">
                                                        ⚠ Quá hạn {overdueDays} ngày
                                                    </div>
                                                )}
                                            </div>
                                        </div>

                                        {/* Trạng thái */}
                                        <div className="flex items-center">
                                            {r.status === "RETURNED" ? (
                                                <span className="px-3 py-1 rounded-lg text-green-400 bg-green-600/20 font-medium">
                                                    RETURNED
                                                </span>
                                            ) : r.status === "OVERDUE" ? (
                                                <span className="px-3 py-1 rounded-lg text-red-400 bg-red-600/20 font-medium">
                                                    OVERDUE
                                                </span>
                                            ) : (
                                                <span className="px-3 py-1 rounded-lg text-blue-400 bg-blue-600/20 font-medium">
                                                    {r.status}
                                                </span>
                                            )}
                                        </div>
                                    </div>
                                );
                            })}
                        </div>


                        {/* Chân modal */}
                        <div className="flex justify-end mt-5">
                            <button
                                onClick={() => setHistoryModal(false)}
                                className="px-5 py-2 bg-zinc-700 hover:bg-zinc-600 rounded-lg"
                            >
                                Đóng
                            </button>
                        </div>
                    </div>
                </div>
            )}

        </div>
    );
}
