package com.ht.fsp.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


//@Configuration
//public class MySQLDataSourceConfig {
//	@Bean(name = "mysqlDataSource")
//	@Qualifier("mysqlDataSource")
//	@ConfigurationProperties(prefix="spring.datasource")
//	public DataSource mysqlDataSource() {
//		return DataSourceBuilder.create().build();
//	}
//
//	@Bean(name = "mysqldbcTemplate")
//	public JdbcTemplate mysqlJdbcTemplate(
//			@Qualifier("mysqlDataSource") DataSource dataSource) {
//		return new JdbcTemplate(dataSource);
//	}
//}
