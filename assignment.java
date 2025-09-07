package l151;

import java.time.LocalDate;
import java.util.*;

abstract class Product {
    protected int id;
    protected String name;
    protected double basePrice;
    protected int stockQty;

    public Product(int id, String name, double basePrice, int stockQty) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.stockQty = stockQty;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getBasePrice() { return basePrice; }
    public int getStockQty() { return stockQty; }

    public void addStock(int qty) { this.stockQty += qty; }

    public boolean reduceStock(int qty) {
        if (qty <= stockQty) {
            stockQty -= qty;
            return true;
        }
        return false;
    }

    public double priceAfterDiscount(double discountPercent) {
        return basePrice - (basePrice * discountPercent / 100);
    }

    public double priceAfterDiscount(String couponCode) {
        if (couponCode.equalsIgnoreCase("SAVE10")) return priceAfterDiscount(10);
        if (couponCode.equalsIgnoreCase("SAVE20")) return priceAfterDiscount(20);
        return basePrice;
    }

    public abstract double finalPrice();
}

class Electronics extends Product {
    private int warrantyYears;
    private double gstRate;

    public Electronics(int id, String name, double basePrice, int stockQty, int warrantyYears, double gstRate) {
        super(id, name, basePrice, stockQty);
        this.warrantyYears = warrantyYears;
        this.gstRate = gstRate;
    }

    public double finalPrice() {
        double tax = basePrice * gstRate / 100;
        return basePrice + tax;
    }

    public int getWarrantyYears() { return warrantyYears; }
}

class Grocery extends Product {
    private LocalDate expiryDate;
    private String unitType;

    public Grocery(int id, String name, double basePrice, int stockQty, LocalDate expiryDate, String unitType) {
        super(id, name, basePrice, stockQty);
        this.expiryDate = expiryDate;
        this.unitType = unitType;
    }

    public double finalPrice() {
        if (expiryDate.isBefore(LocalDate.now().plusDays(3))) {
            return basePrice * 0.95; // 5% discount if near expiry
        }
        return basePrice;
    }
}

class InventoryManager {
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product p) {
        products.add(p);
    }

    public void removeProduct(int id) {
        products.removeIf(p -> p.getId() == id);
    }

    public Product findProduct(int id) {
        for (Product p : products) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public void generateBill(Map<Integer, Integer> cart) {
        double total = 0;
        System.out.println("===== BILL =====");
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            Product p = findProduct(entry.getKey());
            if (p != null && p.reduceStock(entry.getValue())) {
                double itemPrice = p.finalPrice() * entry.getValue();
                total += itemPrice;
                System.out.println(p.getName() + " x " + entry.getValue() + " = " + itemPrice);
            }
        }
        System.out.println("TOTAL: " + total);
    }

    public void lowStockReport(int threshold) {
        System.out.println("===== Low Stock Report =====");
        for (Product p : products) {
            if (p.getStockQty() < threshold) {
                System.out.println(p.getName() + " (Qty: " + p.getStockQty() + ")");
            }
        }
    }
}

public class assignment {
    public static void main(String[] args) {
        InventoryManager manager = new InventoryManager();

        Electronics laptop = new Electronics(1, "Laptop", 50000, 10, 2, 18);
        Grocery milk = new Grocery(2, "Milk", 50, 20, LocalDate.now().plusDays(2), "Litre");

        manager.addProduct(laptop);
        manager.addProduct(milk);

        Map<Integer, Integer> cart = new HashMap<>();
        cart.put(1, 1); 
        cart.put(2, 3); 

        manager.generateBill(cart);

        manager.lowStockReport(5);
    }
}
