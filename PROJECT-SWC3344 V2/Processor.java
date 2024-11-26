import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

public class Processor {
    private LinkedList<CustomerInfo> customerList;
    private Stack<CustomerInfo> completeStack = new Stack<>();
    private List<ServiceInfo> services;
    private QueueManager queueManager;

    public Processor() {
        customerList = new LinkedList<>();
        completeStack = new Stack<>();
        services = new ArrayList<>();
        queueManager = new QueueManager();

        // Initialize services
        services.add(new ServiceInfo("S1", "Car Wash", 1200.99, "2022-01-01", 6.5)); 
        services.add(new ServiceInfo("S2", "Oil Change", 1500.99, "2022-01-02", 5.0)); 
        services.add(new ServiceInfo("S3", "Tire Rotation", 200.99, "2022-01-03", 1.0)); 
        services.add(new ServiceInfo("S4", "Brake Inspection", 250.99, "2022-01-04", 0.5)); 
        services.add(new ServiceInfo("S5", "Battery Replacement", 30.99, "2022-01-05", 1.0)); 
    }

    public String[] getAvailableServices() {
        String[] serviceIds = new String[services.size()];
        for (int i = 0; i < services.size(); i++) {
            serviceIds[i] = services.get(i).getServiceId();
        }
        return serviceIds;
    }

    public LinkedList<CustomerInfo> getCustomerList() {
        return customerList;
    }

    public Stack<CustomerInfo> getCompleteStack() {
        return completeStack;
    }

    private boolean suggest = true; // initially true, will alternate between true and false
    public void loadCustomerDataFromFile(String fileName) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processCustomerData(line);
            }
        }

        for (CustomerInfo customer : customerList) {
            if (customer.getServices().size() <= 3) {
                if (suggest) {
                    queueManager.getQueue1().add(customer);
                } else {
                    queueManager.getQueue2().add(customer);
                }
                suggest = !suggest; // toggle the suggest variable
            } else {
                queueManager.getQueue3().add(customer);
            } 
        }
    } 

    private void processCustomerData(String data) {
        StringTokenizer tokenizer = new StringTokenizer(data, ",");
        if (tokenizer.countTokens() < 3) {
            return;
        }

        String customerId = tokenizer.nextToken();
        String customerName = tokenizer.nextToken();
        String vehiclePlateNumber = tokenizer.nextToken();

        CustomerInfo customer = new CustomerInfo(customerId, customerName, vehiclePlateNumber);
        customerList.add(customer);
    }

    public void addCustomer(String customerId, String customerName, String vehiclePlateNumber) {
        CustomerInfo customer = new CustomerInfo(customerId, customerName, vehiclePlateNumber);

        customerList.add(customer);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("CustomerList.txt", true))) {
            for (CustomerInfo c : customerList) {
                writer.write(c.getCustomerId() + "," + c.getCustomerName() + "," + c.getVehiclePlateNumber() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving customer to file: " + e.getMessage());
        }
    }

    public CustomerInfo getCustomerById(String customerId) {
        for (CustomerInfo customer : customerList) {
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }
        return null;
    }


    public void addServiceToCustomer(String customerId, String serviceId) {
        CustomerInfo customer = getCustomerById(customerId);
        if (customer != null) {
            ServiceInfo service = ServiceInfo.getServiceById(serviceId);
            if (service != null) {
                customer.addService(service);

                // Remove the customer from all queues before re-adding
                queueManager.getQueue1().remove(customer);
                queueManager.getQueue2().remove(customer);
                queueManager.getQueue3().remove(customer);

                // Add to appropriate queue based on service count
                if (customer.getServices().size() <= 3) {
                    if (suggest) {
                        queueManager.getQueue1().add(customer);
                    } else {
                        queueManager.getQueue2().add(customer);
                    }
                    suggest = !suggest; // toggle the suggest variable
                } else {
                    queueManager.getQueue3().add(customer);
                }

                // Save updated customer data to file
                saveCustomerDataToFile("CustomerList.txt");
            }
        }
    }

    private void saveCustomerDataToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (CustomerInfo c : customerList) {
                writer.write(String.format("%s,%s,%s,%s\n",
                        c.getCustomerId(),
                        c.getCustomerName(),
                        c.getVehiclePlateNumber(),
                        String.join(";", c.getServices().stream().map(ServiceInfo::getServiceId).toArray(String[]::new))
                    ));
            }
        } catch (IOException e) {
            System.out.println("Error saving customer data to file: " + e.getMessage());
        }
    }

    public void markCustomerAsComplete(String customerId) {
        CustomerInfo customer = getCustomerById(customerId);
        if (customer != null) {
            completeStack.push(customer);
        }
    }

                
    public int getCompletedTransactionCount() {
        return completeStack.size();
    }

    public double calculateTotalSales() {
        double totalSales = 0;
        for (CustomerInfo customer : customerList) {
            totalSales += customer.getTotalSales();
        }
        return totalSales;
    }

    public List<ServiceInfo> getServiceList() {
        return services;
    }

    public CustomerInfo[] getCustomersBeforeQueue() {
        // Assuming you have a list of customers before they get into the queue
        List<CustomerInfo> customers = new ArrayList<>();
        customers.addAll(customerList); // Add customers from the customer list
        customers.addAll(queueManager.getQueue1()); // Add customers from queue 1
        customers.addAll(queueManager.getQueue2()); // Add customers from queue 2
        customers.addAll(queueManager.getQueue3()); // Add customers from queue 3

        return customers.toArray(new CustomerInfo[0]);
    }

    public int getCustomersLeft() {
        return customerList.size();
    }

    public int getServicesPerformed() {
        int servicesPerformed = 0;
        for (CustomerInfo customer : completeStack) {
            servicesPerformed += customer.getServices().size();
        }
        return servicesPerformed;
    }

    public double getTotalSales() {
        double totalSales = 0;
        for (CustomerInfo customer : customerList) {
            totalSales += customer.getTotalSales();
        }
        for (CustomerInfo customer : completeStack) {
            totalSales += customer.getTotalSales();
        }
        return totalSales;
    }
}