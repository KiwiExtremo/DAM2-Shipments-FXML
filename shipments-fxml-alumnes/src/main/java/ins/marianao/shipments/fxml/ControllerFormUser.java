package ins.marianao.shipments.fxml;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.exception.ExceptionUtils;

import ins.marianao.shipments.fxml.manager.ResourceManager;
import ins.marianao.shipments.fxml.model.Company;
import ins.marianao.shipments.fxml.model.Courier;
import ins.marianao.shipments.fxml.model.LogisticsManager;
import ins.marianao.shipments.fxml.model.Office;
import ins.marianao.shipments.fxml.model.Receptionist;
import ins.marianao.shipments.fxml.model.User;
import ins.marianao.shipments.fxml.model.User.Long;
import ins.marianao.shipments.fxml.services.ServiceQueryCompanies;
import ins.marianao.shipments.fxml.services.ServiceQueryOffices;
import ins.marianao.shipments.fxml.services.ServiceSaveUser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Pair;

public class ControllerFormUser implements Initializable, ChangeListener<Pair<String, String>> {
	@FXML
	private BorderPane viewProfileForm;
	@FXML
	private HBox boxReceptionists;
	@FXML
	private HBox boxCouriers;
	@FXML
	private Button btnSave;
	@FXML
	private ComboBox<Pair<String, String>> cmbRole;
	@FXML
	private TextField txtUsername;
	@FXML
	private PasswordField txtPassword;
	@FXML
	private PasswordField txtConfirm;
	@FXML
	private TextField txtFullname;
	@FXML
	private TextField txtExtension;
	@FXML
	private ComboBox<Office> cmbOffice;
	@FXML
	private TextField txtPlace;
	@FXML
	private ComboBox<Company> cmbCompany;

	private boolean edicio;

	protected Company selectedCompany;

