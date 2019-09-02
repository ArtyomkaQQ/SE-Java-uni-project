package ee.ut.math.tvt.salessystem.ui.controllers;

import com.sun.javafx.collections.ObservableListWrapper;
import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.exception.DateControllEmptyException;
import ee.ut.math.tvt.salessystem.exception.DateControllNegativeException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Encapsulates everything that has to do with the purchase tab (the tab
 * labelled "History" in the menu).
 */
public class HistoryController implements Initializable {

    private static final Logger log = LogManager.getLogger(HistoryController.class);

    private final SalesSystemDAO dao;


    @FXML
    private Button showBetweenDates;

    @FXML
    private Button showLast10;

    @FXML
    private Button showAll;

    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker endDate;

    @FXML
    private TableView<HistoryItem> historyItemTableView;

    @FXML
    private TableView<SoldItem> soldProductsView;

    public HistoryController(HibernateSalesSystemDAO dao) {
        this.dao = dao;
    }

    public HistoryController(InMemorySalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        historyItemTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<HistoryItem>() {
            @Override
            public void changed(ObservableValue<? extends HistoryItem> observableValue, HistoryItem oldValue, HistoryItem newValue) {
                //Check whether item is selected and set value of selected item to Label
                clearSoldProductsView();
                if(historyItemTableView.getSelectionModel().getSelectedItem() != null) {
                    soldProductsView.setItems(new ObservableListWrapper<SoldItem>(newValue.getItems()));
                }
            }
        });
    }

    @FXML
    protected void showBetweenDatesButtonClicked() {
        log.info("Show between dates has been requested");
        LocalDate start = startDate.getValue();
        LocalDate end = endDate.getValue();
        if (datesControll(start,end)){
            historyItemTableView.setItems(new ObservableListWrapper<>(dao.findHistoryItemsBetween(start,end)));
            clearSoldProductsView();
        }
    }

    @FXML
    protected void showLast10() {
        log.info("Show last 10 has been requested");
        resetDateField();
        historyItemTableView.setItems(new ObservableListWrapper<>(dao.find10LastHistoryItems()));
        clearSoldProductsView();
    }
    @FXML
    private void showPurchaseHistory(){
        log.info("Purchase history has been requested");
        resetDateField();
        historyItemTableView.setItems(new ObservableListWrapper<>(dao.findHistoryItems()));
        clearSoldProductsView();
        //dao.findStockItems();
    }

    private void clearSoldProductsView(){
        log.info("Sold products view is cleared");
        soldProductsView.setItems(new ObservableListWrapper<>(new ArrayList<>()));
    }


    private void resetDateField(){
        log.info("Start and End dates are cleared");
        startDate.getEditor().clear();
        endDate.getEditor().clear();
    }

    private boolean datesControll(LocalDate start, LocalDate end){

        try {
            if (start == null || end == null){
                log.info("Dates are empty");
                throw new DateControllEmptyException();
            }
            Period period = Period.between(end, start);
            if (!period.isNegative() && !period.isZero()){
                log.info("Dates are negative");
                throw new DateControllNegativeException();
            }
        } catch (DateControllEmptyException e){
            alertWindow("Date exception",
                    "Date field is empty",
                    "Please select date!");
            return false;

        } catch (DateControllNegativeException e){
            alertWindow("Date exception",
                    "Choosen date difference is negative",
                    "End date is earlier than start day");
            return false;
        }
        return true;
    }

    private void alertWindow(String title, String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void start(Stage stage){

    }
}
