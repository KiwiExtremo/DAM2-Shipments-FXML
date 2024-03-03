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
import ins.marianao.shipments.fxml.model.Company;

public class ServiceQueryCompanies extends ServiceQueryBase<Company> {

	public static final String PATH_REST_COMPANIES = "companies";

	@Override
	protected List<Company> customCall() throws Exception {
		Client client = ResourceManager.getInstance().getWebClient();

		WebTarget webTarget = client.target(ResourceManager.getInstance().getParam("web.service.host.url"))
				.path(PATH_REST_COMPANIES).path(PATH_QUERY_ALL);

		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

		List<Company> companies = new LinkedList<Company>();
		try {
			Response response = invocationBuilder.get();

			if (response.getStatus() != Response.Status.OK.getStatusCode()) {
				throw new Exception(ResourceManager.getInstance().responseErrorToString(response));
			}

			companies = response.readEntity(new GenericType<List<Company>>() {
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

		return companies;
	}
}
