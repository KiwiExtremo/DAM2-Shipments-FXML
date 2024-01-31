package ins.marianao.shipments.fxml.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/* JSON annotations */
/*
 * Maps JSON data to Reception, Assignment or Delivery instance
 * depending on property type
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({ @Type(value = Reception.class, name = Action.RECEPTION),
		@Type(value = Assignment.class, name = Action.ASSIGNMENT),
		@Type(value = Delivery.class, name = Action.DELIVERY) })

/* Lombok */
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Action implements Serializable {

	private static final long serialVersionUID = 1L;

	/* Values for type - MUST be constants */
	public static final String RECEPTION = "RECEPTION";
	public static final String ASSIGNMENT = "ASSIGNMENT";
	public static final String DELIVERY = "DELIVERY";

	public static final String DATE_PATTERN = "dd/MM/yyyy HH:mm:ss";

	public enum Type {
		RECEPTION, ASSIGNMENT, DELIVERY
	}

	/* Lombok */
	@EqualsAndHashCode.Include
	protected Long id;

	protected Type type;

	protected User performer;

	/* JSON */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
	protected Date date = new Date();

	protected Shipment shipment;

	protected Long idShipment;
}
