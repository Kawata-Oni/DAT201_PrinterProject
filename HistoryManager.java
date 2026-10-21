import java.util.ArrayDeque;
import java.util.Deque;

/**
 * ใช้โครงสร้างข้อมูล Stack(LIFO) ด้วย Deque
 */
public class HistoryManager {
    // ประวัติย้อนหลัง สุดอยู่ที่10
    private static final int MAX_HISTORY_SIZE = 10;
    
    // ใช้ Deque เป็น Stack สำหรับเก็บประวัติ
    private Deque<PrintJob> historyStack;

    public HistoryManager() {
        this.historyStack = new ArrayDeque<>();
    }
    
    
    public boolean isEmpty() {
        return historyStack.isEmpty();
    } // แก้เพิ่ม18/10/69 เช็คว่าคิวประวัติว่างหรือไม่ ไว้ในคลาส

    /**
     * Big-O ที่วิเคราะห์
     * บันทึกประวัติ (Push) - Big-O: O(1)
     */																		
    public void recordHistory(PrintJob job) {
        if (job == null) return;

        // ถ้า Stack เต็ม ให้ทำการลบประวัติเก่าสุดออกก่อน (ฝั่งท้ายของ Deque)
        if (historyStack.size() >= MAX_HISTORY_SIZE) {
            historyStack.removeLast();
        }

        // นำงานใหม่ใส่ไว้ที่หัว Stack (LIFO)
        historyStack.addFirst(job);
    }

    /**
     * 2. แสดงประวัติงานพิมพ์ทั้งหมด - Big-O: O(k) เมื่อ k คือจำนวนประวัติ
     */
    public void displayHistory() {
        System.out.println("\n======================================= PRINT JOB HISTORY =======================================");
        if (historyStack.isEmpty()) {
            System.out.println("  (No print history available)");
        } else {
            System.out.println("  +----+---------+-----------------+-------+-----------+-----------------+\n" +
                               "  | No | Job ID  | File Name       | Pages | Status    | Time            |\n" +
                               "  +----+---------+-----------------+-------+-----------+-----------------+");
            
            int index = 1;
            for (PrintJob job : historyStack) {
                String displayFileName = job.getFileName();
                if (displayFileName.length() > 15) {
                    displayFileName = displayFileName.substring(0, 12) + "...";
                }

                System.out.printf("  | %-2d | %-7s | %-15s | %-5d | %-9s | %-15s |\n",
                        index,
                        job.getJobId(),
                        displayFileName,
                        job.getTotalPages(), // แสดงจำนวนหน้าที่เปลี่ยนตามคำสั่ง
                        job.getStatus(),
                        job.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
                index++;
            }
            System.out.println("  +----+---------+-----------------+-------+-----------+-----------------+");
        }
        System.out.println("=================================================================================================");
    }

    /**
     * 3. ฟังก์ชัน Reprint Last Job (ดึงงานล่าสุดจาก History กลับเข้าสู่คิวหลักทันที) - Big-O: O(1)
     */
    public PrintJob reprintLastJob() {
        if (historyStack.isEmpty()) {
            System.out.println(">> [INFO] No history to reprint.");
            return null;
        }

        // ดึงงานล่าสุดออกจากยอด Stack (Pop)
        PrintJob lastJob = historyStack.pollFirst();
        
        // รีเซ็ตสถานะกลับเป็น WAITING เพื่อส่งกลับเข้าคิว
        lastJob.setStatus("WAITING");
        System.out.println(">> [REPRINT SUCCESS] Job " + lastJob.getJobId() + " (" + lastJob.getFileName() + ") has been re-enqueued!");
        
        return lastJob;
    }

    /**
     * 4. เคลียร์ประวัติทั้งหมด (Clear History) - Big-O: O(1)
     */
    public void clearHistory() {
        historyStack.clear();
        System.out.println(">> [INFO] Print history has been cleared.");
    }
}