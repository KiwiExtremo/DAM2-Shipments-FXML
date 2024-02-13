package ins.marianao.shipments.fxml.services;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ins.marianao.shipments.fxml.manager.ResourceManager;
import ins.marianao.shipments.fxml.model.User;
import ins.marianao.shipments.fxml.model.User.Role;

public class ServiceQueryUsers extends ServiceQueryBase<User> {

	public static final String PATH_REST_USERS = "users";


	private Role[] roles;
	private String fullName;

	public ServiceQueryUsers(Role[] roles, String fullName) {
		this.roles = roles;
		this.fullName = fullName;
	}

	@Override
	protected List<User> customCall() throws Exception {
		Client client = ResourceManager.getInstance().getWebClient();

		WebTarget webTarget = client.target(ResourceManager.getInstance().getParam("web.service.host.url"))
				.path(PATH_REST_USERS).path(PATH_QUERY_ALL);

		if (this.roles != null) {
			for (Role role : roles) {
				webTarget = webTarget.queryParam("roles", role.name());
			}
		}

		if (this.fullName != null && !this.fullName.isBlank()) {
			webTarget = webTarget.queryParam("fullName", fullName);
		}

		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

		List<User> users = new LinkedList<User>();
		try {
			Response response = invocationBuilder.get();

			if (response.getStatus() != Response.Status.OK.getStatusCode()) {
				throw new Exception(ResourceManager.getInstance().responseErrorToString(response));
			}

			users = response.readEntity(new GenericType<List<User>>() {
			});

		} catch (ResponseProcessingException e) {
			e.printStackTrace();
			throw new Exception(
					ResourceManager.getInstance().getText("error.service.response.processing") + " " + e.getMessage());
		} catch (ProcessingException e) {
			e.printStackTrace();
			throw new Exception(
					ResourceManager.getInstance().getText("error.service.processing") + " " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return users;
	}
}
