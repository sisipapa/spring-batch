package com.sisipapa.springbatch.batch;

import com.sisipapa.springbatch.dto.TwoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class MultiJob1 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private int chunkSize = 5;

    @Bean
    public Job multiJob1_batchBuild(){
        return jobBuilderFactory.get("multiJob1")
                .start(multiJob1_batchStep1(null))
                .build();
    }

    @Bean
    @JobScope
    public Step multiJob1_batchStep1(@Value("#{jobParameters[version]}") String version){
        log.debug("------------------------");
        log.debug(version);
        log.debug("------------------------");

        return stepBuilderFactory.get("multiJob1_batchStep1")
                .<TwoDto, TwoDto>chunk(chunkSize)
                .reader(multiJob1_Reader(null))
                .processor(multiJob1_Processor(null))
                .writer(multiJob1_Writer(null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<TwoDto> multiJob1_Reader(@Value("#{jobParameters[inFileName]}") String inFileName){
        return new FlatFileItemReaderBuilder<TwoDto>()
                .name("multiJob1_Reader")
                .resource(new ClassPathResource("sample/" + inFileName))
                .delimited().delimiter(":")
                .names("one", "two")
                .targetType(TwoDto.class)
                .recordSeparatorPolicy(new SimpleRecordSeparatorPolicy() {
                    @Override
                    public String postProcess(String record) {
                        log.debug("recordSeparatorPolicy : " + record);
                        if(!record.contains(":")){
                            return null;
                        }
                        return record.trim();
                    }
                })
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<TwoDto, TwoDto> multiJob1_Processor(@Value("#{jobParameters[version]}") String version) {
        log.debug("processor : " + version);
        return item -> new TwoDto(item.getOne(), item.getTwo());
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<TwoDto> multiJob1_Writer(@Value("#{jobParameters[outFileName]}") String outFileName){
        return new FlatFileItemWriterBuilder<TwoDto>()
                .name("multiJob1_Writer")
                .resource(new FileSystemResource("sample/" + outFileName))
                .lineAggregator(item -> {
                    return item.getOne() + " --- " + item.getTwo();
                })
                .build();
    }
}
