## Execution

This application runs in two modes supporting either 8 or 10 arguments.

### Argument Order and Types

 The parameters must be provided in the following order:

**Mode 1:** 

A directory for the metrics needs to be provided. Results won't be collected, only the runtime will be measured.

| **#** | **Name**             | **Type**   | **Description**                                                                                                                                                             |
|:-----:|:---------------------|:-----------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|   1   | `filepath`           | **String** | Path to the input data file.                                                                                                                                                |
|   2   | `epsilon`            | **Float**  | DBSCAN neighborhood radius ($\epsilon$).                                                                                                                                    |
|   3   | `minPts`             | **Int**    | Minimum number of points required to form a dense region ($MinPts$).                                                                                                        |
|   4   | `numberOfPivots`     | **Int**    | Number of pivot points. Enter `-1` to let the amount of pivots be automatically calculated. Intrinsic dimensionality will be used as a measure.                             |
|   5   | `numberOfPartitions` | **Int**    | Number of Spark partitions.                                                                                                                                                 |
|   6   | `samplingDensity`    | **Float**  | Density of random sampling used to select pivots (0.0 < density $\leq$ 1.0. (E.g., 0.01 is 1% of the data)). Sample size significantly impacts pivot selection performance. |
|   7   | `seed`               | **Int**    | Random seed for reproducibility.                                                                                                                                            |
|   8   | `metricsPath`        | **String** | Path to output metrics file.                                                                                                                                                |

---

**Mode 2:**

Flags for collecting results and input data need to be set.

| **#** | **Name**             | **Type**    | **Description**                                                                                                                                                             |
|:-----:|:---------------------|:------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|   1   | `filepath`           | **String**  | Path to the input data file.                                                                                                                                                |
|   2   | `epsilon`            | **Float**   | DBSCAN neighborhood radius ($\epsilon$).                                                                                                                                    |
|   3   | `minPts`             | **Int**     | Minimum number of points required to form a dense region ($MinPts$).                                                                                                        |
|   4   | `numberOfPivots`     | **Int**     | Number of pivot points. Enter `-1` to let the amount of pivots be automatically calculated. Intrinsic dimensionality will be used as a measure.                             |
|   5   | `numberOfPartitions` | **Int**     | Number of Spark partitions.                                                                                                                                                 |
|   6   | `samplingDensity`    | **Float**   | Density of random sampling used to select pivots (0.0 < density $\leq$ 1.0. (E.g., 0.01 is 1% of the data)). Sample size significantly impacts pivot selection performance. |
|   7   | `seed`               | **Int**     | Random seed for reproducibility.                                                                                                                                            |
|   8   | `dataHasHeader`      | **Boolean** | If `true`, skips the first row of the input file. **(Default: `false`)**                                                                                                    |
|   9   | `dataHasRightLabel`  | **Boolean** | If `true`, treats the last column as a ground truth label for evaluation/output. **(Default: `false`)**                                                                     |
|  10   | `collectResult`      | **Boolean** | If `true`, collects results to the driver and prints cluster counts to the console.  **(Default: `false`)**                                                                 |

---

### Usage Example

**Minimal Command (7 Arguments):**
This example omits the optional arguments, defaulting them to `false`. The application will not collect results to the driver. 

```
spark-submit --class app.Main target/dbscanms-assembly.jar data/input.csv 0.5 5 10 8 0.01 42
```

**Full Command (10 Arguments):**
This example uses all optional arguments, setting `dataHasHeader` and `collectResults` to `true`.

```
spark-submit --class app.Main target/dbscanms-assembly.jar data/input.csv 0.5 5 10 8 0.01 42 true false true
```
