package com.prathamcodes.scattermerge.service.provider;

import com.prathamcodes.scattermerge.model.dto.AggregationRequest;
import com.prathamcodes.scattermerge.model.dto.ProviderResult;

public interface ProviderConnector<T> {
    String getProviderId();
    String getProviderName();
    ProviderResult<T> fetchOffers(AggregationRequest request);

}