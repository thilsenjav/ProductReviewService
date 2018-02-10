package com.demo.product.review;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories
@EntityScan(basePackages= {"com.demo.product.review.domain"})
@EnableTransactionManagement
public class JpaConfig {

}
