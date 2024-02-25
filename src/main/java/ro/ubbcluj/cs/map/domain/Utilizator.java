package ro.ubbcluj.cs.map.domain;


import java.util.*;

public class Utilizator extends Entity<Long> {
    private String firstName;
    private String lastName;
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private List<Utilizator> friends;

    public Utilizator(String firstName, String lastName,String username,String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username=username;
        this.password=password;
        this.friends = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Utilizator> getFriends() {
        return friends;
    }

    @Override
    public String toString() {
        return "Utilizator{" + "ID: " + getId() +
                " firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(),getId());// am adaugat getid!!!!!! treb getFriends
    }

    public void addFriend(Utilizator u){
        friends.add(u);
    }

    public boolean removeFriend(Utilizator u) {
        return friends.remove(u);
    }
}