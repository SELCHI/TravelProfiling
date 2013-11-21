package data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lasitha
 * Date: 10/27/13
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class GeoData {

    private String regionName;
    private List<String> regionHierarchy;
    private double latitude;
    private double longitude;

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public List<String> getRegionHierarchy() {
        return regionHierarchy;
    }

    public void setRegionHierarchy(List<String> regionHierarchy) {
        this.regionHierarchy = regionHierarchy;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
