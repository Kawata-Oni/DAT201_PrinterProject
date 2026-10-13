import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public int getTotalPages() {
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

    /**
     * ฟังก์ชันรองรับ ฟีเจอร์ที่ 2: ยกเลิกเฉพาะหน้า (ลดจำนวนหน้าลง)
     * @param pagesToCancel จำนวนหน้าที่ต้องการยกเลิก
     * @return boolean บอกผลลัพธ์ว่าลบสำเร็จหรือไม่
     */
    public boolean cancelPages(int pagesToCancel) {
        if (pagesToCancel > 0 && pagesToCancel < this.totalPages) {
            this.totalPages -= pagesToCancel;
            return true;
        }
        return false; // ไม่ให้ลบจนเหลือ 0 หรือติดลบ (ถ้าจะลบทั้งหมดควรใช้คำสั่งยกเลิกทั้ง Job)
    }

    /**
     * แสดงข้อมูลสรุปแบบอ่านง่าย (สำหรับแสดงผลบน Terminal)
     */
    @Override
    public String toString() {
        String urgencyTag = isUrgent ? "[URGENT] " : "";
        return String.format("%sID: %s | File: %s | Pages: %d | Status: %s | Time: %s",
                urgencyTag,
                jobId,
                fileName,
                totalPages,
                status,
                timestamp.format(FORMATTER));
    }
}