package ro.ubbcluj.cs.map.service;

import ro.ubbcluj.cs.map.domain.Entity;
import ro.ubbcluj.cs.map.domain.Prietenie;
import ro.ubbcluj.cs.map.domain.Utilizator;
import ro.ubbcluj.cs.map.utils.events.UtilizatorChangeEvent;
import ro.ubbcluj.cs.map.utils.observer.Observable;

import java.util.List;

public interface ServiceInterface <ID> extends Observable<UtilizatorChangeEvent> {


    public boolean updateUser(Utilizator User);

    /**
     *  adauga un utilizator in repo
     * @param ut
     *      ut must be not null
     * @return true daca a fost adaugat si false daca nu
     * @throws IllegalArgumentException
     *                   if the given id is null.
     */
    public boolean ServAdaugaUt(Utilizator ut);

    /**
     *  sterge un utilizator in repo
     * @param id
     *      id must be not null
     * @return true daca a fost sters si false daca nu
     * @throws IllegalArgumentException
     *                   if the given id is null.
     */
    public boolean DeleteUser(ID id);

    /**
     *  adauga o prietenie in repo
     * @param u1,u2
     *      u1,u2 must be not null
     * @return true daca a fost adaugat si false daca nu
     * @throws IllegalArgumentException
     *                   if the given id is null.
     */
    public boolean AddFriend(ID u1,ID u2);


    /**
     *  sterge o prietenie din repo
     * @param u1,u2
     *      u1,u2 must be not null
     * @return true daca a fost stearsa prietenia si false daca nu
     * @throws IllegalArgumentException
     *                   if the given id is null.
     */
    public boolean DeleteFriend(ID u1,ID u2);

    /**
     *  returneaza o lista cu prietenii
     * @param
     *
     * @return o lista iterabila cu toate prieteniile sau null daca nu exista
     */
    public Iterable<Prietenie> ServGetPr();
    /**
     *  returneaza o lista cu utilizatori
     * @param
     *
     * @return o lista iterabila cu toti utilizatorii sau null daca nu exista
     */
    public Iterable<Utilizator> ServGetUt();

    /**
     *  cauta un utilizator dupa id
     * @param id
     *          id nu trebuie sa fie null
     *
     * @return utilizatorul  sau null daca nu exista
     */
    public Utilizator getById(ID id);

    /**
     *  afla toti prietenii unui utilizator
     * @param id
     *          id nu trebuie sa fie null
     *
     * @return utilizatorul  sau null daca nu id
     */
    public Iterable<Utilizator> GetFriends(ID id);

    /**
     *  afla numarul de comunitati
     * @param
     *
     *
     * @return numarul de comunitati
     */
    public int Comuntati();
    /**
     *  afla comunitatea cea mai sociabila
     * @param
     *
     *
     * @return o lista cu toti utilizatorii din acea comunitate
     */
    public List<Utilizator> sociabila();


    public List<Prietenie> RelatiiFunc(Long Id,String luna);



}
