package org.example.lostnfind.entity;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import org.example.lostnfind.enums.OurStatus;

@Getter
@Setter
public class Relation {
    private long myId;
    private long itsId;
    private OurStatus ourStatus;
    private Date requestTime;
    private Date okTime;

    public Relation(){}

}
