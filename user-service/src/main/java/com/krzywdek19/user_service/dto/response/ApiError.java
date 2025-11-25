package com.krzywdek19.user_service.dto.response;

import java.util.List;

public record ApiError(String code, String message, List<ApiErrorDetail> details) {
}
