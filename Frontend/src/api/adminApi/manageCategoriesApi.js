import api from "../axiosConfig";

export const getAllCategories = async () => {
  try {
    const res = await api.get("/categories");
    return res.data?.data ?? [];
  } catch (err) {
    console.error("Lỗi getAllCategories:", err);
    return [];
  }
};

export const createCategory = async (data) => {
  try {
    const res = await api.post("/categories", data);
    return res.data?.data ?? null;
  } catch (err) {
    console.error("Lỗi createCategory:", err);
    throw err;
  }
};