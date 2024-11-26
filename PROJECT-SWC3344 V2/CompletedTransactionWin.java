import javax.swing.*;
import java.awt.*;
import java.util.Stack;

public class CompletedTransactionWin extends JFrame {
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 400;

    private JTable completedTransactionsTable;
    private Stack<CustomerInfo> completedTransactions;
    private Processor processor;

    public CompletedTransactionWin(Processor processor) {
        this.processor = processor;
        setTitle("Completed Transactions");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null); // Center the frame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        completedTransactions = processor.getCompleteStack();

        String[] columns = {"Customer Name", "Vehicle Plate No", "Service Details","Dates", "Completion Time", "Total Cost"};

        // Set up the data model for the table
        Object[][] transactionData = getCompletedTransactionData();

        completedTransactionsTable = new JTable(transactionData, columns);
        JScrollPane scrollPane = new JScrollPane(completedTransactionsTable);

        // Add the table to the frame
        add(scrollPane, BorderLayout.CENTER);

        // Get the number of completed transactions
        int completedTransactionsCount = processor.getCompletedTransactionCount();
        System.out.println("Number of completed transactions: " + completedTransactionsCount);
    }

    // Method to extract data from completed transactions for the table
    private Object[][] getCompletedTransactionData() {
        Object[][] data = new Object[completedTransactions.size()][6];
        int index = 0;
        for (CustomerInfo customer : completedTransactions) {
            String services = getServiceDetails(customer);
            double totalCost = calculateTotalCost(customer);
            data[index++] = new Object[]{
                customer.getCustomerName(),
                customer.getVehiclePlateNumber(),
                services,
                customer.getDates(),
                getServiceEta(customer),
                totalCost
            };
        }
        return data;
    }

    private String getServiceDetails(CustomerInfo customer) {
        StringBuilder serviceDetails = new StringBuilder();
        if (!customer.getServices().isEmpty()) {
            for (ServiceInfo service : customer.getServices()) {
                serviceDetails.append(service.getServiceName()).append(service.getDate()).append(service.getCompletionTime());
            }
        } else {
            serviceDetails.append("No services");
        }
        return serviceDetails.toString();
    }

    private String getServiceEta(CustomerInfo customer) {
        StringBuilder eta = new StringBuilder();
        if (!customer.getServices().isEmpty()) {
            for (ServiceInfo service : customer.getServices()) {
                eta.append(service.getEtaAsString()).append(", ");
            }
            // Remove the trailing comma and space
            if (eta.length() > 2) {
                eta.setLength(eta.length() - 2);
            }
        } else {
            eta.append("No services");
        }
        return eta.toString();
    }

    private double calculateTotalCost(CustomerInfo customer) {
        double totalCost = 0;
        for (ServiceInfo service : customer.getServices()) {
            totalCost += service.getCost();
        }
        return totalCost;
    }
}