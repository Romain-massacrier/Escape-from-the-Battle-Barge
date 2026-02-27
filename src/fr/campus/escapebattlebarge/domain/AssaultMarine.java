package fr.campus.escapebattlebarge.domain;

public class AssaultMarine extends Character {

    public AssaultMarine(String name) {
        super(
                "AssaultMarine",
                name,
                12,
                4,
                new OffensiveEquipment("Weapon", "Power Sword", 5)
        );
    }
}