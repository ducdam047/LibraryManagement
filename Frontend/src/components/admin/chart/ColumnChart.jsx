import React, { useEffect, useState } from "react";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  Legend,
  ResponsiveContainer,
} from "recharts";

import { getDashboardColumnChart } from "../../../api/adminApi/dashboardApi";

export default function ColumnChart() {
  const [data, setData] = useState([]);

  useEffect(() => {
    const fetch = async () => {
      try {
        const res = await getDashboardColumnChart();
        setData(res || []);
      } catch (err) {
        console.error("Error loading column chart:", err);
      }
    };
    fetch();
  }, []);

  return (
  <div className="bg-zinc-900 rounded-2xl shadow-xl border border-zinc-800 p-6">
    <h2 className="text-xl font-semibold mb-3 text-center text-gray-200">
      Weekly borrowing and return statistics
    </h2>

    {/* Giảm chiều cao để không bị quá nhiều khoảng dư */}
    <div className="w-full h-[300px]">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart
          data={data}
          margin={{ top: 10, right: 20, left: 10, bottom: 0 }}
        >
          {/* Legend ngay sát tiêu đề → không chiếm chỗ của chart */}
          <Legend
            verticalAlign="top"
            align="center"
            wrapperStyle={{ marginTop: -10 }}
          />

          <CartesianGrid strokeDasharray="3 3" stroke="#444" />

          <XAxis
            dataKey="label"
            stroke="#ccc"
            padding={{ left: 20, right: 20 }}
          />
          <YAxis stroke="#ccc" width={32} />

          <Tooltip
            contentStyle={{
              borderRadius: 10,
              backgroundColor: "#222",
              border: "none",
              color: "#fff",
            }}
          />

          <Bar dataKey="borrowed" fill="#3b82f6" barSize={28} />
          <Bar dataKey="returned" fill="#22c55e" barSize={28} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  </div>
);

}
