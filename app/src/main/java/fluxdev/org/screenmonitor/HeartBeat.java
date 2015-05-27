package fluxdev.org.screenmonitor;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.Model;

import java.util.Date;

/**
 * Created by hiba on 5/22/15.
 */

@Table(name = "HeartBeat")

public class HeartBeat extends Model {
    @Column(name = "beat")
    private Date beat;
    @Column(name = "sequenceNo")
    private int sequence;

    public Date getBeat() {
        return beat;
    }

    public void setBeat(Date beat) {
        this.beat = beat;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
