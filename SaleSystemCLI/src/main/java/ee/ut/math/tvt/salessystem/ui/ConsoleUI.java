package ee.ut.math.tvt.salessystem.ui;

import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.exception.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * A simple CLI (limited functionality).
 */
public class ConsoleUI {
    private static final Logger log = LogManager.getLogger(ConsoleUI.class);

    private final SalesSystemDAO dao;
    private final ShoppingCart cart;

    public ConsoleUI(HibernateSalesSystemDAO dao) {
        this.dao = dao;
        cart = new ShoppingCart(dao);
    }

    public ConsoleUI(InMemorySalesSystemDAO dao) {
        this.dao = dao;
        cart = new ShoppingCart(dao);
    }

    public static void main(String[] args) throws Exception {
        //HibernateSalesSystemDAO dao = new HibernateSalesSystemDAO();
        InMemorySalesSystemDAO dao = new InMemorySalesSystemDAO();
        ConsoleUI console = new ConsoleUI(dao);
        console.run();
    }

    /**
     * Run the sales system CLI.
     */
    public void run() throws IOException {
        log.info("Salesystem CLI started");
        System.out.println("===========================");
        System.out.println("=       Sales System      =");
        System.out.println("===========================");
        printUsage();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            processCommand(in.readLine().trim().toLowerCase());
            System.out.println("Done. ");
        }
    }

    private void showStock() {
        List<StockItem> stockItems = dao.findStockItems();
        System.out.println("-------------------------");
        for (StockItem si : stockItems) {
            System.out.println(si.getId() + " " + si.getName() + " " + si.getPrice() + "Euro (" + si.getQuantity() + " items)");
        }
        if (stockItems.size() == 0) {
            System.out.println("\tNothing");
        }
        System.out.println("-------------------------");
    }

    private void addToStock() {

    }

    private void showCart() {
        System.out.println("-------------------------");
        for (SoldItem si : cart.getAll()) {
            System.out.println(si.getName() + " " + si.getPrice() + "Euro (" + si.getQuantity() + " items)");
        }
        if (cart.getAll().size() == 0) {
            System.out.println("\tNothing");
        }
        System.out.println("-------------------------");
    }

    private void showTeam() {
        System.out.println("-------------------------");
        try (FileReader reader = new FileReader(System.getProperty("user.dir")+ File.separator +"src" + File.separator + "main" + File.separator +"resources" + File.separator +"application.properties")) {
            log.info("Application.properties is read");
            Properties properties = new Properties();
            properties.load(reader);
            String teamName = properties.getProperty("team_name");
            String teamContactPerson = properties.getProperty("team_contact_person");
            String teamMembers = properties.getProperty("team_members");
            System.out.println("Team name: "+teamName);
            System.out.println("Team contact person: "+teamContactPerson);
            System.out.println("Team members: " +teamMembers);
            System.out.println("-------------------------");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

        private void printUsage() {
        System.out.println("-------------------------");
        System.out.println("Usage:");
        System.out.println("h\t\tShow this help");
        System.out.println("w\t\tShow warehouse contents");
        System.out.println("wa IDX NR NA DSC PR \tAdd NR of stock item with index IDX, name NA, description DESC and" +
                "price PR to the stock");
        System.out.println("wr IDX \t Remove stock item with index INDX");
        System.out.println("c\t\tShow cart contents");
        System.out.println("a IDX NR \tAdd NR of stock item with index IDX to the cart");
        System.out.println("p\t\tPurchase the shopping cart");
        System.out.println("r\t\tReset the shopping cart");
        System.out.println("t\t\tShow team info");
        System.out.println("-------------------------");
    }

    private void processCommand(String command) {
        String[] c = command.split(" ");

        if (c[0].equals("h"))
            printUsage();
        else if (c[0].equals("q")) {
            log.info("Salesystem CLI finished");
            System.exit(0);
        }
        else if (c[0].equals("w"))
            showStock();
        else if (c[0].equals("wa") && c.length == 6) {
            try {
                long idx = Long.parseLong(c[1]);
                int amount = Integer.parseInt(c[2]);
                if (amount < 1) {
                    log.error("Amount chould be positive");
                    throw new SalesSystemException();
                }
                String name = c[3];
                String description = c[4];
                double price = Double.parseDouble(c[5]);
                if (price < 0) {
                    log.error("Price should be positive");
                    throw  new SalesSystemException();
                }
                StockItem item = dao.findStockItem(idx);
                if (item != null) {
                    if (item.getName().equals(name)) {
                        item.setQuantity(item.getQuantity() + amount);
                        item.setPrice(price);
                    }
                    else {
                        log.error("Wrong product name given");
                        throw new SalesSystemException();
                    }
                }
                else {
                    StockItem stockItem = new StockItem(idx, name, description, price, amount);
                    dao.saveStockItem(stockItem);
                }
            } catch (SalesSystemException | NoSuchElementException e) {
                log.error(e.getMessage(), e);
            }
        }
        else if (c[0].equals("wr") && c.length == 2) {
            try {
                long idx = Long.parseLong(c[1]);
                StockItem item = dao.findStockItem(idx);
                if (item == null) {
                    log.error("no stock item with this id");
                    throw new SalesSystemException();
                }
                else {
                    dao.removeStockItem(item);
                }
            }
            catch (SalesSystemException | NoSuchElementException e) {
                log.error(e.getMessage(), e);
            }

        }
        else if (c[0].equals("c"))
            showCart();
        else if (c[0].equals("p"))
            cart.submitCurrentPurchase();
        else if (c[0].equals("r"))
            cart.cancelCurrentPurchase();
        else if (c[0].equals("t"))
            showTeam();
        else if (c[0].equals("a") && c.length == 3) {
            try {
                long idx = Long.parseLong(c[1]);
                int amount = 0;
                try {
                    amount = Integer.parseInt(c[2]);
                } catch (SalesSystemException | NumberFormatException e) {
                    System.out.println("Invalid quantity");
                }
                StockItem item = dao.findStockItem(idx);
                if (amount > item.getQuantity()) {
                    System.out.println("Maximum quantity exceeded");
                }
                else if (amount <=0) {
                    System.out.println("Invalid quantity");
                }
                else if (item != null) {
                    cart.addItem(new SoldItem(item, Math.min(amount, item.getQuantity())));
                    item.setQuantity(item.getQuantity() - amount);
                } else {
                    System.out.println("no stock item with id " + idx);
                }
            } catch (SalesSystemException | NoSuchElementException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            System.out.println("unknown command");
        }
    }

}
