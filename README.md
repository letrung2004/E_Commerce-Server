# SÀN GIAO DỊCH THƯƠNG MẠI ĐIỆN TỬ - BACKEND

## Giới Thiệu
Dự án này là backend của hệ thống **Sàn Giao Dịch Thương Mại Điện Tử**, được phát triển bằng **Spring Framework**. Hệ thống hỗ trợ nhiều vai trò như **Quản trị viên, Nhân viên sàn giao dịch, Người bán, Người dùng thường**, với các tính năng liên quan đến mua bán, đánh giá sản phẩm, thanh toán, quản lý cửa hàng và thống kê doanh thu.

## Công Nghệ Sử Dụng
- **Ngôn ngữ:** Java
- **Framework:** Spring MVC
- **ORM:** Hibernate, JPA
- **Cơ sở dữ liệu:** MySQL
- **Xác thực & Phân quyền:** Spring Security, JWT
- **Thanh toán online:** PayPal, Stripe, Zalo Pay, Momo
- **API Documentation:** Swagger

## Chức Năng Chính
### 1. Xác thực & Phân quyền
- Người dùng có thể đăng ký tài khoản với các vai trò: **Người dùng thường, Người bán, Nhân viên sàn giao dịch, Quản trị viên**.
- Người bán (tiểu thương, doanh nghiệp) cần được **nhân viên hệ thống phê duyệt** trước khi có thể giao dịch trên sàn.
- Hỗ trợ đăng nhập bằng JWT Token.

### 2. Chức năng cho Người bán
- **Tạo cửa hàng** và **đăng sản phẩm** để kinh doanh.
- **Xem thống kê doanh thu** theo tháng, quý, năm.

### 3. Chức năng cho Người dùng
- **Tìm kiếm sản phẩm** linh hoạt theo tên, giá, cửa hàng.
- **Sắp xếp sản phẩm** theo tên hoặc giá.
- **Phân trang sản phẩm** (tối đa 20 sản phẩm/trang).
- **Đánh giá và bình luận** về sản phẩm và người bán.
- **Mua hàng online** với nhiều phương thức thanh toán:
  - Thanh toán khi nhận hàng.
  - Thanh toán online qua **PayPal, Stripe, Zalo Pay, Momo**.
- **So sánh sản phẩm** giữa các cửa hàng khác nhau.

### 4. Chức năng cho Quản trị viên
- **Xem thống kê** về tần suất bán hàng, tổng sản phẩm theo tháng, quý, năm.
- **Quản lý người bán**, xác nhận đăng ký.

