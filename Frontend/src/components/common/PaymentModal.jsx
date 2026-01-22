import { getPaymentUrl } from "../../api/userApi/borrowApi";
import toast from "react-hot-toast";

export default function PaymentModal({ open, loan, onClose, onSuccess }) {
    if (!open || !loan) return null;

    return (
        <div className="fixed inset-0 z-50 bg-black/60 flex items-center justify-center">
            <div className="bg-[#1f2937] w-[400px] rounded-xl p-6 text-white">

                <h2 className="text-xl font-semibold mb-4">
                    💳 Thanh toán & Nhận sách
                </h2>

                <div className="space-y-2 text-sm text-gray-300">
                    <p><b>Sách:</b> {loan.title}</p>
                    <p><b>Số ngày mượn:</b> {loan.borrowDays} ngày</p>

                    {loan.borrowFee && (
                        <p><b>Phí mượn:</b> {loan.borrowFee.toLocaleString()}đ</p>
                    )}

                    {loan.depositRequired && (
                        <p><b>Tiền cọc:</b> {loan.depositRequired.toLocaleString()}đ</p>
                    )}
                </div>

                <button
                    onClick={async () => {
                        try {
                            const res = await getPaymentUrl(loan.loanId);
                            window.location.href = res.data;
                        } catch (err) {
                            toast.error(
                                err.response?.data?.message || "Không thể tạo link thanh toán"
                            );
                        }
                    }}
                    className="px-4 py-2 rounded-lg bg-green-600 hover:bg-green-700"
                >
                    Thanh toán bằng VNPAY
                </button>

                <div className="mt-6 flex justify-end gap-3">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 rounded-lg bg-gray-600 hover:bg-gray-700"
                    >
                        Huỷ
                    </button>

                    <button
                        onClick={onSuccess}
                        className="px-4 py-2 rounded-lg bg-green-600 hover:bg-green-700"
                    >
                        Giả lập thành công
                    </button>
                </div>

            </div>
        </div>
    );
}
