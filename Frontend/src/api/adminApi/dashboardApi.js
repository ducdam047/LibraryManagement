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

export const approveRecord = async (recordId) => {
  const res = await api.put(`/borrowed/approve/${recordId}`);
  return res.data;
};

export const rejectRecord = async (recordId) => {
  const res = await api.put(`/borrowed/reject/${recordId}`);
  return res.data;
};

export const confirmReturnRecord = async (recordId) => {
  const res = await api.put(`/borrowed/confirm/${recordId}`);
  return res.data;
};
