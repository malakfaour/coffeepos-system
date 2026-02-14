package view;

import dao.ProductDAO;
import dao.CategoryDAO;
import dao.impl.ProductDAOImpl;
import dao.impl.CategoryDAOImpl;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Category;
import model.Product;
import util.ErrorHandler;

import java.util.List;

public class ProductFormView {

    private final Stage stage;
    private final Product editingProduct;

    private final ProductDAO productDAO = new ProductDAOImpl();
    private final CategoryDAO categoryDAO = new CategoryDAOImpl();

    private TextField txtSku;
    private TextField txtName;
    private TextField txtPrice;
    private TextField txtStock;
    private TextField txtBarcode;

    // NEW FIELD ✔
    private ChoiceBox<Category> cbCategory;

    private CheckBox chkActive;

    public ProductFormView(Stage stage, Product product) {
        this.stage = stage;
        this.editingProduct = product;
    }

    public Parent build() {

        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        root.getStyleClass().add("background-latte");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15));
        header.getStyleClass().add("header-coffee");

        Label title = new Label(editingProduct == null ? "Add Product" : "Edit Product");
        title.getStyleClass().add("header-title");
        header.getChildren().add(title);

        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(20));
        formBox.setAlignment(Pos.CENTER);
        formBox.getStyleClass().add("form-box");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);

        txtSku = new TextField();
        txtSku.getStyleClass().add("input-field");

        txtName = new TextField();
        txtName.getStyleClass().add("input-field");

        // NEW DROPDOWN ✔
        cbCategory = new ChoiceBox<>();
        cbCategory.getStyleClass().add("input-field");

        try {
            List<Category> categories = categoryDAO.findAll();
            cbCategory.getItems().addAll(categories);
        } catch (Exception ex) {
            ErrorHandler.showError("Failed to load categories");
        }

        txtPrice = new TextField();
        txtPrice.getStyleClass().add("input-field");

        txtStock = new TextField();
        txtStock.getStyleClass().add("input-field");

        txtBarcode = new TextField();
        txtBarcode.getStyleClass().add("input-field");

        chkActive = new CheckBox();

        grid.addRow(0, new Label("SKU:"), txtSku);
        grid.addRow(1, new Label("Name:"), txtName);
        grid.addRow(2, new Label("Category:"), cbCategory); // ✔ Replaced text field
        grid.addRow(3, new Label("Price:"), txtPrice);
        grid.addRow(4, new Label("Stock Qty:"), txtStock);
        grid.addRow(5, new Label("Barcode:"), txtBarcode);
        grid.addRow(6, new Label("Active:"), chkActive);

        Button btnSave = new Button("Save");
        btnSave.getStyleClass().add("coffee-button");
        btnSave.setOnAction(e -> onSave());

        Button btnCancel = new Button("Cancel");
        btnCancel.getStyleClass().add("coffee-button-light");
        btnCancel.setOnAction(e -> stage.close());

        HBox buttons = new HBox(15, btnSave, btnCancel);
        buttons.setAlignment(Pos.CENTER);

        formBox.getChildren().addAll(grid, buttons);

        root.setTop(header);
        root.setCenter(formBox);

        if (editingProduct != null) fillForm();

        return root;
    }

    private void fillForm() {
        txtSku.setText(editingProduct.getSku());
        txtName.setText(editingProduct.getName());

        if (editingProduct.getCategory() != null) {
            cbCategory.getSelectionModel().select(editingProduct.getCategory());
        }

        txtPrice.setText(String.valueOf(editingProduct.getPrice()));
        txtStock.setText(String.valueOf(editingProduct.getStockQty()));
        chkActive.setSelected(editingProduct.isActive());
        txtBarcode.setText(editingProduct.getBarcode());
    }

    private void onSave() {
        try {
            if (txtSku.getText().isEmpty() ||
                txtName.getText().isEmpty() ||
                cbCategory.getValue() == null ||
                txtPrice.getText().isEmpty() ||
                txtStock.getText().isEmpty()) {

                ErrorHandler.showWarning("All fields must be filled.");
                return;
            }

            double price = Double.parseDouble(txtPrice.getText());
            int stock = Integer.parseInt(txtStock.getText());
            Category category = cbCategory.getValue(); // ✔ NO MORE TEXT PARSING

            if (editingProduct == null) {
                Product p = new Product();
                p.setSku(txtSku.getText());
                p.setName(txtName.getText());
                p.setCategory(category);
                p.setPrice(price);
                p.setStockQty(stock);
                p.setActive(chkActive.isSelected());
                p.setBarcode(txtBarcode.getText());

                productDAO.create(p);
                ErrorHandler.showInfo("Product added successfully!");
            } else {
                editingProduct.setSku(txtSku.getText());
                editingProduct.setName(txtName.getText());
                editingProduct.setCategory(category);
                editingProduct.setPrice(price);
                editingProduct.setStockQty(stock);
                editingProduct.setActive(chkActive.isSelected());
                editingProduct.setBarcode(txtBarcode.getText());

                productDAO.update(editingProduct);
                ErrorHandler.showInfo("Product updated successfully!");
            }

            stage.close();

        } catch (NumberFormatException ex) {
            ErrorHandler.showWarning("Price and Stock must be numeric.");
        } catch (Exception ex) {
            ErrorHandler.showError("Error saving product: " + ex.getMessage());
        }
    }
}
