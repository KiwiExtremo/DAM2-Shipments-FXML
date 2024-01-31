package ins.marianao.shipments.fxml.services;

import ins.marianao.shipments.fxml.model.User;

public class ServiceSaveUser extends ServiceSaveBase<User> {

	private static final String PATH_INSERT_USER = "save";
	private static final String PATH_UPDATE_USER = "update";

	public ServiceSaveUser(User user, boolean insert) throws Exception {
		super(user, User.class,
				new String[] { ServiceQueryUsers.PATH_REST_USERS, insert ? PATH_INSERT_USER : PATH_UPDATE_USER },
				insert ? Method.POST : Method.PUT);
	}

}
