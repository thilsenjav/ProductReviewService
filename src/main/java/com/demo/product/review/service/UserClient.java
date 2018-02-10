package com.demo.product.review.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.demo.product.review.dto.UserDto;

@FeignClient("user-service")
public interface UserClient {

	@GetMapping("/findByEmail/{email}")
	UserDto findByEmail(@PathVariable String email) throws Exception;

}
