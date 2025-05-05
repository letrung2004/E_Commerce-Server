package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.dto.OrderDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;


//           [1] Người dùng chọn phương thức "Thanh toán qua VNPay" tại FE
//            |
//           [2] FE gọi API POST /place-order với OrderDto (chứa paymentMethod = "VNPay")
//            |
//           [3] BE: createOrder(orderDto)
//                  - Tạo Order, OrderDetail, Payment
//                  - Nếu là VNPay, gọi VNPAYService.createOrder() -> trả về URL thanh toán
//            |
//           [4] BE trả lại URL thanh toán -> FE redirect người dùng đến VNPay
//            |
//           [5] Người dùng thực hiện thanh toán tại cổng VNPay
//            |
//           [6] VNPay redirect về trang return: /vnpay-payment-return?params...
//            |
//           [7] BE nhận callback tại /vnpay-payment-return
//                  - Kiểm tra checksum
//                  - Nếu thành công: cập nhật trạng thái payment = "Đã thanh toán"
//            |
//           [8] BE phản hồi lại kết quả (hoặc redirect về trang Thank You)


@Service
public class VNPayService {
    private static final String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String vnpReturnUrl = "http://localhost:8080/webapp_war_exploded/api/v1/payment/vnpay-return";
    private static final String vnpTmnCode = "STU89ZVN";
    private static final String vnpHashSecret = "WE2L2441DRJAVMW4LMH8FSSBDCYH1FP4";
    private static final String vnpVersion = "2.1.0";
    private static final String vnpCommand = "pay";
    private static final String vnpOrderType = "other";
    private static final String vnpCurrCode = "VND";
    private static final String vnpLocale = "vn";

    public String createPaymentUrl(HttpServletRequest request, OrderDto orderDto) {
        String vnp_TxnRef = orderDto.getTransactionId();
        if (vnp_TxnRef == null || vnp_TxnRef.isEmpty()) {
            throw new IllegalArgumentException("Transaction ID is missing in OrderDto");
        }

        String vnp_IpAddr = getIpAddress(request);
        String vnp_OrderInfo = "Thanh toan don hang: " + orderDto.getUsername() + "_" + vnp_TxnRef;
        long amount = orderDto.getTotal() * 100;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnpVersion);
        vnp_Params.put("vnp_Command", vnpCommand);
        vnp_Params.put("vnp_TmnCode", vnpTmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", vnpCurrCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", vnpOrderType);
        vnp_Params.put("vnp_Locale", vnpLocale);
        vnp_Params.put("vnp_ReturnUrl", vnpReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=');
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String vnp_SecureHash = hmacSHA512(vnpHashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        return vnpUrl + "?" + query;
    }

    public static String hmacSHA512(final String key, final String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) sb.append(String.format("%02x", b & 0xff));
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        return (ipAddress != null) ? ipAddress : request.getRemoteAddr();
    }

    // Hàm xác thực chữ ký trả về từ VNPAY (sử dụng trong bước 3)
    public boolean validateSignature(Map<String, String> vnpParams) {
        String vnp_SecureHash = vnpParams.remove("vnp_SecureHash"); // Lấy và xóa hash khỏi map
        if (vnp_SecureHash == null) return false;

        // Sắp xếp lại các tham số còn lại
        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnpParams.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }

        String calculatedHash = hmacSHA512(vnpHashSecret, hashData.toString());
        return calculatedHash.equalsIgnoreCase(vnp_SecureHash);
    }
}