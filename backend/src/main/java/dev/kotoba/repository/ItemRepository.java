package dev.kotoba.repository;
import dev.kotoba.domain.Item;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
public interface ItemRepository extends JpaRepository<Item,UUID>,JpaSpecificationExecutor<Item> {}
