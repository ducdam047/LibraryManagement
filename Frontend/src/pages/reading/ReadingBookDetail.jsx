import React, { useEffect, useState } from "react";
import { useParams, useNavigate, useLocation  } from "react-router-dom";
import PdfViewerModal from "../pdf/PdfViewerModal";
import { getReadingDetail, saveReadingProgress } from "../../api/userApi/readingApi";
import toast from "react-hot-toast";

export default function ReadingBookDetail() {
  
  const { readingId } = useParams();

  const [reading, setReading] = useState(null);
  const [loading, setLoading] = useState(true);

  const [showPdf, setShowPdf] = useState(false);
  const [lastPage, setLastPage] = useState(1);

  // ✅ FETCH DATA
  useEffect(() => {
    let mounted = true;
    setLoading(true);
    setShowPdf(false); // 🔥 reset modal khi đổi sách

    async function fetchData() {
      try {
        const res = await getReadingDetail(readingId);
        if (!mounted) return;
        setReading(res);
        setLastPage(res.page);
        document.title = `${res.bookName} – Đang đọc`;
      } catch (err) {
        console.error(err);
        if (mounted) setReading(null);
      } finally {
        if (mounted) setLoading(false);
      }
    }

    fetchData();
    return () => {
      mounted = false;
    };
  }, [readingId]);

  const handleClosePdf = async () => {
    try {
      await saveReadingProgress(reading.bookId, lastPage);
      setReading((prev) => ({
        ...prev,
        page: lastPage,
      }));
      toast.success("Đã lưu tiến trình đọc");
    } catch {
      toast.error("Không thể lưu tiến trình");
    }
    setShowPdf(false);
  };

  const handlePageChange = (page) => {
    setLastPage(page);
  };

  // ✅ RETURN SAU KHI KHAI BÁO HẾT HOOK
  if (loading) {
    return (
      <p className="text-center py-16 text-gray-400 animate-pulse text-lg">
        Đang tải sách...
      </p>
    );
  }

  if (!reading) {
    return (
      <p className="text-center py-16 text-red-500 text-lg">
        Không tìm thấy dữ liệu đọc
      </p>
    );
  }

  return (
  <div className="max-w-6xl mx-auto px-6 py-20">
    <div className="relative bg-white rounded-3xl shadow-2xl overflow-hidden border border-gray-200">

      {/* BACKGROUND DECOR */}
      <div className="absolute inset-0 bg-gradient-to-br from-blue-50 via-white to-indigo-50 opacity-70 pointer-events-none" />

      <div className="relative grid grid-cols-1 md:grid-cols-[260px_1fr] gap-12 p-12">

        {/* LEFT – BOOK COVER */}
        <div className="flex flex-col items-center">
          <img
            src={reading.imageUrl}
            alt={reading.bookName}
            className="w-56 h-[380px] object-cover rounded-2xl shadow-xl border border-gray-200"
          />

          <span className="mt-5 px-4 py-1.5 rounded-full bg-green-100 text-green-700 text-sm font-semibold tracking-wide">
            📖 Đang đọc
          </span>
        </div>

        {/* RIGHT – INFO */}
        <div className="flex flex-col justify-between">

          {/* TITLE */}
          <div>
            <h1 className="text-4xl font-extrabold text-gray-900 leading-snug">
              {reading.bookName}
            </h1>

            <p className="mt-2 text-gray-500 text-sm">
              Tiếp tục từ trang bạn đã đọc lần trước
            </p>
          </div>

          {/* ACTION */}
          <div className="mt-12">
            <button
              onClick={() => setShowPdf(true)}
              className="
                w-full sm:w-auto
                px-8 py-4
                rounded-2xl
                bg-blue-600 hover:bg-blue-700
                text-white font-semibold text-base
                shadow-lg hover:shadow-blue-500/40
                transition-all duration-300
                flex items-center justify-center gap-2
              "
            >
              📖 Tiếp tục đọc
            </button>
          </div>
        </div>
      </div>
    </div>

    {/* PDF MODAL */}
    {showPdf && (
      <PdfViewerModal
        key={reading.bookId}
        bookId={reading.bookId}
        startPage={reading.page}
        title={reading.bookName}
        onClose={handleClosePdf}
        onPageChange={handlePageChange}
      />
    )}
  </div>
);

}
