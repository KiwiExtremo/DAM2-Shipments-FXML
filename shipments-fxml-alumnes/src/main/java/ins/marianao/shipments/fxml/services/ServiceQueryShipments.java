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
import ins.marianao.shipments.fxml.model.Shipment;
import ins.marianao.shipments.fxml.model.Shipment.Category;
import ins.marianao.shipments.fxml.model.Shipment.Status;
import ins.marianao.shipments.fxml.model.User.Role;
import javafx.util.Pair;

public class ServiceQueryShipments extends ServiceQueryBase<Shipment> {

    public static final String PATH_REST_SHIPMENT = "shipments";

	private Category[] category;
    private Status[] status;
    private String fullDate;
    

   
	public ServiceQueryShipments(Category[] category, Status[] status, String fullDate) {
		super();
		this.category = category;
		this.status = status;
		this.fullDate = fullDate;
	}




	@Override
    protected List<Shipment> customCall() throws Exception {
        Client client = ResourceManager.getInstance().getWebClient();
        WebTarget webTarget = client.target(ResourceManager.getInstance().getParam("web.service.host.url"))
                                    .path(PATH_REST_SHIPMENT)
                                    .path(PATH_QUERY_ALL);
        if (this.status != null) {
			for (Status state : status) {
				webTarget = webTarget.queryParam("status", state.name());
			}
		}
        
        if (this.category != null) {
			for (Category categ : category) {
				webTarget = webTarget.queryParam("category", categ.name());
			}
		}
        
        if (this.fullDate != null && !this.fullDate.isBlank()) {
			webTarget = webTarget.queryParam("date", fullDate);
		}


        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        List<Shipment> shipments = new LinkedList<Shipment>();
        
        try {
            Response response = invocationBuilder.get();

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                throw new Exception(ResourceManager.getInstance().responseErrorToString(response));
            }

            shipments = response.readEntity(new GenericType<List<Shipment>>() {});

        } catch (ResponseProcessingException e) {
            e.printStackTrace();
            throw new Exception(ResourceManager.getInstance().getText("error.service.response.processing") + " " + e.getMessage());
        } catch (ProcessingException e) {
            e.printStackTrace();
            throw new Exception(ResourceManager.getInstance().getText("error.service.processing") + " " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return shipments;
    }
    
    
}
