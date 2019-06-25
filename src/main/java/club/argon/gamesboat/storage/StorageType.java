package club.argon.gamesboat.storage;

public enum StorageType {

    FLAT_FILE, MYSQL;

    public static StorageType match(String input) {
        for (StorageType typez : values()) {
            if (typez.name().equalsIgnoreCase(input)) {
                return typez;
            }
        }
        return null;
    }

}
