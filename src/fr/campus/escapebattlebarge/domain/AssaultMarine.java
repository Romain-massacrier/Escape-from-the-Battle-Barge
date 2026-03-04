package fr.campus.escapebattlebarge.domain;

public class AssaultMarine extends Player {
    public AssaultMarine(String name) {
        super(name, PlayerClass.ASSAULT_MARINE, 35);
        Weapon sword = new Weapon("Chainsword", 4, 8);
        getInventory().equipWeapon(sword);
    }
}