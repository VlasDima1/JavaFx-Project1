package ro.ubbcluj.cs.map.repository;

import ro.ubbcluj.cs.map.domain.Utilizator;
import ro.ubbcluj.cs.map.domain.validators.Validator;
import ro.ubbcluj.cs.map.repository.paging.Page;
import ro.ubbcluj.cs.map.repository.paging.Pageable;
import ro.ubbcluj.cs.map.repository.paging.PagingRepository;

import java.sql.*;
import java.util.*;

import static java.lang.Math.toIntExact;

public class UserPageDBRepository implements PagingRepository<Long, Utilizator> {

    private String url;
    private String username;
    private String password;

    private Validator<Utilizator> validator;


    public UserPageDBRepository(String url, String username, String password,Validator<Utilizator> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator=validator;
    }

    @Override
    public Optional<Utilizator> findOne(Long longID) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from users " +
                    "where id = ?");

        ) {
            statement.setInt(1, Math.toIntExact(longID));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username=resultSet.getString("username");
                String password=resultSet.getString("password");
                Utilizator u = new Utilizator(firstName,lastName,username,password);
                u.setId(longID);
                return Optional.ofNullable(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();


        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users");
             ResultSet resultSet = statement.executeQuery();
             //PreparedStatement statement1=connection.prepareStatement("se");

        ) {

            while (resultSet.next())
            {
                Long id= resultSet.getLong("id");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                String username=resultSet.getString("username");
                String password=resultSet.getString("password");
                Utilizator user=new Utilizator(firstName,lastName,username,password);
                user.setId(id);
                users.add(user);

            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        String insertSqlStatement = "insert into users(first_name,last_name) values (?,?)";
        validator.validate(entity);
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(insertSqlStatement);

        ){
            statement.setString(1,entity.getFirstName());
            statement.setString(2,entity.getLastName());
            statement.executeUpdate();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return Optional.empty();

    }

    @Override
    public Optional<Utilizator> delete(Long aLong) {

        try( Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement statement=connection.prepareStatement("delete from users where id=?")
        )
        {
            statement.setLong(1,aLong);
            Optional<Utilizator> utilizator=findOne(aLong);
            if(utilizator.isPresent())
            {
                if (statement.executeUpdate()>0)
                {
                    utilizator.get().setId(-1L);
                    return utilizator;
                }
                throw new RuntimeException("Delete failed");
            }

        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Utilizator> update(Utilizator entity) {
        validator.validate(entity);
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement updateStatement = connection.prepareStatement("update users set first_name=?, last_name=? where id=?");

        ){
            updateStatement.setString(1, entity.getFirstName());
            updateStatement.setString(2,entity.getLastName());
            updateStatement.setLong(3, entity.getId());
            updateStatement.executeUpdate();

        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    private int returnNumberOfElements(){
        int numberOfElements=0;
        try (Connection connection = DriverManager.getConnection(url,username,password);
             // pas 2: design & execute SLQ
             PreparedStatement statement = connection.prepareStatement("select count(*) as count from users");
             ResultSet resultSet = statement.executeQuery();
        ) {
            // pas 3: process result set
            while (resultSet.next()){
                numberOfElements = resultSet.getInt("count");
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        // pas 3: return no elements
        return numberOfElements;
    }

    @Override
    public Page<Utilizator> findAll(Pageable pageable) {
        int numberOfElements = returnNumberOfElements();
        int limit = pageable.getPageSize();
        int offset = pageable.getPageSize()*pageable.getPageNumber();

        if(offset >= numberOfElements)
            return new Page<>(new ArrayList<>(), numberOfElements);

        List<Utilizator> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url,username,password);
             // pas 2: design & execute SLQ
             PreparedStatement statement = connection.prepareStatement("select * from users limit ? offset ?");
        ) {
            statement.setInt(2, offset);
            statement.setInt(1,limit);
            ResultSet resultSet = statement.executeQuery();
            // pas 3: process result set
            while (resultSet.next()){
                Long id= resultSet.getLong("id");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                String username=resultSet.getString("username");
                String password=resultSet.getString("password");
                Utilizator user=new Utilizator(firstName,lastName,username,password);
                user.setId(id);
                users.add(user);
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        // pas 3: return result list
        return new Page<>(users, numberOfElements);
    }
}
