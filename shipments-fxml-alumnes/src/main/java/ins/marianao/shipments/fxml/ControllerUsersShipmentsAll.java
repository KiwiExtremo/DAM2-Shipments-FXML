package ins.marianao.shipments.fxml;

import java.net.URL;
import java.util.List;
//import java.util.LinkedList;
//import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

import ins.marianao.shipments.fxml.manager.ResourceManager;
//import ins.marianao.shipments.fxml.model.LogisticsManager;
import ins.marianao.shipments.fxml.model.Shipment;

//import ins.marianao.shipments.fxml.services.ServiceDeleteUser;
import ins.marianao.shipments.fxml.services.ServiceQueryShipments;
//import ins.marianao.shipments.fxml.services.ServiceSaveUser;
//import javafx.beans.property.SimpleBooleanProperty;
//import javafx.beans.property.SimpleIntegerProperty;
//import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;



public class ControllerUsersShipmentsAll extends AbstractControllerPDF {
	@FXML
	private BorderPane viewUsersDirectory;

	@FXML
	private TextField txtFullnameSearch;

	@FXML
	private TableView<Shipment> shipmentsTable;
	@FXML
	private TableColumn<Shipment, Number> colId;
	@FXML
	private TableColumn<Shipment, String> colSender;
	@FXML
	private TableColumn<Shipment, String> colRecipient;
	@FXML
	private TableColumn<Shipment, Integer> colWeight;
	@FXML
	private TableColumn<Shipment, Integer> colHeigth;
	@FXML
	private TableColumn<Shipment, String> colWhidth;
	@FXML
	private TableColumn<Shipment, String> colLength;
	@FXML
	private TableColumn<Shipment, Boolean> colExpress;
	@FXML
	private TableColumn<Shipment, Boolean> colFragile;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle resource) {

		super.initialize(url, resource);

		this.txtFullnameSearch.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				reloadShipments();
			}
		});
		this.colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		this.colSender.setCellValueFactory(new PropertyValueFactory<Shipment, String>("sender"));
		this.colRecipient.setCellValueFactory(new PropertyValueFactory<Shipment, String>("recipient"));
		this.colWeight.setCellValueFactory(new PropertyValueFactory<Shipment, Integer>("weight"));
		this.colHeigth.setCellValueFactory(new PropertyValueFactory<Shipment, Integer>("height"));
		this.colWhidth.setCellValueFactory(new PropertyValueFactory<Shipment, String>("width"));
		this.colLength.setCellValueFactory(new PropertyValueFactory<Shipment, String>("length"));
		this.colExpress.setCellValueFactory(new PropertyValueFactory<Shipment, Boolean>("express"));
		this.colFragile.setCellValueFactory(new PropertyValueFactory<Shipment, Boolean>("fragile"));

		this.reloadShipments();
		//HAY QUE MODIFICAR VISTA SEGUN USUARIO

		//        this.shipmentsTable.setEditable(true);
		//        this.shipmentsTable.getSelectionModel().setCellSelectionEnabled(true);
		//        this.shipmentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		//
		//        this.colId.setMinWidth(40);
		//        this.colId.setMaxWidth(60);
		//        this.colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		//
		//        this.colSender.setMinWidth(100);
		//        this.colSender.setMaxWidth(160);
		//        this.colSender.setCellValueFactory(new PropertyValueFactory<Shipment, String>("sender"));
		//        this.colSender.setCellFactory(TextFieldTableCell.forTableColumn());
		//        this.colSender.setEditable(false);
		//
		//        this.colRecipient.setMinWidth(200);
		//        this.colRecipient.setMaxWidth(260);
		//        this.colRecipient.setCellValueFactory(new PropertyValueFactory<Shipment, String>("recipient"));
		//        this.colRecipient.setCellFactory(TextFieldTableCell.forTableColumn());
		//        this.colRecipient.setEditable(false);
		//        
		//        this.colWeight.setMinWidth(50);
		//        this.colWeight.setMaxWidth(50);
		//        this.colWeight.setCellValueFactory(new PropertyValueFactory<Shipment, Integer>("weight"));
		//        this.colWeight.setEditable(false);
		//
		//        this.colHeigth.setMinWidth(50);
		//        this.colHeigth.setMaxWidth(50);
		//        this.colHeigth.setCellValueFactory(new PropertyValueFactory<Shipment, Integer>("height"));
		//        this.colHeigth.setEditable(false);
		//        
		//        this.colLength.setMinWidth(50);
		//        this.colLength.setMaxWidth(50);
		//        this.colLength.setCellValueFactory(new PropertyValueFactory<Shipment, String>("length"));
		//        this.colLength.setCellFactory(TextFieldTableCell.forTableColumn());
		//        this.colLength.setEditable(false);
		//        
		//        this.colExpress.setMinWidth(50);
		//        this.colExpress.setMaxWidth(50);
		//        this.colExpress.setCellValueFactory(new PropertyValueFactory<Shipment, Boolean>("express"));
		//
		//        this.colFragile.setMinWidth(50);
		//        this.colFragile.setMaxWidth(50);
		//        this.colFragile.setCellValueFactory(new PropertyValueFactory<Shipment, Boolean>("fragile"));

		//        User user = ResourceManager.getInstance().getCurrentUser();

		//        if (!(user instanceof LogisticsManager)) {
		//            this.colFragile.setVisible(false);
		//            this.colFragile.setMinWidth(0);
		//            this.colFragile.setMaxWidth(0);
		//        } else {
		//            this.colFragile.setMinWidth(50);
		//            this.colFragile.setMaxWidth(70);
		//            this.colFragile.setCellValueFactory(
		//                    new Callback<TableColumn.CellDataFeatures<Shipment, Boolean>, ObservableValue<Boolean>>() {
		//                        @Override
		//                        public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Shipment, Boolean> cell) {
		//                            return new SimpleBooleanProperty(false);
		//                        }
		//                    });
		//
		//            this.colFragile.setCellFactory(new ColumnButton<Shipment, Boolean>(
		//                    ResourceManager.getInstance().getText("fxml.text.viewShipments.col.fragile"),
		//                    new Image(getClass().getResourceAsStream("resources/recycle-bin.png"))) {
		//                @Override
		//                public void buttonAction(Shipment shipment) {
		//                    try {
		//                        boolean result = ControllerMenu.showConfirm(
		//                                ResourceManager.getInstance().getText("fxml.text.viewShipments.fragile.title"),
		//                                ResourceManager.getInstance().getText("fxml.text.viewShipments.fragile.text"));
		////                        if (result) {
		////                            deleteShipment(shipment);
		////                        }
		//                    } catch (Exception e) {
		//                        ControllerMenu.showError(ResourceManager.getInstance().getText("error.viewShipments.delete"),
		//                                e.getMessage(), ExceptionUtils.getStackTrace(e));
		//                    }
		//                }
		//            });
		//        }
	}

	//    private void deleteShipment(Shipment shipment) throws Exception {
	//        final ServiceDeleteShipment deleteShipment = new ServiceDeleteShipment(shipment);
	//
	//        deleteShipment.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
	//            @Override
	//            public void handle(WorkerStateEvent t) {
	//                reloadShipments();
	//            }
	//        });
	//
	//        deleteShipment.setOnFailed(new EventHandler<WorkerStateEvent>() {
	//            @Override
	//            public void handle(WorkerStateEvent t) {
	//                Throwable e = t.getSource().getException();
	//
	//                ControllerMenu.showError(ResourceManager.getInstance().getText("error.viewShipments.delete.web.service"),
	//                        e.getMessage(), ExceptionUtils.getStackTrace(e));
	//            }
	//        });
	//
	//        deleteShipment.start();
	//    }

	//    private void saveShipment(Shipment shipment, boolean insert) throws Exception {
	//        final ServiceSaveShipment saveShipment = new ServiceSaveShipment(shipment, insert);
	//
	//        saveShipment.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
	//            @Override
	//            public void handle(WorkerStateEvent t) {
	//                // Refresh table or do other actions after successful save
	//            }
	//        });
	//
	//        saveShipment.setOnFailed(new EventHandler<WorkerStateEvent>() {
	//            @Override
	//            public void handle(WorkerStateEvent t) {
	//                Throwable e = t.getSource().getException();
	//
	//                ControllerMenu.showError(ResourceManager.getInstance().getText("error.viewShipments.save.web.service"),
	//                        e.getMessage(), ExceptionUtils.getStackTrace(e));
	//            }
	//        });
	//
	//        saveShipment.start();
	//    }

	private void reloadShipments() {
		this.shipmentsTable.setEditable(false);

		final ServiceQueryShipments queryShipments = new ServiceQueryShipments();

		queryShipments.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				shipmentsTable.setEditable(true);
				List<Shipment> shipments = queryShipments.getValue();
				populateTable(shipments);
				System.out.println(shipments.size());
			}


		});

		queryShipments.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				shipmentsTable.setEditable(true);

				Throwable e = t.getSource().getException();

				ControllerMenu.showError(ResourceManager.getInstance().getText("error.viewShipments.web.service"),
						e.getMessage(), ExceptionUtils.getStackTrace(e));
			}
		});

		queryShipments.start();
	}

	private void populateTable(List<Shipment> shipments) {
		shipmentsTable.getItems().clear();
		ObservableList<Shipment> shipmentList = FXCollections.observableArrayList(shipments);
		shipmentsTable.setItems(shipmentList);
	}

	//    @Override
	//    protected String htmlContentToPDF() {
	//        List<Shipment> shipments = this.shipmentsTable.getItems();
	//        String search = this.txtFullnameSearch.getText();
	//
	//        List<Pair<String, String>> filters = new LinkedList<>();
	//        if (search != null && !search.isBlank()) {
	//            filters.add(new Pair<>(ResourceManager.getInstance().getText("fxml.text.viewShipments.search.prompt"),
	//                    "\"" + search + "\""));
	//        }
	//
	//        return "<pre>" + ResourceManager.getInstance().shipmentsHtml(shipments, filters) + "</pre>";
	//    }

	@Override
	protected String documentTitle() {
		return ResourceManager.getInstance().getText("fxml.text.viewShipments.report.title");
	}

	@Override
	protected String documentFileName() {
		return ResourceManager.FILE_REPORT_SHIPMENTS;
	}

	@Override
	protected String htmlContentToPDF() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	//	@Override
	//	protected String htmlContentToPDF() throws Exception {
	//		List<Shipment> shimpent = this.shipmentsTable.getItems();
	//		Pair<String, String> role = this.cmbRole.getValue();
	//		String search = this.txtFullnameSearch.getText();
	//
	//		List<Pair<String, String>> filters = new LinkedList<>();
	//		if (role != null) {
	//			filters.add(new Pair<>(ResourceManager.getInstance().getText("fxml.text.viewUsers.role"), role.getValue()));
	//		}
	//		if (search != null && !search.isBlank()) {
	//			filters.add(new Pair<>(ResourceManager.getInstance().getText("fxml.text.viewUsers.search.prompt"),
	//					"\"" + search + "\""));
	//		}
	//
	//		return "<pre>" + ResourceManager.getInstance().usersDirectoryHtml(users, filters) + "</pre>";
	//	}
}
