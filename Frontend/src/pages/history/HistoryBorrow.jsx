import React, { useEffect, useState } from "react";
import { getHistory } from "../../api/userApi/borrowApi";
import toast from "react-hot-toast";

export default function HistoryBorrow() {
  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    try {
      const data = await getHistory();
      setLoans(data); // ✅ BE đã sort rồi
    } catch (err) {
      toast.error("Không tải được lịch sử mượn sách");
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateStr) =>
    dateStr ? new Date(dateStr).toLocaleDateString("vi-VN") : "--";

  const getStatusBadge = (borrowStatus) => {
    switch (borrowStatus) {
      case "ACTIVE":
        return "bg-blue-500/20 text-blue-300";
      case "RETURNED":
        return "bg-green-500/20 text-green-300";
      case "OVERDUE":
        return "bg-red-500/20 text-red-300";
      case "REJECT":
        return "bg-gray-500/20 text-gray-300";
      case "CANCELED":
        return "bg-yellow-500/20 text-yellow-300";
      default:
        return "bg-white/10 text-white";
    }
  };

  const getStatusText = (borrowStatus) => {
    switch (borrowStatus) {
      case "ACTIVE":
        return "Đang mượn";
      case "RETURNED":
        return "Đã trả";
      case "OVERDUE":
        return "Quá hạn";
      case "REJECT":
        return "Bị từ chối";
      case "CANCELED":
        return "Đã hủy";
      default:
        return borrowStatus;
    }
  };

  if (loading) {
    return (
      <p className="text-center text-gray-300 py-10 animate-pulse">
        Đang tải lịch sử...
      </p>
    );
  }

  return (
    <section className="pt-16 -mt-8 px-6 max-w-5xl mx-auto">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-white">
          📜 Lịch sử mượn sách
        </h1>
        <p className="text-gray-400 mt-1">
          Các lượt mượn – trả sách của bạn theo thời gian
        </p>
      </div>

      {loans.length === 0 ? (
        <p className="text-gray-400 text-center">
          Chưa có lịch sử mượn sách
        </p>
      ) : (
        <div className="space-y-4">
          {loans.map((item, index) => (
            <div
              key={item.loanId}
              className="flex gap-6 bg-white/10 backdrop-blur-md
              border border-white/20 rounded-xl p-5"
            >
              {/* TIMELINE DOT */}
              <div className="flex flex-col items-center">
                <div className="w-3 h-3 rounded-full bg-blue-500" />
                {index !== loans.length - 1 && (
                  <div className="w-px flex-1 bg-white/20 mt-2" />
                )}
              </div>

              <img
                src={item.imageUrl}
                alt={item.title}
                className="w-16 h-24 object-cover rounded-lg"
              />

              <div className="flex-1 text-white">
                <h3 className="text-lg font-semibold">{item.title}</h3>
                <p className="text-sm text-gray-300">{item.author}</p>

                <div className="flex gap-6 mt-2 text-sm">
                  <span className="text-blue-300">
                    📅 Mượn: {formatDate(item.borrowDay)}
                  </span>

                  {item.returnedDay && (
                    <span className="text-green-300">
                      ✅ Trả: {formatDate(item.returnedDay)}
                    </span>
                  )}
                </div>
              </div>

              <span
                className={`h-fit px-3 py-1 rounded-full text-sm font-semibold
                ${getStatusBadge(item.status)}`}
              >
                {getStatusText(item.status)}
              </span>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
