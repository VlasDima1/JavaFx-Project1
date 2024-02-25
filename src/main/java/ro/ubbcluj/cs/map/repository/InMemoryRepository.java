package ro.ubbcluj.cs.map.repository;

import ro.ubbcluj.cs.map.domain.Entity;
import ro.ubbcluj.cs.map.domain.validators.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    private Validator<E> validator;
    Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    @Override
    public Optional<E> findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        if (entities.isEmpty())
            return null;
        return entities.values();

    }

    @Override
    public Optional<E> save(E entity) {
        Predicate<E> isNull= Objects::isNull;

        if (isNull.test(entity))//entity==null
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);

        return Optional.ofNullable(entities.putIfAbsent(entity.getId(), entity));
        /*if(entities.get(entity.getId()) != null) {
            return Optional.of(entity);//Optional.ofNullable(entity);
        }
        else entities.put(entity.getId(),entity);
        return Optional.empty();*/
    }

    @Override
    public Optional<E> delete(ID id) {
        findOne(id);
        return Optional.ofNullable(entities.remove(id));
    }


    @Override
    public Optional<E> update(E entity) {
        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        entities.put(entity.getId(),entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return Optional.empty();
        }
        return Optional.of(entity);

    }
}
