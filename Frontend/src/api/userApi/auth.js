import api from "../axiosConfig";

// 🟢 Đăng ký
export const register = async (data) => {
  const res = await api.post("/api/users/signup", data);
  return res.data?.data ?? null;
};

// 🟢 Đăng nhập
export const login = async (data) => {
  const res = await api.post("/api/users/login", data);
  return res.data?.data ?? null;
};