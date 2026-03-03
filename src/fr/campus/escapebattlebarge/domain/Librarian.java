package fr.campus.escapebattlebarge.domain;

public class Librarian extends Player {
    public Librarian(String name) {
        super(name, PlayerClass.LIBRARIAN, 28);
        Weapon staff = new Weapon("Bâton de force", 3, 7);
        getInventory().equipWeapon(staff);
    }
}