import java.io.*;
import java.util.*;

public class Restaurant {

    private static File ordersFile = new File("Orders.txt");
    private static int numChefs;
    private static Chef[] chefs;
    private static Customer[] customers;

    static class Cashier extends Thread {
        public void run() {
            chefs = new Chef[numChefs];
            int numOrdersOfChef = numberOfOrders(ordersFile, numChefs);
            Map<String, Integer> ordersMap = readOrdersFromFile(ordersFile);
            for (int i = 0; i < numChefs; i++) {
                chefs[i] = new Chef();
                chefs[i].chefName = i;
                Map<String, Integer> order = divOrder(ordersMap, numOrdersOfChef);
                chefs[i].orders = order;
                Set<String> keySet = order.keySet();
                remove(ordersMap, keySet);
                System.out.println("chef " + chefs[i].chefName + " gets orders");
                chefs[i].start();
            }
            System.out.println("Cashier complete his task");
        }

    }


    static class Chef extends Thread {
        private int chefName;
        private Map<String, Integer> orders;

        public void run() {
            Set<String> keySet = orders.keySet();
            List<Integer> numList = new ArrayList<>();
            List<String> nameList = new ArrayList<>();
            customers = new Customer[keySet.size()];
            for (String name : keySet) {
                nameList.add(name);
                numList.add(orders.get(name));
            }

            for (int i = 0; i < numList.size(); i++) {
                try {
                    sleep(numList.get(i) * 200);
                    System.out.println("chef " + chefName + " prepare order of " + nameList.get(i));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                customers[i] = new Customer();
                customers[i].numberOfFoods = numList.get(i);
                customers[i].customerName = nameList.get(i);
                customers[i].start();
            }
            System.out.println("Chef " + chefName + " completes his task");
        }

    }

    static class Customer extends Thread {
        private String customerName;
        private int numberOfFoods;

        public void run() {
            int totalPrice = 0, wcPrice = 0;
            try {
                sleep(numberOfFoods * 150);
                System.out.println(customerName + " finished eating food");
                if (numberOfFoods > 10) {
                    sleep(200);
                    System.out.println(customerName + " goes to WC!");
                    wcPrice = 5000;
                } else {
                    wcPrice = 0;
                }
                totalPrice = calulateFoodPrice(numberOfFoods) + wcPrice;
                System.out.println(customerName + " " + totalPrice + " puts" + " on table");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Chefs Number: ");
        numChefs = input.nextInt();
        int result = numberOfOrders(ordersFile, numChefs);
        customers = new Customer[result * numChefs];
        Cashier cashier = new Cashier();
        cashier.start();
    }

    public static int numberOfOrders(File file, int numChefs) {
        int result = 0;
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(file));
            int numOrders = Integer.parseInt(buffer.readLine().trim());
            result = numOrders / numChefs;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, Integer> readOrdersFromFile(File file) {
        Map<String, Integer> ordersMap = new HashMap<>();
        try {
            if (file.exists()) {
                BufferedReader buffer = new BufferedReader(new FileReader(file));
                buffer.skip(1);
                String line;
                while ((line = buffer.readLine()) != null) {
                    String[] numbers = line.split(" ");
                    for (int i = 0; i < numbers.length - 1; i++) {
                        ordersMap.put(numbers[0], Integer.valueOf(numbers[1]));
                    }
                }
            } else {
                System.out.println(file.getAbsolutePath() + " not exist");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ordersMap;
    }

    public static void remove(Map<String, Integer> orders, Set<String> keySet) {
        for (String s : keySet) {
            if (orders.containsKey(s)) {
                orders.remove(s);
            }
        }
    }

    public static Map<String, Integer> divOrder(Map<String, Integer> orders, int numOrdersOfChef) {
        Map<String, Integer> divideOrder = new HashMap<>();
        Set<String> keySet = orders.keySet();
        List<String> nameList = new ArrayList<>();
        List<Integer> numList = new ArrayList<>();
        for (String name : keySet) {
            nameList.add(name);
            numList.add(orders.get(name));
        }
        for (int i = 0; i < numOrdersOfChef; i++) {
            divideOrder.put(nameList.get(i), numList.get(i));
        }
        return divideOrder;
    }

    public static int calulateFoodPrice(int numberOfFoods) {
        int foodPrice = 0;
        if (numberOfFoods % 6 == 0) {
            foodPrice = numberOfFoods * 6000;
        } else {
            foodPrice = numberOfFoods * 8000;
        }
        return foodPrice;
    }
}
