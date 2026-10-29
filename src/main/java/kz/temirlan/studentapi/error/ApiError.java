package kz.temirlan.studentapi.error;

public record ApiError(int status, String message, String path) {
}
