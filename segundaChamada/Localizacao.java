import java.io.Serializable;

public class Localizacao implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private double latitude;
    private double longitude;
    
    public Localizacao(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    
    @Override
    public String toString() {
        return String.format("(%.4f, %.4f)", latitude, longitude);
    }
}