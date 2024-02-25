package ro.ubbcluj.cs.map.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class Prietenie extends Entity<Tuple<Long,Long>> {

    private  Utilizator u1;
    private  Utilizator u2;
    LocalDateTime date;
    private FriendRequest acceptance;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");


    public FriendRequest getAcceptance() {
        return acceptance;
    }

    public void setAcceptance(FriendRequest acceptance) {
        this.acceptance = acceptance;
    }

    public Prietenie(Utilizator u1, Utilizator u2, LocalDateTime datea) {
        this.u1=u1;
        this.u2=u2;
        Tuple<Long,Long> cv= new Tuple<>(u1.getId(),u2.getId());
        this.setId(cv);
        date = datea;
        this.acceptance=FriendRequest.PENDING;
    }


    public Utilizator getUser1() {
        return u1;
    }

    public void setUser1(Utilizator user1) {
        this.u1 = user1;
    }

    public Utilizator getUser2() {
        return u2;
    }

    public void setUser2(Utilizator user2) {
        this.u2 = user2;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {

        return date;
    }

    @Override
    public String toString() {
        return "FriendShip {" +
                "data= " + date.format(formatter) +
                //"user1=" + u1 +
                //", user2=" + u2 +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prietenie))
            return false;
        Prietenie that = (Prietenie) o;
        return id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser1(), getUser2());
    }
}
