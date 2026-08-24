package com.test.franchise_management_api.infrastructure.persistence.adapter;

import com.test.franchise_management_api.domain.model.Branch;
import com.test.franchise_management_api.domain.repository.BranchRepositoryPort;
import com.test.franchise_management_api.infrastructure.persistence.document.BranchDocument;
import com.test.franchise_management_api.infrastructure.persistence.repository.SpringDataBranchRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class BranchRepositoryAdapter implements BranchRepositoryPort {

    private final SpringDataBranchRepository repository;

    public BranchRepositoryAdapter(SpringDataBranchRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Branch> save(Branch branch, String normalizedName) {
        BranchDocument document = new BranchDocument(branch.id(), branch.franchiseId(), branch.name(), normalizedName);
        return repository.save(document).map(saved -> new Branch(saved.getId(), saved.getFranchiseId(), saved.getName()));
    }

    @Override
    public Mono<Branch> findByIdAndFranchiseId(String id, String franchiseId) {
        return repository.findByIdAndFranchiseId(id, franchiseId)
                .map(found -> new Branch(found.getId(), found.getFranchiseId(), found.getName()));
    }

    @Override
    public Flux<Branch> findByFranchiseId(String franchiseId) {
        return repository.findByFranchiseId(franchiseId)
                .map(found -> new Branch(found.getId(), found.getFranchiseId(), found.getName()));
    }

    @Override
    public Mono<Boolean> existsByFranchiseIdAndNormalizedName(String franchiseId, String normalizedName) {
        return repository.existsByFranchiseIdAndNormalizedName(franchiseId, normalizedName);
    }

    @Override
    public Mono<Boolean> existsByFranchiseIdAndNormalizedNameAndIdNot(
            String franchiseId,
            String normalizedName,
            String excludedId
    ) {
        return repository.existsByFranchiseIdAndNormalizedNameAndIdNot(franchiseId, normalizedName, excludedId);
    }
}

