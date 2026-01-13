import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { Search, ShieldAlert } from "lucide-react";
import { getDashboardUsers } from "../../api/adminApi/dashboardApi";
import SearchBar from "../../components/admin/SearchBar";

export default function DashboardUser() {
  const location = useLocation();
  const params = new URLSearchParams(location.search);
  const statusFilter = params.get("status") || "ALL";

  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");

  useEffect(() => {
    const loadUsers = async () => {
      try {
        setLoading(true);
        const statusQuery = statusFilter === "ALL" ? "" : statusFilter;
        const data = await getDashboardUsers(statusQuery);
        setUsers(data);
      } finally {
        setLoading(false);
      }
    };

    loadUsers();
  }, [statusFilter]);

  // 📌 Filter tại FE theo fullName
  const displayed = users.filter((u) =>
    (u.fullName || "").toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="p-6 text-white min-h-screen bg-black">
      <h1 className="text-3xl font-bold mb-6">List of users</h1>

      {/* Search Box */}
      <SearchBar
        placeholder="Tìm theo tên người dùng..."
        value={search}
        onChange={setSearch}
      />

      <div className="bg-zinc-900 rounded-2xl p-6 shadow-xl">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="border-b border-zinc-700">
              <th className="p-3">Full name</th>
              <th className="p-3">Status</th>
              <th className="p-3">Borrowing</th>
              <th className="p-3">Ban until</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan="4" className="p-4 text-center text-zinc-500">
                  Đang tải dữ liệu...
                </td>
              </tr>
            ) : displayed.length === 0 ? (
              <tr>
                <td colSpan="4" className="p-4 text-center text-zinc-500">
                  Không có người dùng phù hợp
                </td>
              </tr>
            ) : (
              displayed.map((u, index) => (
                <tr key={index} className="border-b border-zinc-800">
                  <td className="p-3">{u.fullName || "Không rõ"}</td>

                  <td className="p-3">
                    {u.status === "BANNED" ? (
                      <span className="text-red-400 flex items-center gap-1">
                        <ShieldAlert size={16} />
                        Bị cấm
                      </span>
                    ) : u.status === "BORROWING" ? (
                      <span className="text-yellow-400">Đang mượn</span>
                    ) : (
                      <span className="text-green-400">Hoạt động</span>
                    )}
                  </td>

                  <td className="p-3">
                    {u.bookBorrowing > 0 ? (
                      <span className="text-yellow-400">
                        {u.bookBorrowing} sách
                      </span>
                    ) : (
                      <span className="text-zinc-400">0</span>
                    )}
                  </td>

                  <td className="p-3">
                    {u.banUtil ? (
                      <span className="text-red-300">{u.banUtil}</span>
                    ) : (
                      <span className="text-zinc-400">—</span>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
