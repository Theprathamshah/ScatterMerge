package com.prathamcodes.scattermerge.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "aggregation_jobs")
@Getter
@Setter
public class AggregationJob {

    @Id
    private String jobId;

}
