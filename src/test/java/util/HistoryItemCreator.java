package util;

import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoryItemCreator {

    private final InMemorySalesSystemDAO dao;
    private final ShoppingCart shoppingCart;
    private HistoryItem historyItem;

    public HistoryItemCreator(InMemorySalesSystemDAO dao, ShoppingCart shoppingCart) {
        this.dao = dao;
        this.shoppingCart = shoppingCart;
        this.historyItem = new HistoryItem(LocalDateTime.now());
        this.historyItem.setItems(new ArrayList<>());
        this.historyItem.setId(Any.randomId());
    }

    private List<SoldItem> generateSoldItems(){
        List<SoldItem> result = new ArrayList<>();
        int end = Math.abs(RandomUtils.nextInt(3, 10));
        for (int i = 0; i < end; i++){
            StockItem stockItem = new StockItemCreator().create();
            dao.saveStockItem(stockItem);
            SoldItem soldItem = new SoldItemCreator(stockItem).create();
            result.add(soldItem);
            dao.saveSoldItem(soldItem);
        }
        shoppingCart.getAll().clear();
        return result;
    }

    private void setSoldItemsList(List<SoldItem> soldItemsList){
        this.historyItem.setItems(soldItemsList);
    }

    public HistoryItem create(){
        return historyItem;
    }
}
