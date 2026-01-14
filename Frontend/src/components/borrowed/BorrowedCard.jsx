import React from "react";
import { useNavigate } from "react-router-dom";

export default function BorrowedCard({ book, onReturn }) {
  const navigate = useNavigate();

  // 🔥 Tính toán nhãn hiển thị
  const isOverdue = book.borrowStatus === "OVERDUE";
  const badgeText = isOverdue ? "Quá hạn trả" : "Đang mượn";
  const badgeColor = isOverdue ? "bg-red-600" : "bg-green-500";

  return (
    <div
      onClick={() => navigate(`/borrowed/order-active/${book.bookId}`)}
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
      {/* IMAGE */}
      <div className="relative overflow-hidden">
        <img
          src={book.imageUrl}
          alt={book.title}
          className="w-full h-80 object-cover object-top hover:scale-110 transition duration-500"
        />

        {/* Badge */}
        <span
          className={`absolute top-2 right-2 px-3 py-1 ${badgeColor} text-white text-xs font-semibold rounded-full shadow-md`}
        >
          {badgeText}
        </span>
      </div>

      {/* CONTENT + BUTTON */}
      <div className="p-5 text-white flex flex-col flex-1">
        <div className="flex-1">
          <h4 className="text-lg font-bold truncate">{book.title}</h4>
          <p className="text-sm text-gray-300 mt-1 overflow-hidden text-ellipsis whitespace-nowrap">
            {book.author}
          </p>
        </div>

        <button
          onClick={(e) => {
            e.stopPropagation(); // ❗ không trigger navigate
            onReturn();          // 👉 báo cho Borrowed.jsx mở ConfirmModal
          }}
          className="mt-4 w-full py-2 rounded-lg bg-red-600 hover:bg-red-700 text-white font-medium transition"
        >
          Trả sách
        </button>
      </div>
    </div>
  );
}
