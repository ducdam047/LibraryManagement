import React, { useState } from "react";
import Navbar from "../components/Navbar";

export default function MainLayout({ children }) {
  const [searchQuery, setSearchQuery] = useState("");

  return (
    <div className="min-h-screen bg-black text-white">
      <Navbar onSearch={setSearchQuery} />   {/* 🔥 TRUYỀN LẠI */}
      {React.cloneElement(children, { searchQuery })}
    </div>
  );
}
