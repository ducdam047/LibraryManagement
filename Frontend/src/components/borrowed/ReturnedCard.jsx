import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { checkEvaluateExists } from "../../api/userApi/evaluateApi";

export default function ReturnedCard({ order }) {
  const navigate = useNavigate();

  // --- hooks (luôn ở trên cùng) ---
  const [evaluated, setEvaluated] = useState(false);
  const [checking, setChecking] = useState(true);

  const bookId = order?.bookId;
  const title = order?.title || "";

  useEffect(() => {
    if (!bookId) {
      setChecking(false);
      setEvaluated(false);
      return;
    }

    let mounted = true;
    async function load() {
      setChecking(true);
      try {
        const res = await checkEvaluateExists(title);
        if (mounted) setEvaluated(res?.evaluated === true);
      } catch (err) {
        if (mounted) setEvaluated(false);
      } finally {
        if (mounted) setChecking(false);
      }
    }

    load();
    return () => { mounted = false };
  }, [bookId]);

  // Nếu chưa load order (ví dụ parent đang fetch), trả skeleton chỗ card (không làm vỡ layout)
  if (!order) {
    return (
      <div
        className="
          w-64 flex-shrink-0 bg-white/6 rounded-2xl overflow-hidden
          border border-white/10 p-4 animate-pulse
        "
      >
        <div className="w-full h-44 bg-white/8 rounded-md mb-3" />
        <div className="h-4 bg-white/8 rounded w-3/4 mb-2" />
        <div className="h-3 bg-white/8 rounded w-1/2 mb-2" />
        <div className="h-8 bg-white/8 rounded w-full mt-3" />
      </div>
    );
  }

  // --- Render chính ---
  return (
    <div
      onClick={() => navigate(`/borrowed/order-returned/${order.loanId}`)}
      className="
    bg-white/10 backdrop-blur-lg 
    rounded-2xl overflow-hidden 
    border border-white/20 
    shadow-xl cursor-pointer
    hover:shadow-blue-500/40 hover:scale-[1.03]
    transition-all duration-300
    w-64 flex-shrink-0
    flex flex-col
  "
    >
      <div className="relative overflow-hidden">
        <img
          src={order.imageUrl}
          alt={order.title}
          className="w-full h-80 object-cover object-top hover:scale-110 transition duration-500"
        />

        <span className="absolute top-2 right-2 px-3 py-1 bg-gray-500 text-white text-xs font-semibold rounded-full shadow-md">
          Đã trả
        </span>
      </div>

      {/* CONTENT FIXED-BOTTOM BUTTON */}
      <div className="p-5 text-white flex flex-col flex-1">
        {/* phần thông tin */}
        <div className="flex-1">
          <h4 className="text-lg font-bold truncate">{order.title}</h4>
          <p className="text-sm text-gray-300 mt-1 overflow-hidden text-ellipsis whitespace-nowrap">
            {order.author}
          </p>

          <p className="mt-3 text-sm text-green-400">
            Đã trả ngày: {order.returnedDay}
          </p>
        </div>

        {/* NÚT ĐÁY */}
        <div className="mt-auto pt-4">
          {checking ? (
            <button
              onClick={(e) => e.stopPropagation()}
              className="w-full px-4 py-2 rounded-xl bg-white/8 text-gray-300 cursor-wait"
            >
              ...
            </button>
          )
            : evaluated ? (
              <button
                onClick={(e) => e.stopPropagation()}
                disabled
                className="w-full px-4 py-2 rounded-xl bg-gray-400 text-white cursor-not-allowed"
              >
                ✔ Đã đánh giá
              </button>
            )
              : (
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    navigate("/evaluate/evaluate-book", {
                      state: {
                        bookId,
                        title,
                      },
                    });
                  }}
                  className="w-full px-4 py-2 rounded-xl bg-yellow-500 text-black font-semibold hover:bg-yellow-400 transition"
                >
                  ⭐ Đánh giá sách
                </button>
              )}
        </div>
      </div>
    </div>

  );
}
