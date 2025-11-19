package gr.uoi.dit.master2025.gkouvas.dppclient.model;

import java.util.List;

/**
 * UI Model for representing a DPP Site inside the JavaFX client.
 * This corresponds to SiteDto received from the backend.
 */
public class SiteModel {

    private Long id;
    private String name;
    private String region;
    private String coordinates;



    public SiteModel() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }


}
