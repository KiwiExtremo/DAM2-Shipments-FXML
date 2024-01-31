package ins.marianao.shipments.fxml.services;

import ins.marianao.shipments.fxml.model.User;

public class ServiceDeleteUser extends ServiceDeleteBase<User> {

	private static final String PATH_DELETE_USER = "delete/by/username";

	public ServiceDeleteUser(User user) throws Exception {
		super(user, new String[] { ServiceQueryUsers.PATH_REST_USERS, PATH_DELETE_USER, user.getUsername() });
	}

}
