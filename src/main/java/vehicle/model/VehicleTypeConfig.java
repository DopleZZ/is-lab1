package vehicle.model;

public enum VehicleTypeConfig {
    
    SUBMARINE(150, "подводной лодки", false),
    BOAT(50, "лодки", false),
    CHOPPER(12, "вертолёта", true),
    DEFAULT(Long.MAX_VALUE, "транспорта", true);

    private final long maxCapacity;
    private final String typeName;
    private final boolean hasWheels;

    VehicleTypeConfig(long maxCapacity, String typeName, boolean hasWheels) {
        this.maxCapacity = maxCapacity;
        this.typeName = typeName;
        this.hasWheels = hasWheels;
    }

    public long getMaxCapacity() {
        return maxCapacity;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean hasWheels() {
        return hasWheels;
    }

    public static VehicleTypeConfig forType(VehicleType type) {
        if (type == null) return DEFAULT;
        switch (type) {
            case SUBMARINE: return SUBMARINE;
            case BOAT: return BOAT;
            case CHOPPER: return CHOPPER;
            default: return DEFAULT;
        }
    }

    public static boolean isWaterVehicle(VehicleType type) {
        VehicleTypeConfig config = forType(type);
        return !config.hasWheels();
    }
}
