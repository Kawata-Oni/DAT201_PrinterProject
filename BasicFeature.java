import java.util.ArrayDeque;
import java.util.Deque;
import java.util.ArrayList;
import java.util.List;

// Data Structure : Deque (Double-Ended Queue)

public class BasicFeature {
    // -------------------------------------------------------------
    // Data Structures & Attributes
    // -------------------------------------------------------------
    
    private Deque<PrintJob> waitingQueue; // เก็บงานพิมพ์ที่รออยู่ในคิว
    
    private PrintJob currentPrintingJob; // เก็บงานพิมพ์ที่กำลังพิมพ์อยู่ (ถ้าไม่มีงานพิมพ์อยู่ ให้เป็น null)

    // -------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------
    public BasicFeature() {
        
        this.waitingQueue = new ArrayDeque<>(); // ใช้ ArrayDeque เพราะทำงานเป็น queue ได้ดี (มี firstPoll, addLast, addFirst)
        this.currentPrintingJob = null;
    }

    // -------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------
    public Deque<PrintJob> getWaitingQueue() {
        return waitingQueue;
    }

    public PrintJob getCurrentPrintingJob() {
        return currentPrintingJob;
    }

    // -------------------------------------------------------------
    // Methods Feature 1
    // -------------------------------------------------------------

    // 1. เพิ่มงานปกติลงท้าย waitingQueue
    // Big-O: O(1)
    public void enqueue(PrintJob job) {
        if (job == null) return;
        
        job.setStatus("WAITING");
        waitingQueue.addLast(job); // ต่อท้ายคิว
        System.out.println(">> [SUCCESS] Normal job enqueued: " + job.getJobId() + " (" + job.getFileName() + ")");
    }

    // 2. แทรกงานด่วนไว้หน้า waitingQueue
    // Big-O: O(1)
    public void enqueueUrgent(PrintJob job) {
        if (job == null) return;

        job.setStatus("WAITING");
        waitingQueue.addFirst(job); // แทรกหัวคิวรอทันที
        System.out.println(">> [URGENT] Urgent job inserted to front: " + job.getJobId() + " (" + job.getFileName() + ")");
    }

    // 3. ดึงงานถัดไปส่งให้เครื่องพิมพ์
    // Big-O: O(1)
    public PrintJob processNextJob() {
        PrintJob finishedJob = null;

        // ถ้ามีงานที่กำลังพิมพ์อยู่เดิม ให้เปลี่ยนสถานะเป็น COMPLETED
        if (currentPrintingJob != null) {
            currentPrintingJob.setStatus("COMPLETED");
            finishedJob = currentPrintingJob;
            System.out.println(">> [FINISHED] Job " + finishedJob.getJobId() + " printed successfully!");
            currentPrintingJob = null;
        }

        // ดึงงานถัดไปจากหัวคิวรอขึ้นมาพิมพ์ (ถ้ามี)
        if (!waitingQueue.isEmpty()) {
            currentPrintingJob = waitingQueue.pollFirst(); // O(1)
            currentPrintingJob.setStatus("PRINTING");
            System.out.println(">> [PRINTING] Started printing: " + currentPrintingJob.getJobId() + " (" + currentPrintingJob.getFileName() + ")");
        } else {
            System.out.println(">> [INFO] Queue is empty. No jobs waiting.");
        }

        return finishedJob; // ส่งงานที่เพิ่งเสร็จกลับไปให้ระบบนำไปเข้า History
    }

