package com.cmpt276.project.porject.rank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for accessing RankProfile entities.
 */
@Repository
public interface RankProfileRepository extends JpaRepository<RankProfile, Integer> {

}
