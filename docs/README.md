# 📚 Project Documentation & Planning Hub

Thư mục này là nơi lưu trữ toàn bộ tài liệu kỹ thuật, kiến trúc hệ thống và **các kế hoạch triển khai (Implementation Plans)** của dự án **School Medical Management System**.

---

## 🎯 Quy Tắc Quản Lý Tài Liệu (Documentation Rules)

1. **Kế hoạch trước khi thực thi (Plan-First):**
   * Mọi tính năng lớn, kế hoạch sửa lỗi kiến trúc, tái cấu trúc mã nguồn (refactoring) hoặc nâng cấp bảo mật đều phải được lập kế hoạch chi tiết trong thư mục `docs/plans/` trước khi tiến hành viết mã.
2. **Theo dõi tiến độ & nghiệm thu:**
   * Mỗi file kế hoạch sẽ chứa checklist cụ thể và trạng thái hoàn thành (`[ ]` -> `[x]`).

---

## 📂 Cấu Trúc Thư Mục `docs/`

```text
docs/
├── plans/                  # Lưu trữ các kế hoạch triển khai (Implementation Plans)
│   ├── 01-security-fix-reset-password.md
│   ├── 02-fix-hibernate-bag-fetch.md
│   └── 03-refactor-controllers-and-repositories.md
├── architecture/           # Kiến trúc hệ thống, sơ đồ luồng dữ liệu (Data Flow)
├── database/               # Thiết kế CSDL, ERD, migration scripts
└── README.md               # Mục lục và quy ước tài liệu (file này)
```

---

*Ghi chú: Thư mục này được khởi tạo và duy trì bởi NHTung-0801 và trợ lý AI trong quá trình phát triển dự án.*
