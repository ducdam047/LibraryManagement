import React from "react";

export default function PendingReturnCard({ book }) {
  if (!book) {
    return (
      <div
        className="
          w-64 flex-shrink-0 bg-white/6 rounded-2xl overflow-hidden
          border border-white/10 p-4 animate-pulse
        "
      >
        <div className="w-full h-44 bg-white/8 rounded-md mb-3" />
        <div className="h-4 bg-white/8 rounded w-3/4 mb-2" />
        <div className="h-3 bg-white/8 rounded w-1/2 mb-2" />
        <div className="h-6 bg-white/8 rounded w-full mt-3" />
      </div>
    );
  }

  return (
    <div
      className="
        bg-white/10 backdrop-blur-lg 
        rounded-2xl overflow-hidden 
        border border-white/20 
        shadow-xl
        w-64 flex-shrink-0
        flex flex-col
      "
    >
      {/* IMAGE */}
      <div className="relative overflow-hidden">
        <img
          src={book.imageUrl}
          alt={book.title}
          className="w-full h-80 object-cover object-top"
        />

        <span className="absolute top-2 right-2 px-3 py-1 bg-blue-500 text-white text-xs font-semibold rounded-full shadow-md">
          Chờ trả
        </span>
      </div>

      {/* CONTENT */}
      <div className="p-5 text-white flex flex-col flex-1">
        <div className="flex-1">
          <h4 className="text-lg font-bold truncate">
            {book.title}
          </h4>

          <p className="text-sm text-gray-300 mt-1 overflow-hidden text-ellipsis whitespace-nowrap">
            {book.author}
          </p>

          <p className="mt-3 text-sm text-blue-400">
            📦 Đang chờ xác nhận trả
          </p>
        </div>
      </div>
    </div>
  );
}
