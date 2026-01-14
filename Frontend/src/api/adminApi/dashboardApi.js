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

export const approveorder = async (loanId) => {
  const res = await api.put(`/borrowed/approve/${loanId}`);
  return res.data;
};

export const rejectorder = async (loanId) => {
  const res = await api.put(`/borrowed/reject/${loanId}`);
  return res.data;
};

export const confirmReturnorder = async (loanId) => {
  const res = await api.put(`/borrowed/confirm/${loanId}`);
  return res.data;
};
