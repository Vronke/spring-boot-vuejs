package web.analytics.domain;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Entity
@Table(name = "action_log")
public class ActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private int typeId;
    private String author;
    private String fileName;

    private Date dateInsert;

    protected ActionLog() {
    }

    public ActionLog(String name, int typeId, String author, String fileName) {
        this.name = name;
        this.typeId = typeId;
        this.author = author;
        this.fileName = fileName;
    }

    public long getId() {
        return id;
    }
    public long getIdDesc() {
        return -id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int type) {
        this.typeId = type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDateInsert() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        return format.format(dateInsert);
    }

    public void setDateInsert(Date dateInsert) {
        this.dateInsert = dateInsert;
    }

    @PrePersist
    void dateInsert() {
        this.dateInsert = new Date();
    }
}
