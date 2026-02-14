package view;

import dao.CashRegisterClosingDAO;
import dao.impl.CashRegisterClosingDAOImpl;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.CashRegisterClosing;
import model.Product;
import model.User;
import service.DashboardStatsHelper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DashboardView {

    private final Stage stage;
    private final User currentUser;

    private final CashRegisterClosingDAO closingDAO = new CashRegisterClosingDAOImpl();

    private static final double BOX_W = 300;
    private static final double BOX_H = 220;

    private static final double CHART_W = 300;
    private static final double CHART_H = 260;

    public DashboardView(Stage stage, User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public Parent build() {

        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        /* ---------------------------------------------------------
                          HEADER
        --------------------------------------------------------- */
        HBox header = new HBox();
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header-coffee");

        Label title = new Label("☕ CoffeePOS");
        title.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("Welcome, " + currentUser.getUsername());
        userLabel.getStyleClass().add("header-user");

        header.getChildren().addAll(title, spacer, userLabel);
        root.setTop(header);

        /* ---------------------------------------------------------
                          SIDEBAR
        --------------------------------------------------------- */
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(25));
        sidebar.getStyleClass().add("sidebar");

        Label menuTitle = new Label("Main Menu");
        menuTitle.getStyleClass().add("sidebar-title");

        Button btnSales = createSidebarButton("🧾  Sales");
        Button btnProducts = createSidebarButton("📦  Products");
        Button btnReports = createSidebarButton("📊  Reports");
        Button btnCloseRegister = createSidebarButton("🔒  Close Register");
        Button btnLogout = createSidebarButton("🚪  Logout");

        btnSales.setOnAction(e -> openSales());
        btnProducts.setOnAction(e -> { if (isAdmin()) openProducts(); });
        btnReports.setOnAction(e -> { if (isAdmin()) openReports(); });
        btnCloseRegister.setOnAction(e -> closeRegister());
        btnLogout.setOnAction(e -> stage.close());

        btnProducts.setDisable(!isAdmin());
        btnReports.setDisable(!isAdmin());

        sidebar.getChildren().addAll(menuTitle, btnSales, btnProducts, btnReports, btnCloseRegister, btnLogout);
        root.setLeft(sidebar);

        /* ---------------------------------------------------------
                        MAIN CONTENT WRAPPER
        --------------------------------------------------------- */
        VBox outer = new VBox();
        outer.setPadding(new Insets(25));
        outer.setAlignment(Pos.TOP_CENTER);

        // ⭐ NEW: apply latte brown background
        outer.getStyleClass().add("dashboard-background");

        VBox content = new VBox(30);
        content.setAlignment(Pos.TOP_CENTER);
        content.setMaxWidth(1100);

        /* ---------------------------------------------------------
                        TOP DASHBOARD CARDS
        --------------------------------------------------------- */
        HBox cardsRow = new HBox(25);
        cardsRow.setAlignment(Pos.CENTER);

        VBox salesCard = createDashboardCard("🧾", "Sales", "Create new orders");
        salesCard.setOnMouseClicked(e -> openSales());

        VBox productsCard = createDashboardCard("📦", "Products", "Manage items & stock");
        productsCard.setDisable(!isAdmin());
        productsCard.setOnMouseClicked(e -> { if (isAdmin()) openProducts(); });

        VBox reportsCard = createDashboardCard("📊", "Reports", "Sales analytics");
        reportsCard.setDisable(!isAdmin());
        reportsCard.setOnMouseClicked(e -> { if (isAdmin()) openReports(); });

        VBox logoutCard = createDashboardCard("🚪", "Logout", "Exit system");
        logoutCard.setOnMouseClicked(e -> stage.close());

        cardsRow.getChildren().addAll(salesCard, productsCard, reportsCard, logoutCard);

        /* ---------------------------------------------------------
                        FETCH DATA
        --------------------------------------------------------- */
        DashboardStatsHelper stats = new DashboardStatsHelper();
        Map<String, Double> today = stats.getTodayStatsForCashier(currentUser.getId());

        double totalSales = today.getOrDefault("totalSales", 0.0);
        double totalOrders = today.getOrDefault("totalOrders", 0.0);
        double avgOrder   = today.getOrDefault("avgOrder", 0.0);
        double cashSales  = today.getOrDefault("cashSales", 0.0);
        double cardSales  = today.getOrDefault("cardSales", 0.0);

        /* ---------------------------------------------------------
                            SUMMARY BOX
        --------------------------------------------------------- */
        VBox summaryBox = new VBox(6);
        summaryBox.getStyleClass().add("stats-box");
        summaryBox.setPrefSize(BOX_W, BOX_H);

        Label summaryTitle = new Label(isAdmin()
                ? "📅 Today's Summary (Admin)"
                : "📅 Today's Summary (Your Sales)");
        summaryTitle.getStyleClass().add("stats-title");

        summaryBox.getChildren().addAll(
                summaryTitle,
                new Label("Total Sales: " + String.format("%.2f", totalSales)),
                new Label("• Cash: " + String.format("%.2f", cashSales)),
                new Label("• Card: " + String.format("%.2f", cardSales)),
                new Label("Total Orders: " + (int) totalOrders),
                new Label("Average Order: " + String.format("%.2f", avgOrder))
        );

        /* ---------------------------------------------------------
                        LOW STOCK BOX
        --------------------------------------------------------- */
        List<Product> lowStock = stats.getLowStockProducts();

        VBox lowStockBox = new VBox(6);
        lowStockBox.getStyleClass().add("stats-box");
        lowStockBox.setPrefSize(BOX_W, BOX_H);

        Label stockTitle = new Label("⚠️ Low Stock");
        stockTitle.getStyleClass().add("stats-title");

        lowStockBox.getChildren().add(stockTitle);

        if (lowStock.isEmpty()) {
            lowStockBox.getChildren().add(new Label("All inventory healthy"));
        } else {
            lowStock.forEach(p ->
                    lowStockBox.getChildren().add(
                            new Label("• " + p.getName() + " (" + p.getStockQty() + ")")
                    )
            );
        }

        /* ---------------------------------------------------------
                        CHART DATA
        --------------------------------------------------------- */
        BarChart<String, Number> chart7 =
                createBarChart("Last 7 Days", stats.getLast7DaysSales());

        LineChart<String, Number> hourlyChart =
                createLineChart("Hourly Sales", stats.getTodayHourlySalesForCashier(currentUser.getId()));

        PieChart paymentChart =
                createPie("Payment Split", cashSales, cardSales);

        PieChart categoryChart =
                createCategoryPie("Sales by Category", stats.getTodayCategorySalesForCashier(currentUser.getId()));

        /* ---------------------------------------------------------
                        PERFECT GRID LAYOUT (NO ROW 3)
        --------------------------------------------------------- */

        // Row 1
        HBox row1 = new HBox(25, summaryBox, lowStockBox, chart7);
        row1.setAlignment(Pos.CENTER);

        // Row 2
        HBox row2 = new HBox(25, hourlyChart, paymentChart, categoryChart);
        row2.setAlignment(Pos.CENTER);

        /* ---------------------------------------------------------
                        ADD CONTENT
        --------------------------------------------------------- */
        content.getChildren().addAll(cardsRow, row1, row2);
        outer.getChildren().add(content);

        ScrollPane scrollPane = new ScrollPane(outer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        root.setCenter(scrollPane);
        return root;
    }

    /* ---------------------------------------------------------
                        CHART HELPERS
    --------------------------------------------------------- */

    private BarChart<String, Number> createBarChart(String title, Map<String, Double> data) {
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();

        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setTitle(title);
        chart.setPrefSize(CHART_W, CHART_H);
        chart.getStyleClass().add("chart-box");

        XYChart.Series<String, Number> s = new XYChart.Series<>();
        data.forEach((k, v) -> s.getData().add(new XYChart.Data<>(k, v)));
        chart.getData().add(s);

        return chart;
    }

    private LineChart<String, Number> createLineChart(String title, Map<String, Double> data) {
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();

        LineChart<String, Number> chart = new LineChart<>(x, y);
        chart.setTitle(title);
        chart.setPrefSize(CHART_W, CHART_H);
        chart.getStyleClass().add("chart-box");

        XYChart.Series<String, Number> s = new XYChart.Series<>();
        data.forEach((k, v) -> s.getData().add(new XYChart.Data<>(k, v)));
        chart.getData().add(s);

        return chart;
    }

    private PieChart createPie(String title, double cash, double card) {
        PieChart p = new PieChart();
        p.setTitle(title);
        p.setPrefSize(CHART_W, CHART_H);
        p.getStyleClass().add("chart-box");
        p.getData().addAll(
                new PieChart.Data("Cash", cash),
                new PieChart.Data("Card", card)
        );
        return p;
    }

    private PieChart createCategoryPie(String title, Map<String, Double> data) {
        PieChart p = new PieChart();
        p.setTitle(title);
        p.setPrefSize(CHART_W, CHART_H);
        p.getStyleClass().add("chart-box");

        data.forEach((cat, total) ->
                p.getData().add(new PieChart.Data(cat, total))
        );
        return p;
    }

    /* ---------------------------------------------------------
                      UI HELPERS
    --------------------------------------------------------- */

    private boolean isAdmin() {
        return currentUser.getRole() != null &&
                "ADMIN".equalsIgnoreCase(currentUser.getRole().getName());
    }

    private Button createSidebarButton(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("sidebar-btn");
        return b;
    }

    private VBox createDashboardCard(String icon, String title, String subtitle) {
        VBox c = new VBox(10);
        c.setAlignment(Pos.CENTER);
        c.getStyleClass().add("dashboard-card");

        Label i = new Label(icon);
        i.getStyleClass().add("card-icon");

        Label t = new Label(title);
        t.getStyleClass().add("card-title");

        Label s = new Label(subtitle);
        s.getStyleClass().add("card-subtitle");

        c.getChildren().addAll(i, t, s);
        return c;
    }

    private void openSales() {
        stage.setScene(new Scene(new SalesView(stage, currentUser).build(), 1100, 650));
    }

    private void openProducts() {
        stage.setScene(new Scene(new ProductsView(stage, currentUser).build(), 1000, 600));
    }

    private void openReports() {
        stage.setScene(new Scene(new ReportView(stage, currentUser).build(), 1000, 600));
    }

    private void closeRegister() {
        try {
            DashboardStatsHelper statsHelper = new DashboardStatsHelper();

            Map<String, Double> stats = statsHelper.getTodayStatsForCashier(currentUser.getId());
            double expected = stats.getOrDefault("totalSales", 0.0);

            TextInputDialog dialog = new TextInputDialog();
            dialog.getDialogPane().getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm()
            );
            dialog.getDialogPane().getStyleClass().add("close-register-dialog");

            dialog.getDialogPane().lookup(".header-panel")
                    .getStyleClass().add("close-register-header");

            dialog.getDialogPane().lookup(".content")
                    .getStyleClass().add("close-register-label");

            dialog.getEditor().getStyleClass().add("close-register-field");

            dialog.getDialogPane().lookupButton(ButtonType.OK)
                    .getStyleClass().add("close-register-ok");

            dialog.setTitle("Close Cash Register");
            dialog.setHeaderText("Expected total: " + expected);
            dialog.setContentText("Enter counted amount:");

            var result = dialog.showAndWait();
            if (result.isEmpty()) return;

            double counted = Double.parseDouble(result.get());
            double diff = counted - expected;

            CashRegisterClosing closing = new CashRegisterClosing();
            closing.setCashier(currentUser);
            closing.setClosingDateTime(LocalDateTime.now());
            closing.setExpectedTotal(expected);
            closing.setCountedTotal(counted);
            closing.setDifference(diff);

            closingDAO.create(closing);

            Alert a = new Alert(AlertType.INFORMATION);
            a.setHeaderText("Register Closed");
            a.setContentText("""
                    Expected: %.2f
                    Counted: %.2f
                    Difference: %.2f
                    """.formatted(expected, counted, diff));
            a.show();

        } catch (Exception ex) {
            new Alert(AlertType.ERROR, "Error: " + ex.getMessage()).show();
        }
    }
}
