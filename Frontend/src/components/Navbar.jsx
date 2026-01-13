import React, { useState, useRef, useEffect } from "react";
import { MdCategory } from "react-icons/md";
import { useNavigate, useLocation } from "react-router-dom";

export default function Navbar({ search, setSearch, onSearch }) {
  const navigate = useNavigate();
  const location = useLocation();

  const username = localStorage.getItem("username");
  const role = localStorage.getItem("role"); // ⭐ LẤY QUYỀN
  const isLoggedIn = username && username !== "null" && username !== "undefined";

  const isAdmin = role === "ADMIN"; // ⭐ PHÂN ROLE

  const [showMenu, setShowMenu] = useState(false);
  const [keyword, setKeyword] = useState("");
  const [openCategory, setOpenCategory] = useState(false);

  const menuRef = useRef(null);
  const categoryRef = useRef(null);

  const isActive = (path) => location.pathname === path;

  // ⭐ DANH MỤC USER
  const categories = [
    "Văn học - Tiểu thuyết",
    "Khoa học - Công nghệ",
    "Lịch sử - Văn hoá",
    "Kinh tế - Chính trị",
    "Triết học",
    "Tâm lý học",
    "Phát triển bản thân",
  ];

  // CLOSE MENU
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setShowMenu(false);
      }
      if (categoryRef.current && !categoryRef.current.contains(e.target)) {
        setOpenCategory(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // useEffect(() => {
  //   const timer = setTimeout(() => {
  //     setDebouncedKeyword(keyword);
  //   }, 400); // 400ms sau khi ngừng gõ

  //   return () => clearTimeout(timer);
  // }, [keyword]);

  // useEffect(() => {
  //   onSearch(debouncedKeyword);
  // }, [debouncedKeyword]);

  // ENTER SEARCH
  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      onSearch(keyword);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    localStorage.removeItem("role");
    navigate("/login");
  };

  return (
    <nav className="bg-black/60 backdrop-blur-xl shadow-lg shadow-blue-500/10 px-6 py-3 
    flex items-center justify-between border-b border-white/10 relative z-50 text-white">

      {/* LEFT SIDE */}
      <div className="flex items-center gap-8 ">

        {/* ⭐ LOGO: User → /home, Admin → /admin */}
        <h1
          className="text-2xl font-bold text-blue-400 cursor-pointer hover:text-blue-300 transition"
          onClick={() => {
            if (isAdmin) {
              navigate("/dashboard");
            } else {
              onSearch("");      // 🔥 reset searchQuery
              setKeyword("");    // 🔥 reset ô search
              navigate("/home"); // 🔥 đảm bảo về đúng trang chủ mặc định
            }
          }}
        >
          {role === "ADMIN" ? "Dashboard" : "BookNest"}
        </h1>

        {/* ⭐ MENU DÀNH CHO USER */}
        {!isAdmin && (
          <div className="flex items-center gap-6 text-gray-200 font-medium">

            {/* CATEGORY */}
            <div className="relative " ref={categoryRef}>
              <button
                onClick={() => setOpenCategory((prev) => !prev)}
                className="hover:text-blue-400 flex items-center gap-1 transition"
              >
                <MdCategory size={20} /> Category
              </button>

              {openCategory && (
                <div className="absolute left-0 mt-2 min-w-56 bg-gray-900 text-gray-200 
                shadow-lg border border-white/10 rounded-md py-2 z-50" >

                  {categories.map((cat, idx) => (
                    <button
                      key={idx}
                      onClick={() => {
                        onSearch(cat);
                        setKeyword("");
                        navigate(`/home?category=${encodeURIComponent(cat)}`);
                        setOpenCategory(false);
                      }}
                      className="block w-full text-left px-4 py-2 hover:bg-gray-800 
                      hover:text-blue-400 transition"
                    >
                      {cat}
                    </button>
                  ))}
                </div>
              )}
            </div>

            <button
              onClick={() => navigate("/reading")}
              className={`transition ${isActive("/reading") ? "text-blue-400 font-semibold" : "hover:text-blue-400"}`}
            >
              Reading
            </button>

            <button
              onClick={() => navigate("/wishlist")}
              className={`transition ${isActive("/wishlist") ? "text-blue-400 font-semibold" : "hover:text-blue-400"}`}
            >
              Wishlist
            </button>

            <button
              onClick={() => navigate("/borrowed")}
              className={`transition ${isActive("/borrowed") ? "text-blue-400 font-semibold" : "hover:text-blue-400"}`}
            >
              Borrowed Books
            </button>

            <button
              onClick={() => navigate("/pending")}
              className={`transition ${isActive("/pending") ? "text-blue-400 font-semibold" : "hover:text-blue-400"}`}
            >
              Pending Books
            </button>

            <button
              onClick={() => navigate("/history")}
              className={`transition ${isActive("/history") ? "text-blue-400 font-semibold" : "hover:text-blue-400"}`}
            >
              History Borrow
            </button>
          </div>
        )}

        {/* ⭐ MENU DÀNH CHO ADMIN */}
        {isAdmin && (
          <div className="flex items-center gap-6 text-gray-200 font-medium">

            <button
              onClick={() => navigate("/manage-users")}
              className={`transition ${isActive("/manage-users") ? "text-blue-400 font-semibold" : "hover:text-blue-400"}`}
            >
              Manage Users
            </button>

            <button
              onClick={() => navigate("/manage-books")}
              className={`transition ${isActive("/manage-books") ? "text-blue-400 font-semibold" : "hover:text-blue-400"}`}
            >
              Manage Books
            </button>

            <button
              onClick={() => navigate("/manage-categories")}
              className={`transition ${isActive("/manage-categories") ? "text-blue-400 font-semibold" : "hover:text-blue-400"}`}
            >
              Manage Categories
            </button>

            <button
              onClick={() => navigate("/manage-publishers")}
              className={`transition ${isActive("/manage-publishers") ? "text-blue-400 font-semibold" : "hover:text-blue-400"}`}
            >
              Manage Publishers
            </button>

          </div>
        )}
      </div>

      {/* RIGHT SIDE */}
      <div className="flex items-center gap-4" >

        {/* SEARCH */}
        {!isAdmin && (
          <div className="relative w-96">
            <input
              type="text"
              placeholder="Search books..."
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              onKeyDown={handleKeyDown}
              className="border border-white/20 bg-gray-800 text-white px-4 py-2 rounded-lg shadow-sm 
            focus:outline-none focus:ring focus:ring-blue-500/40 placeholder-gray-400 pr-20 w-full"
            />

            {keyword && (
              <button
                onClick={() => {
                  setKeyword("");
                  onSearch("");
                }}
                className="absolute right-12 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-200"
              >
                ✕
              </button>
            )}

            <button
              onClick={() => onSearch(keyword)}
              className="absolute right-2 top-1/2 -translate-y-1/2 w-8 h-8 flex items-center justify-center 
            bg-gray-700 hover:bg-gray-600 border border-white/10 rounded-md transition"
            >
              🔍
            </button>
          </div>
        )}

        {/* USER DROPDOWN */}
        {isLoggedIn ? (
          <div className="relative" ref={menuRef}>
            <button
              onClick={() => setShowMenu(!showMenu)}
              className="w-10 h-10 rounded-full bg-blue-600 text-white flex items-center 
              justify-center font-bold hover:opacity-80 transition"
            >
              {username?.charAt(0).toUpperCase()}
            </button>

            {showMenu && (
              <div className="absolute right-0 mt-2 bg-gray-900 text-gray-200 shadow-lg border 
              border-white/10 rounded-md w-40 py-2 z-50">

                <button
                  onClick={() => {
                    setShowMenu(false);
                    navigate("/user/profile");
                  }}
                  className="block w-full text-left px-4 py-2 hover:bg-gray-800 hover:text-blue-400 transition"
                >
                  Profile
                </button>

                <button
                  onClick={handleLogout}
                  className="block w-full text-left px-4 py-2 hover:bg-gray-800 
                  text-red-400 hover:text-red-300 transition"
                >
                  Logout
                </button>
              </div>
            )}
          </div>
        ) : (
          <button
            onClick={() => navigate("/login")}
            className="bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-500 transition"
          >
            Login
          </button>
        )}
      </div>
    </nav>
  );
}
