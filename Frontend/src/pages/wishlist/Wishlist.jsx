import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getWishlist } from "../../api/userApi/wishlistApi";
import WishlistCard from "../../components/wishlist/WishlistCard";

export default function Wishlist() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const fetchWishlist = async () => {
    try {
      const res = await getWishlist();
      setItems(res || []);
    } catch (err) {
      console.error("Lỗi tải wishlist:", err);
      setItems([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchWishlist();
  }, []);

  // ⬅️ HÀM XOÁ TRONG UI (KHÔNG CẦN LOAD LẠI)
  const handleRemoved = (removedId) => {
    setItems((prev) => prev.filter((it) => it.wishlistId !== removedId));
  };

  if (loading) {
    return (
      <p className="text-center text-gray-300 py-10 text-lg animate-pulse">
        Đang tải wishlist...
      </p>
    );
  }

  return (
    <section className="pt-16 -mt-8 pb-12 relative w-full">
      <div className="px-10">
        <h1 className="text-3xl font-semibold text-white mb-10 flex items-center gap-3">
          💙 <span>Danh sách yêu thích</span>
        </h1>

        {items.length === 0 ? (
          <p className="text-gray-300 mt-6 text-lg italic">
            Bạn chưa lưu cuốn sách nào vào wishlist.
          </p>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-8">
            {items.map((item) => (
              <WishlistCard
                key={item.wishlistId}
                item={item}
                onRemoved={handleRemoved} // ⬅️ TRUYỀN XUỐNG
              />
            ))}
          </div>
        )}
      </div>
    </section>
  );
}
