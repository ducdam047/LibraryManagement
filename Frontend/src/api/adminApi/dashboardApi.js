import api from "../axiosConfig";

export const getDashboardSummary = async () => {
  const res = await api.get("/dashboard/summary");
  return res.data;
};

export const getDashboardBooks = async (status) => {
  const res = await api.get("/dashboard/books", {
    params: { status },
  });
  return res.data;
};

export const getDashboardUsers = async (status) => {
  const res = await api.get("/dashboard/users", {
    params: { status }
  });
  return res.data;
};

export const getDashboardColumnChart = async () => {
  const res = await api.get("/dashboard/column-chart");
  return res.data;
};

export const getDashboardPieChart = async () => {
  const res = await api.get("/dashboard/pie-chart");
  return res.data;
};

export const approveLoan = async (loanId) => {
  const res = await api.put(`/borrowed/${loanId}/approve`);
  return res.data;
};

export const handoverBook = async (loanId) => {
  const res = await api.put(`/borrowed/${loanId}/handover`);
  return res.data;
};

export const rejectLoan = async (loanId) => {
  const res = await api.put(`/borrowed/${loanId}/reject`);
  return res.data;
};

export const confirmReturnLoan = async (loanId) => {
  const res = await api.put(`/borrowed/${loanId}/confirm`);
  return res.data;
};
