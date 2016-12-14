package org.openexchange.service;

import org.hibernate.ObjectNotFoundException;
import org.openexchange.domain.Currency;
import org.openexchange.domain.Rate;
import org.openexchange.repository.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RateServiceImpl implements RateService {
    @Autowired
    private RateRepository rateRepository;

    @Override
    public List<Rate> findRatesBySource(Currency source) {
        return rateRepository.findByIdSource(source);
    }

    @Override
    public Rate findRate(Currency source, Currency target) {
        Rate.RatePK id = new Rate.RatePK(source, target);
        Rate rate = rateRepository.findOne(id);
        if(rate == null){
            throw new ObjectNotFoundException(id, "Not rate available for the currencies");
        }
        return rate;
    }
}
