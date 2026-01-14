export default function PendingBorrowCard({ book, onCancel }) {
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

      <div className="mt-3 text-yellow-400 text-sm">
        ⏳ Đang chờ duyệt mượn
      </div>

      {/* mt-auto sẽ đẩy nút xuống đáy */}
      <button
        onClick={() => onCancel(book.loanId)}
        className="mt-auto w-full bg-red-500/80 hover:bg-red-600
          text-white py-2 rounded-lg transition"
      >
        Huỷ mượn
      </button>

    </div>
  );
}
