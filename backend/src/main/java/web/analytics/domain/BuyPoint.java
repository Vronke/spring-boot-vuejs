package web.analytics.domain;

import javax.persistence.*;

@Entity
@Table(name = "buyPoint")
public class BuyPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long linkClient;
    private long linkAddress;

    protected BuyPoint() {
    }

    public  BuyPoint(long linkClient, long linkAddress){
        this.linkAddress = linkAddress;
        this.linkClient = linkClient;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLinkClient() {
        return linkClient;
    }

    public void setLinkClient(long linkClient) {
        this.linkClient = linkClient;
    }

    public long getLinkAddress() {
        return linkAddress;
    }

    public void setLinkAddress(long linkAddress) {
        this.linkAddress = linkAddress;
    }
}
