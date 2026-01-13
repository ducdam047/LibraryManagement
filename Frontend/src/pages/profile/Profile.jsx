import React, { useEffect, useState } from "react";
import { getProfile, updateUser, changePassword } from "../../api/userApi/userApi";
import toast from "react-hot-toast";
import {
  Mail,
  Phone,
  MapPin,
  Shield,
  CheckCircle,
  Calendar,
} from "lucide-react";

export default function Profile() {
  // ---------- Hooks ----------
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [isEditing, setIsEditing] = useState(false);
  const [form, setForm] = useState({
    fullName: "",
    phoneNumber: "",
    cid: "",
    address: "",
  });

  const [isChangingPassword, setIsChangingPassword] = useState(false);
  const [passwordForm, setPasswordForm] = useState({
    password: "",
    confirmPassword: "",
  });

  // ---------- Fetch profile ----------
  useEffect(() => {
    const fetchData = async () => {
      try {
        const { data } = await getProfile();
        setUser(data.data);
      } catch (err) {
        setError("Không thể tải thông tin người dùng");
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  // ---------- Edit handlers ----------
  const openEdit = () => {
    setForm({
      fullName: user.fullName || "",
      phoneNumber: user.phoneNumber || "",
      cid: user.cid || "",
      address: user.address || "",
    });
    setIsEditing(true);
  };

  const handleUpdate = async () => {
    try {
      const payload = {
        ...user,
        ...form,
      };

      const updatedUser = await updateUser(payload); // ✅ NHẬN USER TRỰC TIẾP

      setUser(updatedUser);
      setIsEditing(false);
      toast.success("Cập nhật thông tin thành công!");
    } catch (err) {
      console.error("Update failed:", err);
      setIsEditing(false);
      toast.error("Cập nhật thất bại, vui lòng thử lại!");
    }
  };

  const handleChangePassword = async () => {
    if (!passwordForm.password || !passwordForm.confirmPassword) {
      toast.error("Vui lòng nhập đầy đủ!");
      return;
    }

    if (passwordForm.password !== passwordForm.confirmPassword) {
      toast.error("Mật khẩu không khớp!");
      return;
    }

    try {
      const updatedUser = await changePassword(
        passwordForm.password,
        passwordForm.confirmPassword
      );

      setUser(updatedUser);
      setIsChangingPassword(false); // ĐÓNG MODAL LUÔN
      toast.success("Đổi mật khẩu thành công!");
    } catch (err) {
      console.error("Change password failed:", err);
      setIsChangingPassword(false); // vẫn đóng modal
      toast.error("Đổi mật khẩu thất bại!");
    }
  };


  // ---------- Early returns ----------
  if (loading)
    return (
      <div className="flex justify-center items-center h-screen text-gray-600 text-lg">
        Đang tải thông tin người dùng...
      </div>
    );

  if (error)
    return (
      <div className="flex justify-center items-center h-screen text-red-600 text-lg font-semibold">
        {error}
      </div>
    );

  // ---------- UI helpers ----------
  const avatarUrl = `https://ui-avatars.com/api/?name=${encodeURIComponent(
    user.fullName || user.username
  )}&background=4f46e5&color=fff&size=200`;

  const InfoItem = ({ icon: Icon, label }) => (
    <div className="flex items-center gap-3 bg-gray-50 px-4 py-2.5 rounded-xl border border-gray-100 hover:bg-gray-100 transition">
      <Icon className="w-5 h-5 text-indigo-600" />
      <span className="text-gray-700 text-sm">{label}</span>
    </div>
  );

  const NAVBAR_HEIGHT = 64;

  return (
    <div
      className="bg-gradient-to-b from-indigo-50 to-white flex justify-center"
      style={{
        minHeight: `calc(100vh - ${NAVBAR_HEIGHT}px)`,
        paddingTop: 20,
        paddingBottom: 20,
      }}
    >
      <div className="w-full max-w-3xl bg-white shadow-xl rounded-3xl overflow-hidden border border-gray-100"
        style={{ boxShadow: "0 12px 30px rgba(2,6,23,0.12)" }}>

        {/* Banner */}
        <div className="relative h-36 bg-indigo-600">
          <div className="absolute -bottom-12 left-1/2 -translate-x-1/2">
            <img
              src={avatarUrl}
              alt="Avatar"
              className="w-24 h-24 rounded-full border-4 border-white shadow-lg"
            />
          </div>
        </div>

        {/* Basic info */}
        <div className="mt-14 text-center px-6">
          <h2 className="text-xl sm:text-2xl font-bold text-gray-800">
            {user.fullName || user.username}
          </h2>
          <p className="text-gray-500 text-sm">@{user.username}</p>
        </div>

        {/* Main content */}
        <div className="px-6 mt-6 pb-6">
          <h3 className="text-base sm:text-lg font-semibold text-gray-700 mb-3 flex items-center gap-2">
            Thông tin cá nhân
          </h3>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <InfoItem icon={Mail} label={user.email || "Chưa có email"} />
            <InfoItem icon={Phone} label={user.phoneNumber || "Chưa có số điện thoại"} />
            <InfoItem icon={MapPin} label={user.address || "Chưa có địa chỉ"} />
            <InfoItem icon={Shield} label={`Vai trò: ${user.role}`} />
            <InfoItem icon={CheckCircle} label={`Trạng thái: ${user.status}`} />
            <InfoItem
              icon={Calendar}
              label={
                user.createdAt
                  ? new Date(user.createdAt).toLocaleDateString("vi-VN")
                  : "Không rõ"
              }
            />
          </div>

          <div className="mt-6 flex flex-col sm:flex-row gap-3 justify-center items-center">
            <button
              onClick={openEdit}
              className="w-full sm:w-auto px-5 py-2.5 bg-indigo-600 text-white rounded-full hover:bg-indigo-700 transition font-medium shadow-md"
            >
              Chỉnh sửa thông tin
            </button>

            <button
              onClick={() => setIsChangingPassword(true)}
              className="w-full sm:w-auto px-5 py-2.5 bg-gray-200 text-gray-700 rounded-full hover:bg-gray-300 transition font-medium shadow"
            >
              Đổi mật khẩu
            </button>
          </div>
        </div>
      </div>

      {/* Edit modal */}
      {isEditing && (
        <div className="fixed inset-0 bg-black/40 flex justify-center items-center px-4 z-50">
          <div className="bg-white w-full max-w-lg rounded-2xl p-6 shadow-xl">
            <h2 className="text-xl font-semibold mb-4 text-gray-800">Chỉnh sửa thông tin</h2>

            <div className="space-y-3">
              <div>
                <label className="text-sm text-gray-600">Họ tên</label>
                <input
                  type="text"
                  className="w-full px-4 py-2 rounded-lg border text-gray-800"
                  value={form.fullName}
                  onChange={(e) => setForm({ ...form, fullName: e.target.value })}
                />
              </div>

              <div>
                <label className="text-sm text-gray-600">Số điện thoại</label>
                <input
                  type="text"
                  className="w-full px-4 py-2 rounded-lg border text-gray-800"
                  value={form.phoneNumber}
                  onChange={(e) =>
                    setForm({ ...form, phoneNumber: e.target.value })
                  }
                />
              </div>

              <div>
                <label className="text-sm text-gray-600">CCCD</label>
                <input
                  type="text"
                  className="w-full px-4 py-2 rounded-lg border text-gray-800"
                  value={form.cid}
                  onChange={(e) => setForm({ ...form, cid: e.target.value })}
                />
              </div>

              <div>
                <label className="text-sm text-gray-600">Địa chỉ</label>
                <input
                  type="text"
                  className="w-full px-4 py-2 rounded-lg border text-gray-800"
                  value={form.address}
                  onChange={(e) => setForm({ ...form, address: e.target.value })}
                />
              </div>
            </div>

            <div className="mt-5 flex justify-end gap-3">
              <button
                onClick={() => setIsEditing(false)}
                className="px-4 py-2 rounded-lg bg-gray-100 text-gray-700 border hover:bg-gray-200 transition"
              >
                Hủy
              </button>

              <button
                onClick={handleUpdate}
                className="px-4 py-2 rounded-lg bg-indigo-600 text-white"
              >
                Lưu thay đổi
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Change Password modal */}
      {isChangingPassword && (
        <div className="fixed inset-0 bg-black/40 flex justify-center items-center px-4 z-50">
          <div className="bg-white w-full max-w-md rounded-2xl p-6 shadow-xl">
            <h2 className="text-xl font-semibold mb-4 text-gray-800">
              Đổi mật khẩu
            </h2>

            <div className="space-y-3">
              {/* Password */}
              <div>
                <label className="text-sm text-gray-600">Mật khẩu mới</label>
                <input
                  type="password"
                  className="w-full px-4 py-2 rounded-lg border text-gray-800"
                  value={passwordForm.password}
                  onChange={(e) =>
                    setPasswordForm({
                      ...passwordForm,
                      password: e.target.value,
                    })
                  }
                />
              </div>

              {/* Confirm Password */}
              <div>
                <label className="text-sm text-gray-600">Xác nhận mật khẩu</label>
                <input
                  type="password"
                  className={`w-full px-4 py-2 rounded-lg border text-gray-800 ${passwordForm.confirmPassword &&
                    passwordForm.password !== passwordForm.confirmPassword
                    ? "border-red-500"
                    : ""
                    }`}
                  value={passwordForm.confirmPassword}
                  onChange={(e) =>
                    setPasswordForm({
                      ...passwordForm,
                      confirmPassword: e.target.value,
                    })
                  }
                />

                {/* Thông báo lỗi nhỏ */}
                {passwordForm.confirmPassword &&
                  passwordForm.password !== passwordForm.confirmPassword && (
                    <p className="text-xs text-red-500 mt-1">
                      Mật khẩu xác nhận không khớp.
                    </p>
                  )}
              </div>
            </div>

            <div className="mt-5 flex justify-end gap-3">
              <button
                onClick={() => setIsChangingPassword(false)}
                className="px-4 py-2 rounded-lg bg-gray-100 text-gray-700 border hover:bg-gray-200 transition"
              >
                Hủy
              </button>

              <button
                onClick={handleChangePassword}
                disabled={
                  !passwordForm.password ||
                  !passwordForm.confirmPassword ||
                  passwordForm.password !== passwordForm.confirmPassword
                }
                className={`px-4 py-2 rounded-lg text-white transition
            ${!passwordForm.password ||
                    !passwordForm.confirmPassword ||
                    passwordForm.password !== passwordForm.confirmPassword
                    ? "bg-indigo-300 cursor-not-allowed"
                    : "bg-indigo-600 hover:bg-indigo-700"
                  }`}
              >
                Đổi mật khẩu
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}
