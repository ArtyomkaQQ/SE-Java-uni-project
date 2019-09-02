import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import util.Any;
import util.HistoryItemCreator;
import util.SoldItemCreator;
import util.StockItemCreator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class HistoryTest {

    private static InMemorySalesSystemDAO dao = new InMemorySalesSystemDAO();
    private static ShoppingCart shoppingCart = new ShoppingCart(dao);

    @Test
    public void Check_HistoryList_After_Purchase_Not_Empty(){
        shoppingCart.getAll().clear();
        StockItem stockItem1 = new StockItemCreator().create();
        StockItem stockItem2 = new StockItemCreator().create();
        dao.saveStockItem(stockItem1);
        dao.saveStockItem(stockItem2);
        SoldItem soldItem1 = new SoldItemCreator(stockItem1).create();
        SoldItem soldItem2 = new SoldItemCreator(stockItem2).create();

        shoppingCart.addItem(soldItem1);
        shoppingCart.addItem(soldItem2);
        shoppingCart.submitCurrentPurchase();
        List<HistoryItem> historyItemList = dao.findHistoryItems();
        assertFalse(historyItemList.isEmpty());
    }
    @Test
    public void History_Check_Last_10_Transactions() {
        shoppingCart.getAll().clear();
        List<HistoryItem> last10_Created = new ArrayList<>();
        List<HistoryItem> unnaccepted_HistoryItems = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            HistoryItem historyItem = new HistoryItemCreator(dao,shoppingCart).create();
            dao.saveHistoryItem(historyItem);
            if (i >= 5) {
                last10_Created.add(historyItem);
            } else {
                unnaccepted_HistoryItems.add(historyItem);
            }
        }
        List<HistoryItem> last10FromDB = dao.find10LastHistoryItems();
        Assert.assertTrue(last10FromDB.containsAll(last10_Created));
    }
}
