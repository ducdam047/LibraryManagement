import React, { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { useParams, useNavigate } from "react-router-dom";
import { borrowBook } from "../../api/userApi/borrowApi";
import { addToWishlist } from "../../api/userApi/wishlistApi";
import { getBookDetail } from "../../api/userApi/bookApi";
import { getEvaluations } from "../../api/userApi/evaluateApi";
import EvaluateModal from "../../components/EvaluateModal";
import PdfViewerModal from "../pdf/PdfViewerModal";
import { addToReading, getReadingByBookId } from "../../api/userApi/readingApi";

export default function BookDetail() {
  const { title } = useParams();
  const navigate = useNavigate();

  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showPdf, setShowPdf] = useState(false);
  const [pdfLoading, setPdfLoading] = useState(false);
  const [wishlistLoading, setWishlistLoading] = useState(false);
  const [borrowLoading, setBorrowLoading] = useState(false);
  const [showBorrowModal, setShowBorrowModal] = useState(false);
  const [borrowDays, setBorrowDays] = useState(7);
  const [showEvaluateModal, setShowEvaluateModal] = useState(false);
  const [evaluations, setEvaluations] = useState([]);
  const [evaluateLoading, setEvaluateLoading] = useState(false);

  const handleOpenEvaluate = async () => {
    if (!book) return;
    setEvaluateLoading(true);
    setShowEvaluateModal(true);
    try {
      const res = await getEvaluations(book.title);
      setEvaluations(res.data.data || []);
    } catch (err) {
      setEvaluations([]);
    } finally {
      setEvaluateLoading(false);
    }
  };

  const handleAddWishlist = async () => {
    if (!book) return;
    setWishlistLoading(true);
    try {
      await addToWishlist({ bookName: book.title });
      toast.success("Đã thêm vào Wishlist!");
    } catch (err) {
      if (err.response?.data?.message === "This book was added") {
        toast.error("Sách đã có trong Wishlist!");
      } else {
        toast.error("Lỗi khi thêm vào Wishlist!");
      }
    } finally {
      setWishlistLoading(false);
    }
  };

  const handleRead = async () => {
    try {
      if (!book) return;
      let existing = null;
      try {
        existing = await getReadingByBookId(book.bookId);
      } catch (_) { }

      const readingId = existing?.readingId || existing?.id || existing?.data?.readingId;
      if (readingId) {
        navigate(`/reading/${readingId}`);
        return;
      }

      const res = await addToReading(book.bookId);
      const newId = res?.readingId || res?.id || res?.data?.readingId;
      if (!newId) return toast.error("Không lấy được ID Reading!");

      navigate(`/reading/${newId}`);
    } catch (err) {
      toast.error("Không thể mở sách!");
    }
  };

  const handleBorrow = () => {
    if (!book) return;
    setShowBorrowModal(true);
  };

  const handleBorrowSubmit = async () => {
    setBorrowLoading(true);
    try {
      await borrowBook(book.title, borrowDays);
      toast.success("Yêu cầu mượn sách thành công!");
      setShowBorrowModal(false);
    } catch (err) {
      const code = err.response?.data?.code;
      if (code === 3003) toast.error("Bạn không thể mượn quá 5 quyển sách!");
      else if (code === 2001) toast.error("Bạn đã mượn cuốn này rồi!");
      else if (code === 2002) toast.error("Bạn đã yêu cầu mượn cuốn này rồi!");
      else if (code === 3002) toast.error("Tài khoản của bạn đã bị cấm!");
      else toast.error("Không thể mượn sách!");
    } finally {
      setBorrowLoading(false);
    }
  };

  const formatDate = (date) =>
    date.toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });

  useEffect(() => {
    let cancelled = false;

    const fetchBook = async () => {
      try {
        const res = await getBookDetail(title);

        if (!res) {
          document.title = "Không tìm thấy sách";
          return;
        }

        if (!cancelled) {
          setBook(res);
          document.title = `${res.title} - Chi tiết sách`;
        }
      } catch (err) {
        console.error(err);
        document.title = "Không tìm thấy sách";
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    fetchBook();

    return () => {
      cancelled = true;
    };
  }, [title]);


  if (loading)
    return <p className="text-center py-10 text-gray-600 animate-pulse">Đang tải dữ liệu...</p>;

  if (!book)
    return <p className="text-center py-10 text-red-500 text-lg font-semibold">Không tìm thấy sách</p>;

  return (
    <div className="max-w-5xl mx-auto px-6 pt-4 pb-12 space-y-10">
      <div className="mb-4 flex items-center gap-2 text-blue-600">
        <button
          onClick={() => navigate(-1)}
          className="flex items-center gap-2 hover:underline font-medium"
        >
          ← Quay lại
        </button>
      </div>

      <div className="bg-white rounded-3xl shadow-xl p-10 border border-gray-200 flex flex-col md:flex-row gap-10 relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-indigo-50 via-white to-blue-50 opacity-60 pointer-events-none"></div>

        <div className="flex-shrink-0 relative z-10">
          <img
            src={book.imageUrl}
            alt={book.title}
            className="w-64 h-[420px] object-cover rounded-2xl shadow-lg border border-gray-200"
          />
        </div>

        <div className="flex flex-col justify-between relative z-10">
          <div>
            <h1 className="text-4xl font-extrabold text-gray-900 mb-4 leading-snug drop-shadow-sm">
              {book.title}
            </h1>

            <div className="space-y-3 text-gray-700 text-lg">
              <p><span className="font-semibold text-gray-900">Tác giả:</span> {book.author}</p>
              <p><span className="font-semibold text-gray-900">Thể loại:</span> {book.categoryName}</p>
              <p><span className="font-semibold text-gray-900">Nhà xuất bản:</span> {book.publisherName}</p>
              <p><span className="font-semibold text-gray-900">Tổng bản:</span> {book.totalCopies}</p>
              <p>
                <span className="font-semibold text-gray-900">Còn lại:</span>{" "}
                <span className={`font-bold ${book.availableCopies > 0 ? "text-green-600" : "text-red-500"}`}>
                  {book.availableCopies}
                </span>
              </p>
            </div>
          </div>

          {/* ACTION BUTTON SECTION */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 p-6 
bg-white rounded-3xl border border-gray-200 shadow-md">

            {/* Read Online */}
            <button
              onClick={handleRead}
              disabled={!book.pdfPath}
              className={`h-12 rounded-xl font-semibold transition-all
    ${book.pdfPath
                  ? "bg-green-600 hover:bg-green-700 text-white"
                  : "bg-gray-300 text-gray-500 cursor-not-allowed"
                }`}
            >
              📖 Read Preview
            </button>

            {/* Wishlist */}
            <button
              onClick={handleAddWishlist}
              disabled={wishlistLoading}
              className="h-12 rounded-xl bg-pink-500 hover:bg-pink-600 
    text-white font-semibold transition-all"
            >
              ❤️ Wishlist
            </button>

            {/* Borrow */}
            <button
              onClick={handleBorrow}
              disabled={borrowLoading || book.availableCopies === 0}
              className={`h-12 rounded-xl font-semibold transition-all
    ${book.availableCopies > 0
                  ? "bg-blue-600 hover:bg-blue-700 text-white"
                  : "bg-gray-400 text-gray-500 cursor-not-allowed"
                }`}
            >
              📄 Borrow
            </button>

            {/* Evaluate */}
            <button
              onClick={handleOpenEvaluate}
              className="h-12 rounded-xl bg-purple-600 hover:bg-purple-700 
    text-white font-semibold transition-all"
            >
              ⭐ Reviews
            </button>
          </div>

        </div>
      </div>

      <div className="bg-white p-10 rounded-3xl shadow-lg border border-gray-200">
        <h2 className="text-3xl font-bold text-gray-900 mb-4">Tóm tắt nội dung</h2>
        <p className="text-gray-700 leading-relaxed text-lg whitespace-pre-line">
          {book.description || "Chưa có mô tả cho cuốn sách này."}
        </p>
      </div>

      {showPdf && (
        <PdfViewerModal
          pdfPath={book.pdfPath}
          title={book.title}
          onClose={() => setShowPdf(false)}
          pdfLoading={pdfLoading}
          setPdfLoading={setPdfLoading}
        />
      )}

      {showBorrowModal && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50">
          <div className="bg-white w-[420px] rounded-3xl shadow-2xl p-6 animate-fadeIn">

            {/* HEADER */}
            <h3 className="text-2xl font-bold text-gray-900 mb-1">
              📄 Phiếu mượn sách
            </h3>
            <p className="text-sm text-gray-500 mb-5">
              Vui lòng kiểm tra thông tin trước khi xác nhận
            </p>

            {/* BOOK INFO */}
            <div className="bg-gray-50 rounded-2xl p-4 border border-gray-200 mb-4">
              <p className="font-semibold text-gray-900">{book.title}</p>
              <p className="text-sm text-gray-600">✍ {book.author}</p>
              <p className="text-sm text-gray-600">
                🏢 {book.publisherName}
              </p>
            </div>

            {/* BORROW INFO */}
            <div className="space-y-3">
              {/* Borrow days */}
              <div>
                <label className="text-sm text-gray-600 font-medium">
                  Số ngày mượn
                </label>
                <input
                  type="number"
                  min="1"
                  max="30"
                  value={borrowDays}
                  onChange={(e) => setBorrowDays(Number(e.target.value))}
                  className="w-full mt-1 p-2 border rounded-xl focus:ring focus:ring-blue-300 text-black"
                />
              </div>

              {/* Dates */}
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div className="bg-gray-100 p-3 rounded-xl">
                  <p className="text-gray-500">Ngày mượn</p>
                  <p className="font-semibold text-gray-900">
                    {formatDate(new Date())}
                  </p>
                </div>

                <div className="bg-blue-50 p-3 rounded-xl border border-blue-200">
                  <p className="text-gray-500">Ngày trả</p>
                  <p className="font-semibold text-blue-700">
                    {formatDate(
                      new Date(new Date().setDate(new Date().getDate() + borrowDays))
                    )}
                  </p>
                </div>
              </div>
            </div>

            {/* NOTE */}
            <div className="mt-4 text-xs text-gray-500 italic">
              ⚠ Trả sách đúng hạn để tránh bị phạt hoặc hạn chế mượn sách.
            </div>

            {/* ACTIONS */}
            <div className="flex justify-end gap-3 mt-6">
              <button
                onClick={() => setShowBorrowModal(false)}
                className="px-4 py-2 rounded-xl bg-gray-200 hover:bg-gray-300 text-gray-700"
              >
                Hủy
              </button>

              <button
                onClick={handleBorrowSubmit}
                disabled={borrowLoading}
                className="px-5 py-2 rounded-xl bg-blue-600 hover:bg-blue-700 text-white font-semibold"
              >
                {borrowLoading ? "Đang xử lý..." : "Xác nhận mượn"}
              </button>
            </div>
          </div>
        </div>
      )}


      <EvaluateModal
        open={showEvaluateModal}
        onClose={() => setShowEvaluateModal(false)}
        evaluations={evaluations}
        bookId={book.bookId}
        bookTitle={book.title}
      />
    </div>
  );
}
