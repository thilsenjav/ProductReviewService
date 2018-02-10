package com.demo.product.review.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.demo.product.review.dto.ResponseMsg;
import com.demo.product.review.service.ServiceException;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

	@ResponseBody
	@ExceptionHandler(ServiceException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseMsg serviceExceptionHandler(ServiceException ex) {
		return new ResponseMsg(ex.getErrorCode(), ex.getMessage());
	}
}
