import React, { useState } from "react";
import api from "../../api/axiosConfig";
import { useNavigate } from "react-router-dom";

function Register() {
  const navigate = useNavigate();
  // ✅ Khai báo state
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [fullName, setFullName] = useState("");
  const [username, setUsername] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [cid, setCid] = useState("");
  const [address, setAddress] = useState("");

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");

    // Kiểm tra confirmPassword trước khi gọi API
    if (password !== confirmPassword) {
      setError("Password confirmation does not match!");
      return;
    }

    try {
      const res = await api.post("/api/users/signup", {
        username,
        email,
        password,
        confirmPassword,
        fullName,
        phoneNumber,
        cid,
        address,
      });

      console.log("Đăng ký thành công:", res.data);
      navigate("/login")
    } catch (err) {
      console.error("Lỗi đăng ký:", err);

      if (err.response) {
        if (err.response.status === 409) {
          setError("Email or username already exists!");
        } else if (err.response.data?.message) {
          setError(err.response.data.message);
        } else {
          setError("Registration failed. Please try again!");
        }
      } else {
        setError("Network error — please check your connection.");
      }
    }
  };


  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-100 to-emerald-100">
      <div className="flex bg-white rounded-3xl shadow-2xl overflow-hidden w-[1100px] border border-blue-200">
        {/* BÊN TRÁI - Logo và branding */}
        <div className="w-1/3 bg-gradient-to-br from-blue-600 to-green-500 flex flex-col items-center justify-center text-white p-10 space-y-5">
          <img
            src="/assets/library_logo.png"
            alt="Library Logo"
            className="w-28 h-28 rounded-full shadow-lg border-2 border-white"
          />
          <h2 className="text-3xl font-bold tracking-wide">BookNest</h2>
          <p className="text-center text-sm text-blue-50 leading-relaxed">
            Welcome to our library system.
            Create your account to explore thousands of books, borrow online, and manage your reading journey.
          </p>
        </div>

        {/* BÊN PHẢI - Form đăng ký */}
        <div className="flex-1 flex items-center justify-center bg-gradient-to-br from-blue-50 to-emerald-50">
          <form
            onSubmit={handleRegister}
            className="bg-white/90 p-10 rounded-2xl shadow-xl border border-blue-200 w-[700px]"
          >
            <h2 className="text-3xl font-bold text-center mb-8 text-blue-700 tracking-wide">
              📚 Member Registration
            </h2>

            <div className="grid grid-cols-2 gap-8">
              {/* Cột trái */}
              <div className="space-y-5">
                <div>
                  <label className="block text-sm font-semibold text-gray-600 mb-1">
                    Email *
                  </label>
                  <input
                    type="email"
                    placeholder="Enter your email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    className="w-full p-3 border border-blue-300 rounded-xl focus:ring-2 focus:ring-blue-400 outline-none bg-white/80 shadow-sm"
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-600 mb-1">
                    Username *
                  </label>
                  <input
                    type="text"
                    placeholder="Choose a username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                    className="w-full p-3 border border-blue-300 rounded-xl focus:ring-2 focus:ring-blue-400 outline-none bg-white/80 shadow-sm"
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-600 mb-1">
                    Password *
                  </label>
                  <input
                    type="password"
                    placeholder="Enter password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    className="w-full p-3 border border-blue-300 rounded-xl focus:ring-2 focus:ring-blue-400 outline-none bg-white/80 shadow-sm"
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-600 mb-1">
                    Confirm Password *
                  </label>
                  <input
                    type="password"
                    placeholder="Re-enter password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                    className="w-full p-3 border border-blue-300 rounded-xl focus:ring-2 focus:ring-blue-400 outline-none bg-white/80 shadow-sm"
                  />
                </div>
              </div>

              {/* Cột phải */}
              <div className="space-y-5">
                <div>
                  <label className="block text-sm font-semibold text-gray-600 mb-1">
                    Full Name
                  </label>
                  <input
                    type="text"
                    placeholder="Your full name"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    className="w-full p-3 border border-green-300 rounded-xl focus:ring-2 focus:ring-green-400 outline-none bg-white/80 shadow-sm"
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-600 mb-1">
                    Phone Number
                  </label>
                  <input
                    type="text"
                    placeholder="e.g. 0123456789"
                    value={phoneNumber}
                    onChange={(e) => setPhoneNumber(e.target.value)}
                    className="w-full p-3 border border-green-300 rounded-xl focus:ring-2 focus:ring-green-400 outline-none bg-white/80 shadow-sm"
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-600 mb-1">
                    Citizen ID
                  </label>
                  <input
                    type="text"
                    placeholder="Enter your ID"
                    value={cid}
                    onChange={(e) => setCid(e.target.value)}
                    className="w-full p-3 border border-green-300 rounded-xl focus:ring-2 focus:ring-green-400 outline-none bg-white/80 shadow-sm"
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-600 mb-1">
                    Address
                  </label>
                  <input
                    type="text"
                    placeholder="Your address"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                    className="w-full p-3 border border-green-300 rounded-xl focus:ring-2 focus:ring-green-400 outline-none bg-white/80 shadow-sm"
                  />
                </div>
              </div>
            </div>

            {error && <p className="text-red-500 text-sm mt-4">{error}</p>}

            <button
              type="submit"
              className="mt-10 w-full bg-gradient-to-r from-blue-600 to-green-500 hover:from-blue-700 hover:to-green-600 text-white py-3 rounded-xl font-semibold shadow-lg transform hover:scale-[1.02] transition duration-300"
            >
              Register Now
            </button>

            <p className="text-center text-sm text-gray-600 mt-4">
              Already have an account?{" "}
              <span
                className="text-blue-600 hover:underline cursor-pointer"
                onClick={() => navigate("/login")}
              >
                Log in
              </span>
            </p>
          </form>
        </div>
      </div>
    </div>
  );

}

export default Register;
