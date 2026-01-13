import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { ArrowLeft, FileText } from "lucide-react";

export default function ManageBookDetail() {
  const { state } = useLocation();
  const navigate = useNavigate();
  const book = state?.book;

  if (!book) {
    return (
      <div className="p-6 text-white">
        <p>No book data provided.</p>
      </div>
    );
  }

  return (
    <div className="p-8 text-white bg-black min-h-screen">

      {/* BACK BUTTON */}
      <button
        onClick={() => navigate(-1)}
        className="flex items-center gap-2 text-zinc-300 hover:text-white mb-6"
      >
        <ArrowLeft size={20} />
        Back
      </button>

      {/* MAIN CARD */}
      <div className="bg-zinc-900 p-8 rounded-2xl border border-zinc-800 shadow-xl flex gap-10">

        {/* LEFT: Book Image */}
        <div className="flex flex-col items-center">
          <img
            src={book.imageUrl}
            alt={book.title}
            className="w-56 h-80 object-cover rounded-xl shadow-lg border border-zinc-700"
          />

          <a
            href={book.pdfPath}
            target="_blank"
            rel="noopener noreferrer"
            className="mt-5 flex items-center gap-2 bg-blue-600 hover:bg-blue-500 px-4 py-2 rounded-lg transition"
          >
            <FileText size={18} />
            View PDF
          </a>
        </div>

        {/* RIGHT: Info */}
        <div className="flex-1">
          <h1 className="text-4xl font-bold mb-4 text-white">{book.title}</h1>

          <p className="text-lg text-zinc-300 mb-3">
            <span className="font-semibold text-white">Author:</span> {book.author}
          </p>

          <p className="text-lg text-zinc-300 mb-3">
            <span className="font-semibold text-white">Category:</span> {book.categoryName}
          </p>

          <p className="text-lg text-zinc-300 mb-3">
            <span className="font-semibold text-white">Publisher:</span> {book.publisherName}
          </p>

          {/* STATUS */}
          <p className="text-lg mt-5">
            <span className="font-semibold text-white">Status: </span>
            {book.status === "AVAILABLE" ? (
              <span className="px-3 py-1 bg-green-600/20 text-green-400 rounded-lg">
                Available
              </span>
            ) : (
              <span className="px-3 py-1 bg-orange-600/20 text-orange-400 rounded-lg">
                Borrowed
              </span>
            )}
          </p>

          {/* COPIES */}
          <div className="mt-6 grid grid-cols-3 gap-4 max-w-md">
            <div className="bg-zinc-800 p-4 rounded-xl text-center border border-zinc-700">
              <p className="text-sm text-zinc-400">Total</p>
              <p className="text-2xl font-bold">{book.totalCopies}</p>
            </div>

            <div className="bg-zinc-800 p-4 rounded-xl text-center border border-zinc-700">
              <p className="text-sm text-zinc-400">Available</p>
              <p className="text-2xl font-bold text-green-400">
                {book.availableCopies}
              </p>
            </div>

            <div className="bg-zinc-800 p-4 rounded-xl text-center border border-zinc-700">
              <p className="text-sm text-zinc-400">Borrowed</p>
              <p className="text-2xl font-bold text-orange-400">
                {book.borrowedCopies}
              </p>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
}
