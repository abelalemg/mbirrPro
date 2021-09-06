package com.mbirrapi.MbirrPaymentApi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import org.json.simple.JSONObject;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class apirequester extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unchecked")
	public void doPost (HttpServletRequest requests, HttpServletResponse responses) throws ServletException, IOException
	{
		String submit = requests.getParameter("submit") == null ? "" : requests.getParameter("submit");		
		
		if (submit!=null && submit.equals("Withdraw"))
		{
			String name = requests.getParameter("name") == null ? "" : requests.getParameter("name");
			String tel = requests.getParameter("tel") == null ? "" : requests.getParameter("tel");
			Double amount = Double.parseDouble(requests.getParameter("amount") == null ? "" : requests.getParameter("amount"));
			Double amountInCent = amount*100;
			String contentSignature = null;
			
			JSONObject json = new JSONObject();
			json.put("name", "PaymentClient");
			json.put("account_number", "66012470");
			json.put("mobile_number", "0905030507");
			json.put("language", "en");
			JSONObject json1 = new JSONObject();
			json1.put("name", name);
			json1.put("account_number", "");
			json1.put("mobile_number", tel);
			json1.put("language", "en");
			JSONObject json2 = new JSONObject();
			Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			String formattedTimestamp = new SimpleDateFormat("yyyy-MM-dd").format(timestamp);
			json2.put("simulation_only", "false");
			json2.put("client_transaction_id", "qewt994-7ds7sc-4731-addf802-cb4d74bd25a");
			json2.put("timestamp", formattedTimestamp);
			json2.put("amount_in_cents", amountInCent);
			json2.put("operation_type", "Transfer");
			JSONObject json3 = new JSONObject();
			json3.put("sender", json);
			json3.put("beneficiary", json1);
			json3.put("transaction", json2);
			json3.put("end_user_message", "We've refunded your payment. Thank you.");
			String finalJson = json3.toString();
			
			String privateKey = "-----BEGIN PRIVATE KEY-----"
					+ "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALFf/kJIAtYxzIvr"
					+ "iMMTWKJVY8spyA+/pLbjitznHx+ABtqUkn3YbfoBKX2tQVuH86gO+2lZGFUrT0G4"
					+ "KxX7mCg5UyWMcdFVFzoJStyHTEuIxWV8vFh7IDRcHZOHil4If2h75SzyXpPxVjzV"
					+ "AXBuCLW98eM26///eVmOitmHW0mdAgMBAAECgYANDw0wIg8bZ/UwQ/oAqrb21KSR"
					+ "O5VAG5Lr6Bq8IsP21L0scI3MeBe4tUcxuoS6UWsN73RxEB8rfhHKu91oM+rCw/oJ"
					+ "8PH4QlJ2nOeQFP4CmZXriqrfuW6RCKRiB+ggxCHnH64JW8SCzODY7fdq3hG3wwLM"
					+ "dl1WryH8iaqBR9oe3QJBANsTw/l0LSxyuzOKazjNaLIFM04Z8xoRoqhD7QVuAKaV"
					+ "qoJZldq5eJFgC0lewF2KUugxBQNXDEJYlhbPNTXh2b8CQQDPRPTcer0iFAZSsQ6J"
					+ "wWNdRpUnxJjUBBebeZTgwYxuIjv0SLyCB/mcJaL9viCE+c48meYmwKshvdRpTJiu"
					+ "CZujAkEAy2EhGS8iVNY6NhH1kmkXHdU4GPR8PCJNF9rfaqABmKTvA035kXGHnaZF"
					+ "NBrziKNGbmo7lis0pU8qHwjEBD6kbwJAezDY+FJbJ24PdAaYRXgTvtS8wi4vR5RH"
					+ "E7lnq05eUPc3+zFgGUj0KsKT5Yyjd2WiFpLCIDZTgHJ7VTqZZJeBUQJAZ3ypMzOH"
					+ "7BK7Z17WcMCA6aRJ+U4RsoQxXh4vbkrnlraac6uV9BQKiaU0+ws2WckhpMXr1pNp"
					+ "3JGRhEoOwNwjpg=="
					+ "-----END PRIVATE KEY-----";
			
			try{
				privateKey = privateKey.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
				KeyFactory kf = KeyFactory.getInstance("RSA");
				PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
				PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
				byte[] data = finalJson.getBytes("UTF8");
				Signature sig = Signature.getInstance("SHA1withRSA");
				sig.initSign(privKey);
				sig.update(data);
				byte[] signatureBytes = sig.sign();
				contentSignature = Base64.getEncoder().encodeToString(signatureBytes);
			}catch(Exception e){
				System.out.println(e);
			}
			
			URL url = new URL ("https://demo-api.mbirr.com/transactionEngine/payment");
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setConnectTimeout(60000);
            httpUrlConnection.setReadTimeout(60000);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setUseCaches(true);
            httpUrlConnection.setDoInput(true);
			httpUrlConnection.setRequestMethod("POST");
			httpUrlConnection.setRequestProperty("Accept", "application/json");
			httpUrlConnection.setRequestProperty("Content-Type", "application/json");
			httpUrlConnection.setRequestProperty("Content-Signature", contentSignature);
			
			OutputStream outputStream = httpUrlConnection.getOutputStream();
            byte[] byte_value = finalJson.getBytes("UTF-8");
            outputStream.write(byte_value);
            outputStream.flush();
            outputStream.close();
                 
            String jsonResponse = null;
            String server_responce = null;
            int statusCode = 0;
            switch (httpUrlConnection.getResponseCode()) {
			case 200:
				InputStream inputStream = httpUrlConnection.getInputStream();
	            byte[] val_rspns = new byte[2048];
	            int i = 0;
	            StringBuilder stringBuilder = new StringBuilder();
	            while ((i = inputStream.read(val_rspns)) != -1) {
	                stringBuilder.append(new String(val_rspns, 0, i));
	            }
				 server_responce = httpUrlConnection.getResponseMessage();
	             statusCode = httpUrlConnection.getResponseCode();
	             jsonResponse = stringBuilder.toString();
	             inputStream.close();
				break;
			case 499:
				InputStream inputStream1 = httpUrlConnection.getErrorStream();
	            byte[] val_rspns1 = new byte[2048];
	            int i1 = 0;
	            StringBuilder stringBuilder1 = new StringBuilder();
	            while ((i1 = inputStream1.read(val_rspns1)) != -1) {
	                stringBuilder1.append(new String(val_rspns1, 0, i1));
	            }
				 server_responce = httpUrlConnection.getResponseMessage();
	             statusCode = httpUrlConnection.getResponseCode();
	             jsonResponse = stringBuilder1.toString();
	             inputStream1.close();
				break;
			case 500:
				InputStream inputStream2 = httpUrlConnection.getErrorStream();
	            byte[] val_rspns2 = new byte[2048];
	            int i2 = 0;
	            StringBuilder stringBuilder2 = new StringBuilder();
	            while ((i2 = inputStream2.read(val_rspns2)) != -1) {
	                stringBuilder2.append(new String(val_rspns2, 0, i2));
	            }
				 server_responce = httpUrlConnection.getResponseMessage();
	             statusCode = httpUrlConnection.getResponseCode();
	             jsonResponse = stringBuilder2.toString();
	             inputStream2.close();
				break;
			default:
				break;
			}
            System.out.println(server_responce);
            System.out.println(statusCode);
            System.out.println(jsonResponse);
            httpUrlConnection.disconnect();
		}
	}
}