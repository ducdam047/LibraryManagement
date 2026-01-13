import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import toast from "react-hot-toast";

import { getWishlistDetail } from "../../api/userApi/wishlistApi";
import { addToReading, getReadingByBookId } from "../../api/userApi/readingApi";
import { borrowBook } from "../../api/userApi/borrowApi";

import EvaluateModal from "../../components/EvaluateModal";
import { getEvaluations } from "../../api/userApi/evaluateApi";

export default function WishlistBookDetail() {
  const { wishlistId } = useParams();
  const navigate = useNavigate();

  const [item, setItem] = useState(null);
  const [loading, setLoading] = useState(true);

  const [showBorrowModal, setShowBorrowModal] = useState(false);
  const [borrowDays, setBorrowDays] = useState(2);
  const [borrowLoading, setBorrowLoading] = useState(false);

  const [openEvaluate, setOpenEvaluate] = useState(false);
  const [evaluations, setEvaluations] = useState([]);
  const [loadingEvaluate, setLoadingEvaluate] = useState(false);

  const handleBorrow = () => {
    setBorrowDays(2);
    setShowBorrowModal(true);
  };

  const handleBorrowSubmit = async () => {
    if (!item) return;
    setBorrowLoading(true);

    try {
      await borrowBook(item.bookName, borrowDays);
      toast.success("Mượn sách thành công!");
      setShowBorrowModal(false);
    } catch (err) {
      const code = err.response?.data?.code;
      if (code === 3003) toast.error("Bạn đã đạt giới hạn số lượng sách đang mượn!");
      else if (code === 2001) toast.error("Bạn đã mượn cuốn này rồi!");
      else toast.error("Không thể mượn sách!");
    } finally {
      setBorrowLoading(false);
    }
  };

  const handleOpenEvaluate = async () => {
    if (!item) return;

    setLoadingEvaluate(true);
    setOpenEvaluate(true);

    try {
      const res = await getEvaluations(item.bookName);
      setEvaluations(res.data.data || []);
    } catch {
      setEvaluations([]);
    } finally {
      setLoadingEvaluate(false);
    }
  };

  useEffect(() => {
    const fetchDetail = async () => {
      try {
        const data = await getWishlistDetail(wishlistId);
        if (!data) {
          toast.error("Không tìm thấy mục wishlist");
          navigate("/wishlist");
          return;
        }

        setItem(data);
        document.title = `${data.bookName} - Wishlist`;
      } catch {
        toast.error("Lỗi khi tải wishlist");
        navigate("/wishlist");
      } finally {
        setLoading(false);
      }
    };

    fetchDetail();
  }, [wishlistId, navigate]);

  const handleRead = async () => {
    try {
      const existing = await getReadingByBookId(item.bookId);
      const readingId =
        existing?.readingId ?? existing?.id ?? existing?.data?.readingId;

      if (readingId) {
        navigate(`/reading/${readingId}`);
        return;
      }
    } catch (err) {
      if (err.response?.status !== 404) {
        toast.error("Không thể mở sách.");
        return;
      }
    }

    try {
      const newReading = await addToReading(item.bookId);
      const newId =
        newReading?.readingId ?? newReading?.id ?? newReading?.data?.readingId;

      if (!newId) {
        toast.error("Không nhận được ID Reading!");
        return;
      }

      navigate(`/reading/${newId}`);
    } catch {
      toast.error("Không thể mở sách!");
    }
  };

  if (loading) return <p className="text-center py-10">Đang tải...</p>;
  if (!item) return null;

  return (
    <>
      <div className="max-w-5xl mx-auto px-6 py-12">
        <div className="bg-white rounded-2xl shadow-xl p-8 flex flex-col md:flex-row gap-10">

          {/* IMAGE */}
          <div className="flex-shrink-0">
            <img
              src={item.imageUrl}
              alt={item.bookName}
              className="w-64 h-[420px] object-cover rounded-xl shadow-md"
            />
          </div>

          {/* INFO */}
          <div className="flex flex-col justify-between flex-1">
            <div>
              <h1 className="text-4xl font-bold mb-4">{item.bookName}</h1>

              <p className="text-gray-700 mb-4">{item.description}</p>

              <p className="text-sm text-gray-500">
                Người thêm: <b>{item.fullName}</b>
              </p>
              <p className="text-sm text-gray-500">Ngày thêm: {item.createdAt}</p>
            </div>

            {/* BUTTON GROUP */}
            <div className="mt-6 grid grid-cols-3 gap-4">

              {/* READ */}
              {item.pdfPath ? (
                <button
                  onClick={handleRead}
                  className="py-3 w-full bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition"
                >
                  Read
                </button>
              ) : (
                <button className="py-3 w-full bg-gray-400 text-white rounded-xl cursor-not-allowed">
                  No Online Version
                </button>
              )}

              {/* BORROW */}
              <button
                onClick={handleBorrow}
                disabled={borrowLoading}
                className="py-3 w-full bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition"
              >
                {borrowLoading ? "Processing..." : "Borrow"}
              </button>

              {/* EVALUATE */}
              <button
                onClick={handleOpenEvaluate}
                className="py-3 w-full bg-yellow-500 text-white rounded-xl hover:bg-yellow-600 transition"
              >
                {loadingEvaluate ? "Đang tải..." : "Xem đánh giá"}
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* EVALUATE MODAL */}
      <EvaluateModal
        open={openEvaluate}
        onClose={() => setOpenEvaluate(false)}
        evaluations={evaluations}
      />

      {/* BORROW MODAL */}
      {showBorrowModal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white w-80 rounded-2xl shadow-xl p-6">
            <h3 className="text-xl font-semibold mb-3 text-gray-800">Mượn sách</h3>

            <label className="text-gray-600 text-sm">Số ngày muốn mượn</label>
            <input
              type="number"
              min="1"
              value={borrowDays}
              onChange={(e) => setBorrowDays(Number(e.target.value))}
              className="w-full mt-2 p-2 border rounded-lg focus:ring focus:ring-blue-300 text-black"
            />

            <div className="flex justify-end gap-3 mt-5">
              <button
                onClick={() => setShowBorrowModal(false)}
                className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300"
              >
                Hủy
              </button>

              <button
                onClick={handleBorrowSubmit}
                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
              >
                Xác nhận
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
