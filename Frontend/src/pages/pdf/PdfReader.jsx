import React from "react";

import { Worker, Viewer } from "@react-pdf-viewer/core";
import { defaultLayoutPlugin } from "@react-pdf-viewer/default-layout";

import "@react-pdf-viewer/core/lib/styles/index.css";
import "@react-pdf-viewer/default-layout/lib/styles/index.css";

import pkg from "pdfjs-dist/package.json";
const pdfjsVersion = pkg.version;

export default function PdfReader({ pdfUrl, startPage, onPageChange }) {
  // ✅ GỌI TRỰC TIẾP – KHÔNG BỌC
  const defaultLayoutPluginInstance = defaultLayoutPlugin();

  return (
    <div style={{ height: "100%", width: "100%" }}>
      <Worker
        workerUrl={`https://unpkg.com/pdfjs-dist@${pdfjsVersion}/build/pdf.worker.min.js`}
      >
        <Viewer
          fileUrl={pdfUrl}
          initialPage={startPage ? startPage - 1 : 0}
          plugins={[defaultLayoutPluginInstance]}
          onPageChange={(e) => {
            onPageChange?.(e.currentPage + 1);
          }}
        />
      </Worker>
    </div>
  );
}
