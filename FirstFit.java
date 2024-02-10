import java.util.Scanner;

public class FirstFit {

    /**
     * this class is used for modeling Node of memory list
     * <p>
     * every node in memory list stores the process name of the block
     * (if process name set to null it means that this block is free)
     * length of the block
     * and next node in list
     */
    private static class MemoryNode {
        private String processName; //process name of block
        // if set to null it means that this block is free
        private int length; //size of block
        private MemoryNode nextNode; //next block in memory
        //if set to null it means last element in list

        // for constructing a node we need to specify it's size
        //NOTE: this creates a free block in memory
        public MemoryNode(int length) {
            this.length = length;
            this.processName = null;
            this.nextNode = null;
        }

        //some utility method for class variables
        public String getProcessName() {
            return processName;
        }

        public int getLength() {
            return length;
        }

        public MemoryNode getNextNode() {
            return nextNode;
        }

        public void setProcessName(String processName) {
            this.processName = processName;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public void setNextNode(MemoryNode nextNode) {
            this.nextNode = nextNode;
        }

        //get text representation of class objects
        //it returns name of the block process if it's not free
        //or length of block if it is free
        @Override
        public String toString() {
            if (processName == null)
                return "" + length;
            return processName;
        }
    }

    /**
     * this class is used to manage the memory based on the list of blocks that models memory blocks
     * <p>
     * it stores list of memory blocks and number of compaction accrued
     */
    private static class MemoryManager {
        private MemoryNode memoryStartNode; //first block in memory list
        private int compaction; //number of compactions

        // to construct a memory manager we need to specify the length of memory
        // it assumes total memory as a free block in first creation of manager
        public MemoryManager(int initLength) {
            memoryStartNode = new MemoryNode(initLength);
            compaction = 0;
        }

        //generating text representation of class object
        //representation includes number of compactions in first line
        //and at the second line we have process name if block is set to a process
        // or size of block if it is free
        @Override
        public String toString() {
            StringBuilder output = new StringBuilder();
            output.append(compaction).append("\n");//first line
            MemoryNode current = memoryStartNode;
            if (current == null)
                return output.toString();
            do {
                output.append(current.toString());// block information for each node
                output.append(" ");
                current = current.getNextNode();
            } while (current != null);
            output.delete(output.lastIndexOf(" "), output.length()); //exclude extra space from output
            return output.toString();
        }

        // this method handles the release of block that taken by process
        // we dont compact the free spaces after releasing space
        public void release(String processName) {
            MemoryNode current = memoryStartNode;//start from first block of memory
            //search for block that assigned to this process
            //assumed that given process name assigned before releasing it so we don't need to
            //handle release requests for not assigned process names
            while (!current.getProcessName().equals(processName)) current = current.getNextNode();
            //free the block by setting its process name to null
            current.setProcessName(null);
        }

        //method for assigning a memory block for a process
        public void require(String processName, int requiredMemoryLength) {
            MemoryNode current = memoryStartNode;//start from first block of memory
            for (MemoryNode previous = null; current != null; current = current.getNextNode()) {
                //search for free block with enough free space
                if (current.getProcessName() == null && current.getLength() >= requiredMemoryLength) {
                    //if size of free block is equal with needed memory size
                    //we only set the process name of block
                    if (current.getLength() == requiredMemoryLength) {
                        current.setProcessName(processName);
                    } else {
                        //if size of free block is more that we need
                        //then we create new block with requested size
                        //from free block

                        //create new block with needed size
                        MemoryNode entry = new MemoryNode(requiredMemoryLength);
                        //set process of newly created block
                        entry.setProcessName(processName);

                        // assign previous node for new block
                        if (previous != null)
                            previous.setNextNode(entry);
                        else//for assigning new block in beginning of memory
                            memoryStartNode = entry;

                        //update size of founded free block
                        current.setLength(current.getLength() - requiredMemoryLength);

                        //set extra free block after this block
                        entry.setNextNode(current);
                    }
                    return;
                }
                //update previous node of memory based next current node
                previous = current;
            }
            //if we can't find a free block with required size
            //we need compact free spaces together and after that assign a block to process
            //we assumed that we have enough free space for requested process
            //so we don't need to struggle with low memory
            compactAndAssign(processName, requiredMemoryLength);
        }

        //compaction process handler
        //it moves free spaces to end of memory and assigns a block to requested process after movement
        private void compactAndAssign(String processName, int requiredMemoryLength) {
            compaction++;//update number of compactions

            int freeSpaceLength = 0;//used to calculate total free space
            //start from beginning of memory
            MemoryNode current = memoryStartNode;
            //store last process block till searched blocks
            MemoryNode previousFilledSpace = null;
            //go through entire memory
            for (; current != null; current = current.getNextNode()) {
                if (current.getProcessName() == null)// if block is empty pass from it and update total free space
                    freeSpaceLength += current.getLength();
                else {
                    //if block is not empty move it to the next of previous process block
                    //NOTE:by doing so we remove the empty space (holes) between process
                    if (previousFilledSpace == null) // move first process to beginning of memory
                        previousFilledSpace = memoryStartNode = current;
                    else {
                        previousFilledSpace.setNextNode(current);
                        previousFilledSpace = current;
                    }
                }
            }
            //update last process block to dont have any block after it
            if (previousFilledSpace != null)
                previousFilledSpace.setNextNode(null);

            current = previousFilledSpace;
            //create new block with total free size and add it to end of memory
            current.setNextNode(new MemoryNode(freeSpaceLength));
            //getting memory to requested process again
            require(processName, requiredMemoryLength);
        }
    }

    //memory modeler
    private static MemoryManager memory = null;

    /**
     * Entry of program:
     * it controls the flow of process
     * NOTE:It doesn't control errors in input data.
     */
    public static void main(String[] args) {
        //creating an scanner for reading from standard input
        Scanner input = new Scanner(System.in);
        // get size of memory from first input line
        String inputData = input.nextLine();
        int memorySize = Integer.parseInt(inputData);
        //create memory mode with given size
        memory = new MemoryManager(memorySize);
        //read input lines and process them till end of input stream
        while (input.hasNext()) {
            inputData = input.nextLine();
            processInputData(inputData);
        }
        //print memory information(expected output)
        System.out.println(memory.toString());
    }


    //input line handler
    private static void processInputData(String inputData) {
        String[] data = inputData.split(" "); //splitting line to extract exact data
        if (data[0].equalsIgnoreCase("REQUEST")) { // Request for memory to some process
            String processName = data[1];   // get process name from extracted data
            int requiredMemoryLength = Integer.parseInt(data[2]); // get size of memory needed for process
            //handle memory request by using memory manager
            memory.require(processName, requiredMemoryLength);
        } else if (data[0].equalsIgnoreCase("RELEASE")) {//Release memory taken by some process
            String processName = data[1];   // get process name from extracted data
            memory.release(processName);    //handle release memory by using memory manager
        }
    }
}
