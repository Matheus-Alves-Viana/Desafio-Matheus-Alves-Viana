package sheets;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class SheetsQuickstart {


	/**
	 * Prints the names and majors of students in a sample spreadsheet:
	 * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
	 */
	public static void main(String... args) throws IOException, GeneralSecurityException {
		ApiGoogleService apiGoogleService = new ApiGoogleService();
		IntegrationApiGoogle IntegrationApiGoogle = new IntegrationApiGoogle();

		final String spreadsheetId = "1Dmejihw1RZn3USDQSfRCaNuwV6D59gW9bsqJ9LG1OPg";
		final String range = "engenharia_de_software!A4:H27";
		final String valueInputOption = "USER_ENTERED";


		List<List<Object>> values = IntegrationApiGoogle.getResponse(spreadsheetId, range).getValues();

		if (values == null || values.isEmpty()) {
			System.out.println("No data found.");
		} else {
			try {
				apiGoogleService.updateValues(spreadsheetId, valueInputOption, values);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}