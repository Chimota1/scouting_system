package org.example.scoutingsys.exception;

public class ScoutLicenseExpiredException extends RuntimeException {

    private final String agency;

    public ScoutLicenseExpiredException(String agency) {
        super("Ліцензія агентства '" + agency + "' прострочена або заблокована");
        this.agency = agency;
    }

    public String getAgency() {
        return agency;
    }
}
