export default function PendingBorrowCard({ book, onCancel, onPay }) {
  const status = book.borrowStatus;
  
  const isPendingApprove = status === "PENDING_APPROVE";
  const isPendingPayment = status === "PENDING_PAYMENT";

  return (
    <div className="w-[260px] bg-white/10 rounded-xl p-4 text-white flex flex-col">

      <h3
        className="font-semibold text-lg truncate"
        title={book.title}
      >
        {book.title}
      </h3>

      <p className="text-sm text-gray-300 mt-1">
        Số ngày mượn: {book.borrowDays} ngày
      </p>

      {/* STATUS TEXT */}
      {isPendingApprove && (
        <div className="mt-3 text-yellow-400 text-sm">
          ⏳ Đang chờ duyệt mượn
        </div>
      )}

      {isPendingPayment && (
        <div className="mt-3 text-orange-400 text-sm">
          💰 Chờ thanh toán
        </div>
      )}

      {/* ACTION BUTTON */}
      {isPendingApprove && (
        <button
          onClick={() => onCancel(book.loanId)}
          className="mt-auto w-full bg-red-500/80 hover:bg-red-600
            text-white py-2 rounded-lg transition"
        >
          Huỷ mượn
        </button>
      )}

      {isPendingPayment && (
        <button
          onClick={() => onPay(book.loanId)}
          className="mt-auto w-full bg-green-500/80 hover:bg-green-600
            text-white py-2 rounded-lg transition"
        >
          Thanh toán & Nhận sách
        </button>
      )}

    </div>
  );
}
