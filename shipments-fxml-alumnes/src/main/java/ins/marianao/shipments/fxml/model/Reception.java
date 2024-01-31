package ins.marianao.shipments.fxml.model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/* Lombok */
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Reception extends Action implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer trackingNumber;
}
