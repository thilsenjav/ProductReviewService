package com.demo.product.review.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReviewController.class);

	@GetMapping(value = "/")
	public ResponseEntity<?> get() {

		return new ResponseEntity<String>("ReviewController get called ", HttpStatus.OK);
	}

}
