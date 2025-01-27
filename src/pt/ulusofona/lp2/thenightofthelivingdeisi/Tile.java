package pt.ulusofona.lp2.thenightofthelivingdeisi;

public class Tile {
    private Creature creature;
    private Equipment equipment;
    private SafeHeaven safeHeaven;

    public Tile() {
        this.creature = null;
        this.equipment = null;
        this.safeHeaven = null;
    }

    public Creature getCreature() {
        return creature;
    }

    public void setCreature(Creature creature) {
        this.creature = creature;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public SafeHeaven getSafeHeaven() {
        return safeHeaven;
    }

    public void setSafeHeaven(SafeHeaven safeHeaven) {
        this.safeHeaven = safeHeaven;
    }
}
