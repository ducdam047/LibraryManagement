import React from "react";
import AppRoutes from "./routes/AppRoutes";
import { Toaster } from "react-hot-toast";

export default function App() {
  return (
    <>
      {/* Toast UI xuất hiện mọi nơi */}
      <Toaster position="top-right" />

      {/* Toàn bộ route */}
      <AppRoutes />
    </>
  );
}
