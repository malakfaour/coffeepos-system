package view;

import dao.ProductDAO;
import dao.impl.ProductDAOImpl;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Product;
import service.InventoryService;
import service.impl.InventoryServiceImpl;
import util.ErrorHandler;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ProductsView {

    private final Stage stage;
    private final model.User currentUser;

    private TableView<Product> tblProducts;
    private TextField txtSearch;


    private ChoiceBox<String> cbCategoryFilter;
    private ChoiceBox<String> cbSort;
    private List<Product> allProducts;

    private final ProductDAO productDAO = new ProductDAOImpl();
    private final InventoryService inventoryService = new InventoryServiceImpl();

    public ProductsView(Stage stage, model.User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public Parent build() {

        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        root.getStyleClass().add("background-latte");

    
        HBox header = new HBox(10);
        header.setPadding(new Insets(15));
        header.getStyleClass().add("header-coffee");

        Label title = new Label("📦 Product Management");
        title.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnBack = new Button("Back");
        btnBack.getStyleClass().add("coffee-button-light");
        btnBack.setOnAction(e -> backToDashboard());

        header.getChildren().addAll(title, spacer, btnBack);

 
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(12));
        topBar.setAlignment(javafx.geometry.Pos.CENTER);

        txtSearch = new TextField();
        txtSearch.setPromptText("Search product...");
        txtSearch.getStyleClass().add("input-field");

 
        txtSearch.setOnKeyReleased(e -> applyFilters());

        Button btnSearch = new Button("Search");
        btnSearch.getStyleClass().add("coffee-button");
        btnSearch.setOnAction(e -> applyFilters());

        Button btnAdd = new Button("Add");
        btnAdd.getStyleClass().add("coffee-button");
        btnAdd.setOnAction(e -> openForm(null));

        Button btnEdit = new Button("Edit");
        btnEdit.getStyleClass().add("coffee-button");
        btnEdit.setOnAction(e -> editSelected());

        Button btnDelete = new Button("Delete");
        btnDelete.getStyleClass().add("coffee-button-light");
        btnDelete.setOnAction(e -> deleteSelected());

        HBox filterBar = new HBox(10);
        filterBar.setPadding(new Insets(10));
        filterBar.setAlignment(javafx.geometry.Pos.CENTER);

        cbCategoryFilter = new ChoiceBox<>();
        cbCategoryFilter.getStyleClass().add("input-field");
        cbCategoryFilter.getItems().add("All Categories");
        cbCategoryFilter.setValue("All Categories");
        cbCategoryFilter.setOnAction(e -> applyFilters());

        cbSort = new ChoiceBox<>();
        cbSort.getStyleClass().add("input-field");
        cbSort.getItems().addAll(
                "Default",
                "Price: Low → High",
                "Price: High → Low",
                "Name: A → Z",
                "Name: Z → A"
        );
        cbSort.setValue("Default");
        cbSort.setOnAction(e -> applyFilters());

        filterBar.getChildren().addAll(
                new Label("Category:"), cbCategoryFilter,
                new Label("Sort:"), cbSort
        );

        topBar.getChildren().addAll(txtSearch, btnSearch, btnAdd, btnEdit, btnDelete);


        tblProducts = new TableView<>();
        tblProducts.getStyleClass().add("coffee-table");

        TableColumn<Product, String> colSku = new TableColumn<>("SKU");
        colSku.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getSku()));

        TableColumn<Product, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getName()));

        TableColumn<Product, String> colCategory = new TableColumn<>("Category");
        colCategory.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getCategory() != null ? d.getValue().getCategory().getName() : ""
                ));

        TableColumn<Product, Double> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getPrice()));

        TableColumn<Product, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getStockQty()));

        TableColumn<Product, Boolean> colActive = new TableColumn<>("Active");
        colActive.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().isActive()));

        tblProducts.getColumns().addAll(colSku, colName, colCategory, colPrice, colStock, colActive);

   
        VBox centerLayout = new VBox(10, filterBar, tblProducts);
        centerLayout.setPadding(new Insets(10));

        root.setTop(header);
        root.setCenter(centerLayout);
        root.setBottom(topBar);

        loadProducts();
        return root;
    }


    private void loadProducts() {
        try {
            allProducts = inventoryService.listAll();

            cbCategoryFilter.getItems().clear();
            cbCategoryFilter.getItems().add("All Categories");
            cbCategoryFilter.getItems().addAll(
                    allProducts.stream()
                            .map(p -> p.getCategory().getName())
                            .distinct()
                            .sorted()
                            .toList()
            );
            cbCategoryFilter.setValue("All Categories");

            applyFilters();

        } catch (Exception ex) {
            ErrorHandler.showError("Error loading products: " + ex.getMessage());
        }
    }


    private void applyFilters() {
        if (allProducts == null) return;

        String keyword = txtSearch.getText().trim().toLowerCase();
        String selectedCategory = cbCategoryFilter.getValue();
        String sortOption = cbSort.getValue();

        List<Product> filtered = allProducts.stream()
                .filter(p ->
                        p.getName().toLowerCase().contains(keyword) ||
                        p.getSku().toLowerCase().contains(keyword)
                )
                .filter(p ->
                        selectedCategory.equals("All Categories") ||
                        p.getCategory().getName().equals(selectedCategory)
                )
                .collect(Collectors.toList());

  
        switch (sortOption) {
            case "Price: Low → High" -> filtered.sort(Comparator.comparing(Product::getPrice));
            case "Price: High → Low" -> filtered.sort(Comparator.comparing(Product::getPrice).reversed());
            case "Name: A → Z" -> filtered.sort(Comparator.comparing(Product::getName));
            case "Name: Z → A" -> filtered.sort(Comparator.comparing(Product::getName).reversed());
        }

        tblProducts.setItems(FXCollections.observableList(filtered));
    }

    private void openForm(Product product) {
        Stage dialog = new Stage();
        dialog.initOwner(stage);
        dialog.initModality(Modality.WINDOW_MODAL);

        ProductFormView formView = new ProductFormView(dialog, product);
        dialog.setScene(new Scene(formView.build(), 450, 450));
        dialog.setTitle(product == null ? "Add Product" : "Edit Product");
        dialog.showAndWait();
        loadProducts();
    }

    private void editSelected() {
        Product p = tblProducts.getSelectionModel().getSelectedItem();
        if (p == null) {
            ErrorHandler.showWarning("Select a product to edit.");
            return;
        }
        openForm(p);
    }

    private void deleteSelected() {
        Product p = tblProducts.getSelectionModel().getSelectedItem();
        if (p == null) {
            ErrorHandler.showWarning("Select a product to delete.");
            return;
        }
        try {
            productDAO.delete(p.getId());
            loadProducts();
        } catch (Exception ex) {
            ErrorHandler.showError("Delete failed: " + ex.getMessage());
        }
    }

    private void backToDashboard() {
        DashboardView dash = new DashboardView(stage, currentUser);
        stage.setScene(new Scene(dash.build(), 800, 500));
    }
}
