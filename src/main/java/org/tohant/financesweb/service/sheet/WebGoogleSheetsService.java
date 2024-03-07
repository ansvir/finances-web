package org.tohant.financesweb.service.sheet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WebGoogleSheetsService extends AbstractGoogleSheetsService {

    @Autowired
    public WebGoogleSheetsService(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }


    @Override
    public String credentialFilePath() {
        return "classpath:google_api_creds.json";
    }
}