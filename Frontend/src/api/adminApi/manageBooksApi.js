import api from "../axiosConfig";

export const addBook = async (formData) => {
  try {
    const res = await api.post("/book", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    return res.data?.data ?? null;
  } catch (err) {
    console.error("Lỗi addBook:", err);
    throw err;
  }
};

export const updateBook = async (bookId, formData) => {
  try {
    const res = await api.put(`/book/${bookId}`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    return res.data?.data ?? null;
  } catch (err) {
    console.error("Lỗi updateBook:", err);
    throw err;
  }
};

export const deleteBook = async (bookId) => {
  try {
    const res = await api.delete(`/book/${bookId}`);
    return res.data?.message ?? "Deleted";
  } catch (err) {
    console.error("Lỗi deleteBook:", err);
    throw err;
  }
};

export const getAllCategories = () => {
  return api.get("/category/all");
};

export const getAllPublishers = () => {
  return api.get("/publisher/all");
};