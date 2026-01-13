import React, { useEffect, useState } from "react";
import { getReadingList } from "../../api/userApi/readingApi";
import ReadingCard from "../../components/reading/ReadingCard";

export default function Reading() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchReading = async () => {
    try {
      const res = await getReadingList();
      setItems(res || []);
    } catch (err) {
      console.error("Lỗi khi tải Reading:", err);
      setItems([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReading();
  }, []);

  if (loading) {
    return (
      <p className="text-center text-gray-300 py-10 text-lg animate-pulse">
        Đang tải sách đang đọc...
      </p>
    );
  }

  return (
    <section className="pt-16 -mt-8 pb-12 relative w-full">
      <div className="px-10">
        <h1 className="text-3xl font-semibold text-white mb-10 flex items-center gap-3">
          📖 <span>Your Reading List</span>
        </h1>

        {items.length === 0 ? (
          <p className="text-gray-300 mt-6 text-lg italic">
            Bạn chưa đọc dở cuốn sách nào.
          </p>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-8">
            {items.map((item) => (
              <ReadingCard key={item.readingId} item={item} />
            ))}
          </div>
        )}
      </div>
    </section>
  );
}
