package org.tohant.financesweb.api.sheet;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.tohant.financesweb.api.model.HttpResponseDto;
import org.tohant.financesweb.api.model.PaymentDto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleSheetsService {
    private static final String APPLICATION_NAME = "finances";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String SPREADSHEET_ID = "1hPSjkwrVABAE6n8cI4k7L6KEtiDIgIN5PoWFO8rh9R0";

    private final ResourceLoader resourceLoader;

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "classpath:google_api_creds.json";

    public List<PaymentDto> getPayments() {
        try {
            log.info("Get payments request execution...");
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            final String range = "Report!A2:C";
            Sheets service =
                    new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                            .setApplicationName(APPLICATION_NAME)
                            .build();
            ValueRange response = service.spreadsheets().values()
                    .get(SPREADSHEET_ID, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                return createFailedPayment("No data found.", HttpStatus.NO_CONTENT.value());
            } else {
                List<PaymentDto> payments = new ArrayList<>();
                for (List<Object> value : values) {
                    for (int j = 0; j < values.get(0).size(); j += 2) {
                        payments.add(new PaymentDto(value.get(j).toString(),
                                BigDecimal.valueOf(Double.parseDouble(value.get(j + 1).toString())),
                                new HttpResponseDto("Payments found.", HttpStatus.OK.value()),
                                PaymentDto.Type.fromId(Integer.parseInt(value.get(j + 2).toString()))));
                    }
                }
                return payments;
            }
        } catch (IOException e) {
            return createFailedPayment("Internal server error. Details: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        } catch (GeneralSecurityException e) {
            return createFailedPayment("Security error: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = resourceLoader.getResource(CREDENTIALS_FILE_PATH).getInputStream();
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private static List<PaymentDto> createFailedPayment(String message, int status) {
        return List.of(new PaymentDto(null, null, new HttpResponseDto(message, status),
                PaymentDto.Type.OTHER));
    }

}