# Kotoba · Web học tiếng Nhật

MVP tiếng Việt với Next.js → Spring Boot REST API → Supabase PostgreSQL. Supabase Auth cấp JWT; backend xác minh chữ ký, issuer, audience và role. Không có tài khoản giả hoặc dữ liệu cá nhân lưu bằng localStorage.

## Trạng thái bàn giao

Đã viết: các màn hình tổng quan, từ vựng, ngữ pháp, Kanji, từ cá nhân, ôn tập, tiến độ, cài đặt; API CRUD, phân quyền theo người dùng, lịch ôn, migration và dữ liệu mẫu.

Đã kiểm tra: Next.js production build; 8 kiểm thử backend (JUnit + Spring Boot/H2); 3 kiểm thử logic câu hỏi frontend. Đã tạo schema và seed trên Supabase thật, kiểm tra RLS/quyền truy cập và Auth settings. Chưa kiểm chứng end-to-end trình duyệt → Java → Supabase; chưa kiểm chứng giao diện đăng nhập xong trên trình duyệt; chưa triển khai Vercel/Render. Không coi các kiểm thử H2 là kiểm chứng migration PostgreSQL.

Bộ mẫu gồm 25 từ, 10 mẫu ngữ pháp và 10 Kanji. Nhãn JLPT chỉ tham khảo, không phải danh sách chính thức hoặc chương trình N5–N1 đầy đủ.

## Yêu cầu

- Node.js 24, npm; Java 17; Maven 3.9+
- Một dự án Supabase riêng dành cho ứng dụng này
- Supabase Auth dùng khóa ký bất đối xứng ES256 hoặc RS256. Backend không hỗ trợ legacy HS256.

## Chạy trên máy

### 1. Supabase

Tạo dự án, bật đăng nhập email/password. Cấu hình Site URL và redirect URL về frontend của bạn, dùng `http://localhost:3000` khi phát triển. Nếu bật xác nhận email, xác nhận qua email trước khi đăng nhập.

Lấy Project URL và publishable key cho frontend. Lấy thông tin kết nối PostgreSQL dạng session pooler hoặc direct connection cho backend. Không đưa service-role key hay mật khẩu database vào frontend.

### 2. Backend

```bash
cd backend
cp .env.example .env
# Điền giá trị thực trong .env
mvn spring-boot:run
```

Backend tự đọc `.env` theo định dạng Java properties; không thêm dấu nháy bao quanh giá trị. Nếu mật khẩu chứa ký tự escape, nên đặt bằng biến môi trường của hệ điều hành hoặc nơi deploy.

`DATABASE_URL` phải bắt đầu bằng `jdbc:postgresql://`; sử dụng host, port và username được Supabase cung cấp. Dùng `sslmode=require` với dịch vụ thật. Với database mới, Flyway chạy V1–V4 lúc khởi động để tạo bảng, nhập mẫu và giới hạn truy cập. Với dự án đã chuẩn bị qua Supabase, bắt buộc xem hướng dẫn baseline ở `docs/supabase-provisioning.md` để tránh chạy lại V1–V3. Không tự chạy các SQL này trong dashboard rồi khởi động Flyway, vì sẽ làm lệch lịch sử migration.

Kiểm tra `http://localhost:8080/health`. Endpoint này là liveness, không phải bài kiểm tra đăng nhập hoặc database toàn diện.

### 3. Frontend

```bash
cd frontend
cp .env.example .env.local
# Điền URL và publishable key thật
npm ci
npm run dev
```

Mở `http://localhost:3000`, tạo tài khoản, xác nhận email nếu cần rồi đăng nhập. Thiếu cấu hình sẽ hiển thị thông báo thiết lập thay vì màn hình dữ liệu giả.

## Biến môi trường

| Nơi | Biến | Mục đích |
|---|---|---|
| Frontend | NEXT_PUBLIC_API_URL | URL backend gồm `/api/v1` |
| Frontend | NEXT_PUBLIC_SUPABASE_URL | URL dự án Supabase |
| Frontend | NEXT_PUBLIC_SUPABASE_ANON_KEY | Publishable key (tên biến giữ tương thích) |
| Backend | DATABASE_URL | JDBC PostgreSQL URL |
| Backend | DATABASE_USERNAME | Username kết nối |
| Backend | DATABASE_PASSWORD | Mật khẩu, chỉ lưu server |
| Backend | SUPABASE_URL | URL dự án, để xác minh JWT |
| Backend | CORS_ORIGINS | Danh sách origin frontend cách nhau bằng dấu phẩy |
| Backend | PORT | Mặc định 8080 |

