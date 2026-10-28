import java.util.Deque;
import java.util.Iterator;


public class JobSearchManager {

    // กำหนดเวลาเฉลี่ยในการพิมพ์ต่อ 1 หน้า
    private static final int SECONDS_PER_PAGE = 3;


     //ค้นหา Job ID ในคิวรอ พร้อมคำนวณลำดับที่และเวลาโดยประมาณที่ต้องรอก่อนถึงคิว

    public void searchJob(Deque<PrintJob> waitingQueue, PrintJob currentPrintingJob, String targetJobId) {
        if (targetJobId == null || targetJobId.isEmpty()) {
            System.out.println(">> [WARNING] Please enter a valid Job ID to search.");
            return;
        }


        // ตรวจสอบงานที่กำลังพิมพ์อยู่ปัจจุบันก่อน
        if (currentPrintingJob != null && currentPrintingJob.getJobId().equalsIgnoreCase(targetJobId)) {
            System.out.println("\n----------------- SEARCH RESULT -----------------");
            System.out.println(">> Status: Currently printing right now!");
            System.out.println(currentPrintingJob.toString());
            System.out.println("-------------------------------------------------");
            return;
        }

        int queuePosition = 1;
        int accumulatedPagesBefore = 0; // สะสมจำนวนหน้าของงานที่อยู่ก่อนหน้า เพื่อคำนวณเวลา
        boolean found = false;

        // วนลูป Linear Search ผ่าน Deque
        for (PrintJob job : waitingQueue) {
            if (job.getJobId().equalsIgnoreCase(targetJobId)) {
                found = true;

                int estimatedSeconds = accumulatedPagesBefore * SECONDS_PER_PAGE;
                String timeFormatted = formatSecondsToTime(estimatedSeconds);

                System.out.println("\n================= SEARCH RESULT =================");
                System.out.println(">> Status: Found in Waiting Queue");
                System.out.println(">> Queue Position : " + queuePosition);
                System.out.println(">> Estimated Wait : ~" + timeFormatted + " (" + estimatedSeconds + " seconds)");
                System.out.println(">> Details        : " + job.toString());
                System.out.println("=================================================");
                break;
            }

            accumulatedPagesBefore += job.getTotalPages();
            queuePosition++;
        }

        if (!found) {
            System.out.println(">> [INFO] Job ID \"" + targetJobId + "\" was not found in the waiting queue.");
        }
    }


     //ยกเลิกทั้ง Job ออกจากคิวทันที

    public void cancelEntireJob(Deque<PrintJob> waitingQueue, String targetJobId) {
        if (targetJobId == null || targetJobId.isEmpty()) {
            System.out.println(">> [WARNING] Please enter a valid Job ID.");
            return;
        }

        Iterator<PrintJob> iterator = waitingQueue.iterator();
        boolean found = false;

        while (iterator.hasNext()) {
            PrintJob job = iterator.next();
            if (job.getJobId().equalsIgnoreCase(targetJobId)) {
                iterator.remove();
                job.setStatus("CANCELLED");
                System.out.println(">> [CANCELLED SUCCESS] Job ID \"" + targetJobId + "\" has been completely removed from the queue.");
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println(">> [INFO] Job ID \"" + targetJobId + "\" was not found in the waiting queue.");
        }
    }


     //ยกเลิกแบบนับจากหน้า หรือจากหลัง

    public void cancelPagesByPosition(Deque<PrintJob> waitingQueue, String targetJobId, boolean fromFront, int count) {
        PrintJob targetJob = findJobInQueue(waitingQueue, targetJobId);
        if (targetJob == null) {
            System.out.println(">> [INFO] Job ID \"" + targetJobId + "\" was not found in the waiting queue.");
            return;
        }

        boolean success = fromFront ? targetJob.cancelFromFront(count) : targetJob.cancelFromBack(count);
        if (success) {
            String positionStr = fromFront ? "front" : "back";
            System.out.println(">> [SUCCESS] Cancelled " + count + " page(s) from the " + positionStr + " of Job " + targetJobId + ". Remaining pages: " + targetJob.getTotalPages());
        } else {
            System.out.println(">> [ERROR] Cannot cancel pages. Please check the page count.");
        }
    }


     //ยกเลิกเลขหน้าเฉพาะเจาะจง (รองรับหลายหน้า เช่น 7 หรือ 3,5,8-10)

    public void cancelSpecificPages(Deque<PrintJob> waitingQueue, String targetJobId, String pageInput) {
        PrintJob targetJob = findJobInQueue(waitingQueue, targetJobId);
        if (targetJob == null) {
            System.out.println(">> [INFO] Job ID \"" + targetJobId + "\" was not found in the waiting queue.");
            return;
        }

        boolean success = targetJob.cancelSpecificPages(pageInput);
        if (success) {
            System.out.println(">> [SUCCESS] Cancelled specified page(s) for Job " + targetJobId + ". Remaining pages: " + targetJob.getTotalPages());
        } else {
            System.out.println(">> [ERROR] Invalid page format or page numbers out of range.");
        }
    }


     //ค้นหางานในคิวรอ

    private PrintJob findJobInQueue(Deque<PrintJob> waitingQueue, String targetJobId) {
        for (PrintJob job : waitingQueue) {
            if (job.getJobId().equalsIgnoreCase(targetJobId)) {
                return job;
            }
        }
        return null;
    }


     //แปลงวินาทีเป็นรูปแบบ นาที:วินาที ให้อ่านง่าย

    private String formatSecondsToTime(int totalSeconds) {
        if (totalSeconds == 0) return "0 seconds (Ready to print next)";
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        if (minutes > 0) {
            return minutes + " min " + seconds + " sec";
        }
        return seconds + " seconds";
    }
}