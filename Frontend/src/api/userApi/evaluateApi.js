import api from "../axiosConfig";

export const checkEvaluateExists = async (title) => {
  const res = await api.get("/evaluations/status", {
    params: { title },
  });

  return res.data.data;
};

export const getEvaluations = (title) => {
  return api.get(`/evaluations`, {
    params: { title },
  });
};

export const evaluateBook = async (payload) => {
  const res = await api.post("/evaluations", payload);

  return res.data.data;
};

export const getCountRating = async (title) => {
  const res = await api.get("/evaluations/ratings", {
    params: { title },
  });
  return res.data.data;
};

export const getAverageRating = async (title) => {
  const res = await api.get("/evaluations/ratings/average", {
    params: { title },
  });
  return res.data.data;
};