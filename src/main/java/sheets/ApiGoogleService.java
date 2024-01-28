package sheets;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiGoogleService {

    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    IntegrationApiGoogle IntegrationApiGoogle = new IntegrationApiGoogle();

    public ApiGoogleService() throws GeneralSecurityException, IOException {
    }

    private String calculateSituation(String schoolAbsences, String p1, String p2, String p3) { //calculate student situation
        int absences = 0;
        double mean = calculateMean (p1,p2,p3);
        try {
            absences = Integer.parseInt(schoolAbsences);
        } catch (Exception e) {
            e.printStackTrace();
        }
        														
        double percentageAbsences = 60 * 0.25;
        if (absences >= percentageAbsences) {
            return "REPROVADO POR FALTA";
        } else if (mean >= 70 ){
            return "APROVADO";
        } else if (mean >= 50 && mean <= 60) {
            return "EXAME FINAL";
        } else {
            return "REPROVADO POR NOTA";
        }
    }

    private String calculatefinalGradeAcceptance(String schoolAbsences, String p1, String p2, String p3) { //calculate final passing grade

        int absences = 0;
        double mean = calculateMean (p1,p2,p3);
        try {
            absences = Integer.parseInt(schoolAbsences);
        } catch (Exception e) {
            e.printStackTrace();
        }

        double percentageAbsences = 60 * 0.25;
        if (absences >= percentageAbsences) {
            return "0";
        }

        double finalGrade = 70 - mean;
        if (mean >= 50 && mean <= 60) {
            return String.valueOf(finalGrade);
        }
        return "0";
    }

    public BatchUpdateValuesResponse updateValues(String spreadsheetId,
                                             String valueInputOption,
                                             List<List<Object>> values)
            throws IOException {

        int columnIndex2 = 2; // column index for calculating absences
        int columnIndex3 = 3;
        int columnIndex4 = 4;
        int columnIndex5 = 5;

        List<List<Object>> situationList = new ArrayList<>();
        List<List<Object>> finalGradeList = new ArrayList<>(); //add the values ​​of the positions 'situation' and 'final grade'
        for (List<Object> row : values) {
            if (!row.isEmpty()) {
                String situation = calculateSituation(
                        (String) row.get(columnIndex2),
                        (String) row.get(columnIndex3),
                        (String) row.get(columnIndex4),
                        (String) row.get(columnIndex5));

                String finalGrade = calculatefinalGradeAcceptance(
                        (String) row.get(columnIndex2),
                        (String) row.get(columnIndex3),
                        (String) row.get(columnIndex4),
                        (String) row.get(columnIndex5));
                // Add the result to the list of lists
                situationList.add(Arrays.asList(situation));
                finalGradeList.add(Arrays.asList(finalGrade));
            }
        }

        final String columnG = "engenharia_de_software!G4:G" + (3 + situationList.size()); // Dynamically calculate the number of rows
        final String columnH = "engenharia_de_software!H4:H" + (3 + finalGradeList.size());

        List<ValueRange> data = Arrays.asList(
                new ValueRange()
                        .setRange(columnG)         // Set the fields of the column to be modified
                        .setValues(situationList),  // Set the values for the 'situation' column
                new ValueRange()
                        .setRange(columnH)
                        .setValues(finalGradeList)  // Set the values for the 'final grade' column
        );

        BatchUpdateValuesRequest batchUpdateRequest = new BatchUpdateValuesRequest()
                .setValueInputOption(valueInputOption)
                .setData(data); // Create the request with the updated column values

        BatchUpdateValuesResponse result = IntegrationApiGoogle.getService(HTTP_TRANSPORT).spreadsheets().values()
                .batchUpdate(spreadsheetId, batchUpdateRequest)
                .execute(); // Execute the call to the Google Sheets API

        System.out.println("updated successfully!");

        return result;

    }

    private double calculateMean (String p1, String p2, String p3) {

        int notaP1 = 0;
        int notaP2 = 0;
        int notaP3 = 0;
        double mean = 0.0;
        try {
            notaP1 = Integer.parseInt(p1);
            notaP2 = Integer.parseInt(p2);
            notaP3 = Integer.parseInt(p3);

        } catch (Exception e) {
            e.printStackTrace();
        }

        int totalGrade = notaP1 + notaP2 + notaP3;
        mean = totalGrade / 3;

        return mean;
    }
}