package ee.ut.math.tvt.salessystem.ui.controllers;

import com.sun.javafx.collections.ObservableListWrapper;
import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.exception.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Encapsulates everything that has to do with the purchase tab (the tab
 * labelled "Point-of-sale" in the menu). Consists of the purchase menu,
 * current purchase dialog and shopping cart table.
 */
public class PurchaseController implements Initializable {

    private static final Logger log = LogManager.getLogger(PurchaseController.class);

    private final SalesSystemDAO dao;
    private final ShoppingCart shoppingCart;
    private static List<Long> ids = new ArrayList<>();

    @FXML
    private Button newPurchase;
    @FXML
    private Button submitPurchase;
    @FXML
    private Button cancelPurchase;
    @FXML
    private TextField barCodeField;
    @FXML
    private TextField quantityField;
    @FXML
    private MenuButton nameMenuButton;
    @FXML
    private TextField priceField;
    @FXML
    private Button addItemButton;
    @FXML
    private TableView<SoldItem> purchaseTableView;

    public PurchaseController(HibernateSalesSystemDAO dao, ShoppingCart shoppingCart) {
        this.dao = dao;
        this.shoppingCart = shoppingCart;
    }

    public PurchaseController(InMemorySalesSystemDAO dao, ShoppingCart shoppingCart) {
        this.dao = dao;
        this.shoppingCart = shoppingCart;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancelPurchase.setDisable(true);
        submitPurchase.setDisable(true);
        purchaseTableView.setItems(new ObservableListWrapper<>(shoppingCart.getAll()));
        disableProductField(true);

        this.barCodeField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    fillInputsBySelectedStockItem();
                }
            }
        });
    }

    /** Event handler for the <code>new purchase</code> event. */
    @FXML
    protected void newPurchaseButtonClicked() {
        log.info("New sale process started");
        try {
            enableInputs();
            this.priceField.setDisable(true);
            chooseProduct();
        } catch (SalesSystemException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Event handler for the <code>cancel purchase</code> event.
     */
    @FXML
    protected void cancelPurchaseButtonClicked() {
        log.info("Sale cancelled");
        try {
            nameMenuButton.getItems().clear();
            shoppingCart.cancelCurrentPurchase();
            disableInputs();
            purchaseTableView.refresh();
        } catch (SalesSystemException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Event handler for the <code>submit purchase</code> event.
     */
    @FXML
    protected void submitPurchaseButtonClicked() {
        log.info("Sale complete");
        try {
            nameMenuButton.getItems().clear();
            log.debug("Contents of the current basket:\n" + shoppingCart.getAll());
            shoppingCart.submitCurrentPurchase();
            disableInputs();
            purchaseTableView.refresh();
        } catch (SalesSystemException e) {
            log.error(e.getMessage(), e);
        }
    }

    // switch UI to the state that allows to proceed with the purchase
    private void enableInputs() {
        resetProductField();
        disableProductField(false);
        cancelPurchase.setDisable(false);
        submitPurchase.setDisable(false);
        newPurchase.setDisable(true);
    }

    // switch UI to the state that allows to initiate new purchase
    private void disableInputs() {
        resetProductField();
        cancelPurchase.setDisable(true);
        submitPurchase.setDisable(true);
        newPurchase.setDisable(false);
        disableProductField(true);
    }

    private void fillInputsBySelectedStockItem() {
        StockItem stockItem = getStockItemByBarcode();
        if (stockItem != null) {
            priceField.setText(String.valueOf(stockItem.getPrice()));
        } else {
            resetProductField();
        }
    }

    // Search the warehouse for a StockItem with the bar code entered
    // to the barCode textfield.
    private StockItem getStockItemByBarcode() {
        try {
            long code = Long.parseLong(barCodeField.getText());
            return dao.findStockItem(code);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Add new item to the cart.
     */
    @FXML
    public void addItemEventHandler() {
        StockItem stockItem = getStockItemByBarcode();
        if (stockItem != null) {
            int quantity;
            try {
                quantity = Integer.parseInt(quantityField.getText());
                if (quantity <= 0) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning Dialog");
                    alert.setHeaderText("Invalid input!");
                    alert.setContentText("Recheck Your input!");

                    alert.showAndWait();
                }
                if (stockItem.getQuantity() < quantity) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning Dialog");
                    alert.setHeaderText("Max quantity exceeded!");
                    alert.setContentText("Try to enter lower quantity!");

                    alert.showAndWait();
                }
                else {
                    if (quantity > 0) {
                        SoldItem item = new SoldItem(stockItem, quantity);
                        item.setId(stockItem.getId());
                        if (ids.isEmpty()) {
                            ids.add(1L);
                        } else {
                            ids.add(1L);
                        }
                        shoppingCart.addItem(item);
                        purchaseTableView.refresh();
                    }
                }
            } catch (NumberFormatException e) {
                log.warn("Vigane sisend");
            }
        }
    }

    /**
     * Sets whether or not the product component is enabled.
     */
    private void disableProductField(boolean disable) {
        this.addItemButton.setDisable(disable);
        this.barCodeField.setDisable(disable);
        this.quantityField.setDisable(disable);
        this.nameMenuButton.setDisable(disable);
        this.priceField.setDisable(disable);
    }

    /**
     * Reset dialog fields.
     */
    private void resetProductField() {
        barCodeField.setText("");
        quantityField.setText("1");
        priceField.setText("");
    }

    private void chooseProduct() {
        dao.beginTransaction();
        List<StockItem> stockItems = dao.findStockItems();
        dao.commitTransaction();
        for (StockItem item: stockItems) {
            CheckMenuItem checkMenuItem = new CheckMenuItem(item.getName());
            checkMenuItem.setOnAction(e -> {
                if (checkMenuItem.isSelected()) {
                    barCodeField.setText(item.getId().toString());
                    quantityField.setText(String.valueOf(item.getQuantity()));
                    priceField.setText(String.valueOf(item.getPrice()));
                }});
            nameMenuButton.getItems().add(checkMenuItem);
        }
    }
}
