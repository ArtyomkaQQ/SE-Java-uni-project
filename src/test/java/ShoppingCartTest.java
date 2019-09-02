import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.HistoryItem;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.junit.Assert;
import org.junit.Test;
import util.SoldItemCreator;
import util.StockItemCreator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ShoppingCartTest {
    private static InMemorySalesSystemDAO dao = new InMemorySalesSystemDAO();
    private static ShoppingCart shoppingCart = new ShoppingCart(dao);

    @Test
    public void testAddingExistingItem() {
        shoppingCart.getAll().clear();
        StockItem stockItem = dao.findStockItem(4L);
        stockItem.setQuantity(100);
        SoldItem soldItem1 = new SoldItem(stockItem, 50);
        soldItem1.setId(stockItem.getId());
        SoldItem soldItem2 = new SoldItem(stockItem, 50);
        soldItem2.setId(stockItem.getId());
        shoppingCart.addItem(soldItem1);
        shoppingCart.addItem(soldItem2);
        List<SoldItem> soldItems = shoppingCart.getAll();
        int kontroll = 0;
        for (SoldItem item: soldItems) {
            if (item.getName() == soldItem1.getName())
                kontroll = item.getQuantity();
        }
        assertEquals(100, kontroll);
    }

    @Test
    public void testAddingNewItem() {
        shoppingCart.getAll().clear();
        StockItem stockItem = dao.findStockItem(4L);
        stockItem.setQuantity(100);
        SoldItem soldItem = new SoldItem(stockItem, 50);
        soldItem.setId(stockItem.getId());
        shoppingCart.addItem(soldItem);
        boolean isAdded = shoppingCart.getAll().contains(soldItem);
        assertEquals(true, isAdded);
    }

    @Test(expected = NumberFormatException.class)
    public void testAddingItemWithNegativeQuantity() {
        shoppingCart.getAll().clear();
        StockItem stockItem = dao.findStockItem(4L);
        stockItem.setQuantity(100);
        SoldItem soldItem = new SoldItem(stockItem, -50);
        shoppingCart.addItem(soldItem);
    }

    @Test(expected = NumberFormatException.class)
    public void testAddingItemWithQuantityTooLarge() {
        shoppingCart.getAll().clear();
        StockItem stockItem = dao.findStockItem(4L);
        stockItem.setQuantity(100);
        SoldItem soldItem = new SoldItem(stockItem, 105);
        soldItem.setId(stockItem.getId());
        shoppingCart.addItem(soldItem);
    }

    @Test(expected = NumberFormatException.class)
    public void testAddingItemWithQuantitySumTooLarge() {
        shoppingCart.getAll().clear();
        StockItem stockItem = dao.findStockItem(4L);
        stockItem.setQuantity(100);
        SoldItem soldItem1 = new SoldItem(stockItem, 50);
        soldItem1.setId(stockItem.getId());
        SoldItem soldItem2 = new SoldItem(stockItem, 55);
        soldItem2.setId(stockItem.getId());
        shoppingCart.addItem(soldItem1);
        shoppingCart.addItem(soldItem2);
    }

    @Test
    public void testSubmittingCurrentPurchaseDecreasesStockItemQuantity(){
        shoppingCart.getAll().clear();

        StockItem stockItem = new StockItemCreator().create();
        StockItem stockItem2 = new StockItemCreator().create();
        System.out.println(stockItem);
        int stockItem_Before_Sell = stockItem.getQuantity();
        int stockItem2_Before_Sell = stockItem2.getQuantity();

        dao.saveStockItem(stockItem);
        dao.saveStockItem(stockItem2);

        SoldItem soldItem = new SoldItemCreator(stockItem).create();
        SoldItem soldItem2 = new SoldItemCreator(stockItem2).create();
        shoppingCart.addItem(soldItem);
        shoppingCart.addItem(soldItem2);

        int soldItem_Sold_quantity = soldItem.getQuantity();
        int soldItem2_Sold_quantity = soldItem2.getQuantity();


        shoppingCart.submitCurrentPurchase();
        int stockItem_After_sell = stockItem_Before_Sell - soldItem_Sold_quantity;
        int stockItem2_After_sell = stockItem2_Before_Sell - soldItem2_Sold_quantity;

        Assert.assertEquals(dao.findStockItem(stockItem.getId()).getQuantity(), stockItem_After_sell);
        Assert.assertEquals(dao.findStockItem(stockItem2.getId()).getQuantity(), stockItem2_After_sell);
    }

    @Test
    public void testSubmittingCurrentPurchaseBeginsAndCommitsTransaction(){
        shoppingCart.getAll().clear();
        StockItem stockItem = new StockItemCreator().create();
        dao.saveStockItem(stockItem);

        SoldItem soldItem = new SoldItemCreator(stockItem).create();
        shoppingCart.addItem(soldItem);

        int beforePurchase_Begin = dao.getBeginTransaction();
        int beforePurchase_Commit = dao.getCommitTransaction();

        shoppingCart.submitCurrentPurchase();

        Assert.assertEquals(beforePurchase_Begin+1, dao.getBeginTransaction());
        Assert.assertEquals(beforePurchase_Commit+1, dao.getCommitTransaction());
    }

    @Test
    public void testSubmittingCurrentOrderCreatesHistoryItem(){
        shoppingCart.getAll().clear();
        StockItem stockItem = new StockItemCreator().create();
        StockItem stockItem2 = new StockItemCreator().create();
        dao.saveStockItem(stockItem);
        dao.saveStockItem(stockItem2);

        SoldItem soldItem = new SoldItemCreator(stockItem).create();
        SoldItem soldItem2 = new SoldItemCreator(stockItem2).create();
        List<SoldItem> soldItemList = new ArrayList<>(Arrays.asList(soldItem,soldItem2));
        shoppingCart.addItem(soldItem);
        shoppingCart.addItem(soldItem2);

        HistoryItem historyItem = shoppingCart.submitCurrentPurchase();

        Assert.assertTrue(historyItem.getItems().containsAll(soldItemList));
    }

    @Test
    public void testSubmittingCurrentOrderSavesCorrectTime(){
        StockItem stockItem = new StockItemCreator().create();
        dao.saveStockItem(stockItem);

        SoldItem soldItem = new SoldItemCreator(stockItem).create();

        shoppingCart.addItem(soldItem);
        LocalDateTime localDateTime = LocalDateTime.now();
        HistoryItem historyItem = shoppingCart.submitCurrentPurchase();

        int difference = Math.abs(localDateTime.getSecond()- historyItem.getLocalDateTime().getSecond());

        Assert.assertTrue("Difference is less than 1 second", difference <=1 );
    }

    @Test
    public void testCancellingOrder(){
        shoppingCart.getAll().clear();
        StockItem stockItem = new StockItemCreator().create();
        StockItem stockItem2 = new StockItemCreator().create();

        dao.saveStockItem(stockItem);
        dao.saveStockItem(stockItem2);

        SoldItem soldItem = new SoldItemCreator(stockItem).create();
        SoldItem soldItem2 = new SoldItemCreator(stockItem2).create();
        //Check list for false
        List<SoldItem> soldItemList = new ArrayList<>(Arrays.asList(soldItem, soldItem2));
        shoppingCart.addItem(soldItem);
        shoppingCart.addItem(soldItem2);
        shoppingCart.cancelCurrentPurchase();

        StockItem stockItem3 = new StockItemCreator().create();
        dao.saveStockItem(stockItem3);
        SoldItem soldItem3 = new SoldItemCreator(stockItem3).create();

        shoppingCart.addItem(soldItem3);
        shoppingCart.submitCurrentPurchase();

        Assert.assertTrue(dao.getSoldItemList().contains(soldItem3));
        Assert.assertFalse(dao.getSoldItemList().containsAll(soldItemList));
    }

    @Test
    public void testCancellingOrderQuanititesUnchanged(){
        shoppingCart.getAll().clear();
        StockItem stockItem = new StockItemCreator().create();
        dao.saveStockItem(stockItem);
        int stockItem_Quantity_Before_Cancellation = stockItem.getQuantity();
        System.out.println(stockItem_Quantity_Before_Cancellation);
        SoldItem soldItem = new SoldItemCreator(stockItem).create();
        System.out.println(soldItem.getQuantity());
        shoppingCart.addItem(soldItem);

        shoppingCart.cancelCurrentPurchase();

        int stockItem_Quantity_After_Cancellation = dao.findStockItem(stockItem.getId()).getQuantity();
        System.out.println(stockItem_Quantity_After_Cancellation);
        Assert.assertEquals("That quantity is the same after cancelling",
                stockItem_Quantity_Before_Cancellation, stockItem_Quantity_After_Cancellation);
    }


    @Test
    public void Checking_Shopping_Cart_After_Purchase(){
        shoppingCart.getAll().clear();
        StockItem stockItem = new StockItemCreator().create();
        dao.saveStockItem(stockItem);
        SoldItem soldItem = new SoldItemCreator(stockItem).create();
        shoppingCart.addItem(soldItem);
        shoppingCart.submitCurrentPurchase();
        assertTrue(shoppingCart.getAll().isEmpty());
    }


}
