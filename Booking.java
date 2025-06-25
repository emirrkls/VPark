
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Booking implements Serializable { // 'public' olduğundan ve adının doğru yazıldığından emin olun
    private static final long serialVersionUID = 1L; // Serializable için
    private Date startDate;
    private Date endDate;
    // private String customerId; // Gerekirse eklenebilir

    public Booking(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || startDate.after(endDate)) {
            // throw new IllegalArgumentException("Start date cannot be null, after end date, or equal to end date.");
            // Veya projenizdeki InvalidDateException'ı kullanabilirsiniz
            // Ancak basitlik adına şimdilik bu kontrolü yapıcıdan çıkarabiliriz,
            // metotlarda daha detaylı kontrol edilebilir.
            // Şimdilik temel atama:
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    // İki tarih aralığının çakışıp çakışmadığını kontrol eden metot
    public boolean overlaps(Date otherStart, Date otherEnd) {
        if (this.startDate == null || this.endDate == null || otherStart == null || otherEnd == null) {
            return false; // Null tarihlerle işlem yapma
        }
        // this.start < other.end AND this.end > other.start
        return this.startDate.before(otherEnd) && this.endDate.after(otherStart);
    }

    @Override
    public String toString() {
        return "Booking [startDate=" + (startDate != null ? startDate.toGMTString() : "null") +
               ", endDate=" + (endDate != null ? endDate.toGMTString() : "null") + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(startDate, booking.startDate) &&
               Objects.equals(endDate, booking.endDate);
        // && Objects.equals(customerId, booking.customerId); // Eğer customerId eklenirse
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate); // , customerId); // Eğer customerId eklenirse
    }
}