package fluxdev.org.screenmonitor;


import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.Model;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by hiba on 5/10/15.
 */
@Table(name = "Session")

public class Session extends Model {
    @Column(name = "startDate")
    private Date startDate;
    @Column(name = "endDate")
    private Date endDate;
    @Column(name = "duration")
    private long duration;
    //private int seconds;
    public Session() {}

    public Session(Date startDate, Date endDate) {
        super();
        this.startDate = startDate;
        this.endDate = endDate;
        //this.seconds = startDate.compareTo(endDate);
    }

    public long getSessionSeconds() {
        Date endDate = this.endDate;
        if (endDate == null)
            endDate = new Date();
        long diffInMs = endDate.getTime() - startDate.getTime();

        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

        return diffInSec;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        return null;
        //sb.append(startDate.)
    }

    //public int getId() {
    //    return id;
    //}

    //public void setId(int id) {
     //   this.id = id;
    //}

    public static Session getLast() {
        return new Select()
                .from(Session.class)
                .where("endDate is NULL")
                .orderBy("ID")
                .executeSingle();
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


    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
