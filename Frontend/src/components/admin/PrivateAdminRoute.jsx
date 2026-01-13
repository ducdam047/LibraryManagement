import { Navigate } from "react-router-dom";

export default function PrivateAdminRoute({ children }) {
  const role = localStorage.getItem("role");

  if (role !== "ADMIN") {
    return <Navigate to="/home" replace />;
  }

  return children;
}