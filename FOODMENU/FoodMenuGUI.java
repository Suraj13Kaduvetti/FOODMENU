import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodMenuGUI extends JFrame {

    private Map<String, Map<String, FoodItem>> foodItems;

    public FoodMenuGUI() {
        setTitle("Food Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400); // Increased size for better display
        setLocationRelativeTo(null);

        initializeFoodItems();

        createMenuBar();

        // Create a main content panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());

        // Add an image to the main window
        ImageIcon mainImageIcon = new ImageIcon("img/logo.png"); // Replace with the actual path to your main image
        JLabel mainImageLabel = new JLabel(mainImageIcon);
        mainContentPanel.add(mainImageLabel, BorderLayout.CENTER);

        // Set the main content panel
        setContentPane(mainContentPanel);

        setVisible(true);
    }

    private void initializeFoodItems() {
        foodItems = new HashMap<>();

        // Read data from text files
        try {
            foodItems.put("Veg", readFoodItemsFromFile("veg_items.txt"));
            foodItems.put("Non-Veg", readFoodItemsFromFile("non_veg_items.txt"));
            foodItems.put("Dessert", readFoodItemsFromFile("dessert_items.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, FoodItem> readFoodItemsFromFile(String filePath) throws IOException {
        Map<String, FoodItem> foodItemsMap = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length == 3) {
                String itemName = parts[0].trim();
                String imageFilePath = parts[1].trim();
                String descriptionFilePath = parts[2].trim();

                FoodItem foodItem = new FoodItem(itemName, imageFilePath, descriptionFilePath);
                foodItemsMap.put(itemName, foodItem);
            }
        }
        return foodItemsMap;
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenuItem vegMenuItem = new JMenuItem("Veg");
        JMenuItem nonVegMenuItem = new JMenuItem("Non-Veg");
        JMenuItem dessertMenuItem = new JMenuItem("Dessert");

        vegMenuItem.addActionListener(new MenuButtonListener("Veg"));
        nonVegMenuItem.addActionListener(new MenuButtonListener("Non-Veg"));
        dessertMenuItem.addActionListener(new MenuButtonListener("Dessert"));

        menuBar.add(vegMenuItem);
        menuBar.add(nonVegMenuItem);
        menuBar.add(dessertMenuItem);

        setJMenuBar(menuBar);
    }

    private class MenuButtonListener implements ActionListener {
        private String category;

        public MenuButtonListener(String category) {
            this.category = category;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            displayCategoryWindow(category);
        }
    }

    private void displayCategoryWindow(String category) {
        JFrame categoryFrame = new JFrame(category + " Items");
        categoryFrame.setSize(500, 500);
        categoryFrame.setLocationRelativeTo(this);

        Map<String, FoodItem> foodItemsMap = foodItems.get(category);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        // Image Label
        ImageIcon defaultImage = new ImageIcon("img/logo.png");  // Replace with the actual path to your default image
        JLabel imageLabel = new JLabel(defaultImage);
        contentPanel.add(imageLabel, BorderLayout.CENTER);

        // Description Label
        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));

        if (foodItemsMap != null) {
            // Update image and description based on selected food item
            JList<String> foodItemList = new JList<>(foodItemsMap.keySet().toArray(new String[0]));
            foodItemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            foodItemList.addListSelectionListener(e -> {
                String selectedFoodItemName = foodItemList.getSelectedValue();
                if (selectedFoodItemName != null) {
                    FoodItem selectedFoodItem = foodItemsMap.get(selectedFoodItemName);
                    updateImageAndDescription(selectedFoodItem, imageLabel, descriptionArea);
                }
            });

            contentPanel.add(new JScrollPane(foodItemList), BorderLayout.WEST);
        } else {
            descriptionArea.setText("No data available for this category.");
        }

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            categoryFrame.dispose();  // Close the current category window
            // You may add additional logic if needed
        });
        contentPanel.add(backButton, BorderLayout.NORTH);

        contentPanel.add(new JScrollPane(descriptionArea), BorderLayout.SOUTH);

        categoryFrame.setContentPane(contentPanel);
        categoryFrame.setVisible(true);
    }

    private void updateImageAndDescription(FoodItem selectedFoodItem, JLabel imageLabel, JTextArea descriptionArea) {
        // Load image from the selected food item
        ImageIcon imageIcon = new ImageIcon(selectedFoodItem.getImageFilePath());
        imageLabel.setIcon(imageIcon);

        try {
            // Read the full description from the file
            String fullDescription = Files.readString(Paths.get(selectedFoodItem.getDescriptionFilePath()));

            // Limit the description to 500 words
            String limitedDescription = limitDescriptionTo500Words(fullDescription);

            // Set the description in the JTextArea
            descriptionArea.setText(limitedDescription);
        } catch (IOException e) {
            e.printStackTrace();
            descriptionArea.setText("Error loading description.");
        }
    }

    private String limitDescriptionTo500Words(String description) {
        String[] words = description.split("\\s+");
        if (words.length > 500) {
            return String.join(" ", Arrays.copyOfRange(words, 0, 500)) + " ...";
        } else {
            return description;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FoodMenuGUI());
    }

    private static class FoodItem {
        private String itemName;
        private String imageFilePath;
        private String descriptionFilePath;

        public FoodItem(String itemName, String imageFilePath, String descriptionFilePath) {
            this.itemName = itemName;
            this.imageFilePath = imageFilePath;
            this.descriptionFilePath = descriptionFilePath;
        }

        public String getItemName() {
            return itemName;
        }

        public String getImageFilePath() {
            return imageFilePath;
        }

        public String getDescriptionFilePath() {
            return descriptionFilePath;
        }
    }
}
