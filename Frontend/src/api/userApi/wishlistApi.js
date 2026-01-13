import axiosConfig from "../axiosConfig";

// 🟢 Get all wishlist
export const getWishlist = async () => {
  try {
    const res = await axiosConfig.get("/wishlist");
    return res.data?.data ?? [];
  } catch (err) {
    console.error("Lỗi getWishlist:", err);
    return [];
  }
};

// 🔵 Get wishlist detail
export const getWishlistDetail = async (wishlistId) => {
  try {
    const res = await axiosConfig.get(`/wishlist/${wishlistId}`);
    return res.data?.data ?? null;
  } catch (err) {
    console.error("Lỗi getWishlistDetail:", err);
    return null;
  }
};

// 🔵 Add to wishlist
export const addToWishlist = async (data) => {
  try {
    const res = await axiosConfig.post("/wishlist", data);
    return res.data?.data ?? null;
  } catch (err) {
    console.error("Lỗi addToWishlist:", err);
    throw err;
  }
};

// 🔴 Remove from wishlist
export const removeFromWishlist = async (wishlistId) => {
  try {
    const res = await axiosConfig.delete(`/wishlist/${wishlistId}`);
    return res.data?.message ?? "Deleted";
  } catch (err) {
    console.error("Lỗi removeFromWishlist:", err);
    throw err;
  }
};
