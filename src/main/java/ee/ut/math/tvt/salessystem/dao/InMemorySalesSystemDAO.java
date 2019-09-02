package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InMemorySalesSystemDAO implements SalesSystemDAO {

    private final List<StockItem> stockItemList;
    private final List<SoldItem> soldItemList;
    private final List<HistoryItem> historyItemList;
    private int beginTransaction = 0;
    private int commitTransaction = 0;
    private boolean saveStockItemMethodCalled = false;

    public InMemorySalesSystemDAO() {
        List<StockItem> items = new ArrayList<StockItem>();
        items.add(new StockItem(4L, "Free Beer", "Student's delight", 0.0, 100));
        items.add(new StockItem(1L, "Lays chips", "Potato chips", 11.0, 5));
        items.add(new StockItem(2L, "Chupa-chups", "Sweets", 8.0, 8));
        items.add(new StockItem(3L, "Frankfurters", "Beer sauseges", 15.0, 12));

        //This is for testing only
        List<HistoryItem> historyItems = new ArrayList<>();
        HistoryItem i1 = new HistoryItem(LocalDateTime.now());
        i1.addItem(new SoldItem(new StockItem(3L, "Frankfurters", "Beer sauseges", 15.0, 12),10));
        historyItems.add(i1);

        this.stockItemList = items;
        this.soldItemList = new ArrayList<>();
        this.historyItemList = historyItems;
    }

    @Override
    public List<StockItem> findStockItems() {
        return stockItemList;
    }

    @Override
    public StockItem findStockItem(long id) {
        for (StockItem item : stockItemList) {
            if (item.getId() == id)
                return item;
        }
        return null;
    }

    @Override
    public void saveSoldItem(SoldItem item) {
        soldItemList.add(item);
    }

    public List<SoldItem> getSoldItemList() {
        return soldItemList;
    }

    @Override
    public List<HistoryItem> findHistoryItems() {
        return historyItemList;
    }

    @Override
    public List<HistoryItem> findHistoryItemsBetween(LocalDate startDate, LocalDate endDate) {
        List<HistoryItem> result = new ArrayList<>();
        for (HistoryItem item : historyItemList){
            LocalDate ld = item.getLocalDateTime().toLocalDate();
            if (ld.isAfter(startDate) &&  ld.isBefore(endDate)
                || ld.isEqual(startDate)
                || ld.isEqual(endDate)){
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public List<HistoryItem> find10LastHistoryItems() {
        if (historyItemList.size()<11){
            return historyItemList;
        }
        List<HistoryItem> result = new ArrayList<>();
        for (int i = historyItemList.size()-1; i != historyItemList.size()-11; i--){
            result.add(historyItemList.get(i));
        }
        return result;
    }

    @Override
    public void saveHistoryItem(HistoryItem item) {
        historyItemList.add(item);
    }

    @Override
    public void saveStockItem(StockItem stockItem) {
        if (stockItem.getQuantity() < 0) {
            throw new IllegalArgumentException("Amount can not be negative!");
        }
        saveStockItemMethodCalled = true;
        beginTransaction();
        stockItemList.add(stockItem);
        commitTransaction();
    }

    public void addingExistingItem(StockItem existingItem, StockItem stockItem) {
        existingItem.setQuantity(stockItem.getQuantity() + existingItem.getQuantity());
    }

    @Override
    public void removeStockItem(StockItem stockItem) {
        stockItemList.remove(stockItem);
    }

    @Override
    public void clear() {

    }

    @Override
    public void beginTransaction() {
        beginTransaction +=1;
    }

    @Override
    public void rollbackTransaction() {
    }

    @Override
    public void commitTransaction() {
        commitTransaction +=1;
    }

    public int getBeginTransaction() {
        return beginTransaction;
    }

    public int getCommitTransaction() {
        return commitTransaction;
    }

    public List<StockItem> getStockItemList() {
        return stockItemList;
    }

    public boolean getSaveStockItemMethodCalled() {
        return saveStockItemMethodCalled;
    }

    public void setSaveStockItemMethodCalled() {
        saveStockItemMethodCalled = false;
    }
}