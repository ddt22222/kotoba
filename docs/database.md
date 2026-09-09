# Database

| Bảng | Mục đích |
|---|---|
| profiles | UUID tài khoản, timezone, daily goal, theme, target level |
| learning_items | Từ vựng, ngữ pháp, Kanji và từ cá nhân qua type discriminator |
| user_learning_progress | Trạng thái, favorite, lịch SRS theo user + item |
| review_history | Sự kiện đánh giá với requestId duy nhất theo user |

Dùng chung learning_items giúp progress có foreign key thật thay vì cặp itemType/itemId không thể ràng buộc. owner_id chỉ tồn tại cho PERSONAL; nội dung chung phải có owner_id NULL. Từ cá nhân liên kết profile. Profile được tạo khi tài khoản lần đầu sử dụng API, không lưu mật khẩu và không sao chép auth.users.

Migration V1 tạo schema, FK, check constraint, unique constraint, indexes và bật RLS. V2 nhập 45 mục mẫu. Dùng Flyway làm nguồn quản lý migration duy nhất; không trộn với lịch sử Supabase CLI. Migration đã phát hành phải giữ nguyên, thay đổi mới cần file Flyway phiên bản mới.

Learning status: NEW, LEARNING, REVIEW, MASTERED; lưu theo người dùng, không ghi vào catalog. Favorite nằm cùng progress để tránh bảng thừa. Không có study_sessions riêng trong MVP; thống kê dựa trên review_history.

Seed nguồn nằm ở sample-content.json. Một importer tương lai nên validate type, level, độ dài, license và UUID trước khi tạo migration hoặc nhập trong transaction; không nhập trực tiếp nội dung không kiểm duyệt vào catalog production.
