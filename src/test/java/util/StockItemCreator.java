package util;

import ee.ut.math.tvt.salessystem.dataobjects.StockItem;

public class StockItemCreator {

    private StockItem stockItem;

    public StockItemCreator() {
        this.stockItem = new StockItem();
        this.stockItem.setId(Any.randomId());
        this.stockItem.setName(Any.randomName());
        this.stockItem.setPrice(Any.randomDouble());
        this.stockItem.setDescription(Any.randomName());
        this.stockItem.setQuantity(Any.randomInt());
    }

    public StockItem create(){
        return stockItem;
    }
}
