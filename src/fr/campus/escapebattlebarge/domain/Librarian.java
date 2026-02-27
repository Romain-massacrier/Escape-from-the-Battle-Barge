package fr.campus.escapebattlebarge.domain;

public class Librarian extends Character {

    public Librarian(String name) {
        super(
                "Librarian",
                name,
                10,
                3,
                new OffensiveEquipment("Spell", "Smite", 2)
        );
    }
}