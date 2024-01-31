package ins.marianao.shipments.fxml.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.Formula;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ins.marianao.shipments.fxml.model.Action.Type;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/* JSON annotations */
@JsonIgnoreProperties({ "receptionist", "courier", "receptionDate", "deliveryDate", "senderAddress", "recipientAddress",
		"dimensions" })
/* Lombok */
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Shipment implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int MAX_DESCRIPTION = 500;

	public enum Category {
		PARTICULAR, COMPANY, GOVERNMENT
	}

	public static final String PENDING = "PENDING";
	public static final String IN_PROCESS = "IN_PROCESS";
	public static final String DELIVERED = "DELIVERED";

	public enum Status {
		PENDING, IN_PROCESS, DELIVERED
	}

	/* Lombok */
	@EqualsAndHashCode.Include
	private Long id;

	private Category category;

	private Address sender;

	private Address recipient;

	private Float weight;
	private Float height;
	private Float width;
	private Float length;

	private Boolean express;
	private Boolean fragile;

	private String note;

	private List<Action> tracking;

	/* Hibernate */
	@Formula("(SELECT CASE a.type WHEN '" + Action.RECEPTION + "' THEN '" + PENDING + "' " + " WHEN '"
			+ Action.ASSIGNMENT + "' THEN '" + IN_PROCESS + "' " + " WHEN '" + Action.DELIVERY + "' THEN '" + DELIVERED
			+ "' ELSE NULL END FROM actions a "
			+ " WHERE a.date=( SELECT MAX(last_action.date) FROM actions last_action "
			+ " WHERE last_action.shipment_id=a.shipment_id AND last_action.shipment_id=id ))")
	// Lombok
	@Setter(AccessLevel.NONE)
	private Status status;

	/*
	 * @JsonIgnore public Status getStatus() { if (this.tracking == null ||
	 * this.tracking.isEmpty()) return null;
	 * 
	 * switch (this.tracking.get(this.tracking.size() - 1).getType()) { case
	 * RECEPTION: return Status.PENDING;
	 * 
	 * case ASSIGNMENT: return Status.IN_PROCESS;
	 * 
	 * case DELIVERY:
	 * 
	 * return Status.DELIVERED;
	 * 
	 * default: break; }
	 * 
	 * return null; }
	 */

	public User getReceptionist() {
		if (this.tracking == null || tracking.isEmpty()) {
			return null;
		}

		return this.tracking.get(0).getPerformer();
	}

	public Courier getCourier() {
		if (this.tracking == null || tracking.isEmpty()) {
			return null;
		}

		for (Action action : tracking) {
			if (Type.ASSIGNMENT.equals(action.getType())) {
				Assignment assignment = (Assignment) action;
				return assignment.getCourier();
			}
		}

		return null;
	}

	public Date getReceptionDate() {
		if (this.tracking == null || tracking.isEmpty()) {
			return null;
		}

		return this.tracking.get(0).getDate();
	}

	public Date getDeliveryDate() {
		if (this.tracking == null || tracking.isEmpty()) {
			return null;
		}

		Action lastAction = this.tracking.get(this.tracking.size() - 1);

		if (Type.DELIVERY.equals(lastAction.getType())) {
			return lastAction.getDate();
		}

		return null;
	}

	public String getSenderAddress() {
		return this.sender.getAddress();
	}

	public String getRecipientAddress() {
		return this.recipient.getAddress();
	}

	public String getDimensions() {
		// HxWxL
		return (this.height != null ? this.height + "" : "N/A") + System.lineSeparator()
				+ (this.width != null ? this.width + "" : "N/A") + System.lineSeparator()
				+ (this.length != null ? this.length + "" : "N/A");
	}

}
