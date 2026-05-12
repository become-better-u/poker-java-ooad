## 1. TỔNG QUAN (OVERVIEW)
Hệ thống Poker này được xây dựng theo mô hình **State Pattern** để quản lý vòng đời ván đấu và áp dụng nguyên lý **OOAD** để tách biệt logic nghiệp vụ. Tài liệu này phục vụ cho Tester xây dựng test case và BA kiểm tra tính đúng đắn của nghiệp vụ.

---

## 2. KIẾN TRÚC HỆ THỐNG (SYSTEM ARCHITECTURE)

### 2.1. Danh sách các Package chính
- `org.example.poker.app`: Lớp điều khiển chính (`App`) và hiển thị (`CardVisualizer`).
- `org.example.poker.model`: Thực thể dữ liệu (`GameMatch`, `Player`, `Card`, `Deck`, `Rank`, `Suit`).
- `org.example.poker.logic`: Thuật toán cốt lõi (`HandEvaluator`).
- `org.example.poker.state`: Quản lý luồng ván đấu.

---

## 3. LUỒNG TRẠNG THÁI VÁN ĐẤU (GAME FLOW - STATE MACHINE)

Dựa vào State Pattern, mỗi ván đấu trải qua các bước sau:

| Trạng thái | Hành động nghiệp vụ | Chuyển tiếp |
| :--- | :--- | :--- |
| **PreFlopState** | Xáo bài, chia 2 lá bài riêng cho mỗi người, thu tiền mù (SB, BB), vòng cược 1. | FlopState hoặc Showdown |
| **FlopState** | Chia 3 lá bài chung lên bàn, vòng cược 2. | TurnState hoặc Showdown |
| **TurnState** | Chia lá bài chung thứ 4 lên bàn, vòng cược 3. | RiverState hoặc Showdown |
| **RiverState** | Chia lá bài chung thứ 5 lên bàn, vòng cược cuối. | ShowdownState |
| **ShowdownState** | Lật bài, so điểm, xác định người thắng, trả thưởng (Pot). | Kết thúc (null) |

> **Lưu ý cho Tester:** Nếu số người chơi chưa Fold chỉ còn $\le 1$, hệ thống phải chuyển thẳng về `ShowdownState`.

---

## 4. QUY TẮC ĐẶT CƯỢC (BETTING RULES)

Hệ thống áp dụng các quy tắc cược tiêu chuẩn:

### 4.1. Tiền mù (Blinds)
- **Small Blind (SB):** Mặc định $50 (Người chơi thứ 2).
- **Big Blind (BB):** Mặc định $100 (Người chơi thứ 3).
- **Mục đích:** Đảm bảo luôn có tối thiểu $150 trong Pot để bắt đầu.

### 4.2. Các hành động người chơi (Player Actions)
1. **Check:** Chỉ được thực hiện khi chưa có ai đặt cược trước đó trong vòng hiện tại.
2. **Call:** Đặt cược bằng mức cao nhất hiện tại để tiếp tục.
3. **Raise:** Đặt cược cao hơn mức hiện tại. Yêu cầu: `Tiền Raise + Mức theo > Mức cao nhất`.
4. **Fold:** Bỏ bài và mất quyền tham gia Pot hiện tại.
5. **All-in:** Cược toàn bộ số tiền còn lại.

---

## 5. THUẬT TOÁN ĐÁNH GIÁ BÀI (HAND RANKING LOGIC)

Hệ thống đánh giá bộ bài 5 lá tốt nhất từ 7 lá (2 lá trên tay + 5 lá chung) theo trọng số phân tầng:

| Cấp bậc | Tên bộ bài (Hand Rank) | Logic xác định |
| :---: | :--- | :--- |
| **8** | **Sảnh Thùng (Straight Flush)** | Đồng chất + Sảnh liên tiếp. |
| **7** | **Tứ Quý (Four of a Kind)** | 4 lá cùng Rank. |
| **6** | **Cù Lũ (Full House)** | 1 bộ ba + 1 bộ đôi. |
| **5** | **Thùng (Flush)** | 5 lá cùng chất (Suit). |
| **4** | **Sảnh (Straight)** | 5 lá liên tiếp (Xử lý được sảnh A-2-3-4-5). |
| **3** | **Xám Cô (Three of a Kind)** | 3 lá cùng Rank. |
| **2** | **Hai Đôi (Two Pair)** | 2 cặp lá cùng Rank. |
| **1** | **Một Đôi (One Pair)** | 1 cặp lá cùng Rank. |
| **0** | **Mậu Thầu (High Card)** | Lá bài cao nhất trong bộ. |

### Cơ chế phân định thắng thua (Tie-break)
Khi hai người chơi có cùng cấp bậc bài (ví dụ cùng One Pair), hệ thống lấy giá trị lá bài cao nhất của bộ đó để cộng vào trọng số. Nếu vẫn bằng nhau, hệ thống so sánh đến lá bài lẻ cao nhất (Kicker).

---

## 6. XÁC NHẬN NGHIỆP VỤ
- [x] Hệ thống đã triển khai State Pattern đúng thiết kế.
- [x] Luật tiền mù (Blinds) đã được áp dụng.
- [x] Hiển thị bài cho người chơi Human mỗi lượt hành động.
- [x] Tách biệt rõ ràng Model (Dữ liệu) và Logic (Tính điểm).
