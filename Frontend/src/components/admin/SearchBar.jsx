import React from "react";
import { Search } from "lucide-react";

export default function SearchBar({ placeholder, value, onChange }) {
  return (
    <div className="flex items-center gap-3 bg-zinc-900 p-3 rounded-xl w-full max-w-md mb-6">
      <Search size={18} className="text-zinc-400" />
      <input
        placeholder={placeholder}
        className="bg-transparent outline-none flex-1"
        value={value}
        onChange={(e) => onChange(e.target.value)}
      />
    </div>
  );
}
