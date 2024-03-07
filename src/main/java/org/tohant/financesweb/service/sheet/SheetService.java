package org.tohant.financesweb.service.sheet;

import org.tohant.financesweb.service.model.CategoryDto;
import org.tohant.financesweb.service.model.HttpResponseDto;
import org.tohant.financesweb.service.model.PaymentDto;

import java.util.List;

public interface SheetService {

    List<PaymentDto> getPayments();
    List<PaymentDto> getPaymentsFromCache();
    HttpResponseDto addPayment(PaymentDto paymentDto);
    List<CategoryDto> getCategories();
    HttpResponseDto rearrangeCategories(List<Integer> ids);
    int count();

}
