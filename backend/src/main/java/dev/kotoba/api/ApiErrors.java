package dev.kotoba.api;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.*;
import org.springframework.dao.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
@RestControllerAdvice public class ApiErrors {
 @ExceptionHandler(ResponseStatusException.class) ProblemDetail status(ResponseStatusException e){return ProblemDetail.forStatusAndDetail(e.getStatusCode(),e.getReason()==null?"Không tìm thấy nội dung.":e.getReason());}
 @ExceptionHandler(MethodArgumentNotValidException.class) ProblemDetail validation(MethodArgumentNotValidException e){return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Kiểm tra các trường bắt buộc và độ dài nội dung.");}
 @ExceptionHandler({MethodArgumentTypeMismatchException.class,HttpMessageNotReadableException.class}) ProblemDetail malformed(Exception e){return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Dữ liệu không hợp lệ.");}
 @ExceptionHandler({OptimisticLockingFailureException.class,DataIntegrityViolationException.class}) ProblemDetail conflict(Exception e){return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,"Dữ liệu đã thay đổi. Hãy tải lại và thử lại.");}
}
