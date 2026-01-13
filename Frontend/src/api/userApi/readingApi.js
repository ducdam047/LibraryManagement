import axiosConfig from "../axiosConfig";

// Lấy danh sách Reading
export const getReadingList = async () => {
  try {
    const res = await axiosConfig.get("/reading");
    return res.data?.data ?? [];
  } catch (err) {
    console.error("Lỗi getReadingList:", err);
    return [];
  }
};

// Lấy chi tiết 1 item trong Reading
export const getReadingDetail = async (readingId) => {
  try {
    const res = await axiosConfig.get(`/reading/${readingId}`);
    return res.data?.data ?? null; // 🔥 LẤY ReadingModel
  } catch (err) {
    console.error("Lỗi getReadingDetail:", err);
    return null;
  }
};

// Kiểm tra sách có trong Reading bằng bookId
export const getReadingByBookId = async (bookId) => {
  try {
    const res = await axiosConfig.get(`/reading/book/${bookId}`);
    return res.data?.data ?? null; // 🔥 unwrap ApiResponse
  } catch (err) {
    console.error("Lỗi getReadingByBookId:", err);
    return null;
  }
};

// Thêm sách vào Reading
export const addToReading = async (bookId) => {
  try {
    const res = await axiosConfig.post(`/reading/${bookId}`);
    return res.data?.data ?? null;
  } catch (err) {
    console.error("Lỗi addToReading:", err);
    return null;
  }
};

// Lưu tiến trình
export const saveReadingProgress = async (bookId, page) => {
  try {
    const res = await axiosConfig.post("/reading/progress", { bookId, page });
    return res.data?.data ?? null;
  } catch (err) {
    console.error("Lỗi saveReadingProgress:", err);
    return null;
  }
};
