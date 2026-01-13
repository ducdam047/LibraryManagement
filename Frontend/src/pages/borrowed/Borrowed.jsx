import React, { useEffect, useState, useRef } from "react";
import { getBorrowedBooks, getReturnedBooks, returnBook } from "../../api/userApi/borrowApi";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import BorrowedCard from "../../components/borrowed/BorrowedCard";
import ReturnedCard from "../../components/borrowed/ReturnedCard";
import { ChevronLeft, ChevronRight } from "lucide-react";
import ConfirmModal from "../../components/common/ConfirmModal";

export default function Borrowed() {
  const [borrowed, setBorrowed] = useState([]);
  const [returned, setReturned] = useState([]);
  const [loading, setLoading] = useState(true);

  const [confirmData, setConfirmData] = useState(null);
  const navigate = useNavigate();

  // refs for scroll
  const borrowedRef = useRef(null);
  const returnedRef = useRef(null);

  const [borrowedScroll, setBorrowedScroll] = useState({ left: false, right: false });
  const [returnedScroll, setReturnedScroll] = useState({ left: false, right: false });

  // Load active + returned books
  useEffect(() => {
    load();
  }, []);

  async function load() {
    setLoading(true);
    try {
      // 🔥 NEW: lấy danh sách bản ghi đang mượn (ACTIVE + OVERDUE)
      const activeRecords = await getBorrowedBooks();

      // chuyển record → dữ liệu card để không phải sửa BorrowedCard
      const mappedActive = (activeRecords || []).map(r => ({
        recordId: r.recordId,
        bookId: r.bookId,
        title: r.title,
        author: r.author,
        imageUrl: r.imageUrl,

        borrowDay: r.borrowDay,
        dueDay: r.dueDay,
        returnedDay: r.returnedDay,

        status: r.status,        // ACTIVE | OVERDUE
        extendCount: r.extendCount,
      }));

      const done = await getReturnedBooks();

      setBorrowed(mappedActive.slice().reverse());
      setReturned((done || []).slice().reverse());
    } catch (err) {
      console.error("Error loading borrowed/returned books", err);
    } finally {
      setLoading(false);

      setTimeout(() => {
        checkScroll(borrowedRef, setBorrowedScroll);
        checkScroll(returnedRef, setReturnedScroll);
      }, 120);
    }
  }

  const checkScroll = (ref, setter) => {
    if (!ref.current) return;
    const el = ref.current;

    setter({
      left: el.scrollLeft > 0,
      right: el.scrollLeft < el.scrollWidth - el.clientWidth - 5,
    });
  };

  const scroll = (ref, dir) => {
    if (!ref.current) return;
    ref.current.scrollBy({
      left: dir === "left" ? -350 : 350,
      behavior: "smooth",
    });
  };

  useEffect(() => {
    const handleResize = () => {
      checkScroll(borrowedRef, setBorrowedScroll);
      checkScroll(returnedRef, setReturnedScroll);
    };

    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, [borrowed, returned]);

  // Confirm return
  const handleConfirmReturn = async () => {
    if (!confirmData) return;

    try {
      const res = await returnBook(confirmData.bookId);
      toast.success(res.message || "Đã gửi yêu cầu trả sách!");
      setConfirmData(null);
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || "Lỗi khi trả sách");
    }
  };

  if (loading) {
    return (
      <p className="text-center text-gray-300 py-10 text-lg animate-pulse">
        Đang tải danh sách...
      </p>
    );
  }

  return (
    <section className="pt-16 -mt-8 pb-12 w-full relative">
      <div className="px-10">

        {/* ACTIVE BORROWS */}
        <h2 className="text-3xl font-semibold text-white mb-8 flex items-center gap-3">
          📚 <span>Sách đang mượn</span>
        </h2>

        {borrowed.length === 0 ? (
          <p className="text-gray-400 italic mb-12">Bạn hiện chưa mượn cuốn nào.</p>
        ) : (
          <div className="relative mb-20">
            {borrowedScroll.left && (
              <button
                onClick={() => scroll(borrowedRef, "left")}
                className="absolute left-0 top-1/2 -translate-y-1/2
                  bg-white/20 hover:bg-white/40 text-white p-3 rounded-full
                  backdrop-blur-md shadow-lg z-20"
              >
                <ChevronLeft size={24} />
              </button>
            )}

            <div
              ref={borrowedRef}
              onScroll={() => checkScroll(borrowedRef, setBorrowedScroll)}
              className="flex gap-8 overflow-x-auto no-scrollbar pb-4 scroll-smooth"
            >
              {borrowed.map((rec) => (
                <BorrowedCard
                  key={rec.recordId}
                  book={rec}
                  onReturn={() => setConfirmData(rec)}
                />
              ))}
            </div>

            {borrowedScroll.right && (
              <button
                onClick={() => scroll(borrowedRef, "right")}
                className="absolute right-0 top-1/2 -translate-y-1/2
                  bg-white/20 hover:bg-white/40 text-white p-3 rounded-full
                  backdrop-blur-md shadow-lg z-20"
              >
                <ChevronRight size={24} />
              </button>
            )}
          </div>
        )}

        {/* RETURNED BOOKS */}
        <h2 className="text-3xl font-semibold text-white mb-8 flex items-center gap-3">
          📘 <span>Sách đã trả</span>
        </h2>

        {returned.length === 0 ? (
          <p className="text-gray-400 italic">Bạn chưa trả cuốn nào.</p>
        ) : (
          <div className="relative">
            {returnedScroll.left && (
              <button
                onClick={() => scroll(returnedRef, "left")}
                className="absolute left-0 top-1/2 -translate-y-1/2
                  bg-white/20 hover:bg-white/40 text-white p-3 rounded-full
                  backdrop-blur-md shadow-lg z-20"
              >
                <ChevronLeft size={24} />
              </button>
            )}

            <div
              ref={returnedRef}
              onScroll={() => checkScroll(returnedRef, setReturnedScroll)}
              className="flex gap-8 overflow-x-auto no-scrollbar pb-4 scroll-smooth"
            >
              {returned.map((record) => (
                <ReturnedCard
                  key={record.recordId}
                  record={record}
                />
              ))}
            </div>

            {returnedScroll.right && (
              <button
                onClick={() => scroll(returnedRef, "right")}
                className="absolute right-0 top-1/2 -translate-y-1/2
                  bg-white/20 hover:bg-white/40 text-white p-3 rounded-full
                  backdrop-blur-md shadow-lg z-20"
              >
                <ChevronRight size={24} />
              </button>
            )}
          </div>
        )}
      </div>

      {/* CONFIRM RETURN MODAL */}
      <ConfirmModal
        open={!!confirmData}
        title="Xác nhận trả sách?"
        message={
          confirmData
            ? `Bạn có chắc muốn trả "${confirmData.title}"?`
            : ""
        }
        onClose={() => setConfirmData(null)}
        onConfirm={handleConfirmReturn}
      />
    </section>
  );
}
