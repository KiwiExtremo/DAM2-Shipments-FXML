package ins.marianao.shipments.fxml.services;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ins.marianao.shipments.fxml.manager.ResourceManager;
import ins.marianao.shipments.fxml.model.User;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class ServiceLogin extends Service<User> {

	private static final String PATH_AUTHENTICATE = "authenticate";

	private String username;
	private String password;

	public ServiceLogin(String username, String password) throws Exception {
		if (username == null || username.isBlank()) {
			throw new Exception(ResourceManager.getInstance().getText("error.login.find.no.username"));
		}
		if (password == null || password.isBlank()) {
			throw new Exception(ResourceManager.getInstance().getText("error.login.find.no.password"));
		}
		this.username = username;
		this.password = password;
	}

	@Override
	protected Task<User> createTask() {
		return new Task<User>() {
			@Override
			protected User call() throws Exception {
				Client client = ResourceManager.getInstance().getWebClient();

				WebTarget webTarget = client.target(ResourceManager.getInstance().getParam("web.service.host.url"))
						.path(ServiceQueryUsers.PATH_REST_USERS).path(PATH_AUTHENTICATE);

				Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

				// {"username":"alex","password":"1234"}
				JsonObject credentials = Json.createObjectBuilder().add("username", username).add("password", password)
						.build();

				User user = null;
				try {
					Response response = invocationBuilder.post(Entity.entity(credentials, MediaType.APPLICATION_JSON));

					if (response.getStatus() != Response.Status.OK.getStatusCode()) {
						throw new Exception(ResourceManager.getInstance().responseErrorToString(response));
					}
					user = response.readEntity(User.class);
				} catch (ResponseProcessingException e) {
					e.printStackTrace();
					throw new Exception(ResourceManager.getInstance().getText("error.service.response.processing") + " "
							+ e.getMessage());
				} catch (ProcessingException e) {
					e.printStackTrace();
					throw new Exception(
							ResourceManager.getInstance().getText("error.service.processing") + " " + e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
					throw e;
				}

				return user;
			}
		};
	}

}
