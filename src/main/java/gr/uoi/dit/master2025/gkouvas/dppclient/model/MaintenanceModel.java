package gr.uoi.dit.master2025.gkouvas.dppclient.model;

import gr.uoi.dit.master2025.gkouvas.dppclient.rest.BuildingServiceClient;
import gr.uoi.dit.master2025.gkouvas.dppclient.rest.DeviceServiceClient;

import java.time.LocalDate;

/**
 * UI Model for representing a maintenance log entry for a device.
 */
public class MaintenanceModel {

    private Long logId;
    private Long deviceId;
    private LocalDate maintenanceDate;
    private LocalDate plannedDate;
    private LocalDate performedDate;
    private String description;
    private String technician;
    private Long  buildingId;
    /*private transient  String deviceName;
    private transient String buildingName;*/


    private MaintenanceInterval interval;
    private MaintenanceStatus status;

    public MaintenanceInterval getInterval() {
        return interval;
    }

    public void setInterval(MaintenanceInterval interval) {
        this.interval = interval;
    }

    //public void setDeviceName(String deviceName) {
      //  this.deviceName = deviceName;
    //}

    //public String getBuildingName() {
      // return buildingName;
    //}

   // public void setBuildingName(String buildingName) {
       // this.buildingName = buildingName;
   // }

    public Long  getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long  buildingId) {
        this.buildingId = buildingId;
    }

    public MaintenanceModel() {}

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public LocalDate getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(LocalDate maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTechnician() {
        return technician;
    }

    public void setTechnician(String technician) {
        this.technician = technician;
    }

    public String getTargetName() {
        StringBuilder builder = new StringBuilder();
          /*BuildingServiceClient buildingClient = new BuildingServiceClient();
      DeviceServiceClient deviceClient = new DeviceServiceClient();*/
        if (buildingId != null) builder.append( new BuildingServiceClient().getBuilding(buildingId).getName()+" / ");
        if (deviceId != null) builder.append( new DeviceServiceClient().getDevice(deviceId).getName() );

        if(builder.isEmpty())
        return "-";
        else return builder.toString();
    }


    public LocalDate getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(LocalDate plannedDate) {
        this.plannedDate = plannedDate;
    }

    public LocalDate getPerformedDate() {
        return performedDate;
    }

    public void setPerformedDate(LocalDate performedDate) {
        this.performedDate = performedDate;
    }

    /*public String getDeviceName() {
        return deviceName;
    }*/

   /* public BuildingServiceClient getBuildingClient() {
        return buildingClient;
    }*/

    /*public DeviceServiceClient getDeviceClient() {
        return deviceClient;
    }*/

    public MaintenanceStatus getStatus() {
        return status;
    }

    public void setStatus(MaintenanceStatus status) {
        this.status = status;
    }


}
