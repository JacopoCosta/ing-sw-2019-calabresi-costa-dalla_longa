package it.polimi.ingsw.network;

public class User {
    private String name; //unique username

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    //Users are equal if both have the same unique name
    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof User))
            return false;
        return this.name.equals(((User) o).getName());
    }
}
