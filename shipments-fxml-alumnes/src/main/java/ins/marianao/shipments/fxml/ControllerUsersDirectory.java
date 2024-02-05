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
import ins.marianao.shipments.fxml.model.LogisticsManager;
import ins.marianao.shipments.fxml.model.User;
import ins.marianao.shipments.fxml.model.User.Long;
import ins.marianao.shipments.fxml.services.ServiceDeleteUser;
import ins.marianao.shipments.fxml.services.ServiceQueryUsers;
import ins.marianao.shipments.fxml.services.ServiceSaveUser;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.Pair;

public class ControllerUsersDirectory extends AbstractControllerPDF {
	@FXML
	private BorderPane viewUsersDirectory;

	@FXML
	private ComboBox<Pair<String, String>> cmbRole;
	@FXML
	private TextField txtFullnameSearch;

	@FXML
	private TableView<User> usersTable;
	@FXML
	private TableColumn<User, Number> colIndex;
	@FXML
	private TableColumn<User, String> colRole;
	@FXML
	private TableColumn<User, String> colUsername;
	@FXML
	private TableColumn<User, String> colFullname;
	@FXML
	private TableColumn<User, Integer> colExtension;
	@FXML
	private TableColumn<User, String> colLocalion;
	@FXML
	private TableColumn<User, Boolean> colDelete;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle resource) {

		super.initialize(url, resource);

		this.txtFullnameSearch.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				reloadUsers();
			}
		});

		List<Pair<String, String>> roles = Stream.of(User.Long.values())
				.map(new Function<Long, Pair<String, String>>() {
					@Override
					public Pair<String, String> apply(Long t) {
						String key = t.name();
						return new Pair<String, String>(key, resource.getString("text.User." + key));
					}

				}).collect(Collectors.toList());

		ObservableList<Pair<String, String>> listRoles = FXCollections.observableArrayList(roles);
		listRoles.add(0, null);
		// listTipus.addAll(Stream.of(User.Role.values()).map(Enum::toString).collect(Collectors.toList()));

		this.cmbRole.setItems(listRoles);
		this.cmbRole.setConverter(Formatters.getStringPairConverter("User"));

		this.cmbRole.valueProperty().addListener(new ChangeListener<Pair<String, String>>() {
			@Override
			public void changed(ObservableValue<? extends Pair<String, String>> observable,
					Pair<String, String> oldValue, Pair<String, String> newValue) {
				reloadUsers();
			}
		});

		this.reloadUsers();

		this.usersTable.setEditable(true);
		this.usersTable.getSelectionModel().setCellSelectionEnabled(true);
		this.usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		this.colIndex.setMinWidth(40);
		this.colIndex.setMaxWidth(60);
		this.colIndex.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<User, Number>, ObservableValue<Number>>() {
					@Override
					public ObservableValue<Number> call(TableColumn.CellDataFeatures<User, Number> usuari) {
						return new SimpleLongProperty(usersTable.getItems().indexOf(usuari.getValue()) + 1);
					}
				});

		this.colRole.setMinWidth(140);
		this.colRole.setMaxWidth(200);
		// this.colRol.setCellValueFactory(new
		// PropertyValueFactory<User,String>("role"));
		// this.colRol.setCellValueFactory(cellData -> new
		// SimpleStringProperty(cellData.getValue().getRole().toString()));
		this.colRole.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<User, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(TableColumn.CellDataFeatures<User, String> cellData) {
						String key = cellData.getValue().getRole().toString();
						return new SimpleStringProperty(resource.getString("text.User." + key));
					}
				});

		this.colRole.setCellFactory(TextFieldTableCell.forTableColumn());
		this.colRole.setEditable(false);

		this.colUsername.setMinWidth(100);
		this.colUsername.setMaxWidth(160);
		this.colUsername.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
		this.colUsername.setCellFactory(TextFieldTableCell.forTableColumn());
		this.colUsername.setEditable(false);

		this.colFullname.setMinWidth(200);
		this.colFullname.setMaxWidth(260);
		this.colFullname.setCellValueFactory(new PropertyValueFactory<User, String>("fullName"));
		this.colFullname.setCellFactory(TextFieldTableCell.forTableColumn());

		this.colFullname.setOnEditCommit(new EventHandler<CellEditEvent<User, String>>() { // Edició
			@Override
			public void handle(CellEditEvent<User, String> t) {
				try {
					// Usuari client =
					// SistemaGestio.getInstance().getClientByIndex(t.getTablePosition().getRow());
					User usuari = t.getRowValue();
					usuari.setFullName(t.getNewValue());

					saveUsuari(usuari, false);

				} catch (Exception e) {
					ControllerMenu.showError(ResourceManager.getInstance().getText("error.viewUsers.fullName"),
							e.getMessage(), ExceptionUtils.getStackTrace(e));
				}
			}
		});

		this.colExtension.setMinWidth(100);
		this.colExtension.setMaxWidth(140);
		this.colExtension.setCellValueFactory(new PropertyValueFactory<User, Integer>("extension"));
		this.colExtension.setCellFactory(TextFieldTableCell.forTableColumn(Formatters.getExtensioFormatter()));

		this.colExtension.setOnEditCommit(new EventHandler<CellEditEvent<User, Integer>>() { // Edició
			@Override
			public void handle(CellEditEvent<User, Integer> t) {
				try {
					// Usuari client =
					// SistemaGestio.getInstance().getClientByIndex(t.getTablePosition().getRow());
					User user = t.getRowValue();
					user.setExtension(t.getNewValue());

					saveUsuari(user, false);
				} catch (Exception e) {
					ControllerMenu.showError(ResourceManager.getInstance().getText("error.viewUsers.extension"),
							e.getMessage(), ExceptionUtils.getStackTrace(e));
				}
			}
		});

		this.colLocalion.setMinWidth(220);
		this.colLocalion.setMaxWidth(300);
		this.colLocalion.setCellValueFactory(new PropertyValueFactory<User, String>("location"));
		this.colLocalion.setCellFactory(TextFieldTableCell.forTableColumn());

		User user = ResourceManager.getInstance().getCurrentUser();

		if (!(user instanceof LogisticsManager)) {
			this.colDelete.setVisible(false);
			this.colDelete.setMinWidth(0);
			this.colDelete.setMaxWidth(0);
		} else {
			this.colDelete.setMinWidth(50);
			this.colDelete.setMaxWidth(70);
			// define a simple boolean cell value for the action column so that the column
			// will only be shown for non-empty rows.
			this.colDelete.setCellValueFactory(
					new Callback<TableColumn.CellDataFeatures<User, Boolean>, ObservableValue<Boolean>>() {
						@Override
						public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<User, Boolean> cell) {
							return new SimpleBooleanProperty(false);
						}
					});

			this.colDelete.setCellFactory(new ColumnButton<User, Boolean>(
					ResourceManager.getInstance().getText("fxml.text.viewUsers.col.delete"),
					new Image(getClass().getResourceAsStream("resources/recycle-bin.png"))) {
				@Override
				public void buttonAction(User usuari) {
					try {
						boolean result = ControllerMenu.showConfirm(
								ResourceManager.getInstance().getText("fxml.text.viewUsers.delete.title"),
								ResourceManager.getInstance().getText("fxml.text.viewUsers.delete.text"));
						if (result) {
							deleteUsuari(usuari);
						}
					} catch (Exception e) {
						ControllerMenu.showError(ResourceManager.getInstance().getText("error.viewUsers.delete"),
								e.getMessage(), ExceptionUtils.getStackTrace(e));
					}
				}
			});
		}
	}

	private void deleteUsuari(User usuari) throws Exception {
		final ServiceDeleteUser deleteUser = new ServiceDeleteUser(usuari);

		deleteUser.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent t) {
				reloadUsers();
			}
		});

		deleteUser.setOnFailed(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent t) {
				Throwable e = t.getSource().getException();

				ControllerMenu.showError(ResourceManager.getInstance().getText("error.viewUsers.delete.web.service"),
						e.getMessage(), ExceptionUtils.getStackTrace(e));
			}

		});

		deleteUser.start();
	}

	private void saveUsuari(User user, boolean insert) throws Exception {
		final ServiceSaveUser saveUser = new ServiceSaveUser(user, false);

		saveUser.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent t) {
				// refreshUsuaris();
			}
		});

		saveUser.setOnFailed(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent t) {
				Throwable e = t.getSource().getException();

				ControllerMenu.showError(ResourceManager.getInstance().getText("error.viewUsers.save.web.service"),
						e.getMessage(), ExceptionUtils.getStackTrace(e));
			}

		});

		saveUser.start();
	}

	private void reloadUsers() {
		Long[] roles = null;
		Pair<String, String> role = this.cmbRole.getValue();
		String search = this.txtFullnameSearch.getText();

		this.usersTable.setEditable(false);

		if (role != null) {
			roles = new Long[] { Long.valueOf(role.getKey()) };
		}

		final ServiceQueryUsers queryUsers = new ServiceQueryUsers(roles, search);

		queryUsers.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent t) {
				usersTable.setEditable(true);

				usersTable.getItems().clear();

				ObservableList<User> users = FXCollections.observableArrayList(queryUsers.getValue());

				usersTable.setItems(users);
			}
		});

		queryUsers.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				usersTable.setEditable(true);

				Throwable e = t.getSource().getException();

				ControllerMenu.showError(ResourceManager.getInstance().getText("error.viewUsers.web.service"),
						e.getMessage(), ExceptionUtils.getStackTrace(e));
			}

		});

		queryUsers.start();
	}

	@Override
	protected String htmlContentToPDF() {
		List<User> users = this.usersTable.getItems();
		Pair<String, String> role = this.cmbRole.getValue();
		String search = this.txtFullnameSearch.getText();

		List<Pair<String, String>> filters = new LinkedList<>();
		if (role != null) {
			filters.add(new Pair<>(ResourceManager.getInstance().getText("fxml.text.viewUsers.role"), role.getValue()));
		}
		if (search != null && !search.isBlank()) {
			filters.add(new Pair<>(ResourceManager.getInstance().getText("fxml.text.viewUsers.search.prompt"),
					"\"" + search + "\""));
		}

		return "<pre>" + ResourceManager.getInstance().usersDirectoryHtml(users, filters) + "</pre>";
	}

	@Override
	protected String documentTitle() {
		return ResourceManager.getInstance().getText("fxml.text.viewUsers.report.title");
	}

	@Override
	protected String documentFileName() {
		return ResourceManager.FILE_REPORT_USERS;
	}

}
