package web.analytics.domain;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "fias")
public class Fias {
    @Id
    private UUID aoid;
    private String fullAddress;
    private String region;
    private String district;
    private String city;
    private String village;
    private String street;
    private String code;
    private boolean relevance;
    private UUID actualAOID;
    private UUID aoguid;

    public Fias(UUID aoid, String fullAddress, String region, String district, String city, String village, String street, String code, boolean relevance, UUID actualAOID, UUID aoguid) {
        this.aoid = aoid;
        this.fullAddress = fullAddress;
        this.region = region;
        this.district = district;
        this.city = city;
        this.village = village;
        this.street = street;
        this.code = code;
        this.relevance = relevance;
        this.actualAOID = actualAOID;
        this.aoguid = aoguid;
    }

    public Fias() {
    }

    public UUID getAoid() {
        return aoid;
    }

    public void setAoid(UUID aoid) {
        this.aoid = aoid;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isRelevance() {
        return relevance;
    }

    public void setRelevance(boolean relevance) {
        this.relevance = relevance;
    }

    public UUID getActualAOID() {
        return actualAOID;
    }

    public void setActualAOID(UUID actualAOID) {
        this.actualAOID = actualAOID;
    }

    public UUID getAoguid() {
        return aoguid;
    }

    public void setAoguid(UUID aoguid) {
        this.aoguid = aoguid;
    }
}
