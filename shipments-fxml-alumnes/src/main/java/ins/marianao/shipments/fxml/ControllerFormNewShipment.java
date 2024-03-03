package ins.marianao.shipments.fxml;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.exception.ExceptionUtils;

import cat.institutmarianao.shipmentsws.model.Action;
import cat.institutmarianao.shipmentsws.model.Address;
import cat.institutmarianao.shipmentsws.model.Reception;
import cat.institutmarianao.shipmentsws.model.Shipment;
import ins.marianao.shipments.fxml.manager.ResourceManager;
import ins.marianao.shipments.fxml.services.ServiceSaveShipment;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class ControllerFormNewShipment implements Initializable {
	@FXML
	private BorderPane viewProfileForm;
	@FXML
	private Button bSave;
	@FXML
	private ComboBox<String> cmbCategory;
	@FXML
	private TextField tfSender;
	@FXML
	private TextField tfRecipient;
	@FXML
	private TextField tfWeight;
	@FXML
	private TextField tfHeight;
	@FXML
	private TextField tfWidth;
	@FXML
	private TextField tfLength;
	@FXML
	private CheckBox cbExpress;
	@FXML
	private CheckBox cbFragile;
	@FXML
	private TextArea taNote;
	private Address senderAddress,recipientAddress;
	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resource) {

		List<String> categories = Stream.of(Shipment.Category.values())
				.map(category ->  resource.getString("text.Category." + category.name()))
				.collect(Collectors.toList());

		cmbCategory.getItems().addAll(categories);

		setCmFormat(tfHeight);
		setCmFormat(tfLength);
		setCmFormat(tfWidth);

		setGFormat(tfWeight);

		tfSender.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				 Dialog<Address> dialog = new AddressDialog(new Address());
				 Optional<Address> result = dialog.showAndWait();

				 if (result.isPresent()) {
					 senderAddress = result.get();
					 tfSender.setText(senderAddress.getAddress().toString());
				 }		
			}
		});
		
		tfRecipient.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				 Dialog<Address> dialog = new AddressDialog(new Address());
				 Optional<Address> result = dialog.showAndWait();

				 if (result.isPresent()) {
				        recipientAddress = result.get();

				        tfRecipient.setText(recipientAddress.getAddress().toString());
				 }		
			}
		});
	}

	@FXML
	public void saveShipmentClick(ActionEvent event) {
		try {
			float floatHeight = (float) this.tfHeight.getTextFormatter().getValue();
			float floatLength = (float) this.tfLength.getTextFormatter().getValue();
			float floatWidth = (float) this.tfWidth.getTextFormatter().getValue();
			float floatWeigth = (float) this.tfWeight.getTextFormatter().getValue();

			String categoryName = this.cmbCategory.getValue();
			
			Shipment.Category category = Shipment.Category.valueOf(categoryName.toUpperCase());
			
			boolean booleanExpress = this.cbExpress.isSelected();
			boolean booleanFragile = this.cbFragile.isSelected();

			String stringComment = this.taNote.getText();
			
			Shipment shipment = new Shipment();
			
			shipment = Shipment.builder().id(0L)
					.sender(senderAddress)
					.recipient(recipientAddress)
					.height(floatHeight)
					.length(floatLength)
					.weight(floatWeigth)
					.width(floatWidth)
					.category(category)
					.fragile(booleanFragile)
					.express(booleanExpress)
					.note(stringComment)
					.tracking(createReception(shipment))
					.build();
			
			saveShipment(shipment);
			

		} catch (Exception e) {
			ControllerMenu.showError(e.getMessage(), e.getMessage(), ExceptionUtils.getStackTrace(e));
		}

	}
	
	private void saveShipment(Shipment shipment) throws Exception {
		final ServiceSaveShipment saveShipment = new ServiceSaveShipment(shipment);

		saveShipment.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent t) {
				
				ResourceManager.getInstance().getMenuController().shipmentsMenuClick(null);
			}
		});

		saveShipment.setOnFailed(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent t) {
				Throwable e = t.getSource().getException();

				ControllerMenu.showError(ResourceManager.getInstance().getText("error.formShipment.save.web.service"),
						e.getMessage(), ExceptionUtils.getStackTrace(e));
			}

		});

		saveShipment.start();
	}
	
	private List<Action> createReception(Shipment shipment){
		
		List<Action> actionList = new ArrayList<Action>();
		
		Reception reception = new Reception();
		//Esta línea no funciona, la he intentado arreglar pero no he podido.
		//reception = Reception.builder().id(0L).date(new Date()).performer(ResourceManager.getInstance().getCurrentUser()).shipment(shipment).trackingNumber(0).type(Action.Type.RECEPTION).build();
		
		actionList.add(reception);
		
		return actionList;
	}

	private void setCmFormat(TextField textField) {
	    Pattern pattern = Pattern.compile("\\d*");

	    UnaryOperator<TextFormatter.Change> filter = change -> {
	        if (pattern.matcher(change.getControlNewText()).matches()) {
	            return change;
	        } else {
	            return null;
	        }
	    };

	    TextFormatter<Float> textFormatter = new TextFormatter<>(new FloatStringConverter(), 0.0f);
	    textFormatter.valueProperty().addListener((obs, oldValue, newValue) -> {
	        if (newValue != null && !textField.getText().endsWith("cm")) {
	            textField.setText(newValue + "cm");
	        }
	    });

	    textField.setTextFormatter(new TextFormatter<>(filter));
	    textField.setTextFormatter(textFormatter);
	}

	private void setGFormat(TextField textField) {
	    Pattern pattern = Pattern.compile("\\d*");

	    UnaryOperator<TextFormatter.Change> filter = change -> {
	        if (pattern.matcher(change.getControlNewText()).matches()) {
	            return change;
	        } else {
	            return null;
	        }
	    };

	    TextFormatter<Float> textFormatter = new TextFormatter<>(new FloatStringConverter(), 0.0f);
	    textFormatter.valueProperty().addListener((obs, oldValue, newValue) -> {
	        if (newValue != null && !textField.getText().endsWith("g")) {
	            textField.setText(newValue + "g");
	        }
	    });

	    textField.setTextFormatter(new TextFormatter<>(filter));
	    textField.setTextFormatter(textFormatter);
	}
}
