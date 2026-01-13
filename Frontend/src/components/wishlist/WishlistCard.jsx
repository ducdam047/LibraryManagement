import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import ConfirmModal from "../common/ConfirmModal";
import { removeFromWishlist } from "../../api/userApi/wishlistApi";

export default function WishlistCard({ item, onRemoved }) {
  const navigate = useNavigate();
  const [openConfirm, setOpenConfirm] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleOpenDetail = () => {
    navigate(`/book/detail/${encodeURIComponent(item.bookName)}`);
  };

  const handleDelete = async () => {
    try {
      setLoading(true);
      await removeFromWishlist(item.wishlistId);
      onRemoved(item.wishlistId);
    } catch (err) {
      console.error("Xoá thất bại:", err);
    } finally {
      setLoading(false);
      setOpenConfirm(false);
    }
  };

  return (
    <>
      <div
        className="
          bg-white/10 backdrop-blur-lg 
          rounded-2xl overflow-hidden 
          border border-white/20 
          shadow-xl transition-all duration-300
          hover:shadow-blue-500/40 hover:scale-[1.02]
          flex flex-col
        "
      >
        {/* IMAGE */}
        <div onClick={handleOpenDetail} className="relative cursor-pointer">
          <img
            src={item.imageUrl}
            alt={item.bookName}
            className="w-full h-80 object-cover object-top hover:scale-110 transition duration-500"
            style={{ aspectRatio: "3 / 5" }}
          />

          <span
            className="
              absolute top-2 left-2 px-3 py-1 
              bg-blue-600/80 text-white text-xs font-semibold 
              rounded-full shadow-md
            "
          >
            ❤️ Saved
          </span>
        </div>

        {/* CONTENT */}
        <div
          onClick={handleOpenDetail}
          className="p-5 text-white cursor-pointer flex-1"
        >
          <h4 className="text-lg font-bold line-clamp-1">
            {item.bookName}
          </h4>

          <p className="text-xs text-gray-400 mt-3">
            {item.createdAt}
          </p>
        </div>

        {/* DELETE BUTTON */}
        <button
          onClick={(e) => {
            e.stopPropagation(); // ⛔ không trigger card click
            setOpenConfirm(true);
          }}
          className="
            mx-4 mb-4 mt-2
            bg-red-600 hover:bg-red-700
            text-white w-[calc(100%-2rem)]
            py-2 rounded-xl transition font-semibold
          "
        >
          {loading ? "Đang xoá..." : "Xoá khỏi Wishlist"}
        </button>
      </div>

      {/* CONFIRM MODAL */}
      <ConfirmModal
        open={openConfirm}
        title="Xác nhận xoá"
        message="Bạn có chắc muốn xoá cuốn sách này khỏi Wishlist?"
        onClose={() => setOpenConfirm(false)}
        onConfirm={handleDelete}
      />
    </>
  );
}
