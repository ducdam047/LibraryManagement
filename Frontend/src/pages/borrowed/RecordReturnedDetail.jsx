import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getReturnedorderById } from "../../api/userApi/borrowApi";
import toast from "react-hot-toast";

export default function LoanReturnedDetail() {
  const { loanId } = useParams();
  const navigate = useNavigate();

  const [order, setorder] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!loanId) {
      toast.error("loanId không hợp lệ");
      return;
    }
    load();
  }, [loanId]);

  async function load() {
    try {
      const data = await getReturnedorderById(loanId);
      setorder(data);
    } catch (err) {
      toast.error("Không tải được thông tin mượn sách");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <p className="text-center text-gray-300 py-10 text-lg animate-pulse">
        Đang tải dữ liệu...
      </p>
    );
  }

  if (!order) {
    return (
      <p className="text-center text-gray-300 py-10 text-lg">
        Không tìm thấy bản ghi.
      </p>
    );
  }

  return (
    <section className="pt-16 -mt-8 pb-12 w-full">
      <div className="px-10 max-w-4xl mx-auto bg-white/10 backdrop-blur-lg border border-white/20 rounded-2xl shadow-xl p-10">

        {/* HEADER */}
        <div className="flex items-start gap-8">
          <img
            src={order.imageUrl}
            alt={order.title}
            className="w-52 h-72 object-cover rounded-xl shadow-lg border border-white/20"
          />

          <div className="flex-1 text-white">
            <h1 className="text-3xl font-bold mb-2">{order.title}</h1>
            <p className="text-lg text-gray-300 mb-2">{order.author}</p>

            <span className="px-4 py-1 bg-blue-600 text-white rounded-full text-sm font-semibold shadow-md">
              ✔ ĐÃ TRẢ
            </span>

            <div className="mt-6 text-gray-200 space-y-1">
              <p>
                <span className="font-semibold text-white">Ngày mượn:</span>{" "}
                {order.borrowDay}
              </p>
              <p>
                <span className="font-semibold text-white">Ngày trả:</span>{" "}
                {order.returnedDay}
              </p>
            </div>
          </div>
        </div>

        <div className="mt-10">
          <button
            onClick={() => navigate("/borrowed")}
            className="
              px-5 py-2 rounded-lg bg-gray-300 text-gray-800 
              hover:bg-gray-200 transition
            "
          >
            ← Quay lại danh sách
          </button>
        </div>

      </div>
    </section>
  );
}
