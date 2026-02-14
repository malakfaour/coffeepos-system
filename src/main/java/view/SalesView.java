package view;

import dao.ProductDAO;
import dao.impl.ProductDAOImpl;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Order;
import model.OrderItem;
import model.Product;
import model.pricing.PercentDiscount;
import service.SalesService;
import service.impl.SalesServiceImpl;
import util.ErrorHandler;
import config.AppSettings;
import javafx.stage.Modality;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SalesView {

    private final Stage stage;
    private final model.User currentUser;

    private final SalesService salesService = new SalesServiceImpl();
    private final ProductDAO productDAO = new ProductDAOImpl();

    private final Order currentOrder = new Order();

    private TableView<Product> tblProducts;
    private TableView<OrderItem> tblCart;

    private TextField txtSearchProduct;
    private TextField txtScanBarcode;
    private TextField txtQty;
    private ChoiceBox<String> cbPayment;
    private TextField txtPaidAmount;

    private ChoiceBox<String> cbCategoryFilter;
    private ChoiceBox<String> cbSort;

    private Label lblSubtotal;
    private Label lblDiscount;
    private Label lblTax;
    private Label lblTotal;

    private List<Product> allProducts;

    // ✅ FIXED: constructor must use model.User
    public SalesView(Stage stage, model.User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;

        currentOrder.setCashier(currentUser);  // ✔ Correct placement
    }

    public Parent build() {

        BorderPane root = new BorderPane();

        root.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        root.getStyleClass().add("background-latte");

        // ---------- TOP BAR ----------
        HBox top = new HBox(10);
        top.setPadding(new Insets(12));
        top.getStyleClass().add("header-coffee");

        Label lblTitle = new Label("💸 Sales Screen");
        lblTitle.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        txtSearchProduct = new TextField();
        txtSearchProduct.setPromptText("Search SKU/name");
        txtSearchProduct.getStyleClass().add("input-field");
        txtSearchProduct.setOnKeyReleased(e -> applyFilters());

        txtScanBarcode = new TextField();
        txtScanBarcode.setPromptText("Scan barcode...");
        txtScanBarcode.getStyleClass().add("input-field");
        txtScanBarcode.setOnAction(e -> scanBarcode());

        Button btnBack = new Button("Back");
        btnBack.getStyleClass().add("coffee-button-light");
        btnBack.setOnAction(e -> backToDashboard());

        top.getChildren().addAll(lblTitle, spacer, txtSearchProduct, txtScanBarcode, btnBack);

        // ---------- CENTER LAYOUT ----------
        SplitPane center = new SplitPane();

        // LEFT SIDE: PRODUCTS + FILTERS
        VBox left = new VBox(10);
        left.setPadding(new Insets(15));
        left.getStyleClass().add("panel-box");

        HBox filterBar = new HBox(10);
        filterBar.setPadding(new Insets(5, 5, 10, 5));

        cbCategoryFilter = new ChoiceBox<>();
        cbCategoryFilter.getStyleClass().add("input-field");
        cbCategoryFilter.getItems().add("All Categories");
        cbCategoryFilter.setValue("All Categories");
        cbCategoryFilter.setOnAction(e -> applyFilters());

        cbSort = new ChoiceBox<>();
        cbSort.getStyleClass().add("input-field");
        cbSort.getItems().addAll(
                "Default",
                "Price: Low to High",
                "Price: High to Low",
                "Name: A → Z",
                "Name: Z → A"
        );
        cbSort.setValue("Default");
        cbSort.setOnAction(e -> applyFilters());

        filterBar.getChildren().addAll(new Label("Category:"), cbCategoryFilter,
                new Label("Sort:"), cbSort);

        tblProducts = new TableView<>();
        tblProducts.getStyleClass().add("coffee-table");

        TableColumn<Product, String> colSku = new TableColumn<>("SKU");
        colSku.setPrefWidth(90);
        colSku.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getSku()));

        TableColumn<Product, String> colName = new TableColumn<>("Name");
        colName.setPrefWidth(200);
        colName.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getName()));

        TableColumn<Product, Double> colPrice = new TableColumn<>("Price");
        colPrice.setPrefWidth(90);
        colPrice.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getPrice()));

        TableColumn<Product, Integer> colStock = new TableColumn<>("Stock");
        colStock.setPrefWidth(80);
        colStock.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getStockQty()));

        tblProducts.getColumns().addAll(colSku, colName, colPrice, colStock);

        txtQty = new TextField();
        txtQty.setPrefWidth(80);
        txtQty.getStyleClass().add("input-field");

        Button btnAddToCart = new Button("Add to Cart");
        btnAddToCart.getStyleClass().add("coffee-button");
        btnAddToCart.setOnAction(e -> addToCart());

        HBox qtyBox = new HBox(10, new Label("Qty:"), txtQty, btnAddToCart);

        left.getChildren().addAll(filterBar, tblProducts, qtyBox);

        // RIGHT SIDE: CART + TOTALS
        VBox right = new VBox(12);
        right.setPadding(new Insets(15));
        right.getStyleClass().add("panel-box");

        tblCart = new TableView<>();
        tblCart.getStyleClass().add("coffee-table");

        TableColumn<OrderItem, String> colCartName = new TableColumn<>("Product");
        colCartName.setPrefWidth(160);
        colCartName.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getProduct().getName()));

        TableColumn<OrderItem, String> colCartOptions = new TableColumn<>("Options");
        colCartOptions.setPrefWidth(180);
        colCartOptions.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getCustomization() == null ? "" : d.getValue().getCustomization()));

        TableColumn<OrderItem, Integer> colCartQty = new TableColumn<>("Qty");
        colCartQty.setPrefWidth(60);
        colCartQty.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getQuantity()));

        TableColumn<OrderItem, Double> colCartPrice = new TableColumn<>("Unit Price");
        colCartPrice.setPrefWidth(90);
        colCartPrice.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getUnitPrice()));

        TableColumn<OrderItem, Double> colCartTotal = new TableColumn<>("Total");
        colCartTotal.setPrefWidth(100);
        colCartTotal.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getLineTotal()));

        tblCart.getColumns().addAll(colCartName, colCartOptions, colCartQty, colCartPrice, colCartTotal);

        cbPayment = new ChoiceBox<>(FXCollections.observableArrayList("CASH", "CARD"));
        cbPayment.setValue("CASH");

        txtPaidAmount = new TextField();
        txtPaidAmount.setPromptText("0.00");
        txtPaidAmount.getStyleClass().add("input-field");

        Button btnDiscount = new Button("Apply Discount");
        btnDiscount.getStyleClass().add("coffee-button-light");
        btnDiscount.setOnAction(e -> applyDiscount());

        Button btnCheckout = new Button("Checkout");
        btnCheckout.getStyleClass().add("coffee-button");
        btnCheckout.setOnAction(e -> checkout());

        VBox totalsBox = new VBox(8);
        totalsBox.getStyleClass().add("summary-box");

        lblSubtotal = new Label("0.00");
        lblDiscount = new Label("0.00");
        lblTax = new Label("0.00");
        lblTotal = new Label("0.00");
        lblTotal.getStyleClass().add("summary-total");

        totalsBox.getChildren().addAll(
                createTotalRow("Subtotal:", lblSubtotal),
                createTotalRow("Discount:", lblDiscount),
                createTotalRow("Tax:", lblTax),
                new Separator(),
                createTotalRow("TOTAL:", lblTotal)
        );

        right.getChildren().addAll(
                tblCart,
                new HBox(10, new Label("Payment:"), cbPayment),
                new HBox(10, new Label("Paid:"), txtPaidAmount),
                btnDiscount,
                btnCheckout,
                totalsBox
        );

        center.getItems().addAll(left, right);

        root.setTop(top);
        root.setCenter(center);

        loadAllProducts();
        refreshTotals();

        return root;
    }

    private HBox createTotalRow(String title, Label value) {
        return new HBox(10, new Label(title), value);
    }

    private void loadAllProducts() {
        try {
            allProducts = productDAO.findAll();

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

        } catch (Exception e) {
            ErrorHandler.showError("Error loading products: " + e.getMessage());
        }
    }

    private void applyFilters() {
        if (allProducts == null) return;

        String keyword = txtSearchProduct.getText().trim().toLowerCase();
        String selectedCategory = cbCategoryFilter.getValue();
        String sortOption = cbSort.getValue();

        List<Product> filtered = allProducts.stream()
                .filter(p ->
                        p.getName().toLowerCase().contains(keyword) ||
                                p.getSku().toLowerCase().contains(keyword)
                )
                .filter(p ->
                        selectedCategory == null ||
                                selectedCategory.equals("All Categories") ||
                                p.getCategory().getName().equals(selectedCategory)
                )
                .collect(Collectors.toList());

        if (sortOption != null) {
            switch (sortOption) {
                case "Price: Low to High" ->
                        filtered.sort(Comparator.comparing(Product::getPrice));
                case "Price: High to Low" ->
                        filtered.sort(Comparator.comparing(Product::getPrice).reversed());
                case "Name: A → Z" ->
                        filtered.sort(Comparator.comparing(Product::getName));
                case "Name: Z → A" ->
                        filtered.sort(Comparator.comparing(Product::getName).reversed());
                default -> {
                }
            }
        }

        tblProducts.setItems(FXCollections.observableList(filtered));
    }

    private void scanBarcode() {
        String code = txtScanBarcode.getText().trim();
        if (code.isEmpty()) return;

        try {
            Product p = productDAO.findByBarcode(code);
            if (p == null) {
                ErrorHandler.showWarning("No product found for barcode: " + code);
                txtScanBarcode.clear();
                return;
            }

            salesService.addToCart(currentOrder, p, 1);
            tblCart.setItems(FXCollections.observableList(currentOrder.getItems()));
            refreshTotals();

            txtScanBarcode.clear();

        } catch (Exception e) {
            ErrorHandler.showError("Barcode error: " + e.getMessage());
        }
    }

    private void addToCart() {
        Product selected = tblProducts.getSelectionModel().getSelectedItem();
        if (selected == null) {
            ErrorHandler.showWarning("Select a product first.");
            return;
        }

        if (!txtQty.getText().matches("\\d+")) {
            ErrorHandler.showWarning("Enter a valid quantity.");
            return;
        }

        int qty = Integer.parseInt(txtQty.getText());
        if (qty <= 0) {
            ErrorHandler.showWarning("Quantity must be greater than 0.");
            return;
        }

        String categoryName = selected.getCategory().getName().trim().toLowerCase();

        boolean isDrink =
                categoryName.contains("hot drink") ||
                categoryName.contains("cold drink");

        if (isDrink) {
            openCustomizationPopup(selected, qty);
        } else {
            try {
                salesService.addToCart(currentOrder, selected, qty);
                tblCart.setItems(FXCollections.observableList(currentOrder.getItems()));
                refreshTotals();
            } catch (Exception e) {
                ErrorHandler.showError(e.getMessage());
            }
        }
    }

    private void openCustomizationPopup(Product selectedProduct, int qty) {
        Stage popup = new Stage();
        popup.setTitle("Customize Drink");

        Label title = new Label("☕ Customize Your Drink");
        title.getStyleClass().add("customize-title");

        Label lblSize = new Label("Size");
        lblSize.getStyleClass().add("customize-section-title");

        ChoiceBox<String> cbSize = new ChoiceBox<>(FXCollections.observableArrayList(
                "Small",
                "Medium (+0.50)",
                "Large (+1.00)"
        ));
        cbSize.setValue("Medium (+0.50)");
        cbSize.getStyleClass().add("customize-choice");

        Label lblSugar = new Label("Sugar Level");
        lblSugar.getStyleClass().add("customize-section-title");

        ChoiceBox<String> cbSugar = new ChoiceBox<>(FXCollections.observableArrayList(
                "No Sugar", "Less", "Normal", "Extra"
        ));
        cbSugar.setValue("Normal");
        cbSugar.getStyleClass().add("customize-choice");

        Label lblMilk = new Label("Milk Type");
        lblMilk.getStyleClass().add("customize-section-title");

        ChoiceBox<String> cbMilk = new ChoiceBox<>(FXCollections.observableArrayList(
                "Regular",
                "Oat Milk (+0.50)",
                "Almond Milk (+0.50)"
        ));
        cbMilk.setValue("Regular");
        cbMilk.getStyleClass().add("customize-choice");

        CheckBox chkExtraShot = new CheckBox("Extra Espresso Shot (+0.75)");
        chkExtraShot.getStyleClass().add("customize-checkbox");

        Label lblExtraCost = new Label("Extra: 0.00");
        lblExtraCost.getStyleClass().add("customize-extra");

        Button btnApply = new Button("Add to Cart");
        btnApply.getStyleClass().add("customize-add-btn");

        VBox box = new VBox(12);
        box.getStyleClass().add("customize-popup");

        box.getChildren().addAll(
                title,
                lblSize, cbSize,
                lblSugar, cbSugar,
                lblMilk, cbMilk,
                chkExtraShot,
                lblExtraCost,
                btnApply
        );

        Runnable updateExtraCost = () -> {
            double extra = 0;

            if (cbSize.getValue().contains("Medium")) extra += 0.50;
            if (cbSize.getValue().contains("Large")) extra += 1.00;
            if (cbMilk.getValue().contains("+0.50")) extra += 0.50;
            if (chkExtraShot.isSelected()) extra += 0.75;

            lblExtraCost.setText(String.format("Extra: %.2f", extra));
        };

        cbSize.setOnAction(e -> updateExtraCost.run());
        cbMilk.setOnAction(e -> updateExtraCost.run());
        chkExtraShot.setOnAction(e -> updateExtraCost.run());
        updateExtraCost.run();

        btnApply.setOnAction(e -> {
            try {
                String customization =
                        cbSize.getValue() + ", " +
                        cbSugar.getValue() + ", " +
                        cbMilk.getValue() +
                        (chkExtraShot.isSelected() ? ", Extra Shot" : "");

                double customPrice = 0;

                if (cbSize.getValue().contains("Medium")) customPrice += 0.50;
                if (cbSize.getValue().contains("Large")) customPrice += 1.00;
                if (cbMilk.getValue().contains("+0.50")) customPrice += 0.50;
                if (chkExtraShot.isSelected()) customPrice += 0.75;

                salesService.addToCart(currentOrder, selectedProduct, qty);

                OrderItem last = currentOrder.getItems().get(currentOrder.getItems().size() - 1);
                last.setCustomization(customization);
                last.setCustomizationPrice(customPrice);
                last.recalculateLineTotal();

                tblCart.setItems(FXCollections.observableList(currentOrder.getItems()));
                refreshTotals();

                popup.close();
            } catch (Exception ex) {
                ErrorHandler.showError(ex.getMessage());
            }
        });

        Scene scene = new Scene(box, 350, 450);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        popup.setScene(scene);
        popup.initOwner(stage);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.showAndWait();
    }

    private void applyDiscount() {
        try {
            salesService.applyDiscount(currentOrder, new PercentDiscount(0.10));
            refreshTotals();
            ErrorHandler.showInfo("10% discount applied.");
        } catch (Exception e) {
            ErrorHandler.showError("Discount error: " + e.getMessage());
        }
    }

    private void refreshTotals() {
        double tax = currentOrder.getSubtotal() * AppSettings.TAX_RATE;
        currentOrder.setTax(tax);
        currentOrder.recalcTotals();
        lblSubtotal.setText(String.format("%.2f", currentOrder.getSubtotal()));
        lblDiscount.setText(String.format("%.2f", currentOrder.getDiscount()));
        lblTax.setText(String.format("%.2f", currentOrder.getTax()));
        lblTotal.setText(String.format("%.2f", currentOrder.getTotal()));
    }

    private void checkout() {
        try {
            String paymentMethod = cbPayment.getValue();
            if (paymentMethod == null) {
                ErrorHandler.showWarning("Select a payment method.");
                return;
            }

            if (!txtPaidAmount.getText().matches("\\d+(\\.\\d+)?")) {
                ErrorHandler.showWarning("Enter a valid paid amount.");
                return;
            }

            double paidAmount = Double.parseDouble(txtPaidAmount.getText());

            currentOrder.setCashier(currentUser);
            currentOrder.setOrderDateTime(java.time.LocalDateTime.now());
            currentOrder.setPaymentMethod(paymentMethod);
            currentOrder.setOrderCode("ORD-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            refreshTotals();

            Order completedOrder = salesService.checkout(currentOrder, paymentMethod, paidAmount);

            javafx.scene.Node receiptNode =
                    util.ReceiptGenerator.buildReceiptNode(completedOrder, paymentMethod, paidAmount);

            Stage popup = new Stage();
            popup.setTitle("Receipt Preview");

            ScrollPane scroll = new ScrollPane(receiptNode);
            scroll.setFitToWidth(true);

            Button btnPrint = new Button("Print Receipt");
            btnPrint.getStyleClass().add("coffee-button");
            btnPrint.setOnAction(e -> util.ReceiptPrinter.print(receiptNode, popup));

            VBox box = new VBox(10, scroll, btnPrint);
            box.setPadding(new Insets(10));

            popup.setScene(new Scene(box, 350, 500));
            popup.show();

            currentOrder.getItems().clear();
            tblCart.getItems().clear();
            txtQty.clear();
            txtPaidAmount.clear();

            currentOrder.setSubtotal(0);
            currentOrder.setTotal(0);
            currentOrder.setDiscount(0);
            currentOrder.setTax(0);

            refreshTotals();

        } catch (Exception ex) {
            ErrorHandler.showError("Checkout error: " + ex.getMessage());
        }
    }

    private void backToDashboard() {
        DashboardView dash = new DashboardView(stage, currentUser);
        stage.setScene(new Scene(dash.build(), 800, 500));
    }
}
