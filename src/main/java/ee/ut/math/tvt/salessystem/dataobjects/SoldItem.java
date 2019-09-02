package ee.ut.math.tvt.salessystem.dataobjects;


import javax.persistence.*;

/**
 * Already bought StockItem. SoldItem duplicates name and price for preserving history.
 */
@Entity
@Table
public class SoldItem {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private StockItem stockItem;

    @ManyToOne(cascade = CascadeType.ALL)
    private HistoryItem historyId;

    @Column(name = "name")
    private String name;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    private double price;

    public SoldItem(StockItem stockItem, int quantity) {
        this.stockItem = stockItem;
        this.name = stockItem.getName();
        this.price = stockItem.getPrice();
        this.quantity = quantity;
    }

    public SoldItem(StockItem stockItem, HistoryItem historyId, String name, Integer quantity, double price) {
        this.stockItem = stockItem;
        this.historyId = historyId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public SoldItem() {
    }

    public HistoryItem getHistoryId() {
        return historyId;
    }

    public void setHistoryId(HistoryItem historyId) {
        this.historyId = historyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public double getSum() {
        return price * ((double) quantity);
    }

    // RETURNS ID MAY CAUSE PROBLEMS
    public Long getStockItem() {
        return stockItem.getId();
    }

    public void setStockItem(StockItem stockItem) {
        this.stockItem = stockItem;
    }

    @Override
    public String toString() {
        return "SoldItem{" +
                "id=" + id +
                ", stockItem=" + stockItem +
                ", historyId=" + historyId +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
