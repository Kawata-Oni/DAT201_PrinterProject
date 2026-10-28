import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * คลาส PrintJob: โมเดลข้อมูลพื้นฐานสำหรับงานพิมพ์ 1 ชิ้น
 * รองรับการทำงานร่วมกันของฟีเจอร์ 1, 2 และ 3
 */
public class PrintJob {
    // -------------------------------------------------------------
    // Attributes (ข้อมูลประจำงานพิมพ์)
    // -------------------------------------------------------------
    private String jobId;          // รหัสงานพิมพ์ เช่น "JOB-001"
    private String fileName;       // ชื่อไฟล์ เช่น "Midterm_Report.pdf"
    private int totalPages;        // จำนวนหน้าที่ต้องพิมพ์ (สามารถปรับลดได้ถ้ามียกเลิกเฉพาะหน้า)
    private boolean isUrgent;      // สถานะงานด่วน (true = Urgent, false = Normal)
    private String status;         // สถานะงาน: "WAITING", "PRINTING", "COMPLETED", "CANCELLED"
    private LocalDateTime timestamp; // เวลาที่ส่งเข้าคิว

    private List<Integer> cancelledPages; // เก็บรายการเลขหน้าที่ถูกยกเลิก (เช่น หน้าที่ 7, 8, 10)

    // ตัวจัดรูปแบบการแสดงผลวันที่-เวลา
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    // -------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------
    public PrintJob(String jobId, String fileName, int totalPages, boolean isUrgent) {
        this.jobId = jobId;
        this.fileName = fileName;
        this.totalPages = Math.max(totalPages, 1); // ป้องกันกรณีใส่หน้าติดลบหรือ 0
        this.isUrgent = isUrgent;
        this.status = "WAITING";                  // สถานะเริ่มต้นเมื่อเข้าคิว
        this.timestamp = LocalDateTime.now();     // บันทึกเวลาปัจจุบันที่สร้างงาน
        this.cancelledPages = new ArrayList<>();
    }

    // -------------------------------------------------------------
    // Getters & Setters (Encapsulation)
    // -------------------------------------------------------------
    public String getJobId() {
        return jobId;
    }

    public String getFileName() {
        return fileName;
    }

    // คืนค่าจำนวนหน้าที่เหลือจริง (จำนวนทั้งหมด - หน้าที่ถูกยกเลิกไปแล้วที่ไม่ซ้ำกัน)
    public int getTotalPages() {
        int cancelledCount = (cancelledPages != null) ? cancelledPages.size() : 0;
        return totalPages - cancelledCount;
    }

    public int getOriginalTotalPages() {
        return totalPages;
    }

    public boolean isUrgent() {
        return isUrgent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }


    // -------------------------------------------------------------
    // Methods สำหรับซัพพอร์ตฟีเจอร์ต่างๆ
    // -------------------------------------------------------------


     //ยกเลิกจากข้างหน้า (นับจากหน้าแรกไป N หน้า)

    public boolean cancelFromFront(int n) {
        if (n <= 0 || getTotalPages() <= 0) return false;
        int count = 0;
        for (int i = 1; i <= totalPages; i++) {
            if (!cancelledPages.contains(i)) {
                cancelledPages.add(i);
                count++;
                if (count == n) break;
            }
        }
        return count > 0;
    }


     //ยกเลิกจากข้างหลัง (นับจากหน้าสุดท้ายถอยหลังมา N หน้า)

    public boolean cancelFromBack(int n) {
        if (n <= 0 || getTotalPages() <= 0) return false;
        int count = 0;
        for (int i = totalPages; i >= 1; i--) {
            if (!cancelledPages.contains(i)) {
                cancelledPages.add(i);
                count++;
                if (count == n) break;
            }
        }
        return count > 0;
    }


     //ยกเลิกเลขหน้าเฉพาะเจาะจง (รองรับหลายหน้า เช่น "7" หรือ "3,5,8-10")

    public boolean cancelSpecificPages(String pageInput) {
        try {
            String[] parts = pageInput.split(",");
            boolean success = false;

            for (String part : parts) {
                part = part.trim();
                if (part.contains("-")) {
                    // กรณีระบุเป็นช่วง เช่น "5-8"
                    String[] range = part.split("-");
                    int start = Integer.parseInt(range[0].trim());
                    int end = Integer.parseInt(range[1].trim());
                    for (int p = start; p <= end; p++) {
                        if (p >= 1 && p <= totalPages) {
                            // ป้องกันการใส่เลขหน้าซ้ำกัน
                            if (!cancelledPages.contains(p)) {
                                cancelledPages.add(p);
                            }
                            success = true;
                        }
                    }
                } else {
                    // กรณีระบุหน้าเดี่ยวๆ เช่น "7"
                    int p = Integer.parseInt(part);
                    if (p >= 1 && p <= totalPages) {
                        if (!cancelledPages.contains(p)) {
                            cancelledPages.add(p);
                        }
                        success = true;
                    }
                }
            }
            return success;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * แสดงข้อมูลสรุปแบบอ่านง่าย (สำหรับแสดงผลบน Terminal)
     */
    @Override
    public String toString() {
        String urgencyTag = isUrgent ? "[URGENT] " : "";
        String cancelInfo = cancelledPages.isEmpty() ? "" : " (Cancelled Pages: " + cancelledPages + ")";
        return String.format("%sID: %s | File: %s | Pages: %d/%d%s | Status: %s | Time: %s",
                urgencyTag,
                jobId,
                fileName,
                getTotalPages(),
                getOriginalTotalPages(),
                cancelInfo,
                status,
                timestamp.format(FORMATTER));
    }
}
