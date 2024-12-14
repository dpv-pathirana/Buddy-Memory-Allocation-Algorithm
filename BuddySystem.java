import java.util.ArrayList;
import java.util.List;

public class BuddySystem {
    private int totalMemory; // Total memory size in KB
    private List<Block> freeBlocks; // List of free memory blocks

    // Constructor to initialize the memory pool
    public BuddySystem(int totalMemory) {
        this.totalMemory = totalMemory;
        freeBlocks = new ArrayList<>();
        freeBlocks.add(new Block(0, totalMemory)); // Initially, one free block of total size
        System.out.println("Initial Memory Pool: " + totalMemory + " KB");
        System.out.println("-----------------------------------------------------------");
    }

    // Method to allocate memory for a process
    public Block allocate(int size) {
        int requiredSize = nextPowerOfTwo(size); // Find smallest power of 2 greater than size
        Block allocatedBlock = null;
        int bestFitIndex = -1;
        int bestFitSize = Integer.MAX_VALUE;
    
        // Find the smallest block that can accommodate the request
        for (int i = 0; i < freeBlocks.size(); i++) {
            Block block = freeBlocks.get(i);
            if (block.size >= requiredSize && block.size < bestFitSize) {
                bestFitSize = block.size;
                bestFitIndex = i;
            }
        }
    
        // If a suitable block was found
        if (bestFitIndex != -1) {
            Block block = freeBlocks.get(bestFitIndex);
            freeBlocks.remove(bestFitIndex); // Remove the block from free list
            
            // Split blocks until the required size is reached
            while (block.size > requiredSize) {
                int halfSize = block.size / 2;
                Block buddy = new Block(block.start + halfSize, halfSize); // Create buddy block
                block.size = halfSize; // Reduce the size of the current block
                freeBlocks.add(buddy); // Add buddy block to free list
            }
            allocatedBlock = block;
            System.out.println("Allocated " + requiredSize + " KB at address " + block.start 
                             + " | Process Request: " + size + " KB");
        }
    
        if (allocatedBlock == null) {
            System.out.println("Allocation failed: Not enough memory for " + size + " KB");
        }
        return allocatedBlock;
    }

    // Method to deallocate memory
    public void deallocate(Block block) {
        System.out.println("Deallocating " + block.size + " KB at address " + block.start);
        freeBlocks.add(block); // Add the block back to the free list
        mergeBuddies(); // Merge free blocks if possible
    }

    // Method to merge buddy blocks
    private void mergeBuddies() {
        freeBlocks.sort((a, b) -> a.start - b.start); // Sort blocks by starting address

        for (int i = 0; i < freeBlocks.size() - 1; i++) {
            Block current = freeBlocks.get(i);
            Block next = freeBlocks.get(i + 1);

            // Check if they are buddies
            if (current.size == next.size && current.start + current.size == next.start) {
                System.out.println("Merging buddies: " + current.size + " KB at " + current.start + " and " + next.size + " KB at " + next.start);
                current.size *= 2; // Merge into a larger block
                freeBlocks.remove(i + 1); // Remove the next block
                i--; // Recheck for further merging
            }
        }
    }

    // Helper method to find the next power of 2 greater than or equal to a given number
    private int nextPowerOfTwo(int num) {
        int power = 1;
        while (power < num) {
            power *= 2;
        }
        return power;
    }

    private void sortFreeBlocksBySize() {
        freeBlocks.sort((a, b) -> a.size - b.size);
    }

    // Method to display the current memory state
    public void displayMemoryState() {
        System.out.println(">>> Current Free Memory Blocks:");
        sortFreeBlocksBySize();
        for (Block block : freeBlocks) {
            System.out.println("Start: " + block.start + ", Size: " + block.size + " KB");
        }
        System.out.println("-----------------------------------------------------------");
    }

    // Inner class to represent a memory block
    static class Block {
        int start; // Starting address of the block
        int size;  // Size of the block in KB

        public Block(int start, int size) {
            this.start = start;
            this.size = size;
        }
    }

    // Main method to test the buddy system implementation
    public static void main(String[] args) {
        BuddySystem buddySystem = new BuddySystem(1024); // Initialize memory pool (1024 KB)

        // Allocate 60 KB
        Block block1 = buddySystem.allocate(60);
        buddySystem.displayMemoryState();

        // Allocate 500 KB
        Block block2 = buddySystem.allocate(500);
        buddySystem.displayMemoryState();

        // Allocate 225 KB
        Block block3 = buddySystem.allocate(225);
        buddySystem.displayMemoryState();

        // Allocate 110 KB
        Block block4 = buddySystem.allocate(110);
        buddySystem.displayMemoryState();

        // Free 60 KB (corresponding to block1)
        if (block1 != null) {
            buddySystem.deallocate(block1);
        }
        buddySystem.displayMemoryState();

        // Free 500 KB (corresponding to block2)
        if (block2 != null) {
            buddySystem.deallocate(block2);
        }
        buddySystem.displayMemoryState();

        // Free 225 KB (corresponding to block2)
        if (block3 != null) {
            buddySystem.deallocate(block3);
        }
        buddySystem.displayMemoryState();

        // Free 110 KB (corresponding to block2)
        if (block3 != null) {
            buddySystem.deallocate(block4);
        }
        buddySystem.displayMemoryState();
    }
}