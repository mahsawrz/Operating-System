import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class DoctorsBuilding {

    private static int[] numPatientFloor = new int[3];            //Number of patients in each of the three floors
    private static Semaphore parking = new Semaphore(8);  //Semaphore for the critical area => (parking)
    private static Semaphore[] waitingRoom = new Semaphore[3];  //3 Semaphore for 3 critical areas => (3 waiting rooms)
    private static Semaphore[] drRoom = new Semaphore[3];      //3 Senaphore for 3 critical areas =>  (3 doctor rooms)


    //We consider patients as threads
    static class Patient extends Thread {

        private int patient_Number;  //Each patient has a number
        private int floor;           //Each patient is referred to a floor

        public Patient() {//in constructor, create each semaphore and value the "numPatientFloor" array

            for (int i = 0; i < numPatientFloor.length; i++) {
                numPatientFloor[i] = 0;  //At first, there is no patient in each floor
            }
            for (int i = 0; i < waitingRoom.length; i++) {
                waitingRoom[i] = new Semaphore(2); //waiting room capacity : 2
            }
            for (int i = 0; i < drRoom.length; i++) {
                drRoom[i] = new Semaphore(1); //doctor room capacity : 1
            }
        }


        /**
         * The "acquire" method allows entry to the critical area
         * when another patient (thread) is not in that area,
         * and the "release" method notifies that the critical area is empty.
         */
        public void run() {
            int count = 0; //counter for count each patient in each floor

            try {
                //First, get a permit for enter the parking
                System.out.println("Patient[" + patient_Number + "] Wants To Enter Parking");
                // acquiring the parking permit
                parking.acquire();
                System.out.println("Patient[" + patient_Number + "] Entered Parking");
                floor = getFloor(numPatientFloor);//Selects the floor with the lowest number of patients
                count = numPatientFloor[floor]++;
                // acquiring the waiting room permit
                waitingRoom[floor].acquire();
                System.out.println("Patient[" + patient_Number + "] Entered Dr[" + floor + "] Office");
                // acquiring the doctor room permit
                drRoom[floor].acquire();
                //Release the permit => leaving waiting room
                waitingRoom[floor].release();
                System.out.println("Dr[" + floor + "] Visit Patient[" + patient_Number + "]");
                Thread.sleep(100);
                //Release the permit => leaving doctor room
                drRoom[floor].release();
                count--;
                System.out.println("Patient[" + patient_Number + "] Left The Dr[" + floor + "] Office");
                //Release the permit => leaving parking
                parking.release();
                System.out.println("Patient[" + patient_Number + "] Left The Parking");

            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }

    }

    //This method Finds the floor that contains the least number of patients
    public static int getFloor(int[] floors) {
        int index = 0;

        for (int i = 0; i < floors.length; i++) {
            int min = floors[0];
            if (floors[i] < min) {
                min = floors[i];
                index = i;
            }
        }
        return index;
    }


    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        System.out.print("Please Enter The Number of Patients : ");
        int numPatient = input.nextInt();
        Patient[] patients = new Patient[numPatient];


        for (int i = 0; i < patients.length; i++) {
            patients[i] = new Patient();
            patients[i].patient_Number = i; //Numbering each patient
            patients[i].start();  //starting threads
        }
        for (int i = 0; i < patients.length; i++) {
            try {
                patients[i].join();   //waiting for threads
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}
