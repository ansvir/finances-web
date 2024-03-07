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
import org.springframework.format.datetime.joda.LocalDateTimeParser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.tohant.financesweb.api.model.HttpResponseDto;
import org.tohant.financesweb.api.model.PaymentDto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractGoogleSheetsService implements SheetsService {

    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();

    private static final String PAYMENTS_CACHE_KEY = "paymentsCache";

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-M-d H:mm:ss");

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String APPLICATION_NAME = "finances";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String SPREADSHEET_ID = "1hPSjkwrVABAE6n8cI4k7L6KEtiDIgIN5PoWFO8rh9R0";
    private static final int PAGE_COUNT = 20;

    private final ResourceLoader resourceLoader;

    public abstract String credentialFilePath();

    @Override
    public List<PaymentDto> getPayments() {
        try {
            log.info("Get payments request execution...");
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            final String sheetName = getSheetBasedOnUsername();
            final String range = sheetName + "!A2:D";
            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
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
                    for (int j = 0; j < values.get(0).size(); j += 4) {
                        payments.add(new PaymentDto(value.get(j).toString(),
                                BigDecimal.valueOf(Double.parseDouble(value.get(j + 1).toString())),
                                new HttpResponseDto("Payments found.", HttpStatus.OK.value()),
                                PaymentDto.Type.fromId(Integer.parseInt(value.get(j + 2).toString())),
                                LocalDateTime.parse(value.get(j + 3).toString(), dateTimeFormatter)));
                    }
                }
                CACHE.put(PAYMENTS_CACHE_KEY, payments);
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

    @Override
    public HttpResponseDto addPayment(PaymentDto paymentDto) {
        try {
            log.info("Add payment request execution...");
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            final String sheetName = getSheetBasedOnUsername();
            final String range = sheetName + "!A2:D";
            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            ValueRange response = service.spreadsheets().values()
                    .get(SPREADSHEET_ID, range)
                    .execute();
            List<List<Object>> newValues = mapToRecords(paymentDto);
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                ValueRange body = new ValueRange().setValues(newValues);
                service.spreadsheets().values()
                        .update(SPREADSHEET_ID, sheetName + "!A2:D2", body)
                        .setValueInputOption("RAW")
                        .execute();
            } else {
                int lastRow = values.size();
                ValueRange body = new ValueRange().setValues(newValues);
                service.spreadsheets().values()
                        .update(SPREADSHEET_ID, sheetName + "!A" + (lastRow + 2) + ":D" + (lastRow + 2), body)
                        .setValueInputOption("RAW")
                        .execute();
            }
            return new HttpResponseDto("OK", HttpStatus.OK.value());
        } catch (IOException e) {
            return new HttpResponseDto("Internal server error. Details: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        } catch (GeneralSecurityException e) {
            return new HttpResponseDto("Security error: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private List<List<Object>> mapToRecords(PaymentDto paymentDto) {
        String[] dateTime = paymentDto.getDateTime().toString().split("[.T]");
        String plainDateTime = String.join(" ", dateTime[0], dateTime[1]);
        return List.of(List.of(paymentDto.getName(),
                paymentDto.getAmount().setScale(2, RoundingMode.HALF_UP),
                paymentDto.getType().getId(),
                plainDateTime));
    }

    @Override
    public List<PaymentDto> getPaymentsFromCache() {
        if (CACHE.get(PAYMENTS_CACHE_KEY) != null) {
            return (List<PaymentDto>) CACHE.get(PAYMENTS_CACHE_KEY);
        } else {
            return getPayments();
        }
    }

    @Override
    public int count() {
        log.info("Count request execution...");
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            final String sheetName = getSheetBasedOnUsername();
            final String range = sheetName + "!A2:D";
            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            ValueRange response = service.spreadsheets().values()
                    .get(SPREADSHEET_ID, range)
                    .execute();
            return response.getValues().size();
        } catch (GeneralSecurityException | IOException e) {
            log.error("Failed to get count from google sheets.", e);
            return -1;
        }
    }

    @Override
    public int countPaged() {
        return count() / PAGE_COUNT + 1;
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
        InputStream in = resourceLoader.getResource(credentialFilePath()).getInputStream();
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + credentialFilePath());
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
        List<PaymentDto> payment = new ArrayList<>();
        payment.add(new PaymentDto(null, null, new HttpResponseDto(message, status),
                PaymentDto.Type.OTHER, null));
        return payment;
    }

    private List<PaymentDto> getFromCache() {
        if (CACHE.containsKey(PAYMENTS_CACHE_KEY) && CACHE.get(PAYMENTS_CACHE_KEY) != null) {
            return (List<PaymentDto>) CACHE.get(PAYMENTS_CACHE_KEY);
        } else {
            List<PaymentDto> payments = getPayments();
            CACHE.put(PAYMENTS_CACHE_KEY, payments);
            return payments;
        }
    }

    private String getSheetBasedOnUsername() {
        return "R_" + SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
