package web.analytics.domain;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name = "goodAddress")
public class GoodAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String fullAddress;

    @NotNull
    private String houseNumber;

    @NotNull
    private String addressComment;

    @NotNull
    private UUID linkFias;

    protected GoodAddress() {
    }

    public GoodAddress(String address, String houseNumber, String addressComment, UUID linkFias){
        this.fullAddress = address;
        this.houseNumber = houseNumber;
        this.addressComment = addressComment;
        this.linkFias = linkFias;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getAddressComment() {
        return addressComment;
    }

    public void setAddressComment(String addressComment) {
        this.addressComment = addressComment;
    }

    public UUID getLinkFias() {
        return linkFias;
    }

    public void setLinkFias(UUID linkFias) {
        this.linkFias = linkFias;
    }
}
