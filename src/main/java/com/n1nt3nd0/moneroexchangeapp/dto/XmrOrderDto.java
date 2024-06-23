package com.n1nt3nd0.moneroexchangeapp.dto;

import com.n1nt3nd0.moneroexchangeapp.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class XmrOrderDto implements Serializable {
    private PaymentMethod paymentMethod;
    private String amountToBePaid;
    private String username;
    private double xmrQuantity;
    private String xmrAddressForSending;
}