    //4. แสดงรายการและลำดับของงานที่กำลังรอพิมพ์ทั้งหมดในรูปแบบตารางจัดคอลัมน์ตรงกัน
    //Big-O: O(n)
    public void displayQueue() {
        System.out.println("\n======================================= PRINT SPOOLER STATUS =======================================");
        
        // แสดงงานที่กำลังพิมพ์อยู่ ณ ปัจจุบัน
        if (currentPrintingJob != null) {
            String priorityTag = currentPrintingJob.isUrgent() ? "[URGENT]" : "[NORMAL]";
            System.out.printf("[CURRENTLY PRINTING]: %-8s | ID: %-7s | File: %-15s | Pages: %-4d | Time: %s\n",
                    priorityTag,
                    currentPrintingJob.getJobId(),
                    currentPrintingJob.getFileName(),
                    currentPrintingJob.getTotalPages(),
                    currentPrintingJob.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
        } else {
            System.out.println("[CURRENTLY PRINTING]: (Printer is IDLE)");
        }
        
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.println("[WAITING QUEUE] (Total in queue: " + waitingQueue.size() + " job(s)):");

        if (waitingQueue.isEmpty()) {
            System.out.println("  (No jobs waiting in queue)");
        } else {
            // หัวตาราง (Table Header)
            System.out.println("  +----+----------+---------+-----------------+-------+---------+----------+");
            System.out.println("  | No | Priority | Job ID  | File Name       | Pages | Status  | Time     |");
            System.out.println("  +----+----------+---------+-----------------+-------+---------+----------+");

            int queuePosition = 1;
            // Traverse ทุกโหนดใน Deque จากหัวไปท้าย
            // BigO : O(n)
            for (PrintJob job : waitingQueue) {
                String priorityTag = job.isUrgent() ? "URGENT" : "NORMAL";
                
                // จัดช่องไฟให้ตรงกันทุกคอลัมน์:
                // %-2d = ลำดับชิดซ้าย 2 ตัวอักษร
                // %-8s = ความสำคัญ 8 ตัวอักษร
                // %-7s = Job ID 7 ตัวอักษร
                // %-15s = ชื่อไฟล์ 15 ตัวอักษร (ถ้าเกินจะตัดทอนให้อัตโนมัติเพื่อไม่ให้ตารางเบี้ยว)
                String displayFileName = job.getFileName();
                if (displayFileName.length() > 15) {
                    displayFileName = displayFileName.substring(0, 12) + "...";
                }

                System.out.printf("  | %-2d | %-8s | %-7s | %-15s | %-5d | %-7s | %-8s |\n",
                        queuePosition,
                        priorityTag,
                        job.getJobId(),
                        displayFileName,
                        job.getTotalPages(),
                        job.getStatus(),
                        job.getTimestamp().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
                queuePosition++;
            }
            System.out.println("  +----+----------+---------+-----------------+-------+---------+----------+");
        }
        System.out.println("====================================================================================================\n");
    }

    // 5. จัดเรียงลำดับงานใน waitingQueue โดยให้ Urgent Jobs มาก่อน และถ้าเป็นประเภทเดียวกัน ให้เรียงตามจำนวนหน้าที่น้อยที่สุดมาก่อน
    // Insertion Sort => Big-O: O(n^2)
    public void sortByUrgentAndPages() {
       if (waitingQueue == null || waitingQueue.size() <= 1) {
            return;
        }

        // แปลง Deque เป็น List ชั่วคราวเพื่อให้สลับตำแหน่งตาม Index ได้ง่าย
        List<PrintJob> list = new ArrayList<>(waitingQueue);

        // Insertion Sort
        for (int i = 1; i < list.size(); i++) {
            PrintJob key = list.get(i);
            int j = i - 1;

          // วนลูปเปรียบเทียบเพื่อหาตำแหน่งแทรกที่ถูกต้อง
          while (j >= 0 && compareJobs(list.get(j), key) > 0) {
              list.set(j + 1, list.get(j));
              j--;
          }
          list.set(j + 1, key);
      }

       // ล้าง Deque เดิมและใส่ข้อมูลที่เรียงลำดับเสร็จแล้วกลับเข้าไป
      waitingQueue.clear();
      waitingQueue.addAll(list);

      System.out.println(">> [SORTED] Queue reordered: Urgent first, followed by smallest page count.");
    }

    // Method สำหรับเปรียบเทียบลำดับระหว่างสองงาน:
    // คืนค่า -1 ถ้า job1 ควรอยู่ก่อน job2
    // คืนค่า 1 ถ้า job1 ควรอยู่หลัง job2
    // คืนค่า 0 ถ้ามีลำดับเท่ากัน
    
    private int compareJobs(PrintJob job1, PrintJob job2) {
        // condition 1: เปรียบเทียบความเร่งด่วน (Urgent มาก่อน Normal)
        if (job1.isUrgent() && !job2.isUrgent()) {
            return -1; // job1 (urgent) มาก่อน
        }
        if (!job1.isUrgent() && job2.isUrgent()) {
            return 1;  // job2 (urgent) มาก่อน -> job1 ถอยไปหลัง
        }

        // condition 2: ถ้าความเร่งด่วนเท่ากัน ดูที่จำนวนหน้า (หน้าน้อยมาก่อน)
        return Integer.compare(job1.getTotalPages(), job2.getTotalPages());
    }
}