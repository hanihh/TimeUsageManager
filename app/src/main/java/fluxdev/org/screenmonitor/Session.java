package fluxdev.org.screenmonitor;

import java.util.Date;

/**
 * Created by hiba on 5/10/15.
 */
public class Session {
    private int id;
    private Date startDate;
    private Date endDate;

    public Session() {}

    public Session(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getSessionSeconds() {
        if (this.endDate == null)
            return 0;
        return endDate.compareTo(startDate);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        return null;
        //sb.append(startDate.)
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


}
