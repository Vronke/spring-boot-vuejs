package web.analytics.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "rawAddresses")
public class RawAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String inn;

    @NotNull
    private String rawAddress;

    @NotNull
    private long linkBuyPoint;

    protected RawAddress() {
    }

    public RawAddress(String inn, String rawAddress){
        this.inn = inn;
        this.rawAddress = rawAddress;
        this.linkBuyPoint = 0;
    }

    public long getLinkBuyPoint() {
        return linkBuyPoint;
    }

    public void setLinkBuyPoint(long linkBuyPoint) {
        this.linkBuyPoint = linkBuyPoint;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getRawAddress() {
        return rawAddress;
    }

    public void setRawAddress(String rawAddress) {
        this.rawAddress = rawAddress;
    }

}
