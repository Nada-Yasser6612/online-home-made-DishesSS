package com.nada.model;

public class DishesRepresentative extends User {
    private int repId; // dishes_representative.id
    private long minimumCharge;

    public DishesRepresentative() {}

    public DishesRepresentative(int repId, long minimumCharge, int userId, String username, String email, String password, boolean isAdmin, String address, String phone) {
        super(email, password, username, isAdmin, address, phone);
        this.repId = repId;
        this.minimumCharge = minimumCharge;
        this.setId(userId); // inherited from User
    }

    public int getRepId() {
        return repId;
    }

    public void setRepId(int repId) {
        this.repId = repId;
    }

    public long getMinimumCharge() {
        return minimumCharge;
    }

    public void setMinimumCharge(long minimumCharge) {
        this.minimumCharge = minimumCharge;
    }
}
