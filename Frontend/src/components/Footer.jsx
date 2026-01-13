import React from "react";

export default function Footer() {
  return (
    <footer className="bg-[#111] py-6 text-center text-gray-400 mt-auto border-t border-gray-800">
      © {new Date().getFullYear()} Library Portal. All rights reserved.
    </footer>
  );
}
