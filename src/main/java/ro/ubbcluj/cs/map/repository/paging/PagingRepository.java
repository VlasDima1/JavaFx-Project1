package ro.ubbcluj.cs.map.repository.paging;

import ro.ubbcluj.cs.map.domain.Entity;
import ro.ubbcluj.cs.map.repository.Repository;
import ro.ubbcluj.cs.map.repository.paging.Page;

public interface PagingRepository<ID , E extends Entity<ID>> extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);
}
