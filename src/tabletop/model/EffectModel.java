package tabletop.model;

/**
 * Represents a temporary status effect applied to a token.
 *
 * @author Adi
 */
public class EffectModel implements java.io.Serializable {

    private String effectName;
    private int remainingTurns;
    private String effectColor;

    public EffectModel(String effectName, int remainingTurns, String effectColor) {
        super();
        this.effectName = effectName;
        this.remainingTurns = remainingTurns;
        this.effectColor = effectColor;
    }

    public String getEffectName() {
        return this.effectName;
    }

    public void setEffectName(String effectName) {
        this.effectName = effectName;
    }

    public int getRemainingTurns() {
        return this.remainingTurns;
    }

    public void setRemainingTurns(int remainingTurns) {
        this.remainingTurns = remainingTurns;
    }

    public String getEffectColor() {
        return this.effectColor;
    }

    public void setEffectColor(String effectColor) {
        this.effectColor = effectColor;
    }
}
