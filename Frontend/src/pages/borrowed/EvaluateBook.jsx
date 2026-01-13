import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { evaluateBook } from "../../api/userApi/evaluateApi";

export default function EvaluateBook() {
  const navigate = useNavigate();
  const location = useLocation();

  const bookId = location.state?.bookId;
  const title = location.state?.title || "";

  const [rating, setRating] = useState(0);
  const [comment, setComment] = useState("");

  if (!bookId) {
    toast.error("Thiếu thông tin sách cần đánh giá");
    navigate("/borrowed");
    return null; // ⬅ bắt buộc để dừng render
  }

  async function handleSubmit() {
    if (!rating) {
      toast.error("Bạn chưa chọn số sao!");
      return;
    }

    try {
      await evaluateBook({
        bookId: bookId,          // ⭐ QUAN TRỌNG
        rating: rating,          // number là đủ
        comment: comment,
      });

      toast.success("Đánh giá thành công!");
      navigate("/borrowed");
    } catch (err) {
      toast.error(err.response?.data?.message || "Lỗi khi gửi đánh giá");
    }
  }

  return (
    <section className="pt-16 -mt-8 pb-12 w-full">
      <div className="px-10 max-w-xl mx-auto bg-white/10 backdrop-blur-lg border border-white/20 rounded-2xl shadow-xl p-10">

        {/* HEADER */}
        <h1 className="text-3xl font-bold text-white mb-4 text-center">
          ⭐ Đánh giá sách
        </h1>

        <p className="text-gray-300 text-center mb-8">
          Bạn đang đánh giá:{" "}
          <span className="font-semibold text-white">{title}</span>
        </p>

        {/* RATING */}
        <div className="flex justify-center gap-3 mb-6">
          {[1, 2, 3, 4, 5].map((star) => (
            <button
              key={star}
              onClick={() => setRating(star)}
              className={`text-4xl transition ${rating >= star ? "text-yellow-400" : "text-gray-600"
                }`}
            >
              ★
            </button>
          ))}
        </div>

        {/* COMMENT */}
        <label className="text-white font-semibold">Nhận xét</label>
        <textarea
          value={comment}
          onChange={(e) => setComment(e.target.value)}
          placeholder="Viết cảm nhận của bạn về cuốn sách..."
          className="
            w-full mt-2 p-3 rounded-lg h-32 resize-none 
            bg-white/20 text-gray-100 placeholder-gray-400
            border border-white/30 focus:outline-none 
            focus:ring-2 focus:ring-blue-400
          "
        />

        {/* BUTTONS */}
        <div className="flex justify-center gap-4 mt-6">
          <button
            onClick={() => navigate("/borrowed")}
            className="px-5 py-2 bg-gray-300 text-gray-800 rounded-lg hover:bg-gray-200 transition"
          >
            ← Quay lại
          </button>

          <button
            onClick={handleSubmit}
            className="px-5 py-2 bg-yellow-500 text-black font-semibold rounded-lg hover:bg-yellow-400 transition"
          >
            Gửi đánh giá
          </button>
        </div>

      </div>
    </section>
  );
}
