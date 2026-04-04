/**
 * (C) Copyright 2023 Araf Karsh Hamid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fusion.air.microservice.adapters.service;

import io.fusion.air.microservice.domain.entities.order.OrderEntity;
import io.fusion.air.microservice.domain.entities.order.OrderItemEntity;
import io.fusion.air.microservice.domain.entities.order.OrderPaymentEntity;
import io.fusion.air.microservice.domain.entities.order.ShippingAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author: Araf Karsh Hamid
 * @version:
 * @date:
 */

class OrderServiceImplTest {

    private OrderEntity order;

    @BeforeEach
    void setUp() {
        List<OrderItemEntity> items = new ArrayList<OrderItemEntity>();
        items.add(new OrderItemEntity("P001", "Product 1", BigDecimal.ONE, new BigDecimal("29.99")));
        order = OrderEntity.builder()
                .addCustomerId("123")
                .addOrderItems(items)
                .addShippingAddress(new ShippingAddress())
                .addPayment(new OrderPaymentEntity())
                .build();
    }

    @Test
    void requestCreditApproval() {
        assertNotNull(order);
        assertEquals("123", order.getCustomerId());
    }
}
