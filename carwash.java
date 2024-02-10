import java.util.concurrent.*;
import java.util.*;

public class carwash {

    private static final Semaphore carWash_Capacity = new Semaphore(3, true);
    private static final boolean[] first_step_PLACES = new boolean[2];
    private static final Semaphore first_step = new Semaphore(2, true);
    private static final Semaphore second_step = new Semaphore(1, true);
    private static int carCount;

    public static class Car implements Runnable {

        private final int carNumber;

        public Car(int carNumber) {
            this.carNumber = carNumber;
        }

        @Override
        public void run() {

            System.out.println("Car.No " + this.carNumber + " Waiting For Enter The CarWash ");

            try {
                carWash_Capacity.acquire();
                System.out.println("Car.No " + this.carNumber + " Entered CarWash ");
                first_step.acquire();
                int place = -1;

                synchronized (first_step_PLACES) {
                    for (int i = 0; i < first_step_PLACES.length; i++) {
                        if (!first_step_PLACES[i]) {
                            first_step_PLACES[i] = true;
                            place = i;
                            System.out.print("Car.No " + this.carNumber + " Entered Booth ");
                            if (place == 0) {
                                System.out.println("A");
                            } else
                                System.out.println("B");
                            break;
                        }
                    }
                }

                Thread.sleep(100);

                synchronized (first_step_PLACES) {
                    first_step_PLACES[place] = false;
                    System.out.print("Car.No " + this.carNumber + " Exit Booth ");
                    if (place == 0) {
                        System.out.println("A");
                    } else
                        System.out.println("B");
                }

                first_step.release();

                second_step.acquire();
                System.out.println("Car.No " + this.carNumber + " Entered Booth C");

                Thread.sleep(300);

                System.out.println("Car.No " + this.carNumber + " Exit Booth C");
                second_step.release();

                System.out.println("Car.No " + this.carNumber + " Exit CarWash ");
                carWash_Capacity.release();

                carwash.carCount--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please Enter Number of Cars : ");
        int num = scanner.nextInt();
        carCount = num;
        for (int i = 1; i <= num; i++) {
            new Thread(new Car(i)).start();
        }
        new Thread(new Worker()).start();
    }

    public static class Worker implements Runnable {

        @Override
        public void run() {
            while (carCount != 0)
                if (second_step.availablePermits() == 1)
                    System.out.println("Worker is Sleeping");
        }
    }

}
