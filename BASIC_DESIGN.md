# TÀI LIỆU THIẾT KẾ CHI TIẾT (DETAIL DESIGN) - POKER SYSTEM

## 1. GIỚI THIỆU (INTRODUCTION)
Hệ thống mô phỏng trò chơi bài Poker (biến thể Texas Hold'em) được xây dựng trên ngôn ngữ Java. Mục tiêu của thiết kế là áp dụng các nguyên lý **OOAD** và **SOLID** để tạo ra một khung xương (framework) logic bền vững, tách biệt giữa thực thể vật lý, luồng trạng thái và thuật toán tính toán.

## 2. CẤU TRÚC THƯ MỤC DỰ ÁN (PROJECT STRUCTURE)
Dự án được tổ chức theo chuẩn Maven, giúp quản lý các package rõ ràng:

- `org.example.poker.model`: Chứa các thực thể dữ liệu (Card, Deck, Player, GameMatch).
- `org.example.poker.logic`: Chứa bộ xử lý thuật toán cốt lõi (HandEvaluator).
- `org.example.poker.state`: Quản lý luồng ván bài bằng State Pattern (PreFlop, Flop, Turn, River, Showdown).
- `org.example.poker.app`: Điểm khởi chạy chương trình (App.java).
poker-demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/example/poker/
│   │   │       ├── app/            # Điểm khởi chạy và cấu hình hệ thống
│   │   │       │   └── App.java
│   │   │       ├── model/          # Các thực thể dữ liệu (Entities)
│   │   │       │   ├── Card.java
│   │   │       │   ├── Deck.java
│   │   │       │   ├── Player.java
│   │   │       │   ├── Rank.java
│   │   │       │   └── Suit.java
│   │   │       ├── logic/          # Xử lý quy tắc và thuật toán (Control)
│   │   │       │   └── HandEvaluator.java
│   │   │       └── state/          # Quản lý vòng đời ván chơi (State Pattern)
│   │   │           ├── PokerState.java
│   │   │           ├── PreFlopState.java
│   │   │           ├── FlopState.java
│   │   │           ├── TurnState.java
│   │   │           ├── RiverState.java
│   │   │           └── ShowdownState.java
│   └── test/                       # Unit Tests cho Logic và Model
└── pom.xml

## 3. THIẾT KẾ HƯỚNG ĐỐI TƯỢNG (OBJECT-ORIENTED DESIGN)

### 3.1. Các Mẫu thiết kế áp dụng (Design Patterns)
1.  **State Pattern:** Quản lý vòng đời ván bài thông qua interface `PokerState`. Mỗi trạng thái (ví dụ `FlopState`) tự định nghĩa logic chia bài và chuyển tiếp sang trạng thái kế tiếp, giúp loại bỏ các câu lệnh `switch-case` phức tạp.
2.  **Strategy Pattern:** Tách rời logic so sánh bài (`HandEvaluator`) khỏi lớp `Player`. Điều này cho phép thay đổi thuật toán tính điểm hoặc áp dụng cho các biến thể Poker khác mà không ảnh hưởng đến cấu trúc dữ liệu.
3.  **Singleton/Static Utility:** `HandEvaluator` được thiết kế như một lớp tiện ích (Utility Class) để phục vụ việc tính toán tập trung cho toàn bộ các `GameMatch`.

### 3.2. Mối quan hệ giữa các Lớp
- **Composition (Thành phần):** `Deck` chứa 52 đối tượng `Card`.
- **Aggregation (Tập hợp):** `GameMatch` quản lý danh sách các `Player`.
- **Dependency (Phụ thuộc):** `HandEvaluator` phụ thuộc vào `Card` và `Rank` để thực thi thuật toán.

## 4. THUẬT TOÁN TÍNH ĐIỂM (HAND RANKING LOGIC)

Hệ thống áp dụng phương pháp **Trọng số phân tầng (Hierarchical Weighting System)** để quy đổi giá trị của một bộ bài kết hợp (7 lá bài) thành một con số thực (`double`) duy nhất. Phương pháp này cho phép xác định người thắng cuộc chỉ bằng một phép so sánh `>` đơn giản.

### 4.1. Công thức tính điểm tổng quát
$$Score = \text{Base\_Weight} + \text{Tie\_Breaker\_Value}$$

**Trong đó:**
*   **Base\_Weight**: Trọng số cố định dựa trên cấp bậc của bộ bài. Mỗi cấp bậc cách nhau đúng $1,000,000$ đơn vị.
*   **Tie\_Breaker\_Value**: Giá trị bổ trợ (High Card của bộ đó) dùng để phân định thắng thua khi các người chơi có cùng cấp bậc bài.

### 4.2. Bảng phân cấp trọng số thực tế

| Cấp bậc | Tên bộ bài (Hand Rank) | Trọng số cơ sở | Logic xác định (Mã nguồn) |
| :--- | :--- | :--- | :--- |
| **8** | **Sảnh Thùng (Straight Flush)** | $8,000,000$ | `isFlush && straightHighCard > 0` |
| **7** | **Tứ Quý (Four of a Kind)** | $7,000,000$ | `countsMap.containsValue(4)` |
| **6** | **Cù Lũ (Full House)** | $6,000,000$ | `containsValue(3) && containsValue(2)` |
| **5** | **Thùng (Flush)** | $5,000,000$ | `checkFlush(cards) == true` |
| **4** | **Sảnh (Straight)** | $4,000,000$ | `checkStraight(values) > 0` |
| **3** | **Xám Cô (Three of a Kind)** | $3,000,000$ | `countsMap.containsValue(3)` |
| **2** | **Hai Đôi (Two Pair)** | $2,000,000$ | `numPairs >= 2` |
| **1** | **Một Đôi (One Pair)** | $1,000,000$ | `numPairs == 1` |
| **0** | **Mậu Thầu (High Card)** | $0$ | Giá trị lá bài lớn nhất ($2 - 14$) |

### 4.3. Chi tiết các thuật toán kiểm tra logic

#### A. Thuật toán kiểm tra Sảnh (Straight)
Hàm `checkStraight` thực hiện:
1.  **Lọc và Sắp xếp:** Loại bỏ trùng Rank và sắp xếp giảm dần.
2.  **Cửa sổ trượt:** Kiểm tra nếu khoảng cách giữa lá bài đầu và lá bài thứ 5 trong chuỗi bằng 4 đơn vị.
3.  **Trường hợp đặc biệt (Wheel Straight):** Xử lý sảnh thấp nhất **A-2-3-4-5** bằng cách kiểm tra tập hợp `{14, 2, 3, 4, 5}`. Nếu thỏa mãn, trả về điểm số tương ứng với High Card là 5.

#### B. Thuật toán kiểm tra Thùng (Flush)
Hàm `checkFlush` thống kê số lượng quân bài theo từng chất (`Suit`):
*   Nếu bất kỳ chất nào có số lượng xuất hiện $\ge 5$, bộ bài được xác định là Thùng.

#### C. Xử lý phân định thắng thua (Tie-breaking)
Khi hai người chơi có cùng cấp bậc bài, hệ thống lấy giá trị của lá bài cao nhất tạo nên bộ đó (thông qua `getHighCardByCount`) cộng trực tiếp vào `Base_Weight`.
*   **Ví dụ:** Người A có Đôi 8 ($1,000,008$) và Người B có Đôi 10 ($1,000,010$). Kết quả: Người B thắng.

## 5. KẾT LUẬN
Thiết kế này đảm bảo tính đóng gói (Encapsulation) cao, khi logic tính điểm phức tạp được ẩn đi sau một giá trị duy nhất, giúp lớp điều khiển trận đấu (`GameMatch`) vận hành trơn tru và chính xác.