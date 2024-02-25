package ro.ubbcluj.cs.map.repository;

import ro.ubbcluj.cs.map.domain.Message;
import ro.ubbcluj.cs.map.domain.Prietenie;
import ro.ubbcluj.cs.map.domain.Utilizator;
import ro.ubbcluj.cs.map.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MessageDBRepository implements Repository<Long,Message>{

    private String url;
    private String username;
    private String password;
    private Validator<Message> validator;

    public MessageDBRepository(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<Message> findOne(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Iterable<Message> findAll() {
        String findStatement="select * from messages";
        Set<Message> messages = new HashSet<>();
        try(Connection connection=DriverManager.getConnection(url,username,password);
            PreparedStatement preparedStatement=connection.prepareStatement(findStatement);
            ResultSet resultSet = preparedStatement.executeQuery();

            PreparedStatement statement1 = connection.prepareStatement("select * from users where id=?");
            PreparedStatement statement2 = connection.prepareStatement("select * from messages where id=?");
        ){
            ResultSet resultSet2;
            while(resultSet.next())
            {
                Long id=(long) resultSet.getInt("id");
                String mesajul=resultSet.getString("mesajul");
                Long idUt=resultSet.getLong("from_user_id");
                Long idReply=(long) resultSet.getInt("reply_id");

                statement1.setLong(1,idUt);
                resultSet2=statement1.executeQuery();
                resultSet2.next();
                Utilizator utilizator=new Utilizator(resultSet2.getString("first_name"),resultSet2.getString("last_name"),null,null);
                utilizator.setId(idUt);

                Timestamp timestamp=resultSet.getTimestamp("data");
                LocalDateTime date=timestamp.toLocalDateTime();

                Array array =resultSet.getArray("to_user_ids");
                Long[] idArray = (Long[]) array.getArray();
                List<Long> idList = new ArrayList<>(List.of(idArray));

                List<Utilizator> lastList=new ArrayList<Utilizator>();
                for (var a:idList)
                {
                    statement1.setLong(1,a);
                    resultSet2=statement1.executeQuery();
                    resultSet2.next();
                    Utilizator u=new Utilizator(resultSet2.getString("first_name"),resultSet2.getString("last_name"),null,null);
                    u.setId(a);
                    lastList.add(u);
                }


                Message message=new Message(mesajul,utilizator,lastList,date);
                message.setReplyTo(idReply);
                message.setId(id);
                messages.add(message);

            }


        }
        catch (SQLException e)
        {
            throw new RuntimeException();
        }
        return messages;
    }

    @Override
    public Optional<Message> save(Message entity) {
        String insertSqlStatement = "insert INTO messages (mesajul, from_user_id, to_user_ids, data, reply_id)\n" +
                "VALUES (?, ?, ?, ?, ?);";
        validator.validate(entity);
        try(Connection connection= DriverManager.getConnection(url,username,password);

            PreparedStatement statement=connection.prepareStatement(insertSqlStatement);

        ){
            statement.setString(1,entity.getMessage());
            statement.setLong(2,entity.getFrom().getId());
            Array array = connection.createArrayOf("BIGINT", entity.getTo().stream().map(Utilizator::getId).toArray());
            statement.setArray(3,array);
            Timestamp timestamp=Timestamp.valueOf(entity.getDate());
            statement.setTimestamp(4,timestamp);
            Long replyToId = entity.getReplyTo() != null ? entity.getReplyTo() : null;
            statement.setObject(5, replyToId != null ? replyToId.intValue() : null);
            //statement.setInt(5,entity.getReplyTo().getId().intValue());

            statement.executeUpdate();

        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }



        return Optional.empty();
    }

    @Override
    public Optional<Message> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }
}
