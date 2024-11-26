import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MainDashboard extends JFrame {
    private JPanel mainPanel, bottomPanel;
    private ImageIcon icon;
    private Processor processor;
    private Queue<CustomerInfo> queue1, queue2, queue3;
    private JButton btnUploadCustomer, btnAddCustomer;
    private JButton btnOpenQueue1, btnOpenQueue2, btnOpenQueue3;

    public MainDashboard() {
        setTitle("SWC-Project 3344");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());
        icon = new ImageIcon("gta bengkel.jpg");
        setIconImage(icon.getImage());

        processor = new Processor();
        queue1 = new LinkedList<>();
        queue2 = new LinkedList<>();
        queue3 = new LinkedList<>();
        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        createTopPanel();
        createCentrePanel();

        setVisible(true);
    }

    private void createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnUploadCustomer = new JButton("Upload Customer Data");
        btnUploadCustomer.addActionListener(e -> uploadCustomerData());
        topPanel.add(btnUploadCustomer);
        JButton btnOpenCompletedTransWin = new JButton("Open Completed Transactions");
        btnOpenCompletedTransWin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openCompletedTransWin();
            }
        });
        topPanel.add(btnOpenCompletedTransWin);

        btnOpenQueue1 = new JButton("Open Queue 1");
        btnOpenQueue2 = new JButton("Open Queue 2");
        btnOpenQueue3 = new JButton("Open Queue 3");

        btnOpenQueue1.addActionListener(e -> openQueueWindow("Queue 1", queue1));
        btnOpenQueue2.addActionListener(e -> openQueueWindow("Queue 2", queue2));
        btnOpenQueue3.addActionListener(e -> openQueueWindow("Queue 3", queue3));

        topPanel.add(btnOpenQueue1);
        topPanel.add(btnOpenQueue2);
        topPanel.add(btnOpenQueue3);


        btnAddCustomer = new JButton("Add New Customer");
        btnAddCustomer.addActionListener(e -> addNewCustomer());
        topPanel.add(btnAddCustomer);

        mainPanel.add(topPanel, BorderLayout.NORTH);
    }


    private void addNewCustomer() {
        JTextField customerIdField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField plateNumberField = new JTextField();
        JList<ServiceInfo> serviceList = new JList<>(processor.getServiceList().toArray(new ServiceInfo[0]));
        serviceList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    
        JPanel panel = new JPanel(new GridLayout(1, 0));
        panel.add(new JLabel("Customer ID:"));
        panel.add(customerIdField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Vehicle Plate Number:"));
        panel.add(plateNumberField);
        panel.add(new JLabel("Select Services:"));
        panel.add(new JScrollPane(serviceList));
    
        int result = JOptionPane.showConfirmDialog(null, panel, "Add New Customer",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String customerId = customerIdField.getText();
            String customerName = nameField.getText();
            String vehiclePlateNumber = plateNumberField.getText();
            List<ServiceInfo> selectedServices = serviceList.getSelectedValuesList();
    
            if (!customerId.isEmpty() && !customerName.isEmpty() && !vehiclePlateNumber.isEmpty()) {
                CustomerInfo newCustomer = new CustomerInfo(customerId, customerName, vehiclePlateNumber);
                for (ServiceInfo service : selectedServices) {
                    newCustomer.addService(service);
                }
    
                processor.addCustomer(customerId, customerName, vehiclePlateNumber);
                for (ServiceInfo service : selectedServices) {
                    processor.addServiceToCustomer(customerId, service.getServiceId());
                }
    
                JOptionPane.showMessageDialog(this, "New customer added successfully!");
                refreshCustomerTable();
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            }
        }
    }

    private void refreshCustomerTable() {
        DefaultTableModel model = (DefaultTableModel) ((JTable) ((JScrollPane) mainPanel.getComponent(1)).getViewport().getView()).getModel();
        model.setRowCount(0);
        viewCustomersBeforeQueue(model);
    }

    private void uploadCustomerData() {
        try {
            processor.loadCustomerDataFromFile("CustomerList.txt");
            try (BufferedReader reader = new BufferedReader(new FileReader("CustomerList.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(", ");
                    if (data.length >= 4) {
                        String customerId = data[0];
                        String customerName = data[1];
                        String vehiclePlateNumber = data[2];
                        String[] services = data[3].split(";");
    
                        CustomerInfo customer = new CustomerInfo(customerId, customerName, vehiclePlateNumber);
                        customer.setServices(services);
    
                        if (services.length <= 3) {
                            if (services.length <= 1) {
                                queue1.add(customer);
                            } else {
                                queue2.add(customer);
                            }
                        } else {
                            queue3.add(customer);
                        }
                    } else {
                        System.out.println("Invalid data format: " + line);
                        JOptionPane.showMessageDialog(this, "Error: Invalid data format in CustomerList.txt");
                    }
                }
            } catch (IOException ex) {
                // Handle the exception, by displaying an error message
                JOptionPane.showMessageDialog(this, "Error reading customer data: " + ex.getMessage());
            }
            JOptionPane.showMessageDialog(this, "Customer data uploaded successfully!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error uploading customer data: " + ex.getMessage());
        }
    }

    private void openCompletedTransWin() {
        CompletedTransactionWin completedTransWin = new CompletedTransactionWin(processor);
        completedTransWin.setVisible(true);
    }

    private void createCentrePanel() {
        JPanel customerTablePanel = new JPanel();
        customerTablePanel.setLayout(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Customer ID", "Name", "Vehicle No.", "Services"}, 0);
        JTable table = new JTable(model);
        customerTablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(customerTablePanel, BorderLayout.CENTER);
        viewCustomersBeforeQueue(model);
    }

        private void viewCustomersBeforeQueue(DefaultTableModel model) {
            try (BufferedReader reader = new BufferedReader(new FileReader("CustomerList.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(", ");
                    if (data.length >= 4) {
                        String customerId = data[0];
                        String customerName = data[1];
                        String vehiclePlateNumber = data[2];
                        String services = data[3].replace(";", ", ");
        
                        model.addRow(new Object[]{customerId, customerName, vehiclePlateNumber, services});
                    } else {
                        System.out.println("Invalid data format: " + line);
                        JOptionPane.showMessageDialog(this, "Error: Invalid data format in CustomerList.txt");
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading customer data: " + ex.getMessage());
            }
        }
    
        private void openQueueWindow(String title, Queue<CustomerInfo> queue) {
            JDialog dialog = new JDialog(this, title, true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setLayout(new BorderLayout());
        
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (CustomerInfo customer : queue) {
                listModel.addElement(customer.getCustomerName() + " - " + customer.getCustomerId());
            }
        
            JList<String> list = new JList<>(listModel);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(list);
            dialog.add(scrollPane, BorderLayout.CENTER);
        
            JPanel buttonPanel = new JPanel();
            JButton completeButton = new JButton("Complete Transaction");
            completeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedIndex = list.getSelectedIndex();
                    if (selectedIndex != -1) {
                        String selectedItem = listModel.getElementAt(selectedIndex);
                        String customerId = selectedItem.split(" - ")[1];
                        CustomerInfo selectedCustomer = queue.stream()
                            .filter(c -> c.getCustomerId().equals(customerId))
                            .findFirst()
                            .orElse(null);
        
                        if (selectedCustomer != null) {
                            int confirm = JOptionPane.showConfirmDialog(dialog,
                                "Are you sure you want to complete the transaction for " + selectedCustomer.getCustomerName() + "?",
                                "Confirm Transaction", JOptionPane.YES_NO_OPTION);
                            if (confirm == JOptionPane.YES_OPTION) {
                                queue.remove(selectedCustomer);
                                processor.markCustomerAsComplete(selectedCustomer.getCustomerId());
                                listModel.remove(selectedIndex);
                                JOptionPane.showMessageDialog(dialog, "Transaction completed for " + selectedCustomer.getCustomerName());
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Please select a customer from the list.");
                    }
                }
            });
            buttonPanel.add(completeButton);
        
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            buttonPanel.add(closeButton);
        
            dialog.add(buttonPanel, BorderLayout.SOUTH);
        
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }
    
        public static void main(String[] args) {
            new MainDashboard();
        }
    }