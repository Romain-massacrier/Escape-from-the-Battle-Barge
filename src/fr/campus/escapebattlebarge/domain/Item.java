package fr.campus.escapebattlebarge.domain;

public abstract class Item {
    private final String name;
    private final ItemType type;

    protected Item(String name, ItemType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }
    public ItemType getType() { return type; }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }

}
