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
        JobSearchManager jobSearchManager = new JobSearchManager();// ฟีเจอร์ที่ 2 (ของเพื่อนคนที่ 2)
        // HistoryManager historyService = new HistoryManager();
        
        HistoryManager historyService = new HistoryManager();
        //ประกาศใช้งานHistoryManager อันนี้คือฟีเจอร์3ที่เพิ่มเข้ามา (edit by นายพุทธ😁)

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
            System.out.println("[3] Sort Waiting Queue");
            System.out.println("[4] Search & Cancel Job");
            System.out.println("[5] View Job History & Reprint");
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
                    System.out.println("\n--- [2] Process Next Job ---");
                    PrintJob finishedJob = printerService.processNextJob();
                    
                    // หากมีงานเดิมที่พิมพ์เสร็จ จะส่งต่อให้ฟีเจอร์ที่ 3 บันทึกประวัติ
                    if (finishedJob != null) {
                        System.out.println(">> Notification: Job " + finishedJob.getJobId() + " has been recorded in the history.");
                        // TODO: เชื่อมกับฟีเจอร์ที่ 3
                        // historyService.recordHistory(finishedJob);
                        historyService.recordHistory(finishedJob); //เพิ่มบรรทัดนี้เข้าไปนะ Add by นายพุทธ😁
                    }
                    break;

                case "3":
                    System.out.println("\n--- [3] SORT WAITING QUEUE (URGENT & SHORTEST JOB FIRST) ---");
                    if (printerService.getWaitingQueue().isEmpty()) {
                        System.out.println(">> [WARNING] Cannot sort: Queue is currently empty.");
                    } else {
                        printerService.sortByUrgentAndPages();
                        printerService.displayQueue();
                    }
                    break;

                case "4": // ค้นหาและยกเลิกงานพิมพ์ (Feature 2)
                    System.out.println("\n--- [4] Search & Cancel Job ---");
                    System.out.print("Please enter the exact Job ID (e.g., JOB-1): ");
                    String targetId = scanner.nextLine().trim();

                    boolean exists = false;

                    if (printerService.getCurrentPrintingJob() != null &&
                            printerService.getCurrentPrintingJob().getJobId().equalsIgnoreCase(targetId)) {
                        exists = true;
                    } else {
                        for (PrintJob job : printerService.getWaitingQueue()) {
                            if (job.getJobId().equalsIgnoreCase(targetId)) {
                                exists = true;
                                break;
                            }
                        }
                    }
                    
                    

                    // 2. ถ้าหาไม่เจอ ให้แสดงข้อความแล้ว break ออกเพื่อกลับหน้าเมนูหลักทันที
                    if (!exists) {
                        System.out.println(">> [INFO] Job ID \"" + targetId + "\" not found. Please add a job first.");
                        break;
                    }

                    System.out.println("Select action:");
                    System.out.println("  [1] Search Job Status & Estimate Time");
                    System.out.println("  [2] Cancel Entire Job");
                    System.out.println("  [3] Cancel Pages from Front or Back (Count)");
                    System.out.println("  [4] Cancel Specific Page Numbers (e.g., 7 or 3,5,8-10)");
                    System.out.print("Choose action (1-4): ");
                    String actionChoice = scanner.nextLine().trim();

                    if (actionChoice.equals("1")) {
                        jobSearchManager.searchJob(printerService.getWaitingQueue(), printerService.getCurrentPrintingJob(), targetId);
                    } else if (actionChoice.equals("2")) {
                        jobSearchManager.cancelEntireJob(printerService.getWaitingQueue(), targetId);
                    } else if (actionChoice.equals("3")) {
                        System.out.println("Select direction:");
                        System.out.println("  [1] From Front (First N pages)");
                        System.out.println("  [2] From Back (Last N pages)");
                        System.out.print("Choose direction (1-2): ");
                        String dirChoice = scanner.nextLine().trim();

                        System.out.print("Enter number of pages to cancel: ");
                        int count = readIntegerInput(scanner);

                        jobSearchManager.cancelPagesByPosition(printerService.getWaitingQueue(), targetId, dirChoice.equals("1"), count);
                    } else if (actionChoice.equals("4")) {
                        System.out.print("Enter page numbers to cancel (e.g. 7 or 2,4,6-8): ");
                        String pageInput = scanner.nextLine().trim();
                        jobSearchManager.cancelSpecificPages(printerService.getWaitingQueue(), targetId, pageInput);
                    } else {
                        System.out.println(">> [Warning] Invalid action choice.");
                    }
                    break;

                case "5": // ระบบประวัติและพิมพ์ซ้ำ Feature 3 ของ ณัฐเมศร์
                    System.out.println("\n--- [5] View Job History & Reprint ---");
                    historyService.displayHistory();
                    
                    if (!historyService.isEmpty()) {
                    System.out.println("Select action:");
                    System.out.println("  [1] Reprint Last Job (Push back to queue)");
                    System.out.println("  [2] Clear History");
                    System.out.println("  [0] Back to main menu");
                    System.out.print("Choose action (0-2): ");
                    String historyChoice = scanner.nextLine().trim();

                    if (historyChoice.equals("1")) {
                        PrintJob reEnqueuedJob = historyService.reprintLastJob();
                        if (reEnqueuedJob != null) {
                            printerService.enqueue(reEnqueuedJob); // นำงานกลับเข้าคิวรอพิมพ์ปกติ
                        }
                    } else if (historyChoice.equals("2")) {
                        historyService.clearHistory();
                    }
                   }
                    break;
                    // Edit by นายพุทธ 😁

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