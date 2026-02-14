package util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import model.Order;
import model.OrderItem;

public class ReceiptGenerator {

    public static Node buildReceiptNode(Order order, String paymentMethod, double paidAmount) {

        VBox root = new VBox(6);
        root.setPadding(new Insets(15));
        root.setStyle(
                "-fx-font-family: 'Consolas';" +
                "-fx-font-size: 13px;" +
                "-fx-background-color: white;" +
                "-fx-border-color: #A1887F;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

  
        Label title = new Label("COFFEE POS");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);

        Label subtitle = new Label("Customer Receipt");
        subtitle.setAlignment(Pos.CENTER);
        subtitle.setMaxWidth(Double.MAX_VALUE);

        root.getChildren().addAll(title, subtitle, divider());

    
        root.getChildren().add(formatLine("Order:", order.getOrderCode()));
        root.getChildren().add(formatLine("Date:", order.getOrderDateTime().toString()));

        root.getChildren().add(divider());


        root.getChildren().add(centerLabel("Items"));
        root.getChildren().add(divider());

        for (OrderItem i : order.getItems()) {

            HBox itemRow = new HBox(5);
            itemRow.setAlignment(Pos.CENTER_LEFT);

            Label name = new Label(i.getProduct().getName());
            name.setPrefWidth(120);

            Label qty = new Label("x" + i.getQuantity());
            qty.setPrefWidth(30);
            qty.setAlignment(Pos.CENTER_RIGHT);

            Label price = new Label(String.format("%.2f", i.getLineTotal()));
            price.setPrefWidth(60);
            price.setAlignment(Pos.CENTER_RIGHT);

            itemRow.getChildren().addAll(name, qty, price);

            root.getChildren().add(itemRow);
        }

        root.getChildren().add(divider());

 
        VBox totalsBox = new VBox(3);
        totalsBox.setPadding(new Insets(5));
        totalsBox.setStyle(
                "-fx-background-color: #FFF8E1;" +
                "-fx-border-color: #BCAAA4;" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;"
        );

        totalsBox.getChildren().add(formatLine("Subtotal:", String.format("%.2f", order.getSubtotal())));
        totalsBox.getChildren().add(formatLine("Discount:", String.format("%.2f", order.getDiscount())));
        totalsBox.getChildren().add(formatLine("Tax:", String.format("%.2f", order.getTax())));
        totalsBox.getChildren().add(formatLine("TOTAL:", String.format("%.2f", order.getTotal())));

        root.getChildren().add(totalsBox);


        root.getChildren().add(divider());
        root.getChildren().add(formatLine("Payment:", paymentMethod));
        root.getChildren().add(formatLine("Paid:", String.format("%.2f", paidAmount)));
        root.getChildren().add(formatLine("Change:", String.format("%.2f", paidAmount - order.getTotal())));
        root.getChildren().add(divider());

        Label thanks = new Label("Thank you for your purchase! ☕");
        thanks.setAlignment(Pos.CENTER);
        thanks.setMaxWidth(Double.MAX_VALUE);
        thanks.setStyle("-fx-font-size: 12px; -fx-padding: 4 0 0 0;");

        root.getChildren().add(thanks);

        return root;
    }



    private static Node divider() {
        Label div = new Label("----------------------------------------");
        div.setAlignment(Pos.CENTER);
        div.setMaxWidth(Double.MAX_VALUE);
        return div;
    }

    private static HBox formatLine(String label, String value) {
        HBox line = new HBox();
        line.setAlignment(Pos.CENTER_LEFT);

        Label left = new Label(label);
        left.setPrefWidth(100);

        Label right = new Label(value);
        right.setPrefWidth(140);
        right.setAlignment(Pos.CENTER_RIGHT);

        line.getChildren().addAll(left, right);
        return line;
    }

    private static Label centerLabel(String text) {
        Label lbl = new Label(text);
        lbl.setAlignment(Pos.CENTER);
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setStyle("-fx-font-weight: bold;");
        return lbl;
    }
}
