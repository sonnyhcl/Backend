package supplychain.entity;

import java.io.Serializable;
import java.util.Date;

public class Path implements Serializable {

    /**
     * uid
     */
    private static final long serialVersionUID = 243189353403867281L;

    private Date Start;

    private Date End;

    public Date getStart() {
        return Start;
    }

    public void setStart(Date start) {
        Start = start;
    }

    public Date getEnd() {
        return End;
    }

    public void setEnd(Date end) {
        End = end;
    }

}
