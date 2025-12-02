package gr.uoi.dit.master2025.gkouvas.dppclient.model;

public class EnvironmentalInfoModel {

    private Long id;
    private Long deviceId;

    private String materialsComposition;
    private String recyclingInstructions;
    private String hazardousMaterials;

    private Double recyclabilityPercentage;
    private Double deviceWeightKg;

    // getters/setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getMaterialsComposition() {
        return materialsComposition;
    }

    public void setMaterialsComposition(String materialsComposition) {
        this.materialsComposition = materialsComposition;
    }

    public String getRecyclingInstructions() {
        return recyclingInstructions;
    }

    public void setRecyclingInstructions(String recyclingInstructions) {
        this.recyclingInstructions = recyclingInstructions;
    }

    public String getHazardousMaterials() {
        return hazardousMaterials;
    }

    public void setHazardousMaterials(String hazardousMaterials) {
        this.hazardousMaterials = hazardousMaterials;
    }

    public Double getRecyclabilityPercentage() {
        return recyclabilityPercentage;
    }

    public void setRecyclabilityPercentage(Double recyclabilityPercentage) {
        this.recyclabilityPercentage = recyclabilityPercentage;
    }

    public Double getDeviceWeightKg() {
        return deviceWeightKg;
    }

    public void setDeviceWeightKg(Double deviceWeightKg) {
        this.deviceWeightKg = deviceWeightKg;
    }
    public int computeEnvironmentalScore() {

        double recyclability = recyclabilityPercentage != null ? recyclabilityPercentage : 0;
        double weight = deviceWeightKg != null ? deviceWeightKg : 0;

        // Weight score
        double weightScore =
                weight < 1 ? 100 :
                        weight < 3 ? 70 :
                                weight < 10 ? 40 :
                                        10;

        // Hazard score
        String haz = hazardousMaterials == null ? "" : hazardousMaterials.toLowerCase();
        double hazardScore =
                haz.isBlank() ? 100 :
                        haz.contains("pb") || haz.contains("hg") || haz.contains("cr6") ? 20 :
                                60;

        double score =
                (recyclability * 0.6) +
                        (weightScore * 0.2) +
                        (hazardScore * 0.2);

        return (int) Math.round(score);
    }


}

