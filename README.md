# SPENDWISE: ANDROID PERSONAL FINANCE MANAGEMENT

SpendWise là một giải pháp toàn diện được thiết kế để giải quyết bài toán quản lý tài chính cá nhân trên nền tảng Android. Ứng dụng không chỉ tập trung vào việc ghi chép giao dịch mà còn đặt nặng tính toàn vẹn dữ liệu, khả năng hoạt động ngoại tuyến (Offline-first) và trải nghiệm người dùng hiện đại thông qua Declarative UI.

---

## 1. TỔNG QUAN HỆ THỐNG

Dự án được xây dựng dựa trên các tiêu chuẩn phát triển phần mềm hiện đại, đảm bảo khả năng mở rộng và bảo trì dễ dàng.

* **Tên dự án:** SpendWise
* **Nền tảng:** Android (API 24+)
* **Mô hình phát triển:** MVVM (Model-View-ViewModel)
* **Trạng thái:** Hoàn thiện 100% các tính năng cốt lõi

## 2. KIẾN TRÚC KỸ THUẬT (TECH STACK)

Sự kết hợp giữa các thư viện Jetpack mạnh mẽ tạo nên một hệ thống ổn định:

### UI & UX Layer
* **Jetpack Compose:** Xây dựng giao diện dạng khai báo, tối ưu hóa quá trình render và tái sử dụng component.
* **Material Design 3:** Áp dụng hệ thống màu sắc và kiểu chữ mới nhất, hỗ trợ Dynamic Color.

### Data Layer (Offline-First Strategy)
* **Room Database:** Đóng vai trò là "Single Source of Truth", đảm bảo người dùng truy cập dữ liệu tức thì ngay cả khi không có mạng.
* **Firebase Firestore:** Đồng bộ hóa dữ liệu thời gian thực giữa các thiết bị và lưu trữ đám mây an toàn.

### Asynchronous & State Management
* **Kotlin Coroutines & Flow:** Xử lý các tác vụ bất đồng bộ và truyền dữ liệu theo luồng một cách mượt mà, tránh hiện tượng chặn luồng chính (Main Thread).
* **Firebase Authentication:** Quản lý danh tính người dùng và bảo mật phiên đăng nhập.

## 3. CÁC TÍNH NĂNG ĐIỂN HÌNH

| Nhóm tính năng | Chi tiết kỹ thuật | Trạng thái |
|:---|:---|:---:|
| **Xác thực** | Đăng ký/Đăng nhập qua Firebase Auth, bảo mật dữ liệu theo người dùng. | Hoàn thành |
| **Quản lý thu chi** | Thêm, sửa, xóa (CRUD) giao dịch linh hoạt trên Local và Cloud. | Hoàn thành |
| **Phân tích tài chính** | Hệ thống biểu đồ thống kê trực quan dựa trên danh mục chi tiêu. | Hoàn thành |
| **Bộ lọc nâng cao** | Truy vấn dữ liệu phức tạp theo thời gian, số tiền và loại hình giao dịch. | Hoàn thành |
| **Đồng bộ hóa** | Cơ chế tự động đồng bộ khi có kết nối mạng trở lại. | Hoàn thành |

## 4. CẤU TRÚC MÃ NGUỒN (PACKAGE STRUCTURE)

```text
com.spendwise.app
├── data
│   ├── local          # Cấu hình Room Database, DAO, Entity
│   ├── remote         # Firebase Services, API Client
│   └── repository     # Lớp điều phối dữ liệu giữa Local và Remote
├── di                 # Cấu hình Dependency Injection
├── ui
│   ├── screens        # Giao diện các màn hình (Home, Stats, Profile...)
│   ├── components     # Các UI Components tái sử dụng
│   └── theme          # Định nghĩa Color, Typography cho Material 3
└── viewmodel          # Xử lý Logic nghiệp vụ và giữ trạng thái UI
```
