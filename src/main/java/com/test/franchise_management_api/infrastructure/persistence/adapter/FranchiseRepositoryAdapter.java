package com.test.franchise_management_api.infrastructure.persistence.adapter;

import com.test.franchise_management_api.domain.model.Franchise;
import com.test.franchise_management_api.domain.repository.FranchiseRepositoryPort;
import com.test.franchise_management_api.infrastructure.persistence.document.FranchiseDocument;
import com.test.franchise_management_api.infrastructure.persistence.repository.SpringDataFranchiseRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class FranchiseRepositoryAdapter implements FranchiseRepositoryPort {

    private final SpringDataFranchiseRepository repository;

    public FranchiseRepositoryAdapter(SpringDataFranchiseRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise, String normalizedName) {
        FranchiseDocument document = new FranchiseDocument(franchise.id(), franchise.name(), normalizedName);
        return repository.save(document).map(saved -> new Franchise(saved.getId(), saved.getName()));
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return repository.findById(id).map(found -> new Franchise(found.getId(), found.getName()));
    }

    @Override
    public Mono<Boolean> existsByNormalizedName(String normalizedName) {
        return repository.existsByNormalizedName(normalizedName);
    }

    @Override
    public Mono<Boolean> existsByNormalizedNameAndIdNot(String normalizedName, String excludedId) {
        return repository.existsByNormalizedNameAndIdNot(normalizedName, excludedId);
    }
}

