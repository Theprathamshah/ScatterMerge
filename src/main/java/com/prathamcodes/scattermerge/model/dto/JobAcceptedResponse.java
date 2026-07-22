package com.prathamcodes.scattermerge.model.dto;

import com.prathamcodes.scattermerge.model.enums.JobStatus;

public record JobAcceptedResponse(
    String jobId,
    JobStatus status,
    String message,
    String createdAt
) {}
