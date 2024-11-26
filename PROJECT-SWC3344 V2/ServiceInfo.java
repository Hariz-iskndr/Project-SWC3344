import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ServiceInfo {
    private String serviceId;
    private String serviceName;
    private double cost;
    private String date;
    private double etaInHours;

    // Static list of services
    private static List<ServiceInfo> services = new ArrayList<>();

    public ServiceInfo(String serviceId, String serviceName, double cost, String date, double etaInHours) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.cost = cost;
        this.date = date;
        this.etaInHours = etaInHours;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }

    public String getDate() {
        return date;
    }

    public String getEtaAsString() {
        int hours = (int) etaInHours;
        int minutes = (int) ((etaInHours - hours) * 60);
        return String.format("%d hours and %d minutes", hours, minutes);
    }

    public String getCompletionTime() {
        String eta = getEtaAsString();
        String date = getDate();
        // Assuming the date is in the format "yyyy-MM-dd"
        String[] dateParts = date.split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int day = Integer.parseInt(dateParts[2]);

        // Parse the ETA into hours and minutes
        String[] etaParts = eta.split(" hours and ");
        int hours = Integer.parseInt(etaParts[0]);
        int minutes = Integer.parseInt(etaParts[1].split(" minutes")[0]);

        // Calculate the completion time
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0); // Set the date to the start of the day
        calendar.add(Calendar.HOUR, hours);
        calendar.add(Calendar.MINUTE, minutes);

        // Format the completion time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(calendar.getTime());
    }

    public static ServiceInfo getServiceById(String serviceId) {
        for (ServiceInfo service : services) {
            if (service.getServiceId().equals(serviceId)) {
                return service;
            }
        }
        return null;
    }

    public static void addService(ServiceInfo service) {
        services.add(service);
    }
}