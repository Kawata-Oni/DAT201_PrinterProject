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
            System.out.println("Choose Feature:");
            System.out.println("[1] Add Normal Job");
            System.out.println("[2] Add Urgent Job");
            System.out.println("[3] Process Next Job");
            System.out.println("[4] Search & Cancel Job (Feature 2)");
            System.out.println("[5] View Job History & Reprint (Feature 3)");
            System.out.println("[0] Exit Program");
            System.out.print("Please enter your choice (0-5): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": // เพิ่มงานปกติ (Enqueue O(1))
                    System.out.println("\n--- [1] Add Normal Job ---");
                    System.out.print("Please enter the file name (e.g., Report.pdf): ");
                    String normalFileName = scanner.nextLine().trim();
                    if (normalFileName.isEmpty()) normalFileName = "Untitled.pdf";

                    System.out.print("Please enter the number of pages: ");
                    int normalPages = readIntegerInput(scanner);

                    String normalJobId = "JOB-" + (jobCounter++);
                    PrintJob normalJob = new PrintJob(normalJobId, normalFileName, normalPages, false);
                    printerService.enqueue(normalJob);
                    break;

                case "2": // เพิ่มงานด่วน (Urgent Enqueue O(1))
                    System.out.println("\n--- [2] Add Urgent Job ---");
                    System.out.print("Please enter the file name (e.g., Urgent_Exam.pdf): ");
                    String urgentFileName = scanner.nextLine().trim();
                    if (urgentFileName.isEmpty()) urgentFileName = "Urgent_Doc.pdf";

                    System.out.print("Please enter the number of pages: ");
                    int urgentPages = readIntegerInput(scanner);

                    String urgentJobId = "JOB-" + (jobCounter++);
                    PrintJob urgentJob = new PrintJob(urgentJobId, urgentFileName, urgentPages, true);
                    printerService.enqueueUrgent(urgentJob);
                    break;

                case "3": // ดึงงานไปพิมพ์ (Dequeue O(1))
                    System.out.println("\n--- [3] Process Next Job ---");
                    PrintJob finishedJob = printerService.processNextJob();
                    
                    // หากมีงานเดิมที่พิมพ์เสร็จ จะส่งต่อให้ฟีเจอร์ที่ 3 บันทึกประวัติ
                    if (finishedJob != null) {
                        System.out.println(">> Notification: Job " + finishedJob.getJobId() + " has been recorded in the history.");
                        // TODO: เชื่อมกับฟีเจอร์ที่ 3
                        // historyService.recordHistory(finishedJob);
                    }
                    break;

                case "4": // เตรียมไว้ให้ฟีเจอร์ที่ 2 (Search & Cancel)
                    System.out.println("\n--- [4] Search & Cancel Job (Feature 2) ---");
                    System.out.print("Please enter the Job ID you want to search/cancel (e.g., JOB-1): ");
                    String targetId = scanner.nextLine().trim();
                    
                    // TODO: ให้เพื่อนคนที่ 2 มาเขียนลอจิกเรียกใช้ตรงนี้
                    System.out.println(">> [During Development]: sent targetId (" + targetId + ") to JobSearchManager search in Deque");
                    break;

                case "5": // เตรียมไว้ให้ฟีเจอร์ที่ 3 (History & Reprint)
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