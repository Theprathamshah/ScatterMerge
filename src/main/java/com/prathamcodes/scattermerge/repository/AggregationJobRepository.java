package com.prathamcodes.scattermerge.repository;

import com.prathamcodes.scattermerge.entity.AggregationJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregationJobRepository extends JpaRepository<AggregationJob, String> {

}
