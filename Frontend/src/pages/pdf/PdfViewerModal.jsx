import PdfReader from "../pdf/PdfReader";

export default function PdfViewerModal({
  bookId,
  startPage,
  title,
  onClose,
  onPageChange
}) {
  return (
    <div className="fixed inset-0 bg-black/60 flex justify-center items-center z-[9999]">
      <div className="bg-white w-[90%] h-[90%] rounded-xl overflow-hidden shadow-xl flex flex-col">

        <div className="w-full bg-gray-100 px-4 py-2 flex justify-between items-center border-b">
          <span className="font-semibold text-gray-700">📖 {title}</span>

          <button
            onClick={onClose}
            className="bg-red-500 text-white px-3 py-1 rounded-lg hover:bg-red-600"
          >
            Close
          </button>
        </div>

        {/* PDF.js Viewer */}
        <PdfReader
          key={bookId}   // 🔥 QUAN TRỌNG
          pdfUrl={`http://localhost:8080/pdf/${bookId}/preview`}
          startPage={startPage}
          onPageChange={onPageChange}
        />
      </div>
    </div>
  );
}