	protected Office selectedOffice;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle resource) {
		this.edicio = false;

		this.loadOffices(cmbOffice);
		this.loadCompanies(cmbCompany);

		// this.lblUsuari.setText("\u2386");
		// this.lblNom.setText("\u1F604");
		// this.lblExtensio.setText("\u2706");

		this.txtExtension.setTextFormatter(new TextFormatter<Integer>(Formatters.getExtensioFormatter(), 0));

		List<Pair<String, String>> roles = Stream.of(User.Long.values())
				.map(new Function<Long, Pair<String, String>>() {
					@Override
					public Pair<String, String> apply(Long t) {
						String key = t.name();
						return new Pair<String, String>(key, resource.getString("text.User." + key));
					}

				}).collect(Collectors.toList());

		this.cmbRole.setItems(FXCollections.observableArrayList(roles));
		this.cmbRole.setConverter(Formatters.getStringPairConverter("User"));

		User user = ResourceManager.getInstance().getCurrentUser();

		if (user == null) {

			this.cmbRole.getItems().add(0, null);
		} else {
			this.txtUsername.setText(user.getUsername());
			this.txtFullname.setText(user.getFullName());
			this.txtExtension.setText(user.getExtension() + "");

			String key = user.getRole().name();
			this.cmbRole.setValue(new Pair<String, String>(key, resource.getString("text.User." + key)));

			if (user instanceof Courier) {
				Courier courier = (Courier) user;
				Company company = courier.getCompany();

				this.cmbCompany.setValue(company);

				this.enableCourierFields();
			} else {
				Receptionist receptionist = (Receptionist) user;
				Office office = receptionist.getOffice();

				this.cmbOffice.setValue(office);

				LinkedList<Office> officeList = new LinkedList<>();
				officeList.add(office);
				
				this.cmbOffice.setConverter(Formatters.getOfficeConverter(officeList));

				this.txtPlace.setText(receptionist.getPlace());

				this.enableReceptionistFields();
			}
		}
	}

	public void enableEdition() {
		this.edicio = true;

		String key = User.Long.COURIER.name();
		this.cmbRole.setValue(new Pair<String, String>(key, ResourceManager.getInstance().getText("text.User." + key)));

		this.txtUsername.clear();
		this.txtFullname.clear();
		this.txtExtension.clear();
		this.txtPlace.clear();

		this.enableCourierFields();

		this.txtUsername.setDisable(false);
		this.txtUsername.setEditable(true);
		this.cmbRole.setDisable(false);
		this.cmbRole.valueProperty().addListener(this);
	}

	private void enableCourierFields() {
		this.boxCouriers.toFront();
		this.boxReceptionists.toBack();

		// Reset Receptionist fields
		this.txtPlace.clear();
		if (this.cmbOffice.getItems().isEmpty()) {
			this.cmbOffice.setValue(null);
		} else {
			this.cmbOffice.setValue(this.cmbOffice.getItems().get(0));
		}

		this.cmbCompany.setVisible(true);
	}

	private void enableReceptionistFields() {
		this.boxReceptionists.toFront();
		this.boxCouriers.toBack();

		// Reset Courier fields
		if (this.cmbCompany.getItems().isEmpty()) {
			this.cmbCompany.setValue(null);
		} else {
			this.cmbCompany.setValue(this.cmbCompany.getItems().get(0));
		}

		this.cmbCompany.setVisible(false);
	}

	@Override
	public void changed(ObservableValue<? extends Pair<String, String>> observable, Pair<String, String> oldValue,
			Pair<String, String> newValue) {
		if (observable.equals(this.cmbRole.valueProperty())) {
			if (User.Long.COURIER.name().equals(newValue.getKey())) {
				// Couriers
				this.enableCourierFields();
			} else {
				// Receptionists or LogisticsManagers
				this.enableReceptionistFields();
			}
		}
	}

	@FXML
	public void saveProfileClick(ActionEvent event) {
		try {
			Pair<String, String> role = this.cmbRole.getValue();

			String strUsername = this.txtUsername.getText(); /* Disable edition */
			String strFullName = this.txtFullname.getText();
			int numExt = (int) this.txtExtension.getTextFormatter().getValue();
			Office office = this.cmbOffice.getValue();
			String strPlace = this.txtPlace.getText();
			Company company = this.cmbCompany.getValue();

			String password = this.txtPassword.getText();
			String confirm = this.txtConfirm.getText();
			if (password != null && !password.equals(confirm)) {
				throw new Exception(ResourceManager.getInstance().getText("error.formUser.password.check"));
			}

			User user = ResourceManager.getInstance().getCurrentUser();
			if (user != null && !this.edicio) {

				user.setFullName(strFullName);
				user.setExtension(numExt);
				if (password != null) {
					user.setPassword(password);
				}
				if (user instanceof Courier) {
					Courier courier = (Courier) user;
					courier.setCompany(company);
				} else {
					Receptionist receptionist = (Receptionist) user;
					receptionist.setOffice(office);
					receptionist.setPlace(strPlace);
				}

				saveUserProfile(user, false);
			} else {

				if (User.Long.COURIER.name().equals(role.getKey())) {
					user = Courier.builder().username(strUsername).role(User.Long.COURIER).password(password)
							.fullName(strFullName).extension(numExt).company(company).build();
				}

				if (User.Long.RECEPTIONIST.name().equals(role.getKey())) {
					user = Receptionist.builder().username(strUsername).role(User.Long.RECEPTIONIST).password(password)
							.fullName(strFullName).extension(numExt).office(office).place(strPlace).build();
				}

				if (User.Long.LOGISTICS_MANAGER.name().equals(role.getKey())) {
					user = LogisticsManager.builder().username(strUsername).role(User.Long.RECEPTIONIST)
							.password(password).fullName(strFullName).extension(numExt).office(office).place(strPlace)
							.build();
				}

				saveUserProfile(user, true);
			}
		} catch (Exception e) {
			ControllerMenu.showError(e.getMessage(), e.getMessage(), ExceptionUtils.getStackTrace(e));
		}

	}

	private void saveUserProfile(User user, boolean insert) throws Exception {
		final ServiceSaveUser saveUser = new ServiceSaveUser(user, insert);

		saveUser.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent t) {

				User user = saveUser.getValue();

				if (!(user instanceof Courier)) {
					Office office;

					if (user instanceof LogisticsManager) {
						LogisticsManager logManager = (LogisticsManager) user;
						office = logManager.getOffice();
					} else {
						Receptionist receptionist = (Receptionist) user;
						office = receptionist.getOffice();
					}

					if (office != null && !cmbOffice.getItems().contains(office)) { // New Office
						cmbOffice.getItems().add(office);
						// cmbOffice.setConverter(Formatters.getRoomConverter(cmbOffice.getItems()));
					}
				}

				if (insert) {
					ResourceManager.getInstance().getMenuController().usersDirectoryMenuClick(null);
				} else {
					// Update current user
					ResourceManager.getInstance().setCurrentUser(user);

					txtPassword.clear();
					txtConfirm.clear();

					ControllerMenu.showInfo(ResourceManager.getInstance().getText("fxml.text.formUser.update.title"),
							ResourceManager.getInstance().getText("fxml.text.formUser.update.text"));
				}
			}
		});

		saveUser.setOnFailed(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent t) {
				Throwable e = t.getSource().getException();

				ControllerMenu.showError(ResourceManager.getInstance().getText("error.formUser.save.web.service"),
						e.getMessage(), ExceptionUtils.getStackTrace(e));
			}

		});

		saveUser.start();
	}

	protected void loadOffices(ComboBox<Office> combo) {
		final ServiceQueryOffices queryOffices = new ServiceQueryOffices();

		queryOffices.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent t) {
				combo.setVisible(false);
				
				combo.setEditable(true);

				combo.getItems().clear();

				ObservableList<Office> offices = FXCollections.observableArrayList(queryOffices.getValue());

				Office office = combo.getSelectionModel().getSelectedItem();

				combo.setItems(offices);

				combo.setValue(office);

				combo.setConverter(Formatters.getOfficeConverter(offices));

				combo.setVisible(true);

			}
		});

		queryOffices.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				combo.setEditable(true);

				Throwable e = t.getSource().getException();

				ControllerMenu.showError(ResourceManager.getInstance().getText("error.viewOffices.web.service"),
						e.getMessage(), ExceptionUtils.getStackTrace(e));
			}
		});

		queryOffices.start();
	}

	protected void loadCompanies(ComboBox<Company> combo) {

		final ServiceQueryCompanies queryCompanies = new ServiceQueryCompanies();

		queryCompanies.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent t) {
				combo.setEditable(true);

				combo.getItems().clear();

				ObservableList<Company> companies = FXCollections.observableArrayList(queryCompanies.getValue());

				Company company = combo.getSelectionModel().getSelectedItem();

				combo.setItems(companies);

				combo.setValue(company);

				combo.setConverter(Formatters.getCompanyConverter(companies));

			}
		});

		queryCompanies.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				combo.setEditable(true);

				Throwable e = t.getSource().getException();

				ControllerMenu.showError(ResourceManager.getInstance().getText("error.viewCompanies.web.service"),
						e.getMessage(), ExceptionUtils.getStackTrace(e));
			}
		});

		queryCompanies.start();
	}
}
