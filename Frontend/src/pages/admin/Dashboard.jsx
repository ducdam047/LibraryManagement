import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import { useNavigate } from "react-router-dom";
import { Users, Book, Ban, Clock } from "lucide-react";

import {
  getDashboardSummary,
  approveLoan,
  handoverBook,
  rejectLoan,
  confirmReturnLoan,
} from "../../api/adminApi/dashboardApi";

import ColumnChart from "../../components/admin/chart/ColumnChart";
import PieChartComponent from "../../components/admin/chart/PieChart";
import ConfirmModal from "../../components/common/ConfirmModal";

/* ===================== TAB CONST ===================== */
const TABS = {
  APPROVE: "approve",
  PAID: "paid",
  RETURN: "return",
  OVERDUE: "overdue",
};

export default function Dashboard() {
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [processingId, setProcessingId] = useState(null);

  const [activeTab, setActiveTab] = useState(TABS.APPROVE);

  const [pendingApproveLoans, setPendingApproveLoans] = useState([]);
  const [pendingReturnLoans, setPendingReturnLoans] = useState([]);
  const [paidLoans, setPaidLoans] = useState([]);

  const [confirmOpen, setConfirmOpen] = useState(false);
  const [confirmAction, setConfirmAction] = useState(null);
  const [selectedLoan, setSelectedLoan] = useState(null);

  const [stats, setStats] = useState({
    totalBooks: 0,
    availableBooks: 0,
    borrowedBooks: 0,
    totalUsers: 0,
    borrowingUsers: 0,
    bannedUsers: 0,
    overdueLoans: [],
    categoryStats: [],
  });

  /* ===================== CONFIRM ===================== */
  const openConfirmModal = (Loan, action) => {
    setSelectedLoan(Loan);
    setConfirmAction(action);
    setConfirmOpen(true);
  };

  const closeConfirmModal = () => {
    setConfirmOpen(false);
    setSelectedLoan(null);
    setConfirmAction(null);
  };

  const handleConfirm = async () => {
    if (!selectedLoan) return;

    try {
      setProcessingId(selectedLoan.loanId);

      if (confirmAction === "approve") {
        await approveLoan(selectedLoan.loanId);
        toast.success("Duyệt mượn sách thành công");
        setPendingApproveLoans((prev) =>
          prev.filter((r) => r.loanId !== selectedLoan.loanId)
        );
      }

      if (confirmAction === "handover") {
        await handoverBook(selectedLoan.loanId);

        toast.success("Đã giao sách thành công");

        // remove khỏi tab PAID
        setPaidLoans(prev =>
          prev.filter(r => r.loanId !== selectedLoan.loanId)
        );
      }

      if (confirmAction === "reject") {
        await rejectLoan(selectedLoan.loanId);
        toast.success("Đã từ chối yêu cầu");
        setPendingApproveLoans((prev) =>
          prev.filter((r) => r.loanId !== selectedLoan.loanId)
        );
      }

      if (confirmAction === "confirmReturn") {
        await confirmReturnLoan(selectedLoan.loanId);
        toast.success("Đã xác nhận nhận sách");
        setPendingReturnLoans((prev) =>
          prev.filter((r) => r.loanId !== selectedLoan.loanId)
        );
      }
    } catch (err) {
      toast.error(err?.response?.data?.message || "Thao tác thất bại");
    } finally {
      setProcessingId(null);
      closeConfirmModal();
    }
  };

  /* ===================== FETCH ===================== */
  useEffect(() => {
    let mounted = true;
    setLoading(true);

    getDashboardSummary()
      .then((data) => {
        if (!mounted) return;

        const overdueWithDays = (data.overdueLoans || []).map((r) => {
          const due = new Date(r.dueDay);
          const today = new Date();
          const days = Math.floor((today - due) / (1000 * 60 * 60 * 24));
          return { ...r, days };
        });

        setStats({
          totalBooks: data.totalBooks,
          availableBooks: data.availableBooks,
          borrowedBooks: data.borrowedBooks,
          totalUsers: data.totalUsers,
          borrowingUsers: data.borrowingUsers,
          bannedUsers: data.bannedUsers,
          overdueLoans: overdueWithDays,
          categoryStats: data.categoryStats || [],
        });

        setPendingApproveLoans(data.pendingApproveLoans || []);
        setPaidLoans(data.pendingPaidLoans || []);
        setPendingReturnLoans(data.pendingReturnLoans || []);
      })
      .finally(() => mounted && setLoading(false));

    return () => (mounted = false);
  }, []);

  const tableData =
    activeTab === TABS.APPROVE
      ? pendingApproveLoans
      : activeTab === TABS.PAID
        ? paidLoans
        : activeTab === TABS.RETURN
          ? pendingReturnLoans
          : stats.overdueLoans;

  return (
    <div className="p-6 text-white bg-black min-h-screen">
      {/* ===================== STAT ===================== */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <StatCard title="Total books" value={stats.totalBooks} Icon={Book} color="from-blue-500 to-blue-700" onClick={() => navigate("/dashboard/books")} loading={loading} />
        <StatCard title="Books available" value={stats.availableBooks} Icon={Book} color="from-green-500 to-green-700" onClick={() => navigate("/dashboard/books?status=available")} loading={loading} />
        <StatCard title="Books borrowing" value={stats.borrowedBooks} Icon={Book} color="from-orange-500 to-orange-700" onClick={() => navigate("/dashboard/books?status=borrowed")} loading={loading} />
        <StatCard title="Total users" value={stats.totalUsers} Icon={Users} color="from-indigo-500 to-indigo-700" onClick={() => navigate("/dashboard/users")} loading={loading} />
        <StatCard title="Users borrowing" value={stats.borrowingUsers} Icon={Users} color="from-yellow-500 to-yellow-700" onClick={() => navigate("/dashboard/users?status=borrowing")} loading={loading} />
        <StatCard title="Users banned" value={stats.bannedUsers} Icon={Ban} color="from-red-500 to-red-700" onClick={() => navigate("/dashboard/users?status=banned")} loading={loading} />
      </div>

      {/* ===================== CHART ===================== */}
      <div className="mt-12 grid grid-cols-1 lg:grid-cols-2 gap-8">
        <div className="bg-zinc-900 p-8 rounded-2xl border border-zinc-800 h-[450px]">
          <ColumnChart />
        </div>
        <div className="bg-zinc-900 p-8 rounded-2xl border border-zinc-800 h-[450px]">
          <PieChartComponent data={stats.categoryStats} />
        </div>
      </div>

      {/* ===================== Loan TABLE ===================== */}
      <div className="mt-12 p-8 bg-zinc-900 rounded-2xl border border-zinc-800">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-xl font-semibold flex items-center gap-2">
            <Clock />
            Manage Borrow / Return Loans
          </h2>

          <div className="flex bg-zinc-800 rounded-xl p-1">
            <TabButton active={activeTab === TABS.APPROVE} onClick={() => setActiveTab(TABS.APPROVE)}>Approval Pending</TabButton>
            <TabButton active={activeTab === TABS.PAID} onClick={() => setActiveTab(TABS.PAID)}>Paid</TabButton>
            <TabButton active={activeTab === TABS.RETURN} onClick={() => setActiveTab(TABS.RETURN)}>Return Pending</TabButton>
            <TabButton active={activeTab === TABS.OVERDUE} onClick={() => setActiveTab(TABS.OVERDUE)}>Overdue</TabButton>
          </div>
        </div>

        {tableData.length === 0 ? (
          <div className="p-6 text-center text-zinc-400">Không có dữ liệu 🎉</div>
        ) : (
          <table className="w-full text-sm">
            <thead className="bg-zinc-800">
              <tr>
                <th className="p-4 text-left">Full name</th>
                <th className="p-4 text-left">Book name</th>
                <th className="p-4 text-center">
                  {activeTab === TABS.OVERDUE
                    ? "Number of days overdue"
                    : activeTab === TABS.APPROVE
                      ? "Number of days borrowed"
                      : activeTab === TABS.PAID
                        ? "Paid status"
                        : ""}
                </th>
                <th className="p-4 text-center">Action</th>
              </tr>
            </thead>

            <tbody className="divide-y divide-zinc-800">
              {tableData.map((r) => (
                <tr key={r.loanId} className="align-middle">
                  {/* Người dùng */}
                  <td className="p-4 whitespace-nowrap">
                    {r.fullName}
                  </td>

                  {/* Sách */}
                  <td className="p-4 font-semibold">
                    {r.title}
                  </td>

                  {/* CỘT THÔNG TIN PHỤ */}
                  <td className="p-4 text-center">
                    {activeTab === TABS.APPROVE && (
                      <span className="px-3 py-1 rounded-lg bg-zinc-700/60">
                        {r.borrowDays} ngày
                      </span>
                    )}

                    {activeTab === TABS.PAID && (
                      <span className="px-3 py-1 rounded-lg bg-green-600/15 text-green-400 font-semibold">
                        Đã thanh toán
                      </span>
                    )}

                    {activeTab === TABS.OVERDUE && (
                      <span className="px-3 py-1 rounded-lg bg-red-600/15 text-red-400 font-semibold">
                        {r.days} ngày
                      </span>
                    )}

                    {activeTab === TABS.RETURN && (
                      <span className="text-zinc-500">—</span>
                    )}
                  </td>

                  {/* HÀNH ĐỘNG */}
                  <td className="p-4 text-center">
                    {activeTab === TABS.APPROVE && (
                      <>
                        <ActionBtn color="green" onClick={() => openConfirmModal(r, "approve")}>
                          Duyệt
                        </ActionBtn>
                        <ActionBtn color="red" onClick={() => openConfirmModal(r, "reject")}>
                          Từ chối
                        </ActionBtn>
                      </>
                    )}

                    {activeTab === TABS.PAID && (
                      <ActionBtn color="green" onClick={() => openConfirmModal(r, "handover")}>
                        Giao sách
                      </ActionBtn>
                    )}

                    {activeTab === TABS.RETURN && (
                      <ActionBtn color="blue" onClick={() => openConfirmModal(r, "confirmReturn")}>
                        Xác nhận trả
                      </ActionBtn>
                    )}

                    {activeTab === TABS.OVERDUE && (
                      <span className="px-3 py-1 rounded-lg bg-red-600/15 text-red-400 font-semibold">
                        🚫 Đã cấm
                      </span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>


          </table>
        )
        }
      </div >

      <ConfirmModal
        open={confirmOpen}
        title="Xác nhận"
        message={`Bạn chắc chắn muốn thực hiện thao tác này?`}
        onConfirm={handleConfirm}
        onClose={closeConfirmModal}
      />
    </div >
  );
}

/* ===================== SMALL COMPONENT ===================== */
const TabButton = ({ active, children, ...props }) => (
  <button
    {...props}
    className={`px-4 py-2 rounded-lg ${active ? "bg-blue-500/20 text-blue-400" : "text-zinc-400"
      }`}
  >
    {children}
  </button>
);

const ActionBtn = ({ color, children, ...props }) => (
  <button
    {...props}
    className={`px-3 py-1 rounded mr-2 bg-${color}-500/20 text-${color}-400`}
  >
    {children}
  </button>
);

/* ===================== STAT CARD ===================== */
function StatCard({ title, value, Icon, color, onClick, loading }) {
  return (
    <div
      onClick={onClick}
      className={`p-6 rounded-2xl shadow-xl transition ${onClick ? "cursor-pointer hover:scale-[1.03]" : ""
        } bg-gradient-to-br ${color}`}
    >
      {loading ? (
        <div className="animate-pulse">
          <div className="h-4 bg-white/30 rounded w-32 mb-3"></div>
          <div className="h-8 bg-white/40 rounded w-20"></div>
        </div>
      ) : (
        <>
          <div className="flex items-center gap-3 mb-3 opacity-90">
            <Icon size={22} />
            <p className="text-lg">{title}</p>
          </div>
          <h3 className="text-4xl font-bold">{value}</h3>
        </>
      )}
    </div>
  );
}
