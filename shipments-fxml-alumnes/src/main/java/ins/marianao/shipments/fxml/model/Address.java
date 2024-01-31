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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Address implements Serializable {

	private static final long serialVersionUID = 1L;

	/* Lombok */
	@EqualsAndHashCode.Include
	protected Long id;

	private String name;

	private String street;

	private String number;
	private String floor;
	private String door;

	private String city;

	private String province;
	private String postalCode;

	private String country;

	@JsonIgnore
	public String getAddress() {
		String address = this.name + ", " + this.street;
		if (this.number != null && !this.number.isBlank()) {
			address += ", " + this.number;
		}
		if (this.floor != null && !this.floor.isBlank()) {
			address += ", " + this.floor;
		}
		if (this.door != null && !this.door.isBlank()) {
			address += ", " + this.door;
		}
		address += System.lineSeparator() + this.city;
		if ((this.province != null && !this.province.isBlank())
				|| (this.postalCode != null && !this.postalCode.isBlank())) {
			address += "(";
			if (this.postalCode != null && !this.postalCode.isBlank()) {
				address += this.postalCode;
				if (this.province != null && !this.province.isBlank()) {
					address += " " + this.province;
				}
			} else {
				address += this.province;
			}
			address += ")";
		}
		address += System.lineSeparator() + this.country;
		return address;
	}
}
