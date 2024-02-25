package ro.ubbcluj.cs.map.UI;

import ro.ubbcluj.cs.map.domain.Prietenie;
import ro.ubbcluj.cs.map.domain.Utilizator;
import ro.ubbcluj.cs.map.service.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Console implements ConsoleInterface{

    Service srv;
    public Console(Service srv){
        this.srv=srv;
    }

    private void AdaugaUtilizator(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("\n Prenume: ");
        String prim = scanner.nextLine();

        System.out.print("Nume: ");
        String doi = scanner.nextLine();

        Utilizator u = new Utilizator(prim, doi,null,null);
        try {
            if (srv.ServAdaugaUt(u))
                System.out.println("Utilizator adaugat cu succes");
            else
                System.out.println("Acest utilizator exista deja");
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    private void GetAll(){
        Iterable<Utilizator> it= srv.ServGetUt();
        if (!it.iterator().hasNext()) {
            System.out.println("Nu exista utilizaatori");
            return;
        }
        it.forEach(System.out::println);
        //for (var a:it) {
         //   System.out.println(a);
       // }
    }


    private void DeleteUtilizator(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("\n Id-ul: ");
        Long number = scanner.nextLong();

        try {
            if(srv.DeleteUser(number))
                System.out.println("Utilizatorul a fost sters");
            else
                System.out.println("Utilizatorul nu exista");
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

    }


    private void AdaugaPrietenie(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("\n primul id: ");
        Long prim = scanner.nextLong();

        System.out.print("al doilea id: ");
        Long doi = scanner.nextLong();


        try {
            if (srv.AddFriend(prim,doi))
                System.out.println("Prietenia adaugata cu succes");
            else
                System.out.println("Aceasta prietenie exista deja");
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    private void GetAllPr(){
        var it= srv.ServGetPr();
        if (!it.iterator().hasNext()) {
            System.out.println("Nu exista Prietenii");
            return;
        }
        it.forEach(System.out::println);
        //for (var a:it) {
         //   System.out.println(a);
        //}
    }


    private void DeletePrietenie(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("\n primul id: ");
        Long prim = scanner.nextLong();

        System.out.print("al doilea id: ");
        Long doi = scanner.nextLong();

        try {
            if(srv.DeleteFriend(prim,doi))
                System.out.println("Prietenia a fost sterasa");
            else {
                if(srv.DeleteFriend(doi,prim))
                    System.out.println("Prietenia a fost sterasa");
                else
                    System.out.println("Prietenia nu exista");
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

    }

    private void myFriends()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\n Id: ");
        Long prim = scanner.nextLong();

        var it= srv.GetFriends(prim);
        if (it==null) {
            System.out.println("Nu exista acest id");
            return;
        }
        int ok=0;
        for (var a:it) {
            System.out.println(a);
            ok=1;
        }
        if (ok==0) {
            System.out.println("Nu are prieteni");
        }
    }

    public void execute(){

        while (true){
            System.out.println("Meniu:");
            System.out.print("adaugaut ");
            System.out.print("stergeut ");
            System.out.print("getallut ");
            System.out.print("adaugapr ");
            System.out.print("stergepr ");
            System.out.print("getallpr ");
            System.out.print("myfriends ");
            System.out.print("comunitati ");
            System.out.print("sociabila ");
            System.out.print("relatiile ");
            System.out.print("exit ");
            System.out.print("\n Comanda este: ");

            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine();

            switch (choice){
                case "adaugaut":
                    AdaugaUtilizator();
                    break;
                case "stergeut":
                    DeleteUtilizator();
                    break;
                case "getallut":
                    GetAll();
                    break;
                case "adaugapr":
                    AdaugaPrietenie();
                    break;
                case "stergepr":
                    DeletePrietenie();
                    break;
                case "getallpr":
                    GetAllPr();
                    break;
                case "myfriends":
                    myFriends();
                    break;
                case "comunitati":
                    System.out.println("Numarul de comunitati este: " + srv.Comuntati());
                    break;
                case "relatiile":
                    System.out.print("ID-ul utilizatorului: ");
                    scanner = new Scanner(System.in);
                    Long prim = scanner.nextLong();
                    scanner.nextLine();
                    System.out.print("Luna anului: ");
                    String luna=scanner.nextLine();
                    List<Prietenie> alll= srv.RelatiiFunc(prim,luna);
                    if (alll.isEmpty())
                    {
                        System.out.println("Nu are prietenii ");
                    }
                    else {
                        alll.forEach(p->{
                            if (p.getUser1().getId().equals(prim)){
                                System.out.println("Nume: " + p.getUser2().getFirstName() + " Prenume: "
                                        + p.getUser2().getLastName() + " data : " + p.getDate().getDayOfMonth());
                            }
                            else System.out.println("Nume: " + p.getUser1().getFirstName() + " Prenume: "
                                    + p.getUser1().getLastName() + " data : " + p.getDate().getDayOfMonth());
                        });
                    }
                    break;
                case "sociabila":
                    List<Utilizator> all=srv.sociabila();
                    if (all.size()==0)
                    {
                        System.out.println("Nu exista prietenii");
                    }
                    else {
                        System.out.println("Cea mai sociabila comunitate formata din " + all.size() + " utilizatori este: ");
                        for (Utilizator u: all)
                        {
                            System.out.println(u.getId());
                        }
                    }
                    break;
                case "exit":
                    return;
                default:
                    System.out.println("Comanda gresita");

            }

        }
    }
}
