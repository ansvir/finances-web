package org.tohant.financesweb.api.sheet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.tohant.financesweb.api.model.PaymentDto;

@Slf4j
@Service
public class LocalGoogleSheetsService extends AbstractGoogleSheetsService {

    @Autowired
    public LocalGoogleSheetsService(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    public void addPayment(PaymentDto paymentDto) {
    }


    @Override
    public String credentialFilePath() {
        return "classpath:google_api_creds_desktop.json";
    }

}