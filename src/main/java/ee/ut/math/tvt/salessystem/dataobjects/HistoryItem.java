package ee.ut.math.tvt.salessystem.dataobjects;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class HistoryItem {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime date;

    @OneToMany(mappedBy = "historyId")
    private List<SoldItem> items;

    public HistoryItem() {
    }

    public HistoryItem(LocalDateTime date, List<SoldItem> items) {
        this.date = date;
        this.items = items;
    }

    public HistoryItem(LocalDateTime date) {
        this.date = date;
        this.items = new ArrayList<>();
    }

    public double getTotal(){
        double total = 0.0;
        for (SoldItem item: items){
            total +=item.getSum();
        }
        return total;
    }

    public String getTime(){
        int hour = date.getHour();
        int minutes = date.getMinute();
        int seconds = date.getSecond();
        return String.format("%d:%d:%d",hour,minutes,seconds);
    }
    public void addItem(SoldItem item){
        items.add(item);
    }

    public void removeItem(SoldItem item){
        items.remove(item);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date.toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public LocalDateTime getLocalDateTime(){
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<SoldItem> getItems() {
        return items;
    }

    public void setItems(List<SoldItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "HistoryItem{" +
                "id=" + id +
                ", date=" + date +
                '}';
    }
}
