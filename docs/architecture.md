# Kiến trúc

Frontend Next.js 16.3.4, React, TypeScript strict, Tailwind CSS 4. Backend Java 17/Spring Boot 4.1.1, JPA, validation, Spring Security OAuth2 resource server. Database Supabase PostgreSQL. REST trả DTO; không serialize entity trực tiếp.

## Quyền truy cập

Supabase Auth xử lý email/password và refresh session ở trình duyệt. Backend xác minh JWT bất đối xứng, issuer `/auth/v1`, audience `authenticated`, role `authenticated`, thời hạn và UUID subject. Frontend không quyết định userId và không có quyền ghi database trực tiếp. Điều hướng giao diện bằng session chỉ là UX; kiểm soát truy cập thật ở backend.

Dữ liệu cá nhân chỉ được đọc/sửa/xóa khi ownerId khớp subject. Truy cập mục của người khác trả 404. JPA optimistic locking bảo vệ sửa từ cũ. Khóa profile theo người dùng bảo vệ cập nhật lịch ôn đồng thời. requestId + unique constraint chống ghi trùng một lượt đánh giá được thử lại.

RLS bật trên toàn bộ bảng ứng dụng, không có policy cho anon/authenticated. Backend dùng kết nối server có quyền quản lý các bảng; phải giữ riêng thông tin kết nối. Không dùng user_metadata để phân quyền. JWT có thể còn hiệu lực đến lúc hết hạn sau đăng xuất hoặc xóa tài khoản; MVP chưa kiểm tra auth.sessions từng request.

## Luồng ôn

AGAIN: 10 phút, đưa trạng thái về LEARNING. HARD: tối thiểu 1 ngày, tăng theo 1.2 lần khoảng trước. GOOD: tối thiểu 1 ngày, nhân difficulty. EASY: tối thiểu 4 ngày, nhân difficulty và 1.3. Giới hạn 365 ngày; interval >=21 ngày thành MASTERED. Đây là tự đánh giá, chưa có chấm đáp án tự động.

Streak dựa trên ngày có review theo timezone của profile. Đánh dấu đã học bằng tay không tăng streak. NEW không có lịch ôn đến khi người dùng bắt đầu học hoặc luyện tập. Xóa từ cá nhân xóa cả progress và lịch sử của từ.

## Giới hạn cần theo dõi

Catalog lọc và phân trang server-side. Thống kê MVP hiện tổng hợp lịch sử của người dùng trong bộ nhớ; khi lịch sử lớn nên thay bằng aggregate SQL hoặc bảng thống kê theo ngày. Nội dung có search substring; sau khi nhập dataset lớn nên bổ sung chỉ mục tìm kiếm phù hợp. Cần kiểm chứng PostgreSQL, JWT thật, email confirmation, CORS và giao diện mobile trên môi trường đã kết nối trước khi công bố hoàn tất.
