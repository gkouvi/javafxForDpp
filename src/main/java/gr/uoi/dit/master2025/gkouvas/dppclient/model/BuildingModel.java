package gr.uoi.dit.master2025.gkouvas.dppclient.model;

import gr.uoi.dit.master2025.gkouvas.dppclient.rest.SiteServiceClient;

/**
 * UI Model for representing a Building inside a Site.
 */
public class BuildingModel {

    private Long id;
    private String name;
    private String address;
    private Long siteId;
    private String bimModelRef;
    private String bimFormat;

    public String getBimModelRef() {
        return bimModelRef;
    }

    public void setBimModelRef(String bimModelRef) {
        this.bimModelRef = bimModelRef;
    }

    public String getBimFormat() {
        return bimFormat;
    }

    public void setBimFormat(String bimFormat) {
        this.bimFormat = bimFormat;
    }

    public void setQrBase64(String qrBase64) {
        this.qrBase64 = qrBase64;
    }

    private String qrBase64;
    private SiteServiceClient serviceClient = new SiteServiceClient();

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

    public String getSiteFromID() {

        return serviceClient.getSite(siteId).getName();
    }
    public String getSite(){
        return serviceClient.getSite(siteId).getName();
    }

    public String getQrBase64() {
        return qrBase64;
    }

    @Override
    public String toString() {
        return "BuildingModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", siteId=" + siteId +
                ", bimModelRef='" + bimModelRef + '\'' +
                ", bimFormat='" + bimFormat + '\'' +
                ", qrBase64='" + qrBase64 + '\'' +
                ", serviceClient=" + serviceClient +
                '}';
    }
}

