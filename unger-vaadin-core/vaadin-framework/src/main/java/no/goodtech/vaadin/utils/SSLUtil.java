package no.goodtech.vaadin.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Snarfed from:
 * http://stackoverflow.com/questions/23504819/how-to-disable-ssl-certificate
 * -checking-with-spring-resttemplate
 * http://www.jroller.com/jurberg/entry/using_a_hostnameverifier_with_spring
 */
public final class SSLUtil {

	private static final TrustManager[] UNQUESTIONING_TRUST_MANAGER = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType) {
		}
	} };

	public static void turnOffSslChecking() {
		// Install the all-trusting trust manager
		SSLContext sc;
		try {
			sc = SSLContext.getInstance("SSL");
		} catch (NoSuchAlgorithmException e1) {
			throw new RuntimeException(e1);
		}
		try {
			sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
		} catch (KeyManagementException e) {
			throw new RuntimeException(e);
		}
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	public static void turnOnSslChecking() {
		try {
			SSLContext.getInstance("SSL").init(null, null, null);
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private SSLUtil() {
		throw new UnsupportedOperationException("Do not instantiate libraries.");
	}

	public static class NullHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	public static class MySimpleClientHttpRequestFactory extends SimpleClientHttpRequestFactory {

		private final HostnameVerifier verifier;

		public MySimpleClientHttpRequestFactory(HostnameVerifier verifier) {
			this.verifier = verifier;
		}

		@Override
		protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
			if (connection instanceof HttpsURLConnection)
				((HttpsURLConnection) connection).setHostnameVerifier(verifier);
			super.prepareConnection(connection, httpMethod);
		}

	}

	public static void turnOffHostNameChecking(RestTemplate restTemplate) {
		restTemplate.setRequestFactory(new MySimpleClientHttpRequestFactory(new NullHostnameVerifier()));
	}
}