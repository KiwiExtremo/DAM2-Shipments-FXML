package ins.marianao.shipments.fxml.model;

import java.io.Serializable;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/* JSON annotations */
/*
 * Maps JSON data to Receptionist, LogisticsManager or Courier instance depending on
 * property role
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "role", visible = true)
@JsonSubTypes({ @Type(value = Receptionist.class, name = User.RECEPTIONIST),
		@Type(value = LogisticsManager.class, name = User.LOGISTICS_MANAGER),
		@Type(value = Courier.class, name = User.COURIER) })
/* Lombok */
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class User implements Serializable {

	private static final long serialVersionUID = 1L;

	/* Values for role - MUST be constants (can not be enums) */
	public static final String RECEPTIONIST = "RECEPTIONIST";
	public static final String LOGISTICS_MANAGER = "LOGISTICS_MANAGER";
	public static final String COURIER = "COURIER";

	public enum Long {
		RECEPTIONIST, LOGISTICS_MANAGER, COURIER
	}

	public static final int MIN_USERNAME = 2;
	public static final int MAX_USERNAME = 25;
	public static final int MIN_PASSWORD = 4;
	public static final int MIN_FULL_NAME = 3;
	public static final int MAX_FULL_NAME = 100;
	public static final int MAX_EXTENSION = 9999;

	/* Lombok */
	@EqualsAndHashCode.Include
	protected String username;

	protected Long role;

	/* JSON */
	// @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Not present in
	// generated JSON

	protected String password;

	/* Validation */
	@NonNull // Lombok. @RequiredArgsConstructor

	protected String fullName;

	protected Integer extension;

	public boolean allowReception() {
		return false;
	}

	public boolean allowAssignment() {
		return false;
	}

	public boolean allowDeliver() {
		return false;
	}

	public boolean allowUserManagement() {
		return false;
	}

	public abstract String getLocation();
}
