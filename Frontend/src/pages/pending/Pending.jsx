import React, { useEffect, useRef, useState } from "react";
import PendingBorrowCard from "../../components/pending/PendingBorrowCard";
import PendingReturnCard from "../../components/pending/PendingReturnCard";
import { getPendingBorrow, getPendingReturn, cancelPendingBorrow } from "../../api/userApi/pendingApi";
import { addToWishlist } from "../../api/userApi/wishlistApi";
import ConfirmModal from "../../components/common/ConfirmModal";
import { ChevronLeft, ChevronRight } from "lucide-react";
import toast from "react-hot-toast";

export default function Pending() {
    const [pendingBorrow, setPendingBorrow] = useState([]);
    const [pendingReturn, setPendingReturn] = useState([]);
    const [loading, setLoading] = useState(true);

    const borrowRef = useRef(null);
    const returnRef = useRef(null);

    const [borrowScroll, setBorrowScroll] = useState({ left: false, right: false });
    const [returnScroll, setReturnScroll] = useState({ left: false, right: false });

    const [confirmRecord, setConfirmRecord] = useState(null);

    const [showWishlistModal, setShowWishlistModal] = useState(false);
    const [wishlistBook, setWishlistBook] = useState(null);

    useEffect(() => {
        load();
    }, []);

    async function load() {
        setLoading(true);
        try {
            const borrow = await getPendingBorrow();
            const ret = await getPendingReturn();

            setPendingBorrow((borrow || []).reverse());
            setPendingReturn((ret || []).reverse());
        } catch (err) {
            console.error("Load pending error", err);
        } finally {
            setLoading(false);
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

    const handleCancelBorrow = (recordId) => {
        setConfirmRecord(recordId);
    };

    const handleConfirmCancel = async () => {
        if (!confirmRecord) return;

        const cancelledBook = pendingBorrow.find(
            b => b.recordId === confirmRecord
        );

        try {
            await cancelPendingBorrow(confirmRecord);
            toast.success("Đã huỷ yêu cầu mượn");

            setPendingBorrow(prev =>
                prev.filter(b => b.recordId !== confirmRecord)
            );

            // 👉 mở modal wishlist
            setWishlistBook(cancelledBook);
            setShowWishlistModal(true);

        } catch (err) {
            toast.error(
                err.response?.data?.message || "Huỷ yêu cầu mượn thất bại"
            );
        } finally {
            setConfirmRecord(null);
        }
    };

    const handleConfirmWishlist = async () => {
        if (!wishlistBook?.title) return;

        try {
            await addToWishlist({ bookName: wishlistBook.title });
            toast.success("Đã thêm vào Wishlist!");
        } catch (err) {
            if (err.response?.data?.message === "This book was added") {
                toast.error("Sách đã có trong Wishlist!");
            } else {
                toast.error("Lỗi khi thêm vào Wishlist!");
            }
        } finally {
            setShowWishlistModal(false);
            setWishlistBook(null);
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
        <div>
            <section className="pt-16 -mt-8 pb-12 w-full">
                <div className="px-10">

                    {/* PENDING BORROW */}
                    <h2 className="text-3xl font-semibold text-white mb-8 flex items-center gap-3">
                        ⏳ <span>Sách đang chờ duyệt mượn</span>
                    </h2>

                    {pendingBorrow.length === 0 ? (
                        <p className="text-gray-400 italic mb-12">
                            Không có yêu cầu mượn nào đang chờ.
                        </p>
                    ) : (
                        <div className="relative mb-20">
                            {borrowScroll.left && (
                                <button
                                    onClick={() => scroll(borrowRef, "left")}
                                    className="absolute left-0 top-1/2 -translate-y-1/2
                  bg-white/20 hover:bg-white/40 text-white p-3 rounded-full z-20"
                                >
                                    <ChevronLeft />
                                </button>
                            )}

                            <div
                                ref={borrowRef}
                                onScroll={() => checkScroll(borrowRef, setBorrowScroll)}
                                className="flex gap-8 overflow-x-auto no-scrollbar pb-4"
                            >
                                {pendingBorrow.map((book) => (
                                    <PendingBorrowCard
                                        key={book.recordId}
                                        book={book}
                                        onCancel={handleCancelBorrow}
                                    />
                                ))}
                            </div>

                            {borrowScroll.right && (
                                <button
                                    onClick={() => scroll(borrowRef, "right")}
                                    className="absolute right-0 top-1/2 -translate-y-1/2
                  bg-white/20 hover:bg-white/40 text-white p-3 rounded-full z-20"
                                >
                                    <ChevronRight />
                                </button>
                            )}
                        </div>
                    )}

                    {/* PENDING RETURN */}
                    <h2 className="text-3xl font-semibold text-white mb-8 flex items-center gap-3">
                        📦 <span>Sách đang chờ xác nhận trả</span>
                    </h2>

                    {pendingReturn.length === 0 ? (
                        <p className="text-gray-400 italic">
                            Không có sách nào đang chờ nhận trả.
                        </p>
                    ) : (
                        <div className="relative">
                            {returnScroll.left && (
                                <button
                                    onClick={() => scroll(returnRef, "left")}
                                    className="absolute left-0 top-1/2 -translate-y-1/2
                  bg-white/20 hover:bg-white/40 text-white p-3 rounded-full z-20"
                                >
                                    <ChevronLeft />
                                </button>
                            )}

                            <div
                                ref={returnRef}
                                onScroll={() => checkScroll(returnRef, setReturnScroll)}
                                className="flex gap-8 overflow-x-auto no-scrollbar pb-4"
                            >
                                {pendingReturn.map((book) => (
                                    <PendingReturnCard
                                        key={book.recordId}
                                        book={book}
                                    />
                                ))}
                            </div>

                            {returnScroll.right && (
                                <button
                                    onClick={() => scroll(returnRef, "right")}
                                    className="absolute right-0 top-1/2 -translate-y-1/2
                  bg-white/20 hover:bg-white/40 text-white p-3 rounded-full z-20"
                                >
                                    <ChevronRight />
                                </button>
                            )}
                        </div>
                    )}
                </div>
            </section>

            <ConfirmModal
                open={!!confirmRecord}
                title="Xác nhận huỷ mượn?"
                message="Bạn có chắc muốn huỷ yêu cầu mượn sách này không?"
                onClose={() => setConfirmRecord(null)}
                onConfirm={handleConfirmCancel}
            />

            <ConfirmModal
                open={showWishlistModal}
                title="💖 Thêm vào Wishlist?"
                message={
                    wishlistBook
                        ? `Bạn có muốn thêm sách “${wishlistBook.title}” vào Wishlist để xem lại sau không?`
                        : ""
                }
                onClose={() => {
                    setShowWishlistModal(false);
                    setWishlistBook(null);
                }}
                onConfirm={handleConfirmWishlist}
            />


        </div>
    );
}
