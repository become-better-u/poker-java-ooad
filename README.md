# Poker Game Simulation (Java)

Dự án mô phỏng trò chơi bài Poker (biến thể Texas Hold'em) chạy trên giao diện dòng lệnh (Console), được xây dựng bằng ngôn ngữ Java và quản lý bởi Maven.

## 🚀 Tính năng nổi bật
- **Mô hình State Pattern:** Quản lý vòng đời trận đấu chuyên nghiệp qua các giai đoạn (Pre-Flop, Flop, Turn, River, Showdown).
- **Thuật toán đánh giá bài:** Hệ thống tính điểm trọng số giúp phân loại bộ bài (Sảnh, Thùng, Cù Lũ...) và xác định người thắng cuộc chính xác.
- **Bot AI đơn giản:** Cho phép chơi cùng với các đối thủ máy.
- **Luật Blinds:** Áp dụng luật tiền mù Small Blind và Big Blind để tăng tính hấp dẫn.

## 📋 Yêu cầu hệ thống
- **Java JDK:** Phiên bản 17 trở lên (Khuyến nghị Java 21 hoặc 24).
- **Maven:** Phiên bản 3.6 trở lên.

## 🛠️ Cách Build và Chạy dự án

### 1. Clone source code
```bash
git clone https://github.com/become-better-u/poker-java-ooad.git
cd Poker
```

### 2. Biên dịch (Build)
Sử dụng Maven để tải dependencies và biên dịch mã nguồn:
```bash
mvn clean compile
```

### 3. Chạy chương trình (Run)
Cách đơn giản nhất để chạy ngay trên terminal:
```bash
mvn exec:java
```

## 📂 Cấu trúc dự án
Dự án được tổ chức theo các nguyên lý **OOAD** và **SOLID**:
- `org.example.poker.app`: Điểm khởi chạy (`App.java`) và hiển thị bài (`CardVisualizer.java`).
- `org.example.poker.model`: Các thực thể dữ liệu (`Card`, `Player`, `Deck`, `GameMatch`).
- `org.example.poker.logic`: Thuật toán tính điểm (`HandEvaluator.java`).
- `org.example.poker.state`: Quản lý luồng ván bài bằng State Pattern.

## 🎮 Cách chơi
1. Nhập tên người chơi của bạn.
2. Theo dõi các lá bài trên tay và bài chung trên bàn.
3. Chọn các lệnh tương ứng:
   - `1`: **Check/Call** (Kiểm tra hoặc Theo cược)
   - `2`: **Raise** (Tố thêm)
   - `3`: **Fold** (Bỏ bài)
   - `4`: **All-in** (Tố tất cả)

---
Chúc bạn chơi game vui vẻ! 🃏
