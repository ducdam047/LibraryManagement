import React, { useEffect, useState, useRef } from "react";
import { getFeaturedBooks, searchBooks, filterCategory, getTrendingBooks } from "../api/userApi/bookApi";
import { useNavigate } from "react-router-dom";
import { ChevronLeft, ChevronRight } from "lucide-react";

export default function FeaturedBooks({ searchQuery }) {
  const navigate = useNavigate();

  const [books, setBooks] = useState([]);
  const [trendingBooks, setTrendingBooks] = useState([]);
  const [loading, setLoading] = useState(true);

  const availableRef = useRef(null);
  const trendingRef = useRef(null);

  const [availableScroll, setAvailableScroll] = useState({ left: false, right: false });
  const [trendingScroll, setTrendingScroll] = useState({ left: false, right: false });

  const checkScrollability = (ref, setState) => {
    if (!ref.current) return;
    const el = ref.current;

    setState({
      left: el.scrollLeft > 0,
      right: el.scrollLeft < el.scrollWidth - el.clientWidth - 5,
    });
  };

  const scroll = (ref, direction) => {
    if (!ref.current) return;
    ref.current.scrollBy({
      left: direction === "left" ? -350 : 350,
      behavior: "smooth",
    });
  };

  // Load Featured + Trending + Search
  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);

      const categories = [
        "Văn học - Tiểu thuyết",
        "Khoa học - Công nghệ",
        "Lịch sử - Văn hoá",
        "Kinh tế - Chính trị",
        "Triết học",
        "Tâm lý học",
        "Phát triển bản thân",
      ];

      try {
        // Nếu search trống → load featured + trending
        if (!searchQuery || searchQuery.trim() === "") {
          const featuredRes = await getFeaturedBooks();
          const trendingRes = await getTrendingBooks();

          const featuredData = featuredRes?.data ?? featuredRes;
          const rawTrending = trendingRes?.data ?? trendingRes;

          console.log("TRENDING RAW:", rawTrending);

          let normalizedTrending = [];

          if (Array.isArray(rawTrending)) {
            normalizedTrending = rawTrending
              .map(item => {
                // CASE 1: { book, borrowCount }
                if (item.book && typeof item.borrowCount === "number") {
                  return item;
                }

                // CASE 2: [book, borrowCount]
                if (Array.isArray(item) && item.length === 2) {
                  return {
                    book: item[0],
                    borrowCount: item[1],
                  };
                }

                return null;
              })
              .filter(Boolean);
          }

          console.log("TRENDING NORMALIZED:", normalizedTrending);

          if (!cancelled) {
            setBooks(Array.isArray(featuredData) ? featuredData : []);
            setTrendingBooks(normalizedTrending);
          }

          return;
        }

        // Nếu search là category → filter theo category
        if (categories.includes(searchQuery)) {
          const res = await filterCategory(searchQuery);

          const uniqueBooks = [];
          const seenTitles = new Set();

          for (const b of res) {
            if (!seenTitles.has(b.title)) {
              uniqueBooks.push(b);
              seenTitles.add(b.title);
            }
          }

          if (!cancelled) setBooks(uniqueBooks);
          return;
        }

        // Search bình thường
        const res = await searchBooks(searchQuery);
        let data = res?.data ?? res;

        if (!Array.isArray(data)) data = [data];
        if (!cancelled) setBooks(data);
      } catch (err) {
        console.error("Lỗi tải sách:", err);
        if (!cancelled) setBooks([]);
      } finally {
        if (!cancelled) {
          setLoading(false);

          setTimeout(() => {
            checkScrollability(availableRef, setAvailableScroll);
            checkScrollability(trendingRef, setTrendingScroll);
          }, 0);
        }
      }
    }

    load();
    return () => (cancelled = true);
  }, [searchQuery]);

  // Update scroll on resize
  useEffect(() => {
    const updateScroll = () => {
      checkScrollability(availableRef, setAvailableScroll);
      checkScrollability(trendingRef, setTrendingScroll);
    };

    updateScroll();
    window.addEventListener("resize", updateScroll);
    return () => window.removeEventListener("resize", updateScroll);
  }, [books, trendingBooks]);

  if (loading)
    return (
      <p className="text-center py-10 text-lg text-gray-300">Đang tải sách...</p>
    );

  return (
    <section className="py-16 w-full">
      <div className="px-10">
        {/* FEATURED BOOKS */}
        <div className="relative">
          <h3 className="text-3xl font-semibold text-white mb-8 -mt-8 flex items-center gap-3">
            <span>📚</span>
            <span>
              {searchQuery ? `Thể loại: "${searchQuery}"` : "Featured Books"}
            </span>
          </h3>

          {availableScroll.left && (
            <button
              onClick={() => scroll(availableRef, "left")}
              className="absolute left-0 top-1/2 -translate-y-1/2 bg-white/20 hover:bg-white/40 text-white p-3 rounded-full backdrop-blur-md shadow-lg z-20">
              <ChevronLeft size={24} />
            </button>
          )}

          <div
            ref={availableRef}
            onScroll={() => checkScrollability(availableRef, setAvailableScroll)}
            className="relative flex gap-8 overflow-x-auto scroll-smooth no-scrollbar pb-4"
          >
            {books.map((book) => (
              <BookCard key={book.bookId} book={book} navigate={navigate} />
            ))}
          </div>

          {availableScroll.right && (
            <button
              onClick={() => scroll(availableRef, "right")}
              className="absolute right-0 top-1/2 -translate-y-1/2 bg-white/20 hover:bg-white/40 text-white p-3 rounded-full backdrop-blur-md shadow-lg z-20">
              <ChevronRight size={24} />
            </button>
          )}
        </div>

        {/* TRENDING BOOKS */}
        {(!searchQuery || searchQuery.trim() === "") && (
          <div className="relative mt-20">
            <h3 className="text-3xl font-semibold text-white mb-8 flex items-center gap-3">
              <span>🔥</span> <span>Trending Books</span>
            </h3>

            {trendingScroll.left && (
              <button
                onClick={() => scroll(trendingRef, "left")}
                className="absolute left-0 top-1/2 -translate-y-1/2 bg-white/20 hover:bg-white/40 text-white p-3 rounded-full backdrop-blur-md shadow-lg z-20"
              >
                <ChevronLeft size={24} />
              </button>
            )}

            <div
              ref={trendingRef}
              onScroll={() => checkScrollability(trendingRef, setTrendingScroll)}
              className="relative flex gap-8 overflow-x-auto scroll-smooth no-scrollbar pb-4"
            >
              {trendingBooks.map((item) => (
                <BookCard
                  key={item.book.bookId}
                  book={item.book}
                  borrowCount={item.borrowCount}
                  navigate={navigate}
                />
              ))}
            </div>

            {trendingScroll.right && (
              <button
                onClick={() => scroll(trendingRef, "right")}
                className="absolute right-0 top-1/2 -translate-y-1/2 bg-white/20 hover:bg-white/40 text-white p-3 rounded-full backdrop-blur-md shadow-lg z-20"
              >
                <ChevronRight size={24} />
              </button>
            )}
          </div>
        )}
      </div>
    </section>
  );
}

