package util;

import javafx.print.PageLayout;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.stage.Stage;

public class ReceiptPrinter {

    public static void print(Node node, Stage owner) {

        Printer printer = Printer.getDefaultPrinter();
        if (printer == null) {
            ErrorHandler.showError("No printer found.");
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob(printer);
        if (job == null) {
            ErrorHandler.showError("Unable to create print job.");
            return;
        }

        boolean ok = job.showPrintDialog(owner);
        if (!ok) return;

        PageLayout layout = printer.getDefaultPageLayout();

        if (job.printPage(layout, node)) {
            job.endJob();
        } else {
            ErrorHandler.showError("Failed to print receipt.");
        }
    }
}
