/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchain;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.UUID;

/**
 *
 * @author rekha
 */
public class Blockchain {

    /**
     * @param args the command line arguments
     */
    static Map<String, User> userList = new TreeMap<>();
        
    static List<String> userKeys = new ArrayList<>();
    
    static NavigableMap<UUID, Order> orderBook = new TreeMap<>();
    static NavigableMap<Double, Order> sellOrderBook = new TreeMap<>();
    static NavigableMap<Double, Order> buyOrderBook = new TreeMap<>();
        
    static User buyer, seller;
    static Order currentOrder;
    
    public static class User {
        public String name;
        public int coin;
        public double balance;
    }
  
    public static class Order {
        public UUID orderId;
        public User person;
        public String type;
        public Integer coin;
        public Double price;
       
        Order(){
            this.orderId = UUID.randomUUID();
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        InputStreamReader r1=new InputStreamReader(System.in);    
        BufferedReader br=new BufferedReader(r1);
    	try{
            System.out.println("Enter the number of users: ");
            int t = Integer.parseInt(br.readLine());
            System.out.println("Enter the "+ t + " users name, coin and INR in a line for a user separated by space: ");
            for(int i = 0; i < t; i++){
                String arr1 = br.readLine();
                String[] arr = arr1.split(" ");
                User user = new User();
                user.name = arr[0];
                userKeys.add(user.name);
                user.coin = Integer.parseInt(arr[1]);
                user.balance = Double.parseDouble(arr[2]);
                userList.put(arr[0], user);
            }
            System.out.println("Obtions : 1: CancelAll 2: Show Balance 3: Exit");
            System.out.println("Enter the orders with user name, buy/sell, price and quantity of coins in a line per order separated by space: ");
            while(true){
                String arr1 = br.readLine();
                if(arr1.equals("1")){
                    cancelAll();
                }else if(arr1.equals("2")){
                    showBalance();
                }else if(arr1.equals("3")){
                    break;
                }else{
                    String[] arr = arr1.split(" ");
                    UUID orderId = addOrder(arr[0], arr[1], Double.parseDouble(arr[2]), Integer.parseInt(arr[3]));
                    executeOrder(orderId);
                }
            }
        }catch(Exception e){
            System.out.print(e.getMessage());
            return;
        }
    }
    
    static public void showBalance(){
        for(String userKey: userKeys){
            User user = userList.get(userKey);
            System.out.println(user.name + " " + user.coin + " " + user.balance);
        }
    }
    
    static public UUID addOrder(String name, String type, double price, int quantity){
        Order order = new Order();
        order.person = userList.get(name);
        order.coin = quantity;
        order.price = price;
        order.type = type;
        orderBook.put(order.orderId, order);
        if(type.equals("buy")){
            buyOrderBook.put(price, order);
        }else{
            sellOrderBook.put(price, order);
        }
        return order.orderId;
    }
    
    static public void cancelAll(){
        orderBook.clear();
        buyOrderBook.clear();
        sellOrderBook.clear();
    }
    
    static public void cancelOrder(UUID orderId){
        Order order = orderBook.get(orderId);
        orderBook.remove(orderId);
        Collection<Order> orders = new ArrayList<>();
        if(order.type.equals("buy"))
            orders = buyOrderBook.values();
        else
            orders = sellOrderBook.values();
        for(Order buySell: orders){
            if(buySell.orderId == orderId){
                //System.out.println(" buySellInside");
                if(buySell.type.equals("buy"))
                    buyOrderBook.remove(buySell.price);
                else
                    sellOrderBook.remove(buySell.price);
                break;
            }
        }
    }
    
    static public Order getLowestSell(){
        NavigableMap<Double, Order> sellOrders = sellOrderBook;
        Double sellKey = sellOrders.floorKey(currentOrder.price);
        if(sellKey == null)
            return null;
        while(sellOrders.get(sellKey).person.name.equals(currentOrder.person.name) && !sellOrders.isEmpty()){
            sellOrders.remove(sellKey);
            sellKey = sellOrders.floorKey(currentOrder.price);
        }
        return sellOrderBook.get(sellKey);
    }
    
    static public Order getHighestBuy(){
       // System.out.println("inside buy");
        NavigableMap<Double, Order> buyOrders = buyOrderBook;
        Double sellKey = buyOrders.ceilingKey(currentOrder.price);
        //System.out.print("sellkey : " + sellKey);
        if(sellKey == null)
            return null;
        while(buyOrders.get(sellKey).person.name.equals(currentOrder.person.name) && !buyOrders.isEmpty()){
            buyOrders.remove(sellKey);
            sellKey = buyOrders.ceilingKey(currentOrder.price);
        }
            return buyOrderBook.get(sellKey);
    }
    
    static public void executeOrder(UUID orderId){
        //System.out.println("inside executeOrder");
        currentOrder = orderBook.get(orderId);
        Order match;
        
        if(currentOrder.type.equals("buy")){
            if(sellOrderBook.isEmpty())
                return;
            buyer = currentOrder.person;
            match = getLowestSell();
            //System.out.println("inside executeOrder");
            if(match == null)
                return;
            seller = match.person;
        }else{
            if(buyOrderBook.isEmpty())
                return;
            //System.out.print("exit");
            seller = currentOrder.person;
            match = getHighestBuy();
            if(match == null)
                return;
            buyer = match.person;
        }
        if(match != null){
            Order sold = match.coin > currentOrder.coin ? currentOrder:match;
            buyer.balance = buyer.balance - sold.price*sold.coin;
            seller.balance = seller.balance + sold.price*sold.coin;
            buyer.coin = buyer.coin + sold.coin;
            seller.coin = seller.coin - sold.coin;
            userList.replace(buyer.name, buyer);
            userList.replace(seller.name, seller);
            cancelOrder(sold.orderId);
            if(match.coin > currentOrder.coin){
                match.coin = match.coin - sold.coin;
                if(match.type.equals("buy") ){
                    buyOrderBook.replace(match.price, match);
                    //System.out.println("1");
                }else{
                    sellOrderBook.replace(match.price, match);
                    //System.out.println("2");
                }
            }else{
                currentOrder.coin = currentOrder.coin - sold.coin;
                if(currentOrder.type.equals("buy") ){
                    buyOrderBook.replace(match.price, match);
                    //System.out.println("3");
                }else{
                    sellOrderBook.replace(match.price, match);
                    //System.out.println("4");
                }
            }
            //showBalance();
        }
    }
    
}
