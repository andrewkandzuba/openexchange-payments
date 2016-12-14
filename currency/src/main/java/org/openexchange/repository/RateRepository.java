package org.openexchange.repository;

import org.openexchange.domain.Currency;
import org.openexchange.domain.Rate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RateRepository extends JpaRepository<Rate, Rate.RatePK> {
    List<Rate> findByIdSource(Currency source);
}