function BookCard({ book, borrowCount, navigate }) {
  return (
    <div
      onClick={() => navigate(`/book/detail/${encodeURIComponent(book.title)}`)}
      className="relative bg-white/10 backdrop-blur-lg rounded-2xl overflow-hidden flex-shrink-0 border border-white/20 shadow-xl hover:shadow-blue-500/40 hover:scale-[1.03] cursor-pointer transition-all duration-300 w-64"
    >
      <div className="relative overflow-hidden">
        <img
          src={book.imageUrl}
          alt={book.title}
          className="w-full h-80 object-cover object-top transition duration-500"
        />

        <span
          className={`absolute top-2 right-2 px-3 py-1 text-xs font-semibold rounded-full 
            ${book.availableCopies > 0 ? "bg-green-500" : "bg-red-500"} text-white`}
        >
          {book.availableCopies > 0 ? "Available" : "Unavailable"}
        </span>
      </div>

      <div className="p-5 text-white pb-12">
        <h4 className="text-lg font-bold line-clamp-1">{book.title}</h4>
        <p className="text-sm text-gray-300 mt-1 overflow-hidden text-ellipsis whitespace-nowrap">
          {book.author}
        </p>
      </div>

      {typeof borrowCount === "number" && (
        <span className="absolute bottom-3 left-3 text-xs bg-orange-500/80 px-2 py-1 rounded-md text-white">
          🔥 {borrowCount} lượt mượn
        </span>
      )}

      <span className="absolute bottom-3 right-3 text-xs bg-white/20 px-2 py-1 rounded-md text-gray-200">
        {book.availableCopies} bản
      </span>
    </div>
  );
}
