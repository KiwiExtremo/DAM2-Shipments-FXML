package ins.marianao.shipments.fxml.manager;

import java.io.File;
import java.io.StringReader;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.jackson.JacksonFeature;

import ins.marianao.shipments.fxml.ControllerMenu;
import ins.marianao.shipments.fxml.model.User;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Pair;

public class ResourceManager {
	/* Report */
	public static final int COL_1 = 15; // Cat.
	public static final int COL_2 = 12; // Date
	public static final int COL_3 = 35; // User
	public static final int COL_4 = 10; // Prio.
	public static final int COL_5 = 12; // Status
	public static final int GAP = 3;
	public static final int W_TOTAL = COL_1 + COL_2 + COL_3 + COL_4 + COL_5 + 4 * GAP;
	public static final String CHR_BORDER = "·";

	public static final String BASE_DIR = "data";
	public static final String FILE_REPORT_USERS = "users_report.pdf";
	public static final String FILE_REPORT_SHIPMENTS = "shipments_report.pdf";

	private User currentUser; // Logged in user

	private static ResourceManager instance = null;
	private Stage stage;
	private Application app;
	private ResourceBundle applicationBundle;
	private ResourceBundle translationsBundle;
	private ControllerMenu menuController;

	protected ResourceManager() {
	}

	public static ResourceManager getInstance() {
		if (instance == null) {
			instance = new ResourceManager();
		}
		return instance;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public Application getApp() {
		return app;
	}

	public void setApp(Application app) {
		this.app = app;
	}

	public ResourceBundle getApplicationBundle() {
		return applicationBundle;
	}

	public void setApplicationBundle(ResourceBundle applicationBundle) {
		this.applicationBundle = applicationBundle;
	}

	public ResourceBundle getTranslationBundle() {
		return translationsBundle;
	}

	public void setTranslationBundle(ResourceBundle translationsBundle) {
		this.translationsBundle = translationsBundle;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public ControllerMenu getMenuController() {
		return menuController;
	}

	public void setMenuController(ControllerMenu menuController) {
		this.menuController = menuController;
	}

	public String getText(String key) {
		return this.translationsBundle.getString(key);
	}

	public String getParam(String key) {
		return this.applicationBundle.getString(key);
	}

	public String responseErrorToString(Response response) {
		String responseError = "Call Failed with HTTP error code " + response.getStatus();
		if (response.hasEntity()) {
			String body = response.readEntity(String.class);
			// ENTITY {"timestamp":"09/10/2022 12:47:26","status":500,"errors":["User
			// password do not match"]}
			JsonReader jsonReader = Json.createReader(new StringReader(body));
			JsonObject jsonBody = jsonReader.readObject();
			JsonArray jsonErrors = jsonBody.getJsonArray("errors");
			if (jsonErrors != null) {
				List<String> errors = jsonErrors.stream().map(new Function<JsonValue, String>() {
					@Override
					public String apply(JsonValue v) {
						return v.toString();
					}

				}).collect(Collectors.toList());
				responseError += System.lineSeparator() + StringUtils.join(errors.iterator(), System.lineSeparator());
			}

			jsonReader.close();

		}
		return responseError;
	}

	/**
	 * REST client (jersey) with configurations: JSON (jackson) + SSL suport using
	 * shipmentsws KeyStore as trustStore
	 * 
	 * @return REST client
	 * @throws Exception
	 */
	public Client getWebClient() throws Exception {
		SSLContext sslcontext = SSLContext.getInstance("TLS");
		sslcontext.init(null, new TrustManager[] { new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

		} }, new java.security.SecureRandom());

		HostnameVerifier allowAll = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		/*
		 * ClientConfig clientConfig = new ClientConfig();
		 * clientConfig.register(JacksonFeature.class); return
		 * ClientBuilder.newClient(clientConfig);
		 */

		// probe the keystore file and load the keystore entries
		KeyStore trustStore = KeyStore.getInstance(
				new File(getClass().getClassLoader().getResource(this.getParam("client.ssl.trust-store")).toURI()),
				this.getParam("client.ssl.trust-store-password").toCharArray());

		return ClientBuilder.newBuilder().register(JacksonFeature.class) // JSON usually auto-discovered
				// .sslContext(sslcontext)
				.hostnameVerifier(allowAll).trustStore(trustStore).build();
	}

	public String usersDirectoryHtml(List<User> users, List<Pair<String, String>> filters) {
		// TODO html table from user's list filtered
		return "";
	}

	public void exportUsers(File file, SortedSet<User> toExport) throws Exception {
		// TODO export users to XML file
		return;
	}

	public SortedSet<User> importUsers(File file) throws Exception {
		// TODO import users from XML file
		return null;
	}
}
