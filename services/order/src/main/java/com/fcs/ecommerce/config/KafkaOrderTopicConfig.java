/*
 * @(#)KafkaOrderTopicConfig.java
 *
 * Copyright (c) J-Tech Solucoes em Informatica.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of J-Tech.
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with J-Tech.
 */
package com.fcs.ecommerce.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaOrderTopicConfig {

    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder
                .name("order-topic")
                .build();
    }
}
