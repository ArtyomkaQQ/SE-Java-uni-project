import static org.junit.Assert.assertEquals;

import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.junit.Before;
import org.junit.Test;

public class WarehouseManagementTest {
    private StockItem item;
    private InMemorySalesSystemDAO dao;

    @Before
    public void setUp() {
        item = new StockItem(5l, "Tupla", "chocolate", 0.69, 5);
        dao = new InMemorySalesSystemDAO();
    }

    @Test
    public void testAddingItemBeginsAndCommitsTransaction() {
        dao.saveStockItem(item);
        int countBeginTransaction = dao.getBeginTransaction();
        int countCommitTransaction = dao.getCommitTransaction();
        assertEquals(1, countBeginTransaction);
        assertEquals(1, countCommitTransaction);
    }

    @Test
    public void testAddingNewItem() {
        dao.saveStockItem(item);
        long id = item.getId();
        StockItem addedItem = dao.findStockItem(id);
        assertEquals(item.getId(), addedItem.getId());
    }

    @Test
    public void testAddingExistingItem() {
        dao.saveStockItem(item);
        StockItem item = new StockItem(5l, "Tupla", "chocolate", 0.69, 2);
        dao.setSaveStockItemMethodCalled();
        for (StockItem existingItem: dao.getStockItemList()) {
            if (existingItem.getId() == item.getId()) {
                dao.addingExistingItem(existingItem, item);
                break;
            }
        }
        assertEquals(7, this.item.getQuantity());
        assertEquals(false, dao.getSaveStockItemMethodCalled());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingItemWithNegativeQuantity() {
        StockItem stockItem = new StockItem(6l, "KitKat", "chocolate", 0.59, -5);
        dao.saveStockItem(stockItem);
    }
}
