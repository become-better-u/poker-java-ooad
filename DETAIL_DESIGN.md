# TÀI LIỆU THIẾT KẾ CHI TIẾT (TECHNICAL DETAIL DESIGN) - POKER SYSTEM

## 1. KIẾN TRÚC TỔNG QUAN (TECHNICAL ARCHITECTURE)
Hệ thống được thiết kế theo hướng đối tượng (OOAD), sử dụng **State Pattern** làm xương sống để quản lý luồng dữ liệu (Data Flow) và trạng thái (Game State).

### 1.1. Sơ đồ các Class chính (Class Relationships)
- **GameMatch (Context):** Nắm giữ toàn bộ dữ liệu trận đấu (Players, Deck, CommunityCards, Pot). Điều phối chuyển trạng thái qua interface `PokerState`.
- **PokerState (Interface):** Định nghĩa phương thức `handle(GameMatch context)`.
- **HandEvaluator (Utility):** Stateless class chịu trách nhiệm tính toán điểm số từ tập hợp lá bài.
- **Player (Entity):** Quản lý số dư (Balance), bài trên tay (Hand), và trạng thái (Folded, Bet).

---

## 2. THIẾT KẾ LUỒNG TRẠNG THÁI (STATE PATTERN SPECIFICATION)

Mỗi Class State kế thừa từ `PokerState` và triển khai logic nghiệp vụ riêng thông qua các phương thức cốt lõi của `GameMatch`:

### 2.1. Chi tiết các phương thức điều khiển (Control Functions)
Developer cần lưu ý logic bên trong các hàm sau khi triển khai các State:

- **`dealInitialCards()` / `dealCommunityCards()`**: 
    - Thực hiện rút lá bài từ đối tượng `Deck`. 
    - `dealInitialCards` chia 2 lá cho mỗi người chơi.
    - Community cards được đưa vào danh sách `communityCards` của `GameMatch`. Trước khi chia lá bài chung, hệ thống thực hiện "burn" (bỏ) 1 lá bài từ Deck theo luật Poker.
- **`postBlinds(sb, bb)`**: 
    - Xác định Small Blind (người chơi thứ 2) và Big Blind (người chơi thứ 3). 
    - Thực hiện trừ `balance` của người chơi và cộng vào `totalPot`. 
    - Cập nhật `currentBetInRound` của người chơi để làm mốc so sánh cho vòng cược đầu tiên.
- **`handleBettingRound()`**: 
    - **Vòng lặp:** Chạy cho đến khi `roundFinished == true`.
    - **Điều kiện kết thúc:** Tất cả người chơi chưa Fold đều có mức cược bằng với `currentHighestBet`, HOẶC chỉ còn 1 người chơi chưa Fold.
    - **Tương tác người chơi:** Nếu là Human, hiển thị menu lựa chọn và nhận input từ Scanner. Nếu là Bot, tự động gọi `Call` hoặc `Check`.
- **`resetPlayersBets()`**: 
    - Duyệt danh sách người chơi và đặt `currentBetInRound = 0`. 
    - **Lưu ý:** Hàm này phải được gọi ở đầu mỗi vòng cược mới (Flop, Turn, River) để phân biệt mức cược của từng giai đoạn.

### 2.2. PreFlopState
- **Nhiệm vụ:** Khởi tạo ván bài.
- **Luồng thực thi:** 
  1. `deck.shuffle()`: Làm mới bộ bài.
  2. `context.dealInitialCards()`: Chia bài riêng.
  3. `context.postBlinds(50, 100)`: Thu tiền cược bắt buộc.
  4. `context.handleBettingRound()`: Vòng cược đầu tiên.
- **Chuyển tiếp:** Nếu còn $>1$ người chưa Fold $\to$ `FlopState`; Ngược lại $\to$ `ShowdownState`.

### 2.3. FlopState / TurnState / RiverState
- **Nhiệm vụ:** Mở bài chung và cược tiếp diễn.
- **Luồng thực thi:**
  1. `context.dealCommunityCards()`: Mở bài theo số lượng (Flop: 3, Turn: 1, River: 1).
  2. `context.resetPlayersBets()`: Làm sạch mức cược cũ.
  3. `context.handleBettingRound()`: Vòng cược hiện tại.
- **Chuyển tiếp:** Tương tự PreFlop.

### 2.4. ShowdownState
- **Nhiệm vụ:** Tổng kết.
- **Luồng thực thi:**
  1. `HandEvaluator.evaluate()`: Duyệt qua các người chơi chưa Fold, tính điểm bằng cách kết hợp Hand + CommunityCards.
  2. Trao `totalPot` cho người thắng.
  3. Reset `context.setState(null)`.

---

## 3. THIẾT KẾ THUẬT TOÁN (ALGORITHM DESIGN)

### 3.1. Thuật toán Tính điểm (Scoring System)
Điểm số của một bộ bài được tính theo kiểu `double` để dễ dàng so sánh (`scoreA > scoreB`).

**Công thức:** `Score = Base_Weight + Tie_Breaker`

| Cấp bậc | Bộ bài | Trọng số (Base) | Tie-breaker Logic |
| :--- | :--- | :--- | :--- |
| 8 | Straight Flush | 8,000,000 | Giá trị lá cao nhất của sảnh. |
| 7 | Four of a Kind | 7,000,000 | Giá trị lá tứ quý. |
| 6 | Full House | 6,000,000 | Giá trị lá bộ ba. |
| 5 | Flush | 5,000,000 | Giá trị lá cao nhất trong bộ 5 lá đồng chất. |
| 4 | Straight | 4,000,000 | Giá trị lá cao nhất của sảnh (Xử lý riêng A-2-3-4-5 $\to$ 5). |
| 3 | Three of a Kind | 3,000,000 | Giá trị lá bộ ba. |
| 2 | Two Pair | 2,000,000 | Giá trị cặp cao nhất. |
| 1 | One Pair | 1,000,000 | Giá trị cặp đôi. |
| 0 | High Card | 0 | Giá trị lá lớn nhất. |

---

## 4. QUY ĐỊNH VỀ DỮ LIỆU (DATA SPECIFICATION)

- **Rank:** Enum (TWO=2, ..., ACE=14).
- **Suit:** Enum (CLUB, DIAMOND, HEART, SPADE).
- **Card:** Đối tượng bất biến (Immutable) gồm Rank và Suit.
- **Balance:** Kiểu `double` để xử lý tiền tệ.

---

## 5. HƯỚNG DẪN TRIỂN KHAI (IMPLEMENTATION GUIDE)

1. **Khởi tạo:** `App` khởi tạo `GameMatch`, thêm `Players`, đặt `PreFlopState`.
2. **Vòng lặp Game:** 
   ```java
   while (match.getState() != null) {
       match.nextStep();
   }
   ```
3. **Mở rộng:** Để thêm luật mới (ví dụ Side Pot), chỉ cần sửa `GameMatch.handleBettingRound()` mà không cần can thiệp vào các State Class.
