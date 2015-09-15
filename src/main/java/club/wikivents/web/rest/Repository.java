package club.wikivents.web.rest;

import java.util.List;

public interface Repository<ID, T> {

    T getById(ID id);

    T findById(ID id);

    List<T> listAll();

    ID create(T value);

    void delete(ID id);

}
