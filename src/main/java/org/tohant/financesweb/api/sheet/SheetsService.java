package org.tohant.financesweb.api.sheet;

import org.tohant.financesweb.api.model.PaymentDto;

import java.util.List;

public interface SheetsService {

    List<PaymentDto> getPayments();

    void addPayment(PaymentDto paymentDto);

}
