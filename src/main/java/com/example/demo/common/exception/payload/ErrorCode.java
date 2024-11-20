package com.example.demo.common.exception.payload;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

	// 400 - Bad Request
	TOKEN_NOT_CORRECT_FORMAT(HttpStatus.UNAUTHORIZED, "토큰의 형식이 올바르지 않습니다."),
	TOKEN_EXPIRATION(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),

	// 401 - Unauthorized
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
	TOKEN_GENERATION_FAILED(HttpStatus.UNAUTHORIZED, "토큰 생성에 실패하였습니다."),
	TOKEN_VALIDATION_FAILED(HttpStatus.UNAUTHORIZED, "토큰 유효성 검사에 실패하였습니다."),

	// 404 - Not Found

	// 500 - Internal Server Error
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에 문제가 발생했습니다."),
	SIGNATURE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "서명 실패: 알고리즘을 찾지 못했습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
	
	public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
	
}