package com.fcs.ecommerce.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import com.fcs.ecommerce.customer.CustomerClient;
import com.fcs.ecommerce.customer.CustomerResponse;
import com.fcs.ecommerce.exception.BusinessException;
import com.fcs.ecommerce.kafka.OrderConfirmation;
import com.fcs.ecommerce.kafka.OrderProducer;
import com.fcs.ecommerce.orderline.OrderLineRequest;
import com.fcs.ecommerce.orderline.OrderLineService;
import com.fcs.ecommerce.payment.PaymentClient;
import com.fcs.ecommerce.payment.PaymentRequest;
import com.fcs.ecommerce.product.ProductClient;
import com.fcs.ecommerce.product.PurchaseRequest;
import com.fcs.ecommerce.product.PurchaseResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private CustomerClient customerClient;
    @Mock
    private ProductClient productClient;
    @Mock
    private OrderRepository repository;
    @Mock
    private OrderLineService orderLineService;
    @Mock
    private OrderProducer orderProducer;
    @Mock
    private PaymentClient paymentClient;
    @Mock
    private OrderMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        // Arrange
        PurchaseRequest purchaseRequest = new PurchaseRequest(1L, 2);
        OrderRequest orderRequest = new OrderRequest(1L, "Reference", BigDecimal.TEN, PaymentMethod.CREDIT_CARD, "1", List.of(purchaseRequest));

        CustomerResponse customerResponse = mock(CustomerResponse.class);
        when(customerClient.findCustomerById("1")).thenReturn(Optional.of(customerResponse));

        List<PurchaseResponse> purchasedProducts = List.of(mock(PurchaseResponse.class));
        when(productClient.purchaseProducts(anyList())).thenReturn(purchasedProducts);

        Order order = mock(Order.class);
        when(order.getId()).thenReturn(1L);
        when(order.getReference()).thenReturn("REF123");
        when(repository.save(any())).thenReturn(order);

        // Act
        Long orderId = orderService.createOrder(orderRequest);

        // Assert
        assertEquals(1L, orderId);
        verify(orderLineService, times(1)).saveOrderLine(any(OrderLineRequest.class));
        verify(paymentClient, times(1)).requestOrderPayment(any(PaymentRequest.class));
        verify(orderProducer, times(1)).sendOrderConfirmation(any(OrderConfirmation.class));
    }

    @Test
    void shouldThrowsWhenCustomerNotFoundOnCreateOrder() {
        // Arrange
        OrderRequest request = mock(OrderRequest.class);
        when(request.customerId()).thenReturn("1");
        when(customerClient.findCustomerById("1")).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> orderService.createOrder(request));
        assertEquals("Cannot create order:: No customer exists with the provided ID", exception.getMessage());
    }

    @Test
    void shouldFindByIdSuccessfully() {
        // Arrange
        Order order = mock(Order.class);
        when(order.getId()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(order));
        OrderResponse response = mock(OrderResponse.class);
        when(mapper.fromOrder(order)).thenReturn(response);

        // Act
        OrderResponse result = orderService.findById(1L);

        // Assert
        assertEquals(response, result);
    }

    @Test
    void shouldThrowsWhenOrderNotFind() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> orderService.findById(1L));
        assertEquals("No order found with the provided ID: 1", exception.getMessage());
    }
}
