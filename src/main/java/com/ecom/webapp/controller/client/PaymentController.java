package com.ecom.webapp.controller.client;

import com.ecom.webapp.service.OrderService;
import com.ecom.webapp.service.PaymentService;
import com.ecom.webapp.service.impl.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment") // Hoặc prefix phù hợp
public class PaymentController {

    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private PaymentService paymentService;


    // Định nghĩa cấu trúc response trả về cho VNPAY
    private static class VnPayResponse {
        public String RspCode;
        public String Message;

        public VnPayResponse(String rspCode, String message) {
            RspCode = rspCode;
            Message = message;
        }
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<?> handleVnpayReturn(@RequestParam Map<String, String> vnpParams,
                                               HttpServletRequest request) {

        String clientIp = request.getRemoteAddr();
        System.out.println("VNPay callback from IP: " + clientIp);

        System.out.println("VNPAY Return Params: " + vnpParams);

        // 1. Xác thực chữ ký
        if (!vnPayService.validateSignature(new HashMap<>(vnpParams))) { // Tạo bản sao để không thay đổi map gốc khi validate
            System.out.println("VNPAY Return: Invalid Signature");
            // Quan trọng: Trả về mã RspCode=97 cho VNPAY biết chữ ký không hợp lệ
            return ResponseEntity.ok(new VnPayResponse("97", "Invalid Signature"));
        }

        // Lấy các tham số quan trọng
        String vnp_TxnRef = vnpParams.get("vnp_TxnRef");         // Mã giao dịch của bạn
        String vnp_ResponseCode = vnpParams.get("vnp_ResponseCode"); // Mã trạng thái giao dịch từ VNPAY
        String vnp_TransactionStatus = vnpParams.get("vnp_TransactionStatus"); // Trạng thái chi tiết (nếu có)
        String vnp_Amount = vnpParams.get("vnp_Amount");         // Số tiền (đã nhân 100)
        String vnp_TransactionNo = vnpParams.get("vnp_TransactionNo"); // Mã giao dịch tại VNPAY (lưu lại nếu cần)

        try {
           // 5. Xử lý kết quả thanh toán từ VNPAY
            if ("00".equals(vnp_ResponseCode)) {
                // Giao dịch thành công
                System.out.println("VNPAY Return: Payment Success. TxnRef: " + vnp_TxnRef);
                // Cập nhật trạng thái đơn hàng thành "Đã thanh toán" hoặc tương tự
                this.paymentService.markPaymentAsPaid(vnp_TxnRef);

                // Trả về mã 00 cho VNPAY xác nhận đã xử lý thành công
//                return ResponseEntity.ok(new VnPayResponse("00", "Confirm Success"));
                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create("http://localhost:5173/me/my-orders"))
                        .build();
            } else {
                // Giao dịch thất bại
                System.out.println("VNPAY Return: Payment Failed. TxnRef: " + vnp_TxnRef + ", ResponseCode: " + vnp_ResponseCode);
                // Cập nhật trạng thái đơn hàng thành "Thanh toán thất bại" hoặc hủy đơn
                // orderService.updateOrderStatus(order.getId(), OrderStatus.PAYMENT_FAILED, vnp_TransactionNo);

                // Vẫn trả về mã 00 cho VNPAY để xác nhận đã nhận và xử lý thông báo,
                // dù kết quả kinh doanh là thất bại.
                return ResponseEntity.ok(new VnPayResponse("00", "Confirm Success"));
            }

        } catch (Exception e) {
            System.err.println("Error processing VNPAY return: " + e.getMessage());
            e.printStackTrace();
            // Lỗi xử lý phía merchant
            return ResponseEntity.ok(new VnPayResponse("99", "Unknown error")); // Mã 99: Unknown error
        }
    }
}
