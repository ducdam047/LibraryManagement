import React, { useEffect, useState } from "react";
import { getAverageRating, getCountRating } from "../api/userApi/evaluateApi";

export default function EvaluateModal({
  open,
  onClose,
  evaluations = [],
  bookId,
  bookTitle,
}) {
  const [avgRating, setAvgRating] = useState(0);
  const [ratingCount, setRatingCount] = useState([0, 0, 0, 0, 0]); // [5,4,3,2,1]
  const [loading, setLoading] = useState(false);

  const total = ratingCount.reduce((s, c) => s + c, 0);

  const percent = (count) =>
    total === 0 ? 0 : Math.round((count / total) * 100);

  useEffect(() => {
    if (!open || !bookTitle) return;

    const fetchSummary = async () => {
      try {
        setLoading(true);

        const [avg, counts] = await Promise.all([
          getAverageRating(bookTitle),
          getCountRating(bookTitle),
        ]);

        setAvgRating(avg ?? 0);

        const map = { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 };
        counts.forEach((c) => {
          map[c.rating] = c.count;
        });

        setRatingCount([map[5], map[4], map[3], map[2], map[1]]);
      } catch (err) {
        console.error("Failed to fetch rating summary", err);
      } finally {
        setLoading(false);
      }
    };

    fetchSummary();
  }, [open, bookTitle]);

  if (!open) return null;

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-[2000] px-4">
      <div className="bg-white w-full max-w-3xl max-h-[90vh] rounded-3xl shadow-2xl overflow-hidden flex flex-col">

        {/* HEADER */}
        <div className="p-6 border-b bg-gradient-to-r from-indigo-50 to-blue-50 sticky top-0 z-10">
          <h2 className="text-2xl font-bold text-gray-900 text-center">
            Đánh giá người đọc
          </h2>
          {bookTitle && (
            <p className="text-center text-gray-500 mt-1 italic">
              {bookTitle}
            </p>
          )}
        </div>

        {/* BODY */}
        <div className="p-6 overflow-y-auto space-y-8">

          {/* SUMMARY */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* AVG */}
            <div className="flex flex-col items-center justify-center bg-gray-50 rounded-2xl p-6 border">
              {loading ? (
                <p className="text-gray-400">Đang tải...</p>
              ) : (
                <>
                  <p className="text-5xl font-extrabold text-yellow-500">
                    {avgRating.toFixed(1)}
                  </p>
                  <p className="mt-1 text-yellow-600 font-semibold">
                    ⭐⭐⭐⭐⭐
                  </p>
                  <p className="text-gray-500 mt-2">
                    {total} đánh giá
                  </p>
                </>
              )}
            </div>

            {/* DISTRIBUTION */}
            <div className="space-y-2">
              {[5, 4, 3, 2, 1].map((star, idx) => (
                <div key={star} className="flex items-center gap-3">

                  {/* SAO – width cố định */}
                  <div className="w-24 flex gap-0.5 text-yellow-500 text-sm shrink-0">
                    {Array.from({ length: star }).map((_, i) => (
                      <span key={i}>⭐</span>
                    ))}
                  </div>

                  {/* THANH */}
                  <div className="flex-1 bg-gray-200 rounded-full h-2 overflow-hidden">
                    <div
                      className="bg-yellow-500 h-2 rounded-full transition-all"
                      style={{ width: `${percent(ratingCount[idx])}%` }}
                    />
                  </div>

                  {/* COUNT – width cố định */}
                  <span className="w-8 text-right text-sm text-gray-500 shrink-0">
                    {ratingCount[idx]}
                  </span>
                </div>
              ))}
            </div>

          </div>

          {/* EMPTY */}
          {evaluations.length === 0 && (
            <p className="text-center text-gray-500 py-6">
              Chưa có đánh giá nào cho cuốn sách này.
            </p>
          )}

          {/* LIST */}
          <div className="space-y-5">
            {evaluations.map((e, i) => (
              <div
                key={i}
                className="border rounded-2xl p-5 bg-white shadow-sm hover:shadow-md transition"
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold">
                      {e.fullName?.charAt(0)}
                    </div>
                    <div>
                      <p className="font-semibold text-gray-900">
                        {e.fullName}
                      </p>
                      <p className="text-sm text-gray-400">
                        {e.evaluateDay}
                      </p>
                    </div>
                  </div>

                  <div className="text-yellow-600 font-semibold">
                    ⭐ {e.rating}
                  </div>
                </div>

                <p className="mt-4 text-gray-700 leading-relaxed">
                  {e.comment}
                </p>
              </div>
            ))}
          </div>
        </div>

        {/* FOOTER */}
        <div className="p-4 border-t bg-gray-50">
          <button
            onClick={onClose}
            className="w-full py-3 rounded-xl bg-blue-600 text-white font-semibold hover:bg-blue-700 transition"
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
}
