import api from "../axiosConfig";

// Lấy profile hiện tại
export const getProfile = () => api.get("api/users/profile");

// Cập nhật thông tin user
export const updateUser = async (payload) => {
  const res = await api.put("api/users/profile", payload);
  return res.data?.data ?? null;
};

// Đổi mật khẩu
export const changePassword = (password, confirmPassword) =>
  api.put("api/users/password", {
    password,
    confirmPassword,
  });