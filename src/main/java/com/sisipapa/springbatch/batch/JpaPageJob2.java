package com.sisipapa.springbatch.batch;

import com.sisipapa.springbatch.domain.Dept;
import com.sisipapa.springbatch.domain.Dept2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaPageJob2 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private int chunkSize = 10;

    @Bean
    public Job JpaPageJob2_batchBuild(){
        return jobBuilderFactory.get("jpaPageJob2")
                .start(JpaPageJob2_step1())
                .build();
    }

    @Bean
    public Step JpaPageJob2_step1(){
        return stepBuilderFactory.get("jpaPageJob2_step1")
                .<Dept, Dept2>chunk(chunkSize) // read DB, write DB
                .reader(JpaPageJob2_dbItemReader())
                .processor(JpaPageJob2_processor())
                .writer(JpaPageJob2_dbItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Dept> JpaPageJob2_dbItemReader(){
        return new JpaPagingItemReaderBuilder<Dept>()
                .name("JpaPageJob2_dbItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT d FROM Dept d order by dept_no asc")
                .build();
    }

    private ItemProcessor<Dept, Dept2> JpaPageJob2_processor() {
        return dept -> new Dept2(dept.getDeptNo(), "NEW_" + dept.getDName(), "NEW_" + dept.getLoc());
    }

    @Bean
    public JpaItemWriter<Dept2> JpaPageJob2_dbItemWriter(){
        JpaItemWriter<Dept2> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}
