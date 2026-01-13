import React, { useState, useEffect } from "react";
import { FaBookOpen } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import { addToWishlist } from "../../api/userApi/wishlistApi";
import { saveReadingProgress } from "../../api/userApi/readingApi";
import PdfViewerModal from "../../pages/pdf/PdfViewerModal";
import toast from "react-hot-toast";

export default function ReadingCard({ item }) {
  const navigate = useNavigate();

  const [wishlistLoading, setWishlistLoading] = useState(false);
  const [showPdf, setShowPdf] = useState(false);
  const [lastPage, setLastPage] = useState(item.page || 1);

  useEffect(() => {
    setLastPage(item.page || 1);
  }, [item.page]);

  const handleAddWishlist = async (e) => {
    e.stopPropagation();
    setWishlistLoading(true);

    try {
      await addToWishlist({
        bookName: item.bookName,
        imageUrl: item.imageUrl,
        bookId: item.bookId,
        pdfPath: item.pdfPath,
      });
      toast.success("Đã thêm vào Wishlist!");
    } catch (err) {
      if (err.response?.data?.message === "This book was added") {
        toast.error("Sách đã có trong Wishlist!");
      } else {
        toast.error("Không thể thêm vào Wishlist!");
      }
    } finally {
      setWishlistLoading(false);
    }
  };

  const handleClosePdf = async () => {
    try {
      await saveReadingProgress(item.bookId, lastPage);
      item.page = lastPage;
      toast.success("Đã lưu tiến trình đọc");
    } catch {
      toast.error("Không thể lưu tiến trình");
    }
    setShowPdf(false);
  };

  return (
    <>
      <div
        className="
          bg-white/10 backdrop-blur-lg 
          rounded-2xl overflow-hidden 
          border border-white/20 shadow-xl
          transition-all duration-300 w-64 cursor-pointer
          hover:shadow-blue-500/40 hover:scale-[1.03]
          flex flex-col
        "
        onClick={() => navigate(`/reading/${item.readingId}`)}
      >
        {/* Ảnh */}
        <div className="relative overflow-hidden">
          <img
            src={item.imageUrl}
            alt={item.bookName}
            className="w-full h-80 object-cover object-top hover:scale-110 transition duration-500"
          />

          <span className="absolute top-2 left-2 px-3 py-1 bg-blue-600/80 text-white text-xs font-semibold rounded-full shadow-md">
            📖 Reading
          </span>
        </div>

        {/* Nội dung */}
        <div className="p-5 flex-1">
          <h4 className="text-lg font-bold mb-1 line-clamp-1 text-white">
            {item.bookName}
          </h4>

          <p className="text-sm text-gray-300 mb-3 flex items-center gap-2">
            <FaBookOpen className="text-blue-300" />
            Progress: page {item.page}
          </p>
        </div>

        {/* ACTION BUTTONS */}
        <div className="px-4 pb-4 mt-auto">
          <div className="flex gap-3">

            {/* ✅ CONTINUE → MỞ PDF */}
            <button
              onClick={(e) => {
                e.stopPropagation();
                setShowPdf(true);
              }}
              className="
                w-1/2 py-3 
                bg-green-600 hover:bg-green-700 
                text-white font-semibold text-sm 
                rounded-xl shadow-md transition
              "
            >
              📖 Continue
            </button>

            {/* Wishlist */}
            <button
              onClick={handleAddWishlist}
              disabled={wishlistLoading}
              className={`
                w-1/2 py-3 
                bg-pink-500 hover:bg-pink-600
                text-white font-semibold text-sm 
                rounded-xl shadow-md transition
                ${wishlistLoading ? "opacity-60 cursor-not-allowed" : ""}
              `}
            >
              {wishlistLoading ? "..." : "❤️ Wishlist"}
            </button>

          </div>
        </div>
      </div>

      {/* 📄 PDF MODAL */}
      {showPdf && (
        <PdfViewerModal
          bookId={item.bookId}
          startPage={item.page}
          title={item.bookName}
          onClose={handleClosePdf}
          onPageChange={(page) => setLastPage(page)}
        />
      )}
    </>
  );
}
