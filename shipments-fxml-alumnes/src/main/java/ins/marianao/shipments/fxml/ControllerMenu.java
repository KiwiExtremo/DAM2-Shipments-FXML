package ins.marianao.shipments.fxml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.exception.ExceptionUtils;

import ins.marianao.shipments.fxml.manager.ResourceManager;
import ins.marianao.shipments.fxml.model.LogisticsManager;
import ins.marianao.shipments.fxml.model.Receptionist;
import ins.marianao.shipments.fxml.model.User;
import ins.marianao.shipments.fxml.services.ServiceLogin;
import ins.marianao.shipments.fxml.services.ServiceQueryUsers;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ControllerMenu implements Initializable {
	@FXML
	private BorderPane appRootPane;
	@FXML
	private AnchorPane portviewPane;
	@FXML
	private AnchorPane loginForm;

	@FXML
	private TextField txtUsername;
	@FXML
	private PasswordField txtPassword;
	@FXML
	private Button btnLogin;

	@FXML
	private MenuBar menuBar;
	@FXML
	private Menu mnShipments;
	@FXML
	private Menu mnUsers;
	@FXML
	private Menu mnProfile;
	@FXML
	private MenuItem mnItReception;
	@FXML
	private MenuItem mnItAddUser;
	@FXML
	private MenuItem mnItImport;
	@FXML
	private MenuItem mnItExport;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle resource) {
		this.logOff();
	}

	private void logOff() {
		try {
			this.loadView(this.loginForm);

			ResourceManager.getInstance().setCurrentUser(null); // Logoff

			this.disableMenu();

			this.txtUsername.clear();
			this.txtPassword.clear();

		} catch (Exception e) {
			ControllerMenu.showError(ResourceManager.getInstance().getText("error.menu.view.opening"), e.getMessage(),
					ExceptionUtils.getStackTrace(e));
		}
	}

	/**
	 * Called when Sortir menuItem is fired.
	 *
	 * @param event the action event.
	 */
	@FXML
	public void logoffMenuClick(ActionEvent event) {
		System.exit(0);
	}

	/**
	 * Called when btnLogin button is fired.
	 *
	 * @param event the action event.
	 */
	@FXML
	public void loginClick(ActionEvent event) {

		try {
			String username = this.txtUsername.getText();
			String password = this.txtPassword.getText();

			final ServiceLogin login = new ServiceLogin(username, password);

			login.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent t) {
					User user = login.getValue();

					ResourceManager.getInstance().setCurrentUser(user); // Login

					openUserForm(true);

					enableMenu(user);
				}
			});

			login.setOnFailed(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent t) {
					Throwable e = t.getSource().getException();

					ControllerMenu.showError(ResourceManager.getInstance().getText("error.menu.login"), e.getMessage(),
							ExceptionUtils.getStackTrace(e));
				}
			});

			login.start();

		} catch (Exception e) {
			ControllerMenu.showError(ResourceManager.getInstance().getText("error.menu.login"), e.getMessage(),
					ExceptionUtils.getStackTrace(e));
		}
	}

	/**
	 * Called when Reception menuItem is fired.
	 *
	 * @param event the action event.
	 */
	@FXML
	public void receptionMenuClick(ActionEvent event) {
		// TODO open shipment's form
		return;
	}

	/**
	 * Called when Shipments menuItem is fired.
	 *
	 * @param event the action event.
	 */
	@FXML
	public void shipmentsMenuClick(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewUsersShipments.fxml"),
					ResourceManager.getInstance().getTranslationBundle());
			BorderPane vista = (BorderPane) loader.load();

			this.loadView(vista);
		} catch (Exception e) {
			ControllerMenu.showError(ResourceManager.getInstance().getText("error.menu.view.opening"), e.getMessage(),
					ExceptionUtils.getStackTrace(e));
		}
	}

	/**
	 * Called when New User menuItem is fired.
	 *
	 * @param event the action event.
	 */
	@FXML
	public void newUserMenuClick(ActionEvent event) {
		this.openUserForm(false);
	}

	/**
	 * Called when Users directory menuItem is fired.
	 *
	 * @param event the action event.
	 */
	@FXML
	public void usersDirectoryMenuClick(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewUsersDirectory.fxml"),
					ResourceManager.getInstance().getTranslationBundle());
			BorderPane vista = (BorderPane) loader.load();

			this.loadView(vista);
		} catch (Exception e) {
			ControllerMenu.showError(ResourceManager.getInstance().getText("error.menu.view.opening"), e.getMessage(),
					ExceptionUtils.getStackTrace(e));
		}
	}

	/**
	 * Called when Import users menuItem is fired.
	 *
	 * @param event the action event.
	 */
	@FXML
	public void importUsersMenuClick(ActionEvent event) {
		try {
			File file = this.openFileChooser(ResourceManager.getInstance().getText("error.menu.file.open"), true);

			if (file != null) {
				SortedSet<User> imported = ResourceManager.getInstance().importUsers(file);

				// TODO Import users and save/persist
				imported = new TreeSet<User>();
				imported.add(null);

				ControllerMenu.showInfo(ResourceManager.getInstance().getText("info.app.title"),
						ResourceManager.getInstance().getText("fxml.text.menu.import.ok"));
			}
		} catch (Exception e) {
			ControllerMenu.showError(ResourceManager.getInstance().getText("error.menu.import"), e.getMessage(),
					ExceptionUtils.getStackTrace(e));
		}
	}

	/**
	 * Called when Export menuItem is fired.
	 *
	 * @param event the action event.
	 */
	@FXML
	public void exportUsersMenuClick(ActionEvent event) {
		try {
			File file = this.openFileChooser(ResourceManager.getInstance().getText("error.menu.file.write"), false);

			if (file != null) {
				final ServiceQueryUsers queryUsers = new ServiceQueryUsers(null, null);

				queryUsers.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

					@Override
					public void handle(WorkerStateEvent t) {
						SortedSet<User> toExport = new TreeSet<>(queryUsers.getValue());

						try {
							ResourceManager.getInstance().exportUsers(file, toExport);
							ControllerMenu.showInfo(ResourceManager.getInstance().getText("info.app.title"),
									ResourceManager.getInstance().getText("fxml.text.menu.export.ok"));
						} catch (Exception e) {
							ControllerMenu.showError(ResourceManager.getInstance().getText("error.menu.export"),
									e.getMessage(), ExceptionUtils.getStackTrace(e));
						}
					}
				});

				queryUsers.setOnFailed(new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent t) {
						Throwable e = t.getSource().getException();

						ControllerMenu.showError(ResourceManager.getInstance().getText("error.menu.web.service"),
								e.getMessage(), ExceptionUtils.getStackTrace(e));
					}

				});

				queryUsers.start();
			}
		} catch (Exception e) {
			ControllerMenu.showError(ResourceManager.getInstance().getText("error.menu.export"), e.getMessage(),
					ExceptionUtils.getStackTrace(e));
		}
	}

	/**
	 * Called when Edit Profile menuItem is fired.
	 *
	 * @param event the action event.
	 */
	@FXML
	public void editProfileMenuClick(ActionEvent event) {
		this.openUserForm(true);
	}

	/**
	 * Called when Desconnectar menuItem is fired.
	 *
	 * @param event the action event.
	 */
	@FXML
	public void logoffClick(ActionEvent event) {

		this.logOff();
	}

	/**
	 * Called when About menuItem is fired.
	 *
	 * @param event the action event.
	 * @throws IOException
	 */
	@FXML
	public void aboutMenuClick(ActionEvent event) {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About ...");
		alert.setHeaderText(null);
		alert.setContentText(
				"Copyright@" + Calendar.getInstance(new Locale("ES_CA")).get(Calendar.YEAR) + "\nÀlex Macia");
		alert.showAndWait();
	}

	public void loadView(Pane vista) {
		if (vista == null) {
			return;
		}

		if (checkViewAlreadyLoaded(vista.getId())) {
			return;
		}

		this.portviewPane.getChildren().clear();

		// appRootPane.setPrefHeight(appRootPane.getHeight() - 80.0);

		this.portviewPane.getChildren().add(vista);

		AnchorPane.setTopAnchor(vista, 0.0);
		AnchorPane.setBottomAnchor(vista, 0.0);
		AnchorPane.setLeftAnchor(vista, 0.0);
		AnchorPane.setRightAnchor(vista, 0.0);
		// this.portviewPane.setVisible(true);
	}

	private boolean checkViewAlreadyLoaded(String id) {
		if (id == null || this.portviewPane != null || this.portviewPane.getChildren() != null) {
			return false;
		}

		Iterator<Node> fills = this.portviewPane.getChildren().iterator();

		while (fills.hasNext()) {
			Node aux = fills.next();
			if (id.equals(aux.getId())) {
				return true;
			}
		}

		return false;
	}

	private void enableMenu(User user) {
		this.mnShipments.setVisible(true);
		this.mnUsers.setVisible(true);
		this.mnProfile.setVisible(true);

		/*
		 * this.menuBar.getMenus().add(1, this.mnShipments);
		 * this.menuBar.getMenus().add(2, this.mnUsers); this.menuBar.getMenus().add(3,
		 * this.mnProfile);
		 */

		if (user != null) {
			if (user instanceof Receptionist) {
				this.mnItReception.setDisable(false);
			}
			if (user instanceof LogisticsManager) {
				this.mnItAddUser.setDisable(false);
				this.mnItImport.setDisable(false);
				this.mnItExport.setDisable(false);
			}
		}
	}

	private void disableMenu() {

		this.mnItReception.setDisable(true);
		this.mnItAddUser.setDisable(true);
		this.mnItImport.setDisable(true);
		this.mnItExport.setDisable(true);

		/*
		 * this.menuBar.getMenus().remove(this.mnShipments);
		 * this.menuBar.getMenus().remove(this.mnUsers);
		 * this.menuBar.getMenus().remove(this.mnProfile);
		 */

		this.mnShipments.setVisible(false);
		this.mnUsers.setVisible(false);
		this.mnProfile.setVisible(false);

	}

	private void openUserForm(boolean perfil) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewFormUser.fxml"),
					ResourceManager.getInstance().getTranslationBundle());
			BorderPane vista = (BorderPane) loader.load();

			ControllerFormUser controllerFormUsuari = loader.getController();
			if (!perfil) {
				controllerFormUsuari.enableEdition();
			}

			this.loadView(vista);
		} catch (Exception e) {
			e.printStackTrace();
			ControllerMenu.showError(ResourceManager.getInstance().getText("error.menu.view.opening"), e.getMessage(),
					ExceptionUtils.getStackTrace(e));
		}
	}

	public static Button addIconToButton(Button button, Image image, int size) {
		ImageView logo = new ImageView(image);
		logo.setFitWidth(size);
		logo.setFitHeight(size);
		button.setGraphic(logo);
		button.setText(null);
		return button;
	}

	public static void showError(String title, String sms, String trace) {
		showAlert(ResourceManager.getInstance().getText("alert.title.error"), title, sms, trace, AlertType.ERROR,
				"error-panel");
	}

	public static void showError(String title, String sms) {
		showAlert(ResourceManager.getInstance().getText("alert.title.error"), title, sms, null, AlertType.ERROR,
				"error-panel");
	}

	public static void showInfo(String title, String sms) {
		showAlert(ResourceManager.getInstance().getText("alert.title.information"), title, sms, null,
				AlertType.INFORMATION, "info-panel");
	}

	public static boolean showConfirm(String title, String sms) {
		Optional<ButtonType> result = showAlert(ResourceManager.getInstance().getText("alert.title.confirm"), title,
				sms, null, AlertType.CONFIRMATION, "info-panel");

		return result.get() == ButtonType.OK;
	}

	private static Optional<ButtonType> showAlert(String title, String header, String sms, String trace,
			AlertType tipus, String paneId) {

		Alert alert = new Alert(tipus);
		alert.setTitle(title);
		alert.getDialogPane().setId(paneId);
		alert.setHeaderText(header);
		alert.setContentText(sms);
		alert.setResizable(true);

		if (trace == null) {
			alert.getDialogPane().setPrefSize(400, 120);
		} else {
			alert.getDialogPane().setPrefSize(520, 200);

			TextArea textArea = new TextArea(trace);
			textArea.setEditable(false);
			textArea.setWrapText(true);

			// textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxWidth(600);
			textArea.setMaxHeight(Double.MAX_VALUE);
			textArea.setMaxHeight(300);
			textArea.setMinHeight(300);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);

			GridPane expContent = new GridPane();
			expContent.setMaxWidth(Double.MAX_VALUE);
			expContent.setMaxWidth(600);
			expContent.add(textArea, 0, 1);

			// Set expandable Exception into the dialog pane.
			alert.getDialogPane().setExpandableContent(expContent);
			alert.getDialogPane().setExpanded(false);

			// Change Listener => property has been recalculated

			alert.getDialogPane().expandedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							alert.getDialogPane().requestLayout();
							Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
							stage.sizeToScene();
						}
					});
				}
			});

			// Lambda version

			/*
			 * alert.getDialogPane().expandedProperty().addListener((observable, oldvalue,
			 * newvalue) -> {
			 * 
			 * Platform.runLater(() -> { alert.getDialogPane().requestLayout(); Stage stage
			 * = (Stage) alert.getDialogPane().getScene().getWindow(); stage.sizeToScene();
			 * }); });
			 */

			// Invalidation Listener => property and has to be recalculated

			/*
			 * alert.getDialogPane().expandedProperty().addListener((observable) -> {
			 * 
			 * Platform.runLater(() -> { alert.getDialogPane().requestLayout(); Stage stage
			 * = (Stage) alert.getDialogPane().getScene().getWindow(); stage.sizeToScene();
			 * }); });
			 */
		}

		return alert.showAndWait();
	}

	private File openFileChooser(String title, boolean open) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("BIN", "*.bin"),
				new FileChooser.ExtensionFilter("Tots", "*.*"));
		File file;
		if (open) {
			file = fileChooser.showOpenDialog(ResourceManager.getInstance().getStage());
		} else {
			file = fileChooser.showSaveDialog(ResourceManager.getInstance().getStage());
		}
		return file;
	}
}
