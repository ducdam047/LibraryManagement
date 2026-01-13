import api from "../axiosConfig";

export const getHistory = async () => {
  const res = await api.get("/borrowed/history");
  return res.data;
};

export const getBorrowedBooks = async () => {
  const res = await api.get("/borrowed/list-record-borrowed");
  return res.data;
};

export const getReturnedBooks = async () => {
  const res = await api.get("/borrowed/list-record-returned");
  return res.data;
};

export async function getBorrowedRecordById(bookId) {
  const res = await api.get(`/borrowed/record-active/${bookId}`);
  return res.data;
}

export async function getReturnedRecordById(recordId) {
  const res = await api.get(`/borrowed/record-returned/${recordId}`);
  return res.data;
}

export const borrowBook = async (title, borrowDays) => {
  const res = await api.post("/borrowed/borrow-book", {
    title,
    borrowDays
  });
  return res.data;
};

export const returnBook = async (bookId) => {
  const res = await api.put("/borrowed/return-book", {
    bookId: Number(bookId),
  });

  return res.data;
};

export const extendBook = async (bookId, extendDays) => {
  try {
    const res = await api.put("/borrowed/extend-book", {
      bookId: Number(bookId),
      extendDays,
    });
    return res.data;
  } catch (err) {
    throw err.response?.data || err;
  }
};
