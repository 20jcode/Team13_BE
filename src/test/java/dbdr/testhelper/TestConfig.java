package dbdr.testhelper;

import dbdr.domain.core.alarm.service.AlarmService;
import dbdr.domain.core.messaging.service.CallSqsService;
import dbdr.domain.core.messaging.service.LineMessagingService;
import dbdr.domain.core.messaging.service.LineService;
import dbdr.domain.core.messaging.service.SmsMessagingService;
import dbdr.domain.core.messaging.service.SmsService;
import dbdr.domain.core.ocr.controller.OcrController;
import dbdr.domain.core.s3.controller.S3Controller;
import dbdr.domain.core.s3.service.S3Service;
import dbdr.global.configuration.OpenAiSummarizationConfig;
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@TestConfiguration
public class TestConfig {
    // 외부 api와 연관된 Bean들은 Mock로 대체

    @Bean
    @Primary
    CallSqsService callSqsService(){
        return Mockito.mock(CallSqsService.class);
    }

    @Bean
    @Primary
    AlarmService alarmService(){
        return Mockito.mock(AlarmService.class);
    }

    @Bean
    @Primary
    OpenAiSummarizationConfig openAiSummarizationConfig(){
        return Mockito.mock(OpenAiSummarizationConfig.class);
    }

    @Bean
    @Primary
    SmsMessagingService smsMessagingService(){
        return Mockito.mock(SmsMessagingService.class);
    }

    @Bean
    @Primary
    SqsAsyncClient sqsAsyncClient(){
        return Mockito.mock(SqsAsyncClient.class);
    }

    @Bean
    @Primary
    SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient) {
        return Mockito.mock(SqsTemplate.class);
    }

    @Bean
    @Primary
    LineService lineService(){
        return Mockito.mock(LineService.class);
    }

    @Bean
    @Primary
    LineMessagingService lineMessagingService(){
        return Mockito.mock(LineMessagingService.class);
    }

    @Bean
    @Primary
    S3Controller s3Controller(){
        return Mockito.mock(S3Controller.class);
    }

    @Bean
    @Primary
    S3Service s3Service(){
        return Mockito.mock(S3Service.class);
    }

    @Bean
    @Primary
    OcrController ocrController(){
        return Mockito.mock(OcrController.class);
    }



}
