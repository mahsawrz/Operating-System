import java.util.Scanner;
import java.util.concurrent.Semaphore;

//  class for shared values between threads
class Capacity {
    //  static property representing number of patients in each level
    static int[] level = {0, 0, 0};

    //  static method for finding level with minimum number of patients
    public static int getMinLevel() {
        //  initialize with first value
        int i = 0;
        int min = level[ 0];

        //  finding minimum
        for( int j = 1; j < 3; ++j) {
            if( level[ j] < min) {
                i = j;
                min = level[ j];
            }
        }

        //  returning index of level with minimum number of patients
        return i;
    }
}

//  patient class, each thread represents a patient
class Patient extends Thread {
    //  defining properties used by thread
    private int number;                 //  number of patient
    private int level;                  //  the level patient chose
    private Semaphore parking;          //  permit of parking lot
    private Semaphore[] waitingRoom;    //  permit of waiting room
    private Semaphore[] clinic;         //  permit of doctor's office

    //  constructor method
    public Patient( int number, Semaphore parking, Semaphore[] waitingRoom, Semaphore[] clinic) {
        this.number = number;
        this.parking = parking;
        this.waitingRoom = waitingRoom;
        this.clinic = clinic;
    }

    //  main procedure of thread
    @Override
    public void run() {
        try {
            System.out.println( "Patient No.[" + this.number + "] wants to Enter Parking");
            //  waiting for parking permit
            this.parking.acquire();
            //  getting permit of parking
            System.out.println( "Patient No.[" + this.number + "] Entered Parking");

            //  getting level with minimum number of patients
            level = Capacity.getMinLevel();

            //  patient enters to the chosen level
            Capacity.level[ level]++;

            //  waiting for permit of waiting room of level
            this.waitingRoom[ level].acquire();
            System.out.println( "Patient No.[" + this.number + "] Entered Dr[" + this.level + "] office");

            //  waiting for permit of doctor's office
            this.clinic[ level].acquire();

            //  patient leaves waiting room
            this.waitingRoom[ level].release();

            System.out.println( "Dr[" + this.level + "] visit patient No.[" + this.number + "]");

            //  visits takes 0.1 seconds
            Thread.sleep( 100);

            //  patient leave doctor's office
            this.clinic[ level].release();

            //  patient leaves from the chosen level
            Capacity.level[ level]--;

            System.out.println( "Patient No.[" + this.number + "] left the Dr[" + this.level + "] office");

            //  patient leave parking
            this.parking.release();

            System.out.println( "Patient No.[" + this.number + "] left the parking");

        } catch( InterruptedException e) {
            System.out.println( e);
        }
    }
}

public class Clinic {
    public static void main( String[] args) throws InterruptedException {

        //  variable for storing parking lot capacity
        int PARKING_CAPACITY = 8;

        //  variable for storing waiting room capacity
        int WAITING_ROOM_CAPACITY = 2;

        //  variable for storing capacity of doctor's office
        int CLINIC_CAPACITY = 1;

        //  creating an instance of scanner class
        Scanner input = new Scanner( System.in);

        //  getting number of patients from input
        System.out.print( "Enter number of patients: ");
        int n = input.nextInt();

        //  semaphore of parking lot
        Semaphore parking = new Semaphore( PARKING_CAPACITY);

        //  semaphore of waiting room of each level
        Semaphore[] waitingRoom = new Semaphore[ 3];
        //  initialize each waiting room semaphore
        for( int i = 0; i < 3; ++i)
            waitingRoom[i] = new Semaphore( WAITING_ROOM_CAPACITY);

        //  semaphore of doctor's office for each level
        Semaphore[] clinic = new Semaphore[ 3];
        //  initialize office semaphore of each level
        for( int i = 0; i < 3; ++i)
            clinic[ i] = new Semaphore( CLINIC_CAPACITY);

        //  creating an array of patient threads
        Patient[] patients = new Patient[ n];

        //  initiate and start patient threads
        for( int i = 0; i < n; ++i) {
            patients[ i] = new Patient( i, parking, waitingRoom, clinic);
            patients[ i].start();
        }

        //  waiting for threads to finish their job
        for( int i = 0; i < n; ++i)
            patients[ i].join();
    }
}
