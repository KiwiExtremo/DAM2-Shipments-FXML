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
public class Assignment extends Action implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int MIN_PRIORITAT = 1;
	public static final int MAX_PRIORITAT = 3;

	private Courier courier;

	private Integer priority;

}
