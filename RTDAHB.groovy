import java.io.FileInputStream; 
import java.io.IOException; 
import java.io.InputStreamReader; 
import java.net.URL; 
import java.util.*; 
import java.util.concurrent.*; 
import java.nio.charset.StandardCharsets; 
import javax.net.ssl.HttpsURLConnection; 
import javax.net.ssl.HostnameVerifier; 
import javax.net.ssl.SSLSession; 
import org.apache.camel.Exchange; 
import org.json.JSONArray; 
import org.json.JSONObject; 
import org.w3c.dom.Document; 
import org.w3c.dom.Element; 
import org.w3c.dom.Node; 
import org.w3c.dom.NodeList; 
import javax.xml.bind.DatatypeConverter; 
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory; 
import org.xml.sax.InputSource; 
import java.net.http.HttpClient; 
import java.net.http.HttpRequest; 
import java.net.http.HttpResponse; 
import javax.net.ssl.KeyManagerFactory; 
import javax.net.ssl.SSLContext; 
import javax.net.ssl.TrustManagerFactory; 
import java.security.KeyStore; 
import java.time.Duration;



public class RTDRequest {
	
private static Map map1 = Collections.synchronizedMap(new HashMap());

	private static Document convertStringToXMLDocument(String xmlString) {
			//Parser that produces DOM object trees from XML content
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			 
			//API to obtain DOM Document instance
			DocumentBuilder builder = null;
			try
			{
				//Create DocumentBuilder with default configuration
				builder = factory.newDocumentBuilder();
				 
				//Parse the content to Document object
				Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
				return doc;
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return null;
		}
  	
	public static Map<String, String> fetchCustomerInfo(String EntityID, Map<String, String> map) {
		if (EntityID == null || EntityID.trim().isEmpty()) {
			throw new IllegalArgumentException("EntityID cannot be null or empty");
		}
		// SSL Configuration for MTLS 
		//String keystorePath = "/opt/sas/viya/config/etc/boss/mycert.p12"; 
		//String keystorePassword = "changeit"; 
		String truststorePath = "/opt/sas/viya/config/etc/boss/custom-truststore.jks"; 
		String truststorePassword = "changeit"; 
		
		System.setProperty("https.proxyHost", "webproxy-azuan.vsp.sas.com"); 
		System.setProperty("https.proxyPort", "3128"); 
		
		// Load the keystore (client certificate) 
		/*
		KeyStore keyStore = KeyStore.getInstance("PKCS12"); 
		try (FileInputStream keyStoreInputStream = new FileInputStream(keystorePath)) { 
		keyStore.load(keyStoreInputStream, keystorePassword.toCharArray()); 
		} 
		*/
		// Load the truststore (CA certificates) 
		KeyStore trustStore = KeyStore.getInstance("JKS"); 
		try (FileInputStream trustStoreInputStream = new FileInputStream(truststorePath)) { 
		trustStore.load(trustStoreInputStream, truststorePassword.toCharArray()); 
		} 
		
		// Initialize KeyManagerFactory with the keystore 
		
		//KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()); 
		//keyManagerFactory.init(keyStore, keystorePassword.toCharArray()); 
		
		// Initialize TrustManagerFactory with the truststore 
		
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()); 
		trustManagerFactory.init(trustStore); 
			
		// Create and initialize SSLContext 
		SSLContext sslContext = SSLContext.getInstance("TLS"); 
		sslContext.init(null, trustManagerFactory.getTrustManagers(), null); 
		
		// Create HttpClient with SSLContext 
		HttpClient httpClient =  HttpClient.newBuilder() 
		.version(HttpClient.Version.HTTP_2) 
		.sslContext(sslContext) // Use the custom SSLContext 
		.connectTimeout(Duration.ofSeconds(60)) 
		.build(); 
		
		String customerId = EntityID; 
		String apiUrl = String.format("https://api.cit.dtp.alhilalbankuaedev.ae/sas/v1/customers/%s", customerId);
		System.out.println("Preparing to hit the customer info API URL:" + apiUrl);
		HttpRequest apiRequest = HttpRequest.newBuilder() 
		.uri(URI.create(apiUrl)) 
		.header("Content-Type", "application/json")  
		.timeout(Duration.ofSeconds(60)) 
		.GET() 
		.build(); 
		
		System.out.println("Connection established to" + apiUrl); 
		HttpResponse<String> apiResponse = httpClient.send(apiRequest, HttpResponse.BodyHandlers.ofString()); 
		System.out.println("Response received: " + apiResponse.body());
		
			// Parse JSON response according to the provided schema
		//JSONObject jsonObject = new JSONObject(apiResponse.body());
		JSONObject jsonObject = new JSONObject();

// Adding all fields to JSONObject
		jsonObject.put("CustomerId", "330a48eb-9a17-33a4-a563-92f91a953d33");
		jsonObject.put("Dob", "1988-08-13");
		jsonObject.put("MobileNumber", "+555566610939");
		jsonObject.put("PreferredName", "abhi jain");
		jsonObject.put("FirstName", "MOHIT");
		jsonObject.put("LastName", "PURI");
		jsonObject.put("FullName", "MOHIT PURI");
		jsonObject.put("Gender", "MALE");
		jsonObject.put("Nationality", "AE");
		jsonObject.put("CountryOfBirth", "IN");
		jsonObject.put("CityOfBirth", "new delhi");
		jsonObject.put("Language", "en");
		jsonObject.put("Email", "Scotty19@yahoo.com");
		jsonObject.put("EmailState", "VERIFIED");

		// Create Address object
		JSONObject address = new JSONObject();
		address.put("Department", "Apart");
		address.put("SubDepartment", "503");
		address.put("BuildingNumber", "34");
		address.put("StreetName", "Butina");
		address.put("TownName", "Dibba Al Hisn");
		address.put("CountrySubDivision", "Dubai");
		address.put("Country", "AE");
		address.put("PostalCode", JSONObject.NULL);

		// Create AddressLine array
		JSONArray addressLine = new JSONArray();
		addressLine.put("Sharjah");
		address.put("AddressLine", addressLine);

		// Add address to main JSON
		jsonObject.put("Address", address);

		// Continue with remaining fields
		jsonObject.put("CustomerState", "ACCOUNT_CARD_ACTIVATION");
		jsonObject.put("Cif", "6610939");
		jsonObject.put("CustomerType", "BANKING");
		jsonObject.put("AgeGroup", "21+");
		jsonObject.put("OnboardedBy", JSONObject.NULL);
		jsonObject.put("EidStatus", "VALID");
		jsonObject.put("InvalidEIDReason", JSONObject.NULL);
		jsonObject.put("CustomerMigrationStatus", "NEW_TO_DIGITAL");
		jsonObject.put("FinancialProductEligible", true);
		jsonObject.put("NationalityFullName", "United Arab Emirates");
		jsonObject.put("CountryOfBirthFullName", "United Arab Emirates");
		jsonObject.put("VerificationMethod", "NON_EFR");
		jsonObject.put("IsAdditionalIncome", false);
		jsonObject.put("IsDualNational", false);
		jsonObject.put("DualNationality", JSONObject.NULL);
		jsonObject.put("CustomerSegment", JSONObject.NULL);
		jsonObject.put("CustomerDocumentNumber", "784196293684155");
		jsonObject.put("CustomerStatusReason", "ACTIVE");
		jsonObject.put("CustomerStatus", "ACTIVE");
		jsonObject.put("LastKycDate", JSONObject.NULL);
		jsonObject.put("NextKycDate", JSONObject.NULL);

		// Create PoliticallyExposedDetails object
		JSONObject politicallyExposedDetails = new JSONObject();
		politicallyExposedDetails.put("IsExposed", false);
		politicallyExposedDetails.put("RoleCode", JSONObject.NULL);
		jsonObject.put("PoliticallyExposedDetails", politicallyExposedDetails);

		// Create AccountOpeningDetails object
		JSONObject accountOpeningDetails = new JSONObject();
		accountOpeningDetails.put("AccountPurposeCode", JSONObject.NULL);
		accountOpeningDetails.put("TransactionsPerMonthCode", JSONObject.NULL);
		jsonObject.put("AccountOpeningDetails", accountOpeningDetails);

		jsonObject.put("OverallRiskRating", "NEUTRAL");
		
		
		map.put("CUSTOMER_ID", jsonObject.opt("CustomerId") != JSONObject.NULL ? jsonObject.optString("CustomerId", "") : "");
        map.put("DOB", jsonObject.opt("Dob") != JSONObject.NULL ? jsonObject.optString("Dob", "") : "");
        map.put("MOBILE_NUMBER", jsonObject.opt("MobileNumber") != JSONObject.NULL ? jsonObject.optString("MobileNumber", "") : "");
        map.put("PREFERRED_NAME", jsonObject.opt("PreferredName") != JSONObject.NULL ? jsonObject.optString("PreferredName", "") : "");
        map.put("FIRST_NAME", jsonObject.opt("FirstName") != JSONObject.NULL ? jsonObject.optString("FirstName", "") : "");
        map.put("LAST_NAME", jsonObject.opt("LastName") != JSONObject.NULL ? jsonObject.optString("LastName", "") : "");
        map.put("FULL_NAME", jsonObject.opt("FullName") != JSONObject.NULL ? jsonObject.optString("FullName", "") : "");
        map.put("GENDER", jsonObject.opt("Gender") != JSONObject.NULL ? jsonObject.optString("Gender", "") : "");
        map.put("NATIONALITY", jsonObject.opt("Nationality") != JSONObject.NULL ? jsonObject.optString("Nationality", "") : "");
        map.put("DUAL_NATIONALITY", jsonObject.opt("DualNationality") != JSONObject.NULL ? jsonObject.optString("DualNationality", "") : "");
        map.put("CUSTOMER_STATUS", jsonObject.opt("CustomerStatus") != JSONObject.NULL ? jsonObject.optString("CustomerStatus", "") : "");

        // Adding missing string fields
        map.put("COUNTRY_OF_BIRTH", jsonObject.opt("CountryOfBirth") != JSONObject.NULL ? jsonObject.optString("CountryOfBirth", "") : "");
        map.put("CITY_OF_BIRTH", jsonObject.opt("CityOfBirth") != JSONObject.NULL ? jsonObject.optString("CityOfBirth", "") : "");
        map.put("LANGUAGE", jsonObject.opt("Language") != JSONObject.NULL ? jsonObject.optString("Language", "") : "");
        map.put("EMAIL", jsonObject.opt("Email") != JSONObject.NULL ? jsonObject.optString("Email", "") : "");
        map.put("EMAIL_STATE", jsonObject.opt("EmailState") != JSONObject.NULL ? jsonObject.optString("EmailState", "") : "");
        map.put("CUSTOMER_STATE", jsonObject.opt("CustomerState") != JSONObject.NULL ? jsonObject.optString("CustomerState", "") : "");
        map.put("CIF", jsonObject.opt("Cif") != JSONObject.NULL ? jsonObject.optString("Cif", "") : "");
        map.put("CUSTOMER_TYPE", jsonObject.opt("CustomerType") != JSONObject.NULL ? jsonObject.optString("CustomerType", "") : "");
        map.put("AGE_GROUP", jsonObject.opt("AgeGroup") != JSONObject.NULL ? jsonObject.optString("AgeGroup", "") : "");
        map.put("ONBOARDED_BY", jsonObject.opt("OnboardedBy") != JSONObject.NULL ? jsonObject.optString("OnboardedBy", "") : "");
        map.put("EID_STATUS", jsonObject.opt("EidStatus") != JSONObject.NULL ? jsonObject.optString("EidStatus", "") : "");
        map.put("INVALID_EID_REASON", jsonObject.opt("InvalidEIDReason") != JSONObject.NULL ? jsonObject.optString("InvalidEIDReason", "") : "");
        map.put("CUSTOMER_MIGRATION_STATUS", jsonObject.opt("CustomerMigrationStatus") != JSONObject.NULL ? jsonObject.optString("CustomerMigrationStatus", "") : "");
        map.put("NATIONALITY_FULL_NAME", jsonObject.opt("NationalityFullName") != JSONObject.NULL ? jsonObject.optString("NationalityFullName", "") : "");
        map.put("COUNTRY_OF_BIRTH_FULL_NAME", jsonObject.opt("CountryOfBirthFullName") != JSONObject.NULL ? jsonObject.optString("CountryOfBirthFullName", "") : "");
        map.put("VERIFICATION_METHOD", jsonObject.opt("VerificationMethod") != JSONObject.NULL ? jsonObject.optString("VerificationMethod", "") : "");
        map.put("CUSTOMER_SEGMENT", jsonObject.opt("CustomerSegment") != JSONObject.NULL ? jsonObject.optString("CustomerSegment", "") : "");
        map.put("CUSTOMER_DOCUMENT_NUMBER", jsonObject.opt("CustomerDocumentNumber") != JSONObject.NULL ? jsonObject.optString("CustomerDocumentNumber", "") : "");
        map.put("CUSTOMER_STATUS_REASON", jsonObject.opt("CustomerStatusReason") != JSONObject.NULL ? jsonObject.optString("CustomerStatusReason", "") : "");
        map.put("LAST_KYC_DATE", jsonObject.opt("LastKycDate") != JSONObject.NULL ? jsonObject.optString("LastKycDate", "") : "");
        map.put("NEXT_KYC_DATE", jsonObject.opt("NextKycDate") != JSONObject.NULL ? jsonObject.optString("NextKycDate", "") : "");
        map.put("OVERALL_RISK_RATING", jsonObject.opt("OverallRiskRating") != JSONObject.NULL ? jsonObject.optString("OverallRiskRating", "") : "");

        // Boolean fields (converted to string)
        map.put("IS_ELIGIBLE", String.valueOf(jsonObject.optBoolean("FinancialProductEligible", false)));
        map.put("IS_ADDITIONAL_INCOME", String.valueOf(jsonObject.optBoolean("IsAdditionalIncome", false)));
        map.put("IS_DUAL_NATIONAL", String.valueOf(jsonObject.optBoolean("IsDualNational", false)));

        // Parsing Address Details
        JSONObject address = jsonObject.optJSONObject("Address");
        if (address != null) {
            map.put("DEPARTMENT", address.opt("Department") != JSONObject.NULL ? address.optString("Department", "") : "");
            map.put("SUB_DEPARTMENT", address.opt("SubDepartment") != JSONObject.NULL ? address.optString("SubDepartment", "") : "");
            map.put("BUILDING_NUMBER", address.opt("BuildingNumber") != JSONObject.NULL ? address.optString("BuildingNumber", "") : "");
            map.put("STREET_NAME", address.opt("StreetName") != JSONObject.NULL ? address.optString("StreetName", "") : "");
            map.put("TOWN_NAME", address.opt("TownName") != JSONObject.NULL ? address.optString("TownName", "") : "");
            map.put("ADDRESS_COUNTRY", address.opt("Country") != JSONObject.NULL ? address.optString("Country", "") : "");
            map.put("POSTAL_CODE", address.opt("PostalCode") != JSONObject.NULL ? address.optString("PostalCode", "") : "");
            map.put("COUNTRY_SUB_DIVISION", address.opt("CountrySubDivision") != JSONObject.NULL ? address.optString("CountrySubDivision", "") : "");

            // Handling AddressLine array - improved to handle multiple entries
            JSONArray addressLineArray = address.optJSONArray("AddressLine");
            if (addressLineArray != null) {
                // Store first address line
                map.put("ADDRESS_LINE", addressLineArray.length() > 0 ? addressLineArray.getString(0) : "");
                
                // Store additional address lines if they exist
                for (int i = 1; i < addressLineArray.length(); i++) {
                    map.put("ADDRESS_LINE_" + (i + 1), addressLineArray.getString(i));
                }
            } else {
                map.put("ADDRESS_LINE", "");
            }
        }

        // Parsing Politically Exposed Details
        JSONObject ped = jsonObject.optJSONObject("PoliticallyExposedDetails");
        if (ped != null) {
            map.put("IS_EXPOSED", String.valueOf(ped.optBoolean("IsExposed", false)));
            map.put("ROLE_CODE", ped.opt("RoleCode") != JSONObject.NULL ? ped.optString("RoleCode", "") : "");
        }

        // Parsing Account Opening Details
        JSONObject aod = jsonObject.optJSONObject("AccountOpeningDetails");
        if (aod != null) {
            map.put("ACCOUNT_PURPOSE_CODE", aod.opt("AccountPurposeCode") != JSONObject.NULL ? aod.optString("AccountPurposeCode", "") : "");
            map.put("TRANSACTIONS_PER_MONTH_CODE", aod.opt("TransactionsPerMonthCode") != JSONObject.NULL ? aod.optString("TransactionsPerMonthCode", "") : "");
        }

			return map;
	}
				
	
	
