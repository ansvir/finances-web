package org.tohant.financesweb.api.sheet;

import org.tohant.financesweb.api.model.HttpResponseDto;
import org.tohant.financesweb.api.model.PaymentDto;

import java.util.List;

public interface SheetsService {

    List<PaymentDto> getPayments();
    List<PaymentDto> getPaymentsFromCache();
    int count();
    int countPaged();
    HttpResponseDto addPayment(PaymentDto paymentDto);

}
