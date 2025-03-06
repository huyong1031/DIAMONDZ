package com.himedia.spserver.repository;

import com.himedia.spserver.entity.OrdersDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersDetailRepository extends JpaRepository<OrdersDetail, Integer> {

    List<OrdersDetail> findByOrders_OrderSeq(int orderSeq);
    Optional<OrdersDetail> findByOrders_OrderSeqAndProduct_ProductSeq(Integer orderSeq, Integer productSeq);

}
