package br.com.wjaa.ranchucrutes.utils;


public class HttpConnectionUtil {
	/*public static String sendRegistrationIdToBackend(String regId){
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://www.thiengo.com.br/doc/projects/android/gcm/ctrl/CtrlGcm.php");
		String answer = "";
		
		try{
			ArrayList<NameValuePair> valores = new ArrayList<NameValuePair>();
			valores.add(new BasicNameValuePair("method", "save-gcm-registration-id"));
			valores.add(new BasicNameValuePair("reg-id", regId));
			
			httpPost.setEntity(new UrlEncodedFormEntity(valores));
			HttpResponse resposta = httpClient.execute(httpPost);
			answer = EntityUtils.toString(resposta.getEntity());
		}
		catch(NumberFormatException e){ e.printStackTrace(); }
		catch(NullPointerException e){ e.printStackTrace(); }
		catch(ClientProtocolException e){ e.printStackTrace(); }
		catch(IOException e){ e.printStackTrace(); }
		return(answer);
	}*/
}
