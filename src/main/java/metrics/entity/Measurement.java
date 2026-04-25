package metrics.entity;

import java.util.Map;

public record Measurement<T extends ClusterParameters, U extends DatasetParameters>(
        String algo,
        long algoTimeMs,
        T clusterParameters,
        U datasetParameters,
        Map<String, Object> additionalMetrics) {

    public Measurement(String algo,
                       long algoTimeMs,
                       T clusterParameters,
                       U datasetParameters) {
        this(algo, algoTimeMs, clusterParameters, datasetParameters, Map.of());
    }
}

