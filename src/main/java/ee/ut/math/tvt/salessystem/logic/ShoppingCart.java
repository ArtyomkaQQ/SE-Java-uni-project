package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.exception.SalesSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShoppingCart {

    private final SalesSystemDAO dao;
    private final List<SoldItem> items = new ArrayList<>();
    private static final Logger log = LogManager.getLogger(ShoppingCart.class);

    public ShoppingCart(HibernateSalesSystemDAO dao) {
        this.dao = dao;
    }

    public ShoppingCart(InMemorySalesSystemDAO dao) {
        this.dao = dao;
    }

    /**
     * Add new SoldItem to table.
     */
    public void addItem(SoldItem item) {
        if (item.getQuantity() <= 0)
            throw new NumberFormatException("Invalid data");
        int kontroll = 1;
        StockItem stockItem = dao.findStockItem(item.getStockItem());
        if (stockItem.getQuantity() < item.getQuantity())
            throw new NumberFormatException("Invalid data");
        for (SoldItem soldItem: items) {
            if (soldItem.getName() == item.getName()) {
                stockItem.setQuantity(stockItem.getQuantity() - item.getQuantity());
                soldItem.setQuantity(soldItem.getQuantity() + item.getQuantity());
                kontroll = 0;
            }
        }

        if (kontroll == 1) {
            if (stockItem.getQuantity() < item.getQuantity())
                throw new NumberFormatException("Invalid data");
            stockItem.setQuantity(stockItem.getQuantity() - item.getQuantity());
            items.add(item);
        }
        log.debug("Added " + item.getName() + " quantity of " + item.getQuantity());
    }

    public List<SoldItem> getAll() {
        return items;
    }

    public void cancelCurrentPurchase() {
        for (SoldItem soldItem : items){
            StockItem stockItem = dao.findStockItem(soldItem.getStockItem());
            stockItem.setQuantity(stockItem.getQuantity() + soldItem.getQuantity());
        }
        items.clear();
    }

    public HistoryItem submitCurrentPurchase() {
        // TODO decrease quantities of the warehouse stock

        // note the use of transactions. InMemorySalesSystemDAO ignores transactions
        // but when you start using hibernate in lab5, then it will become relevant.
        // what is a transaction? https://stackoverflow.com/q/974596
        //dao.beginTransaction();
        dao.beginTransaction();
        log.info(items);
        try {
            HistoryItem historyItem = new HistoryItem(LocalDateTime.now());
            dao.saveHistoryItem(historyItem);
            System.out.println("HISTORYID " + historyItem.getId());
            for (SoldItem soldItem : items){
                soldItem.setHistoryId(historyItem);
                System.out.println(soldItem.getHistoryId());
                System.out.println(soldItem.getId());
                historyItem.addItem(soldItem);

                dao.saveSoldItem(soldItem);

            }
            dao.commitTransaction();
            items.clear();
            return historyItem;
        } catch (Exception e) {
            dao.rollbackTransaction();
            throw e;
        }
    }
}