package ee.ut.math.tvt.salessystem.ui.controllers;

import com.sun.javafx.collections.ObservableListWrapper;
import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class StockController implements Initializable {

    private static final Logger log = LogManager.getLogger(StockController.class);

    private final SalesSystemDAO dao;

    @FXML
    private Button addItem;

    @FXML
    private Button resetItem;

    @FXML
    private TableView<StockItem> warehouseTableView;

    @FXML
    private TextField barCodeField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    public StockController(HibernateSalesSystemDAO dao) {
        this.dao = dao;
    }

    public StockController(InMemorySalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Stock controller is initialized");
        refreshStockItems();
    }

    @FXML
    public void refreshButtonClicked() {
        log.info("Warehouse has been refreshed");
        refreshStockItems();
    }

    private void refreshStockItems() {
        warehouseTableView.setItems(new ObservableListWrapper<>(dao.findStockItems()));
        warehouseTableView.refresh();
    }

    @FXML
    public void addProductButtonClicked() {
        log.info("Product has been added");
        addItemEventHandler();
    }

    public void addItemEventHandler() {
        try {
            Long barCode = Long.valueOf(barCodeField.getText());
            int quantity = Integer.valueOf(quantityField.getText());
            String name = String.valueOf(nameField.getText());
            double price = Double.valueOf(priceField.getText());
            if (quantity < 1) {
                throw new RuntimeException();
            }
            if (price < 0) {
                throw new RuntimeException();
            }
            StockItem item = dao.findStockItem(barCode);
            if (item != null) {
                if (item.getName().equals(name)) {
                    item.setQuantity(item.getQuantity() + quantity);
                    item.setPrice(price);
                }
                else {
                    throw new RuntimeException();
                }
            }
            else {
                String description = "";
                StockItem stockItem = new StockItem(barCode, name, description, price, quantity);
                dao.saveStockItem(stockItem);
            }
        }
        catch (RuntimeException e) {
            log.error("Wrong input data"+ e);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Wrong input data!");
            alert.setContentText("Try to enter correct data!");
            alert.showAndWait();
        }
    }

    @FXML
    public void resetProductButtonClicked() {
        log.info("Product has been removed");
        resetProductField();
    }

    public void resetProductField() {
        try {
            Long barCode = Long.valueOf(barCodeField.getText());
            StockItem stockItem = dao.findStockItem(barCode);
            stockItem.setQuantity(0);
        }
        catch (RuntimeException e) {
            log.error("Wrong input data");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Wrong input data!");
            alert.setContentText("Try to enter correct data!");
            alert.showAndWait();
        }
    }

    @FXML
    public void showWarehouse() {
    }
}