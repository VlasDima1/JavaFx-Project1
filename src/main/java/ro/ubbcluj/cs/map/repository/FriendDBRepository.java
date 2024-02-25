package ro.ubbcluj.cs.map.repository;

import ro.ubbcluj.cs.map.domain.*;
import ro.ubbcluj.cs.map.domain.validators.Validator;
import ro.ubbcluj.cs.map.repository.UserDBRepository;

import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class FriendDBRepository implements Repository<Tuple<Long,Long>, Prietenie > {

    private String url;
    private String username;
    private String password;
    private Validator<Prietenie> validator;

    public FriendDBRepository(String url, String username, String password,Validator<Prietenie> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator=validator;
    }

    @Override
    public Optional<Prietenie> findOne(Tuple<Long, Long> longLongTuple) {
        try(Connection connection=DriverManager.getConnection(url,username,password);
            PreparedStatement statement=connection.prepareStatement("select * from friends" +
                    " where (id1 = ? AND id2 = ?) OR (id2 = ? AND id1 = ?) ");
            PreparedStatement statement1=connection.prepareStatement("select * from users where id=?")
        )
        {
            statement.setLong(1,longLongTuple.getLeft());
            statement.setLong(2,longLongTuple.getRight());
            statement.setLong(3,longLongTuple.getLeft());
            statement.setLong(4,longLongTuple.getRight());
            ResultSet resultSet=statement.executeQuery();
            if (resultSet.next())
            {
                Timestamp timestamp=resultSet.getTimestamp("date");
                Long id1=resultSet.getLong("id1");
                Long id2=resultSet.getLong("id2");

                String acc = resultSet.getString("status");
                statement1.setLong(1,id1);
                resultSet=statement1.executeQuery();
                resultSet.next();
                Utilizator u1=new Utilizator(resultSet.getString("first_name"),resultSet.getString("last_name"),null,null);
               u1.setId(id1);//longLongTuple.getLeft()

                statement1.setLong(1,id2);
                resultSet=statement1.executeQuery();
                resultSet.next();
                Utilizator u2=new Utilizator(resultSet.getString("first_name"),resultSet.getString("last_name"),null,null);
                u2.setId(id2);

                Prietenie prietenie=new Prietenie(u1,u2,timestamp.toLocalDateTime());

                prietenie.setId(new Tuple<>(id1,id2));
                if (Objects.equals(acc, "ACCEPTED"))
                    prietenie.setAcceptance(FriendRequest.ACCEPTED);
                if (Objects.equals(acc, "PEDNDING"))
                    prietenie.setAcceptance(FriendRequest.PENDING);
                if (Objects.equals(acc, "REJECTED"))
                    prietenie.setAcceptance(FriendRequest.REJECTED);
               // prietenie.setUser1(u1);
                //prietenie.setUser2(u2);

                return Optional.of(prietenie);

            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> prietenies = new HashSet<>();
        try(Connection connection=DriverManager.getConnection(url,username,password);
            PreparedStatement statement=connection.prepareStatement("select * from friends");
            ResultSet resultSet = statement.executeQuery();

            PreparedStatement statement1 = connection.prepareStatement("select * from users where id=?");
        )
        {
            ResultSet resultSet2;
            while (resultSet.next()){
                Timestamp timestamp=resultSet.getTimestamp("date");
                LocalDateTime date=timestamp.toLocalDateTime();

                Long id1=resultSet.getLong("id1");
                Long id2=resultSet.getLong("id2");

                String acc=resultSet.getString("status");

                statement1.setLong(1,id1);
                resultSet2=statement1.executeQuery();
                resultSet2.next();
                //System.out.println(resultSet2.getString(2));
                Utilizator u1=new Utilizator(resultSet2.getString("first_name"),resultSet2.getString("last_name"),null,null);
                u1.setId(id1);

                statement1.setLong(1,id2);
                resultSet2=statement1.executeQuery();
                resultSet2.next();
                Utilizator u2=new Utilizator(resultSet2.getString("first_name"),resultSet2.getString("last_name"),null,null);
                u2.setId(id2);

                Prietenie prietenie=new Prietenie(u1,u2,date);
                prietenie.setId(new Tuple<>(id1,id2));
                if (Objects.equals(acc, "ACCEPTED"))
                    prietenie.setAcceptance(FriendRequest.ACCEPTED);
                if (Objects.equals(acc, "PEDNDING"))
                    prietenie.setAcceptance(FriendRequest.PENDING);
                if (Objects.equals(acc, "REJECTED"))
                    prietenie.setAcceptance(FriendRequest.REJECTED);

                prietenies.add(prietenie);
            }
            return prietenies;

        }
        catch (SQLException e)
        {
            throw new RuntimeException();
        }
    }

    @Override
    public Optional<Prietenie> save(Prietenie entity) {
        validator.validate(entity);
        try(Connection connection= DriverManager.getConnection(url,username,password);
            PreparedStatement statement=connection.prepareStatement("insert into friends values (?,?,?,?)")
        )
        {
            statement.setLong(1,entity.getId().getLeft());
            statement.setLong(2,entity.getId().getRight());
            Timestamp timestamp=Timestamp.valueOf(entity.getDate());
            statement.setTimestamp(3,timestamp);
            statement.setString(4,entity.getAcceptance().toString());

            statement.executeUpdate();

        }
        catch (SQLException e) {
            throw new RuntimeException();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Prietenie> delete(Tuple<Long, Long> longLongTuple) {
        try(Connection connection=DriverManager.getConnection(url,username,password);
            PreparedStatement updateStatement=connection.prepareStatement("delete from friends where " +
                    "(id1 = ? AND id2 = ?) OR (id1 = ? AND id2 = ?)")
        )
        {
            Optional<Prietenie> pr=findOne(longLongTuple);
            if (pr.isPresent())
            {
                updateStatement.setLong(1,longLongTuple.getLeft());
                updateStatement.setLong(2,longLongTuple.getRight());
                updateStatement.setLong(3,longLongTuple.getRight());
                updateStatement.setLong(4,longLongTuple.getLeft());
                if (updateStatement.executeUpdate()>0)
                {
                    pr.get().setId(new Tuple<>(-1L,-1L));
                    return pr;
                }
                throw new SQLException("Delete failed");
            }

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Prietenie> update(Prietenie entity) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement updateStatement = connection.prepareStatement("update friends set status=? where id1=? AND id2=?");

        ){
            updateStatement.setLong(2, entity.getUser1().getId());
            updateStatement.setLong(3, entity.getUser2().getId());

            updateStatement.setString(1,entity.getAcceptance().toString());


            if (updateStatement.executeUpdate() > 0){
                ResultSet resultSet = updateStatement.getGeneratedKeys();
//                if (resultSet.next()){
//                    //Means movie is updated
//                    return Optional.of(entity);
//                }
//                else{
//                    //failed update; todo: add failure logic
//                    System.out.println("Update failed");
//                }
            }

        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }
}
