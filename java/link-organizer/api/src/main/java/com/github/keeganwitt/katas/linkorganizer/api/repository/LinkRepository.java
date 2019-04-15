package com.github.keeganwitt.katas.linkorganizer.api.repository;

import com.github.keeganwitt.katas.linkorganizer.api.model.Link;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('ROLE_USER')")
@RepositoryRestResource
public interface LinkRepository extends PagingAndSortingRepository<Link, Long> {

}
