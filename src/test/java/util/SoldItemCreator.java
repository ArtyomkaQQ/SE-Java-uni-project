package util;

import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.apache.commons.lang3.RandomUtils;

public class SoldItemCreator {

    private SoldItem soldItem;

    public SoldItemCreator(StockItem stockItem) {
        this.soldItem = new SoldItem(stockItem, RandomUtils.nextInt(1,stockItem.getQuantity()));
        this.soldItem.setId(Any.randomId());
    }

    public SoldItem create(){
        return soldItem;
    }



}
