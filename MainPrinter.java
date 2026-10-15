import java.util.Scanner;

public class MainPrinter {
    // กำหนดตัวนับสำหรับสร้างรหัสงานอัตโนมัติ เช่น JOB-1, JOB-2
    private static int jobCounter = 1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // -------------------------------------------------------------
        // เริ่มต้นสร้าง Instance ของฟีเจอร์หลัก
        // -------------------------------------------------------------
        BasicFeature printerService = new BasicFeature(); // ฟีเจอร์ที่ 1 (ของคุณ)
        
        // TODO: เมื่อเพื่อนทำฟีเจอร์ 2 และ 3 เสร็จ ให้นำมาประกาศใช้งานตรงนี้:
        // JobSearchManager searchService = new JobSearchManager();
        // HistoryManager historyService = new HistoryManager();

        System.out.println("==================================================");
        System.out.println("   PRINT SPOOLER DEMO (Java Console Application)");
        System.out.println("==================================================");

        boolean isRunning = true;

        while (isRunning) {
            // แสดงสถานะภาพรวมของคิวปัจจุบันเสมอ เพื่อให้เห็นการเปลี่ยนแปลงแบบ real-time
            printerService.displayQueue();

            // เมนูคำสั่งหลัก
            System.out.println("Select an option:");
            System.out.println("[1] Add Print Job");
            System.out.println("[2] Process Next Job");
            System.out.println("[3] Search & Cancel Job (Feature 2)");
            System.out.println("[4] View Job History & Reprint (Feature 3)");
            System.out.println("[0] Exit Program");
            System.out.print("Please enter your choice (0-5): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": // เพิ่มงานพิมพ์ (รวมทั้งงานปกติและงานด่วน)
                    System.out.println("\n--- [1] Add Print Job ---");
                    System.out.print("Enter file name (e.g., Report.pdf): ");
                    String fileName = scanner.nextLine().trim();
                    if (fileName.isEmpty()) fileName = "Untitled.pdf";

                    System.out.print("Enter total pages: ");
                    int totalPages = readIntegerInput(scanner);

                    // ให้เลือกประเภทของงานพิมพ์
                    System.out.println("Select job priority:");
                    System.out.println("  [1] Normal Job");
                    System.out.println("  [2] Urgent Job (VIP / Insert to Front)");
                    System.out.print("Choose priority type (1-2): ");
                    
                    boolean isUrgent = false;
                    while (true) {
                        String priorityChoice = scanner.nextLine().trim();
                        if (priorityChoice.equals("1")) {
                            isUrgent = false;
                            break;
                        } else if (priorityChoice.equals("2")) {
                            isUrgent = true;
                            break;
                        } else {
                            System.out.print("Invalid choice. Please choose 1 (Normal) or 2 (Urgent): ");
                        }
                    }

                    // สร้าง PrintJob Object
                    String jobId = "JOB-" + (jobCounter++);
                    PrintJob newJob = new PrintJob(jobId, fileName, totalPages, isUrgent);

                    // เลือกว่าจะเรียก enqueue ท้ายคิว หรือ enqueueUrgent แทรกหัวคิว (O(1) ทั้งคู่)
                    if (isUrgent) {
                        printerService.enqueueUrgent(newJob);
                    } else {
                        printerService.enqueue(newJob);
                    }
                    break;

                case "2": // ดึงงานไปพิมพ์ (Dequeue O(1))
                    System.out.println("\n--- [3] Process Next Job ---");
                    PrintJob finishedJob = printerService.processNextJob();
                    
                    // หากมีงานเดิมที่พิมพ์เสร็จ จะส่งต่อให้ฟีเจอร์ที่ 3 บันทึกประวัติ
                    if (finishedJob != null) {
                        System.out.println(">> Notification: Job " + finishedJob.getJobId() + " has been recorded in the history.");
                        // TODO: เชื่อมกับฟีเจอร์ที่ 3
                        // historyService.recordHistory(finishedJob);
                    }
                    break;

                case "3": // เตรียมไว้ให้ฟีเจอร์ที่ 2 (Search & Cancel)
                    System.out.println("\n--- [4] Search & Cancel Job (Feature 2) ---");
                    System.out.print("Please enter the Job ID you want to search/cancel (e.g., JOB-1): ");
                    String targetId = scanner.nextLine().trim();
                    
                    // TODO: ให้เพื่อนคนที่ 2 มาเขียนลอจิกเรียกใช้ตรงนี้
                    System.out.println(">> [During Development]: sent targetId (" + targetId + ") to JobSearchManager search in Deque");
                    break;

                case "4": // เตรียมไว้ให้ฟีเจอร์ที่ 3 (History & Reprint)
                    System.out.println("\n--- [5] View Job History & Reprint (Feature 3) ---");
                    // TODO: ให้เพื่อนคนที่ 3 มาเขียนลอจิกเรียกใช้ตรงนี้
                    System.out.println(">> [During Development]: sent request to HistoryManager(Stack)");
                    break;

                case "0":
                    System.out.println("\n>> [Notification] Shutting down the program... Thank you for using the service.");
                    isRunning = false;
                    break;

                default:
                    System.out.println("\n>> [Warning] Invalid option. Please select 0-5 only.");
                    break;
            }

            // เว้นวรรคให้อ่านผลลัพธ์ง่ายขึ้นในแต่ละรอบ
            if (isRunning) {
                System.out.println("\n>> [Notification] Press Enter to continue...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    /**
     * Helper Method สำหรับรับค่าตัวเลขจำนวนหน้า ป้องกันโปรแกรม Crash เมื่อผู้ใช้พิมพ์ตัวอักษร
     */
    private static int readIntegerInput(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value <= 0) {
                    System.out.print("The number of pages must be a positive integer. Please try again: ");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid integer: ");
            }
        }
    }
}