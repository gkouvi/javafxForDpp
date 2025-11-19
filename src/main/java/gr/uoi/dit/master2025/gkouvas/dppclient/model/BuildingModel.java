package gr.uoi.dit.master2025.gkouvas.dppclient.model;

/**
 * UI Model for representing a Building inside a Site.
 */
public class BuildingModel {

    private Long id;
    private String name;
    private String address;
    private Long siteId;

    public BuildingModel() {}

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}

