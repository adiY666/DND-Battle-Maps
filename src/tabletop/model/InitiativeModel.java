package tabletop.model;

/**
 * Represents an initiative entry instance.
 * <p></p>
 * Stores combat ordering information for this tabletop.model.InitiativeModel.
 *
 * @author Adi
 */
public class InitiativeModel implements java.io.Serializable {

    private String entityName;
    private int initiativeValue;

    /**
     * Instantiates a new initiative tracking entry.
     *
     * @param entityName      the combatant name
     * @param initiativeValue the rolled dice score
     */
    public InitiativeModel(String entityName, int initiativeValue) {
        this.entityName = entityName;
        this.initiativeValue = initiativeValue;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public int getInitiativeValue() {
        return this.initiativeValue;
    }

    public void setInitiativeValue(int initiativeValue) {
        this.initiativeValue = initiativeValue;
    }

}
