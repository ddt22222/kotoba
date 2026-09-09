# Supabase đã cấu hình

Dự án hiện có: `iahyumhsedwpqvjjkkan` (Singapore). API URL: `https://iahyumhsedwpqvjjkkan.supabase.co`.

## Đã thực hiện và xác minh

- Migration Supabase `initialize_kotoba`: nội dung đúng từ Flyway V1 và V2.
- Migration Supabase `kotoba_server_only_access`: nội dung đúng từ Flyway V3.
- Có 4 bảng: profiles, learning_items, user_learning_progress, review_history.
- Đếm trực tiếp database: 25 từ vựng, 10 ngữ pháp, 10 Kanji.
- RLS bật trên 4 bảng. Quyền trực tiếp của anon/authenticated đã thu hồi; chỉ backend có quyền DB mới truy cập được.
- JWKS hiện có ES256. Email Auth đang bật; đăng ký cho phép; cần xác nhận email.
- Security advisor không còn WARN/ERROR, chỉ INFO “RLS enabled no policy”. Đây là deny-by-default chủ ý khi không dùng Data API cho nghiệp vụ. Tham khảo: https://supabase.com/docs/guides/database/database-linter?lint=0008_rls_enabled_no_policy

## Kết nối backend lần đầu với đúng dự án này

Schema đã có V1–V3 thông qua Supabase MCP nhưng chưa có Flyway history. Không chạy backend mặc định trên schema này và không chạy lại SQL seed. Dùng profile một lần:

```bash
cd backend
# Điền DATABASE_URL / DATABASE_USERNAME / DATABASE_PASSWORD / SUPABASE_URL trong .env
mvn spring-boot:run -Dspring-boot.run.profiles=supabase-bootstrap
```

Profile này cho phép Flyway baseline tại version 3, sau đó chạy V4 để bảo vệ bảng lịch sử migration. Hibernate vẫn validate schema. Sau lần khởi động thành công, bỏ profile bootstrap; những thay đổi sau được quản lý bằng Flyway như bình thường. Trên Render dùng SPRING_PROFILES_ACTIVE=supabase-bootstrap cho lần deploy đầu, sau thành công xóa biến này và redeploy.

Chỉ dùng profile này cho dự án trên đã được kiểm tra có đúng V1–V3. Không dùng để bỏ qua lỗi schema trên database bất kỳ. Supabase history là dấu vết provisioning ban đầu; từ sau baseline, Flyway là công cụ quản lý migration chính. Không tiếp tục chỉnh schema bằng hai công cụ song song.

## Còn thiếu trước khi dùng web

- Thông tin JDBC database trong backend (không gửi mật khẩu vào chat; điền trực tiếp nơi chạy/deploy).
- Repository GitHub có quyền đọc/ghi: kết nối hiện chưa có installation/repository khả dụng.
- Kết nối tài khoản Render và Vercel; deploy backend và frontend, cập nhật CORS/Site URL/redirect URL.
- Kiểm thử đăng ký, xác nhận email, refresh session, lưu/sửa/xóa từ và review end-to-end.

Frontend đã có .env.local với publishable key trong workspace, không đưa vào file ZIP hoặc Git. Backend tests đã đạt 8 test ở lần kiểm tra trước; lần chạy lại sau provisioning bị chặn vì Maven cache không còn parent dependency ở chế độ offline, nên chưa xác nhận lại build Java mới nhất.
