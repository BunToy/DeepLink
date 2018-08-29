package com.guessmusic.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 
 * @version V1.0
 * @ClassName: HttpClient
 * @Description: HTTP
 * @author 董超 dc.sh@aresoft.cn
 * @date 2013-11-29 上午11:19:08
 */
public class HttpClient {

	static {
		Security.setProperty("jdk.certpath.disabledAlgorithms", "");
	}

	/**
	 * https 域名校验
	 */
	private class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	/**
	 * https 证书管理
	 */
	private class TrustAnyTrustManager implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
	}

	private static final String GET = "GET";
	private static final String POST = "POST";

	private static final SSLSocketFactory sslSocketFactory = initSSLSocketFactory();
	private static final TrustAnyHostnameVerifier trustAnyHostnameVerifier = new HttpClient().new TrustAnyHostnameVerifier();
	private static final RequestConfig config = new RequestConfig();

	private static SSLSocketFactory initSSLSocketFactory() {
		try {
			TrustManager[] tm = { new HttpClient().new TrustAnyTrustManager() };
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, tm, new java.security.SecureRandom());
			return sslContext.getSocketFactory();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static HttpURLConnection getHttpConnection(String url, String method, RequestConfig config)
			throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
		URL _url = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
		if (conn instanceof HttpsURLConnection) {
			((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
			((HttpsURLConnection) conn).setHostnameVerifier(trustAnyHostnameVerifier);
		}

		conn.setRequestMethod(method);

		if(POST.equals(conn.getRequestMethod())){
			conn.setDoOutput(true);
			conn.setDoInput(true);
		}

		conn.setConnectTimeout(config.getConnectTimeout());
		conn.setReadTimeout(config.getReadTimeout());

		conn.setRequestProperty("Content-Type", config.getContentType());
		conn.setRequestProperty("User-Agent", config.getUserAgent());
		String referer = config.getReferer();
		if (referer != null) {
			conn.setRequestProperty("referer", referer);
		}
		Map<String, String> headers = config.getHeader();
		if (headers != null && !headers.isEmpty()) {
			for (Entry<String, String> entry : headers.entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		return conn;
	}

	/**
	 * Send GET request
	 */
	public static String get(String url, RequestParam queryParas, RequestConfig config) {
		HttpURLConnection conn = null;
		try {
			conn = getHttpConnection(buildUrlWithQueryString(url, queryParas, config), GET, config);
			conn.connect();
			return readResponseString(conn, config);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	public static String get(String url, RequestConfig config) {
		return get(url, null, config);
	}

	public static String get(String url, RequestParam queryParas) {
		return get(url, queryParas, config);
	}

	public static String get(String url) {
		return get(url, null, config);
	}

	/**
	 * Send POST request
	 */
	public static String post(String url, RequestConfig config, RequestParam param, String data) {
		HttpURLConnection conn = null;
		OutputStream out = null;
		try {
			byte[] content = {};
			if (data != null) {
				conn = getHttpConnection(buildUrlWithQueryString(url, param, config), POST, config);
				content = data.getBytes(config.getCharSet());
			} else {
				conn = getHttpConnection(url, POST, config);
				data = buildRequestData(param, config);
				if (data != null) {
					content = data.getBytes(config.getCharSet());
				}
			}
			conn.connect();
			out = conn.getOutputStream();
			out.write(content);
			return readResponseString(conn, config);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	public static String post(String url, RequestParam param, String data) {
		return post(url, config, param, data);
	}

	public static String post(String url, RequestConfig config, String data) {
		return post(url, config, null, data);
	}

	public static String post(String url, String data) {
		return post(url, config, null, data);
	}

	/**
	 * Send POST request
	 */
	public static String post(String url, RequestConfig config, RequestParam param) {
		return post(url, config, param, null);
	}

	public static String post(String url, RequestParam param) {
		return post(url, config, param, null);
	}

	public static String post(String url, RequestConfig config) {
		return post(url, config, null, null);
	}

	public static String post(String url) {
		return post(url, config, null, null);
	}

	public static String upload(String url, FileItem file) {
		return upload(url, null, Arrays.asList(file), config);
	}

	public static String upload(String url, FileItem file, RequestParam param) {
		return upload(url, param, Arrays.asList(file), config);
	}

	public static String upload(String url, FileItem file, RequestConfig config) {
		return upload(url, null, Arrays.asList(file), config);
	}

	public static String upload(String url, FileItem file, RequestParam param, RequestConfig config) {
		return upload(url, param, Arrays.asList(file), config);
	}

	public static String upload(String url, List<FileItem> fileParams) {
		return upload(url, null, fileParams, config);
	}

	public static String upload(String url, List<FileItem> fileParams, RequestParam param) {
		return upload(url, param, fileParams, config);
	}

	public static String upload(String url, List<FileItem> fileParams, RequestConfig config) {
		return upload(url, null, fileParams, config);
	}

	public static String upload(String url, List<FileItem> fileParams, RequestParam param, RequestConfig config) {
		return upload(url, param, fileParams, config);
	}

	public static String upload(String url, RequestParam param, List<FileItem> fileParams, RequestConfig config) {
		String boundary = "---------------------------" + System.currentTimeMillis(); // 随机分隔线
		HttpURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			String ctype = "multipart/form-data; boundary=" + boundary;
			config.setContentType(ctype);
			conn = getHttpConnection(url, POST, config);
			out = conn.getOutputStream();
			byte[] entryBoundaryBytes = ("--" + boundary + "\r\n").getBytes(config.getCharSet());
			if (param != null) {
				// 组装文本请求参数
				Set<Entry<String, String>> textEntrySet = param.entrySet();
				for (Entry<String, String> textEntry : textEntrySet) {
					byte[] textBytes = getTextEntry(textEntry.getKey(), textEntry.getValue(), config.getCharSet());
					out.write(entryBoundaryBytes);
					out.write(textBytes);
				}
			}
			// 组装文件请求参数
			for (FileItem fileItem : fileParams) {
				byte[] fileBytes = getFileEntry(fileItem.getFieldName(), fileItem.getFileName(), fileItem.getSuffix(), config.getCharSet());
				out.write(entryBoundaryBytes);
				out.write(fileBytes);
				out.write(fileItem.getContent());
				out.write("\r\n".getBytes(config.getCharSet()));
			}
			byte[] endBoundaryBytes = ("--" + boundary + "--\r\n").getBytes(config.getCharSet());
			out.write(endBoundaryBytes);
			rsp = readResponseString(conn, config);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return rsp;
	}

	private static byte[] getTextEntry(String fieldName, String fieldValue, String charset) throws IOException {
		StringBuilder entry = new StringBuilder();
		entry.append("Content-Disposition: form-data; name=\"");
		entry.append(fieldName);
		entry.append("\"\r\n\r\n");
		entry.append(fieldValue);
		entry.append("\r\n");
		return entry.toString().getBytes(charset);
	}

	private static byte[] getFileEntry(String fieldName, String fileName, String suffix, String charset) throws IOException {
		StringBuilder entry = new StringBuilder();
		entry.append("Content-Disposition: form-data; name=\"");
		entry.append(fieldName);
		entry.append("\"; filename=\"");
		entry.append(fileName);
		entry.append("\"\r\nContent-Type: ");
		entry.append(MimeUtil.getMimeType(suffix));
		entry.append("\r\n\r\n");
		return entry.toString().getBytes(charset);
	}

	private static String readResponseString(HttpURLConnection conn, RequestConfig config) {
		StringBuilder sb = new StringBuilder();
		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			inputStream = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(inputStream, config.getCharSet()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Build queryString of the url
	 */
	public static String buildRequestData(RequestParam param, RequestConfig config) {
		if (param == null || param.isEmpty()) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean hasParam = false;
		for (Entry<String, String> entry : param.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (value != null && !"".equals(value))
				try {
					value = URLEncoder.encode(value, config.getCharSet());
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			if (hasParam) {
				sb.append("&");
			} else {
				hasParam = true;
			}
			sb.append(key).append("=").append(value);
		}
		return sb.toString();
	}

	/**
	 * Build queryString of the url
	 */
	private static String buildUrlWithQueryString(String url, RequestParam param, RequestConfig config) {
		if (param == null || param.isEmpty())
			return url;
		StringBuilder sb = new StringBuilder(url);
		boolean isFirst;
		if (url.indexOf("?") == -1) {
			isFirst = true;
			sb.append("?");
		} else {
			isFirst = false;
		}
		for (Entry<String, String> entry : param.entrySet()) {
			if (isFirst)
				isFirst = false;
			else
				sb.append("&");
			String key = entry.getKey();
			String value = entry.getValue();
			if (value != null && !"".equals(value))
				try {
					value = URLEncoder.encode(value, config.getCharSet());
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			sb.append(key).append("=").append(value);
		}
		return sb.toString();
	}

	public static class RequestConfig {
		private String charSet = "UTF-8";
		private int connectTimeout = 30000;
		private int readTimeout = 30000;
		private String contentType = "application/x-www-form-urlencoded";
		private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:44.0) Gecko/20100101 Firefox/44.0";
		private String referer;
		private Map<String, String> header;

		public static RequestConfig defaultConfig() {
			return new RequestConfig();
		}

		public String getCharSet() {
			return charSet;
		}

		public RequestConfig setCharSet(String charSet) {
			this.charSet = charSet;
			return this;
		}

		public int getConnectTimeout() {
			return connectTimeout;
		}

		public RequestConfig setConnectTimeout(int connectTimeout) {
			this.connectTimeout = connectTimeout;
			return this;
		}

		public int getReadTimeout() {
			return readTimeout;
		}

		public RequestConfig setReadTimeout(int readTimeout) {
			this.readTimeout = readTimeout;
			return this;
		}

		public String getContentType() {
			return contentType;
		}

		public RequestConfig setContentType(String contentType) {
			this.contentType = contentType;
			return this;
		}

		public String getUserAgent() {
			return userAgent;
		}

		public RequestConfig setUserAgent(String userAgent) {
			this.userAgent = userAgent;
			return this;
		}

		public String getReferer() {
			return referer;
		}

		public RequestConfig setReferer(String referer) {
			this.referer = referer;
			return this;
		}

		public Map<String, String> getHeader() {
			return header;
		}

		public void setHeader(Map<String, String> header) {
			this.header = header;
		}

		public RequestConfig addHeader(String headerStr, String value) {
			if (header == null) {
				header = new HashMap<String, String>();
			}
			header.put(headerStr, value);
			return this;
		}

		@Override
		public String toString() {
			return "RequestConfig [charSet=" + charSet + ", connectTimeout=" + connectTimeout + ", readTimeout=" + readTimeout + ", contentType=" + contentType + ", userAgent="
					+ userAgent + ", referer=" + referer + ", header=" + header + "]";
		}

	}

	public static class RequestParam {
		private Map<String, String> param;

		public RequestParam() {
			super();
			this.param = new HashMap<String, String>();
		}

		public RequestParam(Map<String, String> param) {
			this.param = param;
		}

		public static RequestParam build() {
			return new RequestParam();
		}

		public boolean isEmpty() {
			return param.isEmpty();
		}

		public RequestParam addParam(String name, String value) {
			param.put(name, value);
			return this;
		}

		public Set<Entry<String, String>> entrySet() {
			return param.entrySet();
		}
	}

}