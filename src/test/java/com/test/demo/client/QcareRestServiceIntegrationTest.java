package com.test.demo.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class QcareRestServiceIntegrationTest {

    @Autowired
    private QcareRestService qcareRestService;

    @Test
    void qcareRestServiceBeanIsCreated() {
        // Then
        assertThat(qcareRestService).isNotNull();
        assertThat(qcareRestService).isInstanceOf(QcareRestService.class);
    }

    @Test
    void qcareRestServiceIsProxyInstance() {
        // Then - Verify it's a proxy created by HttpServiceProxyFactory
        assertThat(qcareRestService.getClass().getName()).contains("$Proxy");
    }
}
