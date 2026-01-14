import api from "../axiosConfig";

export const getAllUsers = async () => {
  try {
    const res = await api.get("/api/users");
    return res.data?.data ?? [];
  } catch (err) {
    console.error("Lỗi getAllUsers:", err);
    return [];
  }
};

export const getorderList = async (userId) => {
  const res = await api.get(`/borrowed/list-loan/${userId}`);
  return res.data;
};