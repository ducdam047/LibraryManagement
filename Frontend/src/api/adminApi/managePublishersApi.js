import api from "../axiosConfig";

export const getAllPublishers = async () => {
  try {
    const res = await api.get("/publishers");
    return res.data?.data ?? [];
  } catch (err) {
    console.error("Lỗi getAllPublishers:", err);
    return [];
  }
};

export const createPublisher = async (data) => {
  try {
    const res = await api.post("/publishers", data);
    return res.data?.data ?? null;
  } catch (err) {
    console.error("Lỗi createPublisher:", err);
    throw err;
  }
};