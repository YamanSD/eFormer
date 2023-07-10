package eformer.back.eformer_backend.repository;

import eformer.back.eformer_backend.model.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
public interface ItemRepository extends CrudRepository<Item, Integer> {
    Optional<Item> findByName(String name);

    List<Item> findAllByIntroductionDateAfter(Date date);

    List<Item> findAllByIntroductionDateBefore(Date date);

    boolean existsByNameIgnoreCase(String name);
}
