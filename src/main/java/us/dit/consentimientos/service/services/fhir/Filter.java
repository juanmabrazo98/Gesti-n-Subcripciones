package us.dit.consentimientos.service.services.fhir;

public class Filter {
    private String parameter;
    private String value;
    private String comparator;
    private String modifier;

    public Filter(String parameter, String value, String comparator, String modifier) {
        this.parameter = parameter;
        this.value = value;
        this.comparator = comparator;
        this.modifier = modifier;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
}

