import java.util.Scanner;

public class FifoPageReplacementAlgorithm {
    /**
     * Class for modeling page replacement management using FIFO algorithm
     */
    public static class PagesQueue {
        private final int[] q; //array for storing id of pages that already in memory
        private int head, tail;//variable for storing head and tail of queue for FIFO algorithm

        //to construct this manager we need to specify number of frames we have in memory as size
        public PagesQueue(int size) {
            q = new int[size + 1]; // create array
            head = tail = 0;    //set queue to be empty
        }

        //check for existence of page in memory
        public boolean contains(int pageId) {
            //go from head of queue to tail of it and check page is there or not
            for (int i = head; i != tail; i = (i + 1) % q.length) {
                if (q[i] == pageId)
                    return true;
            }
            return false;
        }

        //inserting a page into a frame using FIFO replacement algorithm
        public void importPage(int pageId) {
            //check is queue if full
            //if queue is full than exclude page on the head of queue
            if ((tail + 1) % q.length == head)
                head = (head+1) % q.length;
            //insert page in tail of queue
            q[tail] = pageId;
            //update tail value
            tail = (tail + 1) % q.length;
        }
    }


    /**
     * Entry point of program:
     * it controls flow of the process
     */
    public static void main(String[] args) {
        //used to store memory size and page size given from input
        int memorySize, pageSize;
        // create an scanner to read from standard input
        Scanner input = new Scanner(System.in);
        //read process size (We dont need it now so only read it and pass)
        input.nextLine();
        //read memory size from input
        memorySize = Integer.parseInt(input.nextLine());
        //read page size form input
        pageSize = Integer.parseInt(input.nextLine());
        //calculate number of frames
        int numberOfFrames = memorySize / pageSize;
        //create queue with length of number of frames
        PagesQueue pagesInMemory = new PagesQueue(numberOfFrames);
        //used to store instruction number and page number given from input
        int instructionId, pageId;
        //store number of happened page faults
        int numberOfPageFault = 0;
        //read input to the end
        while (input.hasNextLine()) {
            //get instruction number
            instructionId = Integer.parseInt(input.nextLine());
            //calculate page id of instruction
            pageId = instructionId / pageSize;
            //check is page already in memory or not
            if (pagesInMemory.contains(pageId))
                continue;
            //when page is not presented in memory
            numberOfPageFault++; // update number of page faults
            pagesInMemory.importPage(pageId); // insert page to a frame based on FIFO order
        }
        //print number of page faults(expected output)
        System.out.println(numberOfPageFault);
    }
}
