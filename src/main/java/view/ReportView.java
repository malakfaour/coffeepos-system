package view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Order;
import service.impl.ReportServiceImpl;
import util.ErrorHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReportView {

    private final Stage stage;
    private final model.User currentUser;

    private DatePicker dpStart;
    private DatePicker dpEnd;
    private TableView<Order> tblReport;

    private Label lblTotalSales;
    private Label lblOrderCount;
    private Label lblAvgOrder;

    // 🔥 Z-Report labels
    private Label lblZCash;
    private Label lblZCard;
    private Label lblZDiscount;
    private Label lblZTax;
    private Label lblZNet;

    // Z-Report Panel
    private VBox zReportPanel;

    private final ReportServiceImpl reportService = new ReportServiceImpl();

    public ReportView(Stage stage, model.User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public Parent build() {

        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        root.getStyleClass().add("background-latte");

        // ---------- HEADER ----------
        HBox header = new HBox(10);
        header.getStyleClass().add("header-coffee");
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("📊 Sales Reports");
        title.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnBack = new Button("Back");
        btnBack.getStyleClass().add("coffee-button-light");
        btnBack.setOnAction(e -> backToDashboard());

        header.getChildren().addAll(title, spacer, btnBack);

        // ---------- FILTER BAR ----------
        HBox filters = new HBox(10);
        filters.setPadding(new Insets(15));
        filters.setAlignment(Pos.CENTER);
        filters.getStyleClass().add("report-filter-card");

        dpStart = new DatePicker();
        dpEnd = new DatePicker();
        dpStart.getStyleClass().add("input-field");
        dpEnd.getStyleClass().add("input-field");

        Button btnLoad = new Button("Load");
        btnLoad.getStyleClass().add("coffee-button");
        btnLoad.setOnAction(e -> loadReport());

        Button btnZ = new Button("Z-Report");
        btnZ.getStyleClass().add("coffee-button-light");
        btnZ.setOnAction(e -> loadZReport());

        filters.getChildren().addAll(
                new Label("Start:"), dpStart,
                new Label("End:"), dpEnd,
                btnLoad, btnZ
        );

        // ---------- SUMMARY CARDS ----------
        HBox summary = new HBox(20);
        summary.setPadding(new Insets(20));
        summary.setAlignment(Pos.CENTER);
        summary.getStyleClass().add("summary-cards-container");

        lblTotalSales = createSummaryCard("💰 Total Sales", "0.00");
        lblOrderCount = createSummaryCard("📝 Total Orders", "0");
        lblAvgOrder = createSummaryCard("📈 Avg Order", "0.00");

        summary.getChildren().addAll(lblTotalSales, lblOrderCount, lblAvgOrder);

        // ---------- Z-REPORT PANEL ----------
        zReportPanel = new VBox(10);
        zReportPanel.setPadding(new Insets(20));
        zReportPanel.setAlignment(Pos.CENTER_LEFT);
        zReportPanel.getStyleClass().add("zreport-panel");
        zReportPanel.setVisible(false); // hidden until user clicks "Z-Report"

        Label zTitle = new Label("📘 Z-REPORT SUMMARY");
        zTitle.getStyleClass().add("summary-card-title");

        lblZCash = new Label("💵 Cash Sales: 0.00");
        lblZCard = new Label("💳 Card Sales: 0.00");
        lblZDiscount = new Label("🏷 Discount Total: 0.00");
        lblZTax = new Label("🧾 Tax Total: 0.00");
        lblZNet = new Label("🟢 Net Sales: 0.00");

        zReportPanel.getChildren().addAll(
                zTitle,
                lblZCash,
                lblZCard,
                lblZDiscount,
                lblZTax,
                lblZNet
        );

        // ---------- TABLE ----------
        VBox tableCard = new VBox(10);
        tableCard.getStyleClass().add("report-table-card");
        tableCard.setPadding(new Insets(15));

        Label tblTitle = new Label("Orders List");
        tblTitle.getStyleClass().add("report-table-title");

        tblReport = new TableView<>();
        tblReport.getStyleClass().add("coffee-table");

        TableColumn<Order, String> colCode = new TableColumn<>("Order Code");
        colCode.setPrefWidth(150);
        colCode.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getOrderCode()));

        TableColumn<Order, String> colDate = new TableColumn<>("Date");
        colDate.setPrefWidth(200);
        colDate.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getOrderDateTime() != null ?
                                d.getValue().getOrderDateTime().toString() : ""
                ));

        TableColumn<Order, String> colCashier = new TableColumn<>("Cashier");
        colCashier.setPrefWidth(150);
        colCashier.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getCashier() != null ?
                                d.getValue().getCashier().getUsername() : ""
                ));

        TableColumn<Order, Double> colTotal = new TableColumn<>("Total");
        colTotal.setPrefWidth(100);
        colTotal.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getTotal()));

        tblReport.getColumns().addAll(colCode, colDate, colCashier, colTotal);

        tableCard.getChildren().addAll(tblTitle, tblReport);

        // ---------- FINAL LAYOUT ----------
        VBox centerLayout = new VBox(20);
        centerLayout.setPadding(new Insets(20));
        centerLayout.getChildren().addAll(filters, summary, zReportPanel, tableCard);

        root.setTop(header);
        root.setCenter(centerLayout);

        return root;
    }

    // ---------- SUMMARY CARD FACTORY ----------
    private Label createSummaryCard(String title, String value) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("summary-card");

        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("summary-card-title");

        Label lblValue = new Label(value);
        lblValue.getStyleClass().add("summary-card-value");

        card.getChildren().addAll(lblTitle, lblValue);

        return lblValue;
    }

    // ---------- LOAD NORMAL REPORT ----------
    private void loadReport() {
        LocalDate start = dpStart.getValue();
        LocalDate end = dpEnd.getValue();

        if (start == null || end == null) {
            ErrorHandler.showWarning("Please select both start and end dates.");
            return;
        }

        try {
            List<Order> orders = reportService.getOrdersBetween(start, end);
            tblReport.setItems(FXCollections.observableList(orders));

            double total = orders.stream().mapToDouble(Order::getTotal).sum();
            double avg = orders.isEmpty() ? 0 : total / orders.size();

            lblTotalSales.setText(String.format("%.2f", total));
            lblOrderCount.setText(String.valueOf(orders.size()));
            lblAvgOrder.setText(String.format("%.2f", avg));

        } catch (Exception e) {
            ErrorHandler.showError("Error loading report: " + e.getMessage());
        }
    }

    // ---------- LOAD Z-REPORT ----------
    private void loadZReport() {
        LocalDate day = dpStart.getValue();

        if (day == null) {
            ErrorHandler.showWarning("Select a date for Z-Report (use Start Date).");
            return;
        }

        try {
            Map<String, Double> z = reportService.getDailyZReport(day);

            lblZCash.setText("💵 Cash Sales: " +
                    String.format("%.2f", z.getOrDefault("cashSales", 0.0)));
            lblZCard.setText("💳 Card Sales: " +
                    String.format("%.2f", z.getOrDefault("cardSales", 0.0)));
            lblZDiscount.setText("🏷 Discount Total: " +
                    String.format("%.2f", z.getOrDefault("discountTotal", 0.0)));
            lblZTax.setText("🧾 Tax Total: " +
                    String.format("%.2f", z.getOrDefault("taxTotal", 0.0)));
            lblZNet.setText("🟢 Net Sales: " +
                    String.format("%.2f", z.getOrDefault("netTotal", 0.0)));

            zReportPanel.setVisible(true);
            ErrorHandler.showInfo("Z-Report loaded for " + day);

        } catch (Exception e) {
            ErrorHandler.showError("Error generating Z-Report: " + e.getMessage());
        }
    }

    // ---------- BACK ----------
    private void backToDashboard() {
        DashboardView dash = new DashboardView(stage, currentUser);
        stage.setScene(new Scene(dash.build(), 800, 500));
    }
}
