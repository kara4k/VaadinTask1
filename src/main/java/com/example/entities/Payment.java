package com.example.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Payment {

    public static final byte TYPE_CARD = 1;
    public static final byte TYPE_CASH = 2;

    @Column(name = "PAYMENT_TYPE")
    private Byte type;

    @Column(name = "PREPAYMENT")
    private Byte prePayment;

    public Payment() {
    }

    public Payment(Payment payment) {
        if (payment == null) return;

        type = payment.getType();
        prePayment = payment.getPrePayment();
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Byte getPrePayment() {
        return prePayment;
    }

    public void setPrePayment(Byte prePayment) {
        this.prePayment = prePayment;
    }
}
