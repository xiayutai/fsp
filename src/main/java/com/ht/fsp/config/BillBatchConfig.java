package com.ht.fsp.config;


import com.ht.fsp.bean.AlipayTranDO;
import com.ht.fsp.bean.HopPayTranDO;
import com.ht.fsp.listener.AlipaySkipListener;
import com.ht.fsp.processor.AlipayItemProcessor;
import com.ht.fsp.processor.AlipayValidateProcessor;
import com.ht.fsp.reader.AlipayFileItemReader;
import com.ht.fsp.writer.AlipayDBItemWriter;
import com.ht.fsp.writer.AlipayFileItemWriter;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



@Configuration
@EnableBatchProcessing
public class BillBatchConfig {
	@Autowired
	public JobLauncher jobLauncher;

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	private AlipayFileItemReader alipayFileItemReader;

	@Autowired
	private AlipayItemProcessor alipayItemProcessor;

	@Autowired
	private AlipayFileItemWriter alipayFileItemWriter;

	@Autowired
	private AlipayDBItemWriter alipayDBItemWriter;

	@Autowired
	private AlipaySkipListener listener;

	public void run() {
		try {
			String dateParam = new Date().toString();
			JobParameters param =    new JobParametersBuilder().addString("date", dateParam).toJobParameters();
			System.out.println(dateParam);
			JobExecution execution = jobLauncher.run(importAliJob(), param);             //执行job
			System.out.println("Exit Status : " + execution.getStatus());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Bean
	public Job importAliJob() {
		return jobBuilderFactory.get("importAliJob")
				.incrementer(new RunIdIncrementer())
				.flow(step1())
				.next(step2())
				.end()
				.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<AlipayTranDO, HopPayTranDO> chunk(10)
				.reader(alipayFileItemReader.getMultiAliReader())
				.processor(alipayItemProcessor)
				.writer(alipayFileItemWriter.getAlipayItemWriter())
				.build();
	}


	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2")
				.<AlipayTranDO, AlipayTranDO> chunk(10)
				.reader(alipayFileItemReader.getMultiAliReader())
				.writer(alipayDBItemWriter)
				.faultTolerant()
				.skipLimit(20)
				.skip(Exception.class)
				.listener(listener)
				.retryLimit(3)
				.retry(RuntimeException.class)
				.build();
	}

	@Bean
	public Step step3() {
		CompositeItemProcessor<AlipayTranDO,HopPayTranDO> compositeItemProcessor = new CompositeItemProcessor<AlipayTranDO,HopPayTranDO>();
		List compositeProcessors = new ArrayList();
		compositeProcessors.add(new AlipayValidateProcessor());
		compositeProcessors.add(new AlipayItemProcessor());
		compositeItemProcessor.setDelegates(compositeProcessors);
		return stepBuilderFactory.get("step3")
				.<AlipayTranDO, HopPayTranDO> chunk(10)
				.reader(alipayFileItemReader.getMultiAliReader())
				.processor(compositeItemProcessor)
				.writer(alipayFileItemWriter.getAlipayItemWriter())
				.build();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(4);
		taskExecutor.afterPropertiesSet();
		return taskExecutor;
	}

	@Bean
	public Step step4() {
		return stepBuilderFactory.get("step3")
				.<AlipayTranDO, HopPayTranDO> chunk(10)
				.reader(alipayFileItemReader.getMultiAliReader())
				.processor(alipayItemProcessor)
				.writer(alipayFileItemWriter.getAlipayItemWriter())
				.taskExecutor(taskExecutor())
				.throttleLimit(4)
				.build();
	}

//    @Bean
//    public JobRepository jobRepository(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception{
//
//        JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
//        jobRepositoryFactoryBean.setDataSource(dataSource);
//        jobRepositoryFactoryBean.setTransactionManager(transactionManager);
//        jobRepositoryFactoryBean.setDatabaseType(DatabaseType.MYSQL.name());
//
//        return jobRepositoryFactoryBean.getObject();
//    }

    /**
     * JobRepository，用来注册Job的容器
     * jobRepositor的定义需要dataSource和transactionManager，Spring Boot已为我们自动配置了
     * 这两个类，Spring可通过方法注入已有的Bean
     * @param dataSource
     * @param transactionManager
     * @return
     * @throws Exception
     */
    @Bean
    public JobRepository jobRepository(DataSource dataSource, PlatformTransactionManager transactionManager)throws Exception{

        JobRepositoryFactoryBean jobRepositoryFactoryBean =
                new JobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setDataSource(dataSource);
        jobRepositoryFactoryBean.setTransactionManager(transactionManager);
        jobRepositoryFactoryBean.setDatabaseType(DatabaseType.MYSQL.name());
        return jobRepositoryFactoryBean.getObject();
    }

    /**
     * JobLauncher定义，用来启动Job的接口
     * @param dataSource
     * @param transactionManager
     * @return
     * @throws Exception
     */
    @Bean
    public SimpleJobLauncher jobLauncher( DataSource dataSource, PlatformTransactionManager transactionManager)throws Exception{
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository(dataSource, transactionManager));
        return jobLauncher;
    }



}
