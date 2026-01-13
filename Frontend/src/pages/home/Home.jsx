import React, { useState } from "react";
import FeaturedBooks from "../../components/FeaturedBooks";
import Reading from "../../pages/reading/Reading";
import Wishlist from "../../pages/wishlist/Wishlist";
import Footer from "../../components/Footer";

export default function Home({ searchQuery }) {
  const [page, setPage] = useState("featured");

  return (
    <div className="min-h-screen bg-black text-white">

      {page === "featured" && <FeaturedBooks searchQuery={searchQuery} />}
      {page === "reading" && <Reading />}
      {page === "wishlist" && <Wishlist />}

      <Footer />
    </div>
  );
}
