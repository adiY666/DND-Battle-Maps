/**
 * Represents a logical coordinate instance.
 * <p></p>
 * Stores horizontal and vertical positions for this Coordinate.
 *
 * @author Adi
 */
class Coordinate implements java.io.Serializable {

    private double coordinateHorizontal;
    private double coordinateVertical;

    public Coordinate() {
        this(0.0, 0.0);
    }

    /**
     * Instantiates a new coordinate instance.
     *
     * @param coordinateHorizontal the horizontal position
     * @param coordinateVertical   the vertical position
     */
    public Coordinate(double coordinateHorizontal, double coordinateVertical) {
        this.coordinateHorizontal = coordinateHorizontal;
        this.coordinateVertical = coordinateVertical;
    }

    public double getCoordinateHorizontal() {
        return this.coordinateHorizontal;
    }

    public void setCoordinateHorizontal(double coordinateHorizontal) {
        this.coordinateHorizontal = coordinateHorizontal;
    }

    public double getCoordinateVertical() {
        return this.coordinateVertical;
    }

    public void setCoordinateVertical(double coordinateVertical) {
        this.coordinateVertical = coordinateVertical;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || this.getClass() != object.getClass()) return false;
        Coordinate that = (Coordinate) object;
        return Double.compare(that.coordinateHorizontal, this.coordinateHorizontal) == 0 && Double.compare(that.coordinateVertical, this.coordinateVertical) == 0;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.coordinateHorizontal, this.coordinateVertical);
    }

    @Override
    public String toString() {
        return "Coordinate{x=" + this.coordinateHorizontal + ", y=" + this.coordinateVertical + "}";
    }

}
