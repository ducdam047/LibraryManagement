import axiosConfig from "../axiosConfig";

export const getFeaturedBooks = async () => {
  try {
    const res = await axiosConfig.get("/api/books/featured");
    return res.data?.data ?? [];
  } catch (err) {
    console.error("Lỗi getFeaturedBooks:", err);
    return [];
  }
};

export const getTrendingBooks = async () => {
  try {
    const res = await axiosConfig.get("/api/books/trending");
    return res.data?.data ?? [];
  } catch (err) {
    console.error("Lỗi getTrendingBooks:", err);
    return [];
  }
};

export const getBookDetail = async (title) => {
  try {
    const res = await axiosConfig.get("/api/books/detail", {
      params: { title },
    });

    return res.data?.data ?? null;
  } catch (err) {
    console.error("Lỗi getBookDetail:", err);
    return null;
  }
};

export const searchBooks = async (title) => {
  const res = await axiosConfig.get(`/action/searchTitle/${encodeURIComponent(title)}`);

  const book = res.data;  // backend trả về 1 object

  // ⚡ Trả về luôn một mảng
  return book ? [book] : [];
};

export const filterCategory = async (categoryName) => {
  try {
    const res = await axiosConfig.get("/api/books", {
      params: { categoryName },
    });

    return res.data?.data ?? [];
  } catch (err) {
    console.error("Lỗi filterCategory:", err);
    return [];
  }
};
