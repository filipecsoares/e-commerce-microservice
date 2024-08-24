package com.fcs.ecommerce.order;

import com.fcs.ecommerce.customer.CustomerClient;
import com.fcs.ecommerce.exception.BusinessException;
import com.fcs.ecommerce.orderline.OrderLineRequest;
import com.fcs.ecommerce.orderline.OrderLineService;
import com.fcs.ecommerce.product.ProductClient;
import com.fcs.ecommerce.product.PurchaseRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;

    @Transactional
    public Long createOrder(OrderRequest request) {
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

        var purchasedProducts = productClient.purchaseProducts(request.products());

        var order = this.repository.save(mapper.toOrder(request));

        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }
//        var paymentRequest = new PaymentRequest(
//                request.amount(),
//                request.paymentMethod(),
//                order.getId(),
//                order.getReference(),
//                customer
//        );
//        paymentClient.requestOrderPayment(paymentRequest);
//
//        orderProducer.sendOrderConfirmation(
//                new OrderConfirmation(
//                        request.reference(),
//                        request.amount(),
//                        request.paymentMethod(),
//                        customer,
//                        purchasedProducts
//                )
//        );

        return order.getId();
    }

    public List<OrderResponse> findAllOrders() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromOrder)
                .collect(Collectors.toList());
    }
}