	public void prepareADCBResponse(Exchange exchange) {
		String textXml = exchange.getIn().getBody();
		//System.out.println(textXml);
		String textXml1 = textXml.replace('"','/');
		String[] splitXml = textXml1.split("entityType=/", 2);
		String newXml1 = splitXml[1].replaceFirst("/>", "</entityType>");
		String textXml2 = "<entityRequest><entityType>" + newXml1;

		Document doc = convertStringToXMLDocument(textXml2);
		Map map = new HashMap();

		String entityType = null;
		String entityID = null;
		String multiOrg = null;
		String username = null;
		String contactID = null;
		String contactType = null;
		try 
			{
				doc.getDocumentElement().normalize();
					NodeList nList = doc.getElementsByTagName("entityRequest");
					Node nNode = nList.item(0);
					
					Element eElement = (Element) nNode;
					entityType = (eElement.getElementsByTagName("entityType")).item(0).getTextContent();
			        entityID = (eElement.getElementsByTagName("entityID")).item(0).getTextContent();
					multiOrg = (eElement.getElementsByTagName("multiOrg")).item(0).getTextContent();
					username = (eElement.getElementsByTagName("username")).item(0).getTextContent(); /* Release 2 Addition*/
					if((eElement.getElementsByTagName("contactID").item(0)) != null)
					{	contactID = (eElement.getElementsByTagName("contactID")).item(0).getTextContent(); }
					if((eElement.getElementsByTagName("contactType")).item(0) != null)
					{	contactType = (eElement.getElementsByTagName("contactType")).item(0).getTextContent(); }
						
			} catch (Exception e) {
				e.printStackTrace();
		}		
		switch (entityType)
			{    
			case "X" : 
				try {
					TimeLimitedCodeBlock.runWithTimeout(new Runnable() {
						@Override
						public void run() {
							try {
								MultifetchCustomerInfo m2= new MultifetchCustomerInfo(entityID);
								Thread t2= new Thread(m2);
								
								
				
								t2.start(); 
								t2.join();
								
							
							}
							catch (InterruptedException e) {
								System.out.println("was interuupted! 3");
							}
						}
					}, 60, TimeUnit.SECONDS);
				}
				catch (TimeoutException e) {
					System.out.println("Got timeout! 4");
				}
				break;
}
map.putAll(map1);


	if (contactType == null)
			contactType = "Customer Id Missing";
		if (contactID == null)
			contactID = "Customer Id Missing";

		if (entityType == 'X')
		{
			contactType = entityType;
			contactID = entityID;	
		}
			
		map.put("/entityRequest/entityType", entityType);
		map.put("/entityRequest/entityID", entityID);
		map.put("/entityRequest/multiOrg", multiOrg);
		map.put("/entityRequest/contactID", contactID);
		map.put("/entityRequest/contactType", contactType);
		
			exchange.getIn().setBody(map);

    
}

}




class MultifetchCustomerInfo implements Runnable{  
    
	private String entityID;

	public MultifetchCustomerInfo(String _entityID) {
		this.entityID = _entityID;
	}
	
    @Override
	public void run(){  
		RTDRequest.fetchCustomerInfo(entityID,RTDRequest.map1); 
	}
}


class TimeLimitedCodeBlock {

	public static void runWithTimeout(final Runnable runnable, long timeout, TimeUnit timeUnit) throws Exception {
		runWithTimeout(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				runnable.run();
				return null;
			}
		}, timeout, timeUnit);
	}

	public static <T> T runWithTimeout(Callable<T> callable, long timeout, TimeUnit timeUnit) throws Exception {
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final Future<T> future = executor.submit(callable);
		executor.shutdown(); // This does not cancel the already-scheduled task.
		try {
			return future.get(timeout, timeUnit);
		}
		catch (TimeoutException e) {
			future.cancel(true);
			throw e;
		}
		catch (ExecutionException e) {
			//unwrap the root cause
			Throwable t = e.getCause();
			if (t instanceof Error) {
				throw (Error) t;
			} else if (t instanceof Exception) {
				throw (Exception) t;
			} else {
				throw new IllegalStateException(t);
			}
		}
	}

}