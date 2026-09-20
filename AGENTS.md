# 📜 AGENTS.md — Quy Tắc Hoạt Động Của AI Coding Agent

Tài liệu này là **tập hợp các quy tắc bắt buộc (Mandatory Rules & Behavioral Constraints)** dành cho tất cả các AI Coding Agent (Antigravity, Claude Code, Cursor, Copilot, OpenCode, v.v.) khi làm việc trong repository **School Medical Management System**.

Mọi AI khi tương tác với dự án này **BẮT BUỘC PHẢI TUÂN THỦ 100%** các quy tắc dưới đây, không có ngoại lệ.

---

## 🛑 NGUYÊN TẮC CỐT LÕI (CORE DIRECTIVES)

### 1. Luôn Lập Kế Hoạch Trước Khi Hành Động (Plan-First Principle)
- **CẤM** tự ý sửa đổi file mã nguồn, cấu hình hoặc thực thi các thay đổi lớn khi chưa có kế hoạch chi tiết.
- Mỗi khi tiếp nhận một yêu cầu tính năng, sửa lỗi lớn hoặc tái cấu trúc:
  1. Phân tích hiện trạng, luồng dữ liệu và các file bị ảnh hưởng.
  2. Lập bản kế hoạch chi tiết (lưu vào `docs/plans/<tên-kế-hoạch>.md` hoặc artifact `implementation_plan.md`).
  3. **DỪNG LẠI (STOP)** và chờ người dùng (User) xác nhận/phê duyệt kế hoạch trước khi bắt đầu viết code.

---

### 2. Cấm Tự Ý Quyết Định Khi Có Nhiều Lựa Chọn / Phát Sinh (Ask Before Deciding)
- Trong quá trình phân tích hoặc thực hiện kế hoạch, nếu xuất hiện:
  - Nhiều phương án kỹ thuật khác nhau (ví dụ: chọn thư viện, chọn giải pháp kiến trúc).
  - Vấn đề phát sinh ngoài dự kiến (unexpected issues/bugs).
  - Đánh đổi giữa các tiêu chí (Performance vs Readability, Breaking Change vs Backward Compatibility).
- **CẤM** tự ý chọn một phương án và âm thầm triển khai.
- **BẮT BUỘC PHẢI HỎI Ý KIẾN USER**. Khi hỏi, bắt buộc phải trình bày theo cấu trúc:
  1. **Bối cảnh & Vấn đề:** Mô tả ngắn gọn, dễ hiểu nguyên nhân và tại sao cần đưa ra quyết định.
  2. **Các phương án khả thi:** Đưa ra ít nhất 2 phương án cụ thể, phân tích rõ:
     - *Phương án A:* Ưu điểm, nhược điểm, rủi ro.
     - *Phương án B:* Ưu điểm, nhược điểm, rủi ro.
  3. **Đề xuất khuyến nghị (Recommendation):** Nêu rõ agent đề xuất chọn phương án nào và giải thích lý do thuyết phục nhất.
  4. Chờ phản hồi và quyết định cuối cùng từ User.

---

### 3. Tuyệt Đối Không Tự Ý Push Lên GitHub (No Unauthorized Git Push)
- Agent được phép:
  - Chạy `git status`, `git diff`, kiểm tra trạng thái repo.
  - Tạo commit local gọn gàng, nguyên tử (atomic commit) khi đã kiểm tra và hoàn thành một phần việc.
- **CẤM TUYỆT ĐỐI** chạy lệnh `git push` lên GitHub (hoặc bất kỳ remote nào) nếu **chưa có sự đồng ý hoặc yêu cầu bằng văn bản rõ ràng từ User**.
- Chỉ được thực hiện `git push` khi User yêu cầu trực tiếp (ví dụ: *"hãy push lên github"* hoặc *"tôi đồng ý push"*).

---

### 4. Kỷ Luật Phạm Vi (Scope Discipline)
- **Chỉ can thiệp vào các file trong phạm vi công việc đã thỏa thuận:**
  - Không tự ý "tiện tay" sửa code, đổi format ở các file xung quanh.
  - Không tự ý refactor những đoạn code không liên quan đến task hiện tại.
  - Giữ nguyên toàn bộ comment, docstring hiện có trong file (trừ khi đoạn code đó bị thay thế hoàn toàn).
- Nếu phát hiện vấn đề ngoài phạm vi: ghi nhận vào mục lưu ý trong báo cáo hoặc hỏi User, không tự ý sửa.

---

### 5. Cổng Xác Thực Bắt Buộc (Mandatory Verification Gate)
- **Không bao giờ tuyên bố hoàn thành khi chưa kiểm chứng:**
  - Sau khi sửa mã nguồn Java, bắt buộc phải chạy lệnh biên dịch kiểm tra:
    ```powershell
    .\mvnw clean test-compile
    ```
  - Chỉ khi kết quả trả về `BUILD SUCCESS` (0 lỗi), mới được phép coi bước đó là hoàn thành.
  - Nếu gặp lỗi biên dịch hoặc runtime: phải phân tích kỹ root cause và sửa dứt điểm, không sửa qua loa hoặc lấp liếm lỗi.

---

### 6. Quản Lý Bí Mật & Bảo Mật Tuyệt Đối (Security & Secrets Hygiene)
- **CẤM** hardcode mật khẩu, email cá nhân, API keys, database credentials vào mã nguồn (`application.properties`, file Java, scripts,...).
- Mọi thông tin nhạy cảm phải:
  - Dùng biến môi trường (Environment Variables: `${SPRING_MAIL_USERNAME}`, `${DB_PASSWORD}`,...).
  - Lưu trữ tại file `.env` hoặc `application-dev.properties`.
  - Luôn đảm bảo các file bí mật đã được đưa vào [.gitignore](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/.gitignore).

---

### 7. Đồng Bộ Tài Liệu & Báo Cáo (Documentation & Progress Sync)
- Mọi kế hoạch triển khai phải được lưu trữ trong thư mục `docs/plans/`.
- Khi hoàn thành bất kỳ giai đoạn nào:
  - Cập nhật dấu tick `[x]` vào file kế hoạch tổng thể [docs/plans/master-upgrade-plan.md](file:///d:/Old_Project/School_Medical_Management_System/School-Medical-Management-System/docs/plans/master-upgrade-plan.md).
  - Cung cấp hướng dẫn kiểm tra thực tế (Manual Testing Guide) để User có thể tự mình chạy thử và nghiệm thu.

---

## 📋 Checklist Mỗi Khi Bắt Đầu Nhiệm Vụ Mới

Trước khi gõ bất kỳ dòng code nào, Agent hãy tự rà soát:
- [ ] Mình đã có bản kế hoạch chi tiết chưa?
- [ ] User đã phê duyệt kế hoạch này chưa?
- [ ] Có vấn đề nào cần User lựa chọn phương án không?
- [ ] Mình có đang chạm vào file nào ngoài phạm vi không?
- [ ] Mình đã chạy build kiểm tra (`BUILD SUCCESS`) chưa?
- [ ] Mình có đang chuẩn bị push lên git mà chưa xin phép không?
