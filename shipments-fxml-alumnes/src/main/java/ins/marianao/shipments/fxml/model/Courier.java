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
public class Courier extends User implements Serializable {

	private static final long serialVersionUID = 1L;

	private Company company;

	@Override
	public boolean allowDeliver() {
		return true;
	}

	@JsonIgnore
	@Override
	public String getLocation() {
		return this.company.getName();
	}
}
