package ins.marianao.shipments.fxml;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
//import java.util.LinkedList;
//import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

import ins.marianao.shipments.fxml.manager.ResourceManager;
import ins.marianao.shipments.fxml.model.Action;
import ins.marianao.shipments.fxml.model.Address;
//import ins.marianao.shipments.fxml.model.LogisticsManager;
import ins.marianao.shipments.fxml.model.Shipment;
import ins.marianao.shipments.fxml.model.User;
import ins.marianao.shipments.fxml.model.User.Role;
//import ins.marianao.shipments.fxml.services.ServiceDeleteUser;
import ins.marianao.shipments.fxml.services.ServiceQueryShipments;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.Pair;

public class ControllerUsersShipmentsAll extends AbstractControllerPDF {
	@FXML
	private BorderPane viewUsersDirectory;
	@FXML
	private TextField txtFullnameSearch;
	@FXML
	private ComboBox<Pair<String, String>> cmbStatus;
	@FXML
	private ComboBox<Pair<String, String>> cmbCategory;
	@FXML
	private TableView<Shipment> shipmentsTable;

	@FXML
	private TableColumn<Shipment, Number> colId;
	@FXML
	private TableColumn<Shipment, String> colCateg;
	@FXML
	private TableColumn<Shipment, String> colDate;
	@FXML
	private TableColumn<Shipment, Address> colSender;
	@FXML
	private TableColumn<Shipment, Address> colRecipient;
	@FXML
	private TableColumn<Shipment, String> colSize;
	@FXML
	private TableColumn<Shipment, Integer> colWeight;
	@FXML
	private TableColumn<Shipment, Integer> colHeight;
	@FXML
	private TableColumn<Shipment, String> colWidth;
	@FXML
	private TableColumn<Shipment, String> colLength;
	@FXML
	private TableColumn<Shipment, Boolean> colExpress;
	@FXML
	private TableColumn<Shipment, Boolean> colFragile;
	@FXML
	private TableColumn<Shipment, String> colStatus;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle resource) {

		super.initialize(url, resource);

//		this.txtFullnameSearch.textProperty().addListener(new ChangeListener<String>() {
//			@Override
//			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//				reloadShipments();
//			}
//		});

		
		setCombos(resource);

		//setComboCategory(resource);

		this.colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		this.colId.setMinWidth(10);
		this.colId.setMaxWidth(20);
		
		this.colCateg.setCellValueFactory(new PropertyValueFactory<Shipment, String>("category"));
		this.colId.setMinWidth(50);
		
		this.colDate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Shipment, String>, ObservableValue<String>>(){
				@Override
				public ObservableValue<String> call(TableColumn.CellDataFeatures<Shipment, String> cellData){
					List<Action> traking = cellData.getValue().getTracking();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					return new SimpleStringProperty(sdf.format(traking.get(traking.size()-1).getDate()));
				}
		});
		this.colDate.setMinWidth(100);
		
		this.colSender.setCellValueFactory(new PropertyValueFactory<Shipment, Address>("sender"));
		this.colSender.setCellFactory(TextFieldTableCell.forTableColumn(Formatters.getSenderConverter()));
		this.colSender.setMinWidth(100);
		
		this.colRecipient.setCellValueFactory(new PropertyValueFactory<Shipment, Address>("recipient"));
		this.colRecipient.setCellFactory(TextFieldTableCell.forTableColumn(Formatters.getRecipientConverter()));
		this.colRecipient.setMinWidth(100);

		this.colWeight.setCellValueFactory(new PropertyValueFactory<Shipment, Integer>("weight"));
		this.colWeight.setMinWidth(15);

		
		this.colSize.setCellValueFactory(new PropertyValueFactory<Shipment, String>("dimensions"));
		this.colSize.setMinWidth(75);
		
		this.colExpress.setCellValueFactory(new PropertyValueFactory<Shipment, Boolean>("express"));
		this.colExpress.setMinWidth(15);
		
		this.colFragile.setCellValueFactory(new PropertyValueFactory<Shipment, Boolean>("fragile"));
		this.colFragile.setMinWidth(15);
		
		this.colStatus.setCellValueFactory(new PropertyValueFactory<Shipment, String>("status"));
		this.colStatus.setMinWidth(75);
		this.reloadShipments();
		// HAY QUE MODIFICAR VISTA SEGUN USUARIO

	}

	private void setCombos(ResourceBundle resource) {
		List<Pair<String, String>> status = Arrays.stream(Shipment.Status.values()).map(s -> {
			String key = s.name();
			return new Pair<>(key, resource.getString("text.Status." + key));
		}).collect(Collectors.toList());

		ObservableList<Pair<String, String>> listStatus = FXCollections.observableArrayList(status);
//			listStatus.add(0, null);

		this.cmbStatus.setItems(listStatus);
		this.cmbStatus.setConverter(Formatters.getStringPairConverter("Status"));

		this.cmbStatus.valueProperty().addListener((observable, oldValue, newValue) -> {
			reloadShipments();
		});
		List<Pair<String, String>> category = Arrays.stream(Shipment.Category.values()).map(c -> {
			String key = c.name();
			return new Pair<>(key, resource.getString("text.Category." + key));
		}).collect(Collectors.toList());

		ObservableList<Pair<String, String>> listCategory = FXCollections.observableArrayList(category);

		this.cmbCategory.setItems(listCategory);
		this.cmbCategory.setConverter(Formatters.getStringPairConverter("Category"));

		this.cmbCategory.valueProperty().addListener((observable, oldValue, newValue) -> {
			reloadShipments();
		});
	}


	private void reloadShipments() {
		this.shipmentsTable.setEditable(false);
		Pair<String, String> status = this.cmbStatus.getValue();
		Pair<String, String> category = this.cmbCategory.getValue();


		final ServiceQueryShipments queryShipments = new ServiceQueryShipments(status, category);

		queryShipments.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				shipmentsTable.setEditable(true);
				List<Shipment> shipments = queryShipments.getValue();
				
//				for (Shipment shipment : shipments) {
//					Shipment shipmentWithUpdatedDate = shipment;
//					// get last date
//					shipmentWithUpdatedDate.getReceptionDate();
//					
//					shipment = shipmentWithUpdatedDate;
//				}
				
				populateTable(shipments);
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

	// @Override
	// protected String htmlContentToPDF() {
	// List<Shipment> shipments = this.shipmentsTable.getItems();
	// String search = this.txtFullnameSearch.getText();
	//
	// List<Pair<String, String>> filters = new LinkedList<>();
	// if (search != null && !search.isBlank()) {
	// filters.add(new
	// Pair<>(ResourceManager.getInstance().getText("fxml.text.viewShipments.search.prompt"),
	// "\"" + search + "\""));
	// }
	//
	// return "<pre>" + ResourceManager.getInstance().shipmentsHtml(shipments,
	// filters) + "</pre>";
	// }

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

	// @Override
	// protected String htmlContentToPDF() throws Exception {
	// List<Shipment> shimpent = this.shipmentsTable.getItems();
	// Pair<String, String> role = this.cmbRole.getValue();
	// String search = this.txtFullnameSearch.getText();
	//
	// List<Pair<String, String>> filters = new LinkedList<>();
	// if (role != null) {
	// filters.add(new
	// Pair<>(ResourceManager.getInstance().getText("fxml.text.viewUsers.role"),
	// role.getValue()));
	// }
	// if (search != null && !search.isBlank()) {
	// filters.add(new
	// Pair<>(ResourceManager.getInstance().getText("fxml.text.viewUsers.search.prompt"),
	// "\"" + search + "\""));
	// }
	//
	// return "<pre>" + ResourceManager.getInstance().usersDirectoryHtml(users,
	// filters) + "</pre>";
	// }
}
