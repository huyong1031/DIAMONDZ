package com.himedia.spserver.dto;

import lombok.Data;

@Data
public class ProductLikeRequest {
    private String memberId;
    private int productSeq;

}
