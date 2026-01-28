public enum StockType {
    COMMON("Common"),
    PREFERRED("Preferred") ;
    private final String label;
    StockType(String label) {
        this.label = label;
    }
    public String getLabel() {
        return label;
    }
}
