import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomerInfo {
    private String customerId;
    private String customerName;
    private String vehiclePlateNumber;
    private List<ServiceInfo> services;

    public CustomerInfo(String customerId, String customerName, String vehiclePlateNumber) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.vehiclePlateNumber = vehiclePlateNumber;
        this.services = new ArrayList<>();
    }

    public void addService(ServiceInfo service) {
        services.add(service);
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getVehiclePlateNumber() {
        return vehiclePlateNumber;
    }

    public List<ServiceInfo> getServices() {
        return services;
    }

    public double calculateTotalCost() {
        return services.stream()
                .mapToDouble(ServiceInfo::getCost)
                .sum();
    }

    public double getTotalSales() {
        return calculateTotalCost();
    }

    public String getServiceDetails() {
        StringBuilder serviceDetails = new StringBuilder();
        for (ServiceInfo service : services) {
            serviceDetails.append(service.getServiceName()).append(", ");
        }
        // Remove the trailing comma and space
        if (serviceDetails.length() > 2) {
            serviceDetails.setLength(serviceDetails.length() - 2);
        }
        return serviceDetails.toString();
    }

    public String getDates() {
        StringBuilder dates = new StringBuilder();
        for (ServiceInfo service : services) {
            dates.append(service.getDate()).append(", ");
        }
        // Remove the trailing comma and space
        if (dates.length() > 2) {
            dates.setLength(dates.length() - 2);
        }
        return dates.toString();
    }

   public void setServices(String[] serviceIds) {
       this.services.clear();
       for (String serviceId : serviceIds) {
           ServiceInfo service = ServiceInfo.getServiceById(serviceId);
           if (service != null) {
               this.services.add(service);
           }
       }
   }
}