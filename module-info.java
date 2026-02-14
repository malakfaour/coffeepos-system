module CoffeePOS {
	requires javafx.controls;
	 requires javafx.fxml;
	 requires java.sql;


	
	 requires jbcrypt;


	    opens controller to javafx.fxml;
	    opens model to javafx.fxml;
	    opens model.pricing to javafx.fxml;

	    opens app to javafx.graphics, javafx.fxml;

	    exports controller;
	    exports app;
	    exports model;
	    exports model.pricing;

}

