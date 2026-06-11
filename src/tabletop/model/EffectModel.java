package tabletop.model;

/**
 * Represents a status condition instance.
 * <p></p>
 * Encapsulates combat effect duration for this tabletop.model.EffectModel.
 *
 * @author Adi
 */
public class EffectModel implements java.io.Serializable {

    private String effectName;
    private String displayColor;
    private int remainingTurns;

    /**
     * Instantiates a new effect.
     *
     * @param effectName     the description string
     * @param remainingTurns the combat rounds
     * @param displayColor   the color identity
     */
    public EffectModel(String effectName, int remainingTurns, String displayColor) {
        this.effectName = effectName;
        this.remainingTurns = remainingTurns;
        this.displayColor = displayColor;
    }

    public String getEffectName() {
        return this.effectName;
    }

    public void setEffectName(String effectName) {
        this.effectName = effectName;
    }

    public String getDisplayColor() {
        return this.displayColor;
    }

    public void setDisplayColor(String displayColor) {
        this.displayColor = displayColor;
    }

    public int getRemainingTurns() {
        return this.remainingTurns;
    }

    public void setRemainingTurns(int remainingTurns) {
        this.remainingTurns = remainingTurns;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || this.getClass() != object.getClass()) return false;
        EffectModel that = (EffectModel) object;
        return this.remainingTurns == that.remainingTurns && java.util.Objects.equals(this.effectName, that.effectName);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.effectName, this.remainingTurns);
    }

    @Override
    public String toString() {
        return "tabletop.model.EffectModel{name='" + this.effectName + "'}";
    }

}