Các biến NEXT_PUBLIC được đóng vào bản build. Thay chúng phải build/deploy frontend lại.

## Luồng sử dụng

1. Đăng nhập → mở Từ vựng/Ngữ pháp/Kanji, tìm và lọc cấp độ.
2. Mở chi tiết, đánh dấu đã học để đưa vào lịch ôn.
3. Mở Từ của tôi, nhập Từ vựng + Nghĩa; Ví dụ tùy chọn, nhấn Enter.
4. Sửa trong bảng rồi nhấn biểu tượng lưu. Nút chi tiết cho phép thêm cách đọc, cấp độ và ghi chú.
5. Ôn theo lịch hoặc luyện một bộ lọc; hiện đáp án, tự đánh giá Chưa nhớ/Hơi khó/Nhớ được/Rất dễ.
6. Xem tiến độ và đặt mục tiêu, múi giờ, chế độ màu.

## Build và test

```bash
cd backend
mvn test
mvn package
```

```bash
cd frontend
npm ci
npm run build
node --experimental-strip-types --test tests/quiz.test.ts
```

Backend test dùng H2 biệt lập. Không kết nối test suite tới database có dữ liệu thật. Kiểm thử API bao gồm CRUD, chặn người dùng khác, version conflict, tìm kiếm/phân trang, review idempotency, tiến độ và kiểm tra múi giờ. Cần kiểm tra đăng ký, refresh token và CORS với môi trường thật trước khi sử dụng chính thức.

## Deploy

### GitHub

Repository: https://github.com/ddt22222/kotoba. Đã có `.gitignore`; kiểm tra không có `.env`, mật khẩu hoặc khóa bí mật trước khi push. Workflow CI có trong `.github/workflows/ci.yml`.

### Render (backend)

Tạo Web Service từ repository, runtime Docker, root directory `backend`, Dockerfile `Dockerfile`. Điền toàn bộ biến backend. Health check `/health`. Blueprint mẫu tại `render.yaml`; lựa chọn gói và kiểm tra giá/hạn mức trực tiếp trong tài khoản trước khi tạo dịch vụ. Docker build bỏ qua test; CI phải chạy và đạt trước khi triển khai.

### Vercel (frontend)

Import cùng repository, chọn root directory `frontend`, framework Next.js. Điền 3 biến frontend. NEXT_PUBLIC_API_URL trỏ đến HTTPS của Render kèm `/api/v1`. Sau khi có domain Vercel, cập nhật CORS_ORIGINS ở Render và Site URL/redirect URL ở Supabase Auth. Bật deploy từ nhánh chính trong từng dịch vụ nếu muốn tự động triển khai.

Đã tạo các bảng ứng dụng trong dự án Supabase hiện có; chưa tạo dịch vụ Vercel/Render. Xem `docs/supabase-provisioning.md` trước khi khởi chạy backend trên dự án đã chuẩn bị. Giá, free tier và giới hạn có thể thay đổi; kiểm tra ở bước chọn gói.

## Cấu trúc và phát triển tiếp

- `frontend/app`: route App Router, layout server; các màn hình tương tác dùng Client Components.
- `frontend/components`: UI, auth, danh sách, bảng từ cá nhân, quiz, dashboard.
- `frontend/lib`: API, hook dữ liệu, từ điển UI và logic câu hỏi.
- `backend/src/main/java/dev/kotoba`: controller → service → repository → entity.
- `backend/src/main/resources/db/migration`: Flyway SQL.
- `docs/sample-content.json`: dữ liệu nguồn cho seed/import về sau.
- `docs/architecture.md`, `docs/database.md`: quyết định kiến trúc và schema.

MVP dùng chi tiết dạng modal thay vì các trang URL riêng. Biểu đồ hiện thể hiện số lượt ôn; chưa có biểu đồ số mục học mới theo ngày. Quiz lấy tối đa 30 mục ở trang đầu của bộ lọc; ôn đến hạn trả tối đa 100 mục mỗi lượt tải. Chưa có CSV import UI, SRS nâng cao, mục tương tự có liên kết hay bộ giáo trình đầy đủ. Những phần này cần phát triển thêm.
