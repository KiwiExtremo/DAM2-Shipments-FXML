package ins.marianao.shipments.fxml.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/* Lombok */
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Receptionist extends User implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int MAX_PLACE = 100;

	private Office office;

	/* JSON */
	// @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)

	private String place;

	@Override
	public boolean allowReception() {
		return true;
	}

	@Override
	public boolean allowAssignment() {
		return true;
	}

	@JsonIgnore
	@Override
	public String getLocation() {
		return this.office.getName() + " (" + this.place + ")";
	}
}
