package gr.uoi.dit.master2025.gkouvas.dppclient.model;

public class ModelForSiteAndBuilding {

    private String siteName;
    private String buildingName;
    private Long buildingId;
    private Long siteId;



    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    @Override
    public String toString() {
        return  siteName + '\\'  + buildingName ;
    }
}
