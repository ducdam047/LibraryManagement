import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { Search } from "lucide-react";
import { getDashboardBooks } from "../../api/adminApi/dashboardApi";
import SearchBar from "../../components/admin/SearchBar";

export default function DashboardBook() {
  const location = useLocation();
  const params = new URLSearchParams(location.search);
  const statusFilter = params.get("status") || "ALL";

  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");

  useEffect(() => {
    const loadBooks = async () => {
      setLoading(true);
      try {
        const data = await getDashboardBooks(
          statusFilter === "ALL" ? null : statusFilter
        );
        setBooks(data);
      } catch (e) {
        console.error("Failed to load books:", e);
      }
      setLoading(false);
    };

    loadBooks();
  }, [statusFilter]);

  // 📌 Filter tại FE
  const displayed = books.filter((b) =>
    b.title?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="p-6 text-white min-h-screen bg-black">
      <h1 className="text-3xl font-bold mb-6">List of books</h1>

      {/* Search Box */}
      <SearchBar
        placeholder="Tìm theo tên sách..."
        value={search}
        onChange={setSearch}
      />

      <div className="bg-zinc-900 rounded-2xl p-6 shadow-xl">
        <table className="w-full text-left border-collapse">
          <thead>
            <tr className="border-b border-zinc-700">
              <th className="p-3">Book title</th>
              <th className="p-3">Author</th>
              <th className="p-3">Category</th>
              <th className="p-3">Status</th>
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
                  Không có sách phù hợp
                </td>
              </tr>
            ) : (
              displayed.map((b) => (
                <tr key={b.bookId} className="border-b border-zinc-800">
                  <td className="p-3">{b.title}</td>
                  <td className="p-3">{b.author}</td>
                  <td className="p-3">{b.categoryName}</td>
                  <td className="p-3">
                    {b.status === "AVAILABLE" ? (
                      <span className="text-green-400">Khả dụng</span>
                    ) : (
                      <span className="text-red-400">Đang mượn</span>
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
