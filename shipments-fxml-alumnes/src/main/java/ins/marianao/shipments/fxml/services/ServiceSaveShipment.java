package ins.marianao.shipments.fxml.services;

import cat.institutmarianao.shipmentsws.model.Shipment;

public class ServiceSaveShipment extends ServiceSaveBase<Shipment>{

	private static final String PATH_INSERT_SHIPMENT = "save/shipment";
	
	public ServiceSaveShipment(Shipment entity) throws Exception {
        super(entity, Shipment.class, new String[] { ServiceQueryShipments.PATH_REST_SHIPMENTS
        		,PATH_INSERT_SHIPMENT }, Method.POST);
    }

}
