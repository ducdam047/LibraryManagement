import axios from "../axiosConfig";

export const getPendingBorrow = async () => {
  const res = await axios.get("/pending/borrow");
  return res.data?.data;
};

export const getPendingReturn = async () => {
  const res = await axios.get("/pending/return");
  return res.data?.data;
};

export const cancelPendingBorrow = async (recordId) => {
  const res = await axios.put(`/pending/cancel/${recordId}`);
  return res.data;
};