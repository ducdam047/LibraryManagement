import React, { useEffect, useState } from "react";
import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer
} from "recharts";
import { getDashboardPieChart } from "../../../api/adminApi/dashboardApi";

export default function PieChartComponent() {
  const [data, setData] = useState([]);

  useEffect(() => {
    const fetch = async () => {
      try {
        const res = await getDashboardPieChart();
        setData(res);
      } catch (err) {
        console.error("Error loading pie chart data", err);
      }
    };
    fetch();
  }, []);

  const COLORS = [
    "#6A5ACD",
    "#20B2AA",
    "#FFB347",
    "#FF6F61",
    "#4A90E2",
    "#C06C84",
    "#AED581",
  ];

  return (
    <div className="bg-zinc-900 rounded-2xl shadow-xl border border-zinc-800 p-6">
      <h2 className="text-xl font-semibold mb-3 text-center text-gray-200">
        Borrowing statistics by category
      </h2>

      <div className="w-full h-[300px]">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={data}
              dataKey="quantityBorrowed"
              nameKey="category"
              cx="40%"
              cy="50%"
              outerRadius={110}
              stroke="none"
            >
              {data.map((entry, index) => (
                <Cell key={index} fill={COLORS[index % COLORS.length]} />
              ))}
            </Pie>

            <Tooltip
              contentStyle={{
                borderRadius: 10,
                backgroundColor: "#222",
                border: "none",
              }}
              labelStyle={{
                color: "#fff",  // label (tiêu đề)
              }}
              itemStyle={{
                color: "#fff", // text từng mục
              }}
            />

            <Legend
              layout="vertical"
              verticalAlign="middle"
              align="right"
              wrapperStyle={{ right: 10 }}
            />
          </PieChart>
        </ResponsiveContainer>
      </div>
    </div>
  );


}
