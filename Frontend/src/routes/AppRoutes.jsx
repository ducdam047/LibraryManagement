import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import MainLayout from "../layouts/MainLayout";
import Home from "../pages/home/Home";
import Login from "../pages/auth/Login";
import Register from "../pages/auth/Register";
import Profile from "../pages/profile/Profile";
import BookDetail from "../pages/home/BookDetail";
import Reading from "../pages/reading/Reading";
import Wishlist from "../pages/wishlist/Wishlist";
import ReadingBookDetail from "../pages/reading/ReadingBookDetail";
import WishlistBookDetail from "../pages/wishlist/WishlistBookDetail"
import Borrowed from "../pages/borrowed/Borrowed";
import RecordBorrowedDetail from "../pages/borrowed/RecordBorrowedDetail";
import RecordReturnedDetail from "../pages/borrowed/RecordReturnedDetail";
import EvaluateBook from "../pages/borrowed/EvaluateBook";
import Pending from "../pages/pending/Pending";
import HistoryBorrow from "../pages/history/HistoryBorrow";
import PrivateAdminRoute from "../components/admin/PrivateAdminRoute";
import Dashboard from "../pages/admin/Dashboard";
import DashboardBook from "../pages/admin/DashboardBook";
import DashboardUser from "../pages/admin/DashboardUser";
import ManageBooks from "../pages/admin/book/ManageBooks";
import AddBook from "../pages/admin/book/AddBook";
import UpdateBook from "../pages/admin/book/UpdateBook";
import AdminBookDetail from "../pages/admin/book/AdminBookDetail";
import ManageUsers from "../pages/admin/user/ManageUsers";
import ManageCategories from "../pages/admin/category/ManageCategories";
import AddCategory from "../pages/admin/category/AddCategory";
import ManagePublishers from "../pages/admin/publisher/ManagePublishers";
import AddPublisher from "../pages/admin/publisher/AddPublisher";

export default function AppRoutes() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/home" />} />

        {/* TẤT CẢ TRANG CÓ NAVBAR */}
        <Route
          path="/home"
          element={
            <MainLayout>
              <Home />
            </MainLayout>
          }
        />

        <Route
          path="/reading"
          element={
            <MainLayout>
              <Reading />
            </MainLayout>
          }
        />

        <Route
          path="/reading/:readingId"
          element={
            <MainLayout>
              <ReadingBookDetail />
            </MainLayout>
          }
        />

        <Route
          path="/wishlist"
          element={
            <MainLayout>
              <Wishlist />
            </MainLayout>
          }
        />

        <Route
          path="/wishlist/:id"
          element={
            <MainLayout>
              < WishlistBookDetail />
            </MainLayout>
          }
        />

        <Route
          path="/borrowed"
          element={
            <MainLayout>
              < Borrowed />
            </MainLayout>
          }
        />

        <Route
          path="/borrowed/order-active/:id"
          element={
            <MainLayout>
              <RecordBorrowedDetail />
            </MainLayout>
          }
        />

        <Route
          path="/borrowed/order-returned/:loanId"
          element={
            <MainLayout>
              <RecordReturnedDetail />
            </MainLayout>
          }
        />

        <Route
          path="/evaluate/evaluate-book"
          element={
            <MainLayout>
              <EvaluateBook />
            </MainLayout>
          }
        />

        <Route
          path="/pending"
          element={
            <MainLayout>
              < Pending />
            </MainLayout>
          }
        />

        <Route
          path="/history"
          element={
            <MainLayout>
              < HistoryBorrow />
            </MainLayout>
          }
        />

        <Route
          path="/user/profile"
          element={
            <MainLayout>
              <Profile />
            </MainLayout>
          }
        />

        <Route
          path="/book/detail/:title"
          element={
            <MainLayout>
              <BookDetail />
            </MainLayout>
          }
        />

        <Route
          path="/dashboard"
          element={
            <PrivateAdminRoute>
              <MainLayout>
                <Dashboard />
              </MainLayout>
            </PrivateAdminRoute>
          }
        />

        <Route
          path="/dashboard/books"
          element={
            <MainLayout>
              <DashboardBook />
            </MainLayout>
          }
        />

        <Route
          path="/dashboard/users"
          element={
            <MainLayout>
              <DashboardUser />
            </MainLayout>
          }
        />

        <Route
          path="/manage-books"
          element={
            <PrivateAdminRoute>
              <MainLayout>
                <ManageBooks />
              </MainLayout>
            </PrivateAdminRoute>
          }
        />

        <Route
          path="/manage-books/detail"
          element={
            <PrivateAdminRoute>
              <MainLayout>
                <AdminBookDetail />
              </MainLayout>
            </PrivateAdminRoute>
          }
        />

        <Route
          path="/manage-books/add"
          element={
            <PrivateAdminRoute>
              <MainLayout>
                <AddBook />
              </MainLayout>
            </PrivateAdminRoute>
          }
        />

        <Route
          path="/manage-books/update"
          element={
            <PrivateAdminRoute>
              <MainLayout>
                <UpdateBook />
              </MainLayout>
            </PrivateAdminRoute>
          }
        />

        <Route
          path="/manage-users"
          element={
            <PrivateAdminRoute>
              <MainLayout>
                <ManageUsers />
              </MainLayout>
            </PrivateAdminRoute>
          }
        />

        <Route
          path="/manage-categories"
          element={
            <PrivateAdminRoute>
              <MainLayout>
                <ManageCategories />
              </MainLayout>
            </PrivateAdminRoute>
          }
        />

        <Route
          path="/manage-categories/add"
          element={
            <PrivateAdminRoute>
              <MainLayout>
                <AddCategory />
              </MainLayout>
            </PrivateAdminRoute>
          }
        />

        <Route
          path="/manage-publishers"
          element={
            <PrivateAdminRoute>
              <MainLayout>
                <ManagePublishers />
              </MainLayout>
            </PrivateAdminRoute>
          }
        />

        <Route
          path="/manage-publishers/add"
          element={
            <PrivateAdminRoute>
              <MainLayout>
                <AddPublisher />
              </MainLayout>
            </PrivateAdminRoute>
          }
        />

        {/* TRANG KHÔNG CÓ NAVBAR */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </Router>
  );
}
