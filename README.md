# Benchmark Utility for Spark Structured Streaming on Amazon EMR

We wanted to have performance benchmarks for various scenarios as part of the RocksDb State Storage implementation [SPARK-28120](https://issues.apache.org/jira/browse/SPARK-28120).
This was specifically forked to run on Amazon EMR with intent to test Spark Structured Streaming on different instance types (Compute Optimzed, Memory Optimzed, Storage Optimized)

###### Launch an Amazon EMR Cluster

As part of this benchmark,launch 2 EMR Clusters (one with Compute optimzed instance and another with Memory Optimzed instance).
Follow this link, if you are going to use [AWS Management Console](https://docs.aws.amazon.com/emr/latest/ManagementGuide/emr-launch-with-quick-options.html)

##### Cluster1:
    aws emr create-cluster \
    --log-uri s3://myBucket/myLog \
    --release-label emr-6.15.0 \
    --instance-groups InstanceGroupType=MASTER,InstanceCount=1,InstanceType=c5d.xlarge InstanceGroupType=CORE,InstanceCount=2,InstanceType=c5d.xlarge \
    --auto-terminate

##### Cluster2:
    aws emr create-cluster \
    --log-uri s3://myBucket/myLog \
    --release-label emr-6.15.0 \
    --instance-groups InstanceGroupType=MASTER,InstanceCount=1,InstanceType=r5d.xlarge InstanceGroupType=CORE,InstanceCount=2,InstanceType=r5d.xlarge \
    --auto-terminate

SSH into EMR Primary Node and clone this repo, change directory to benchmark folder:

       git clone https://github.com/knkarthik01/spark-rocksdb-benchmark.git
       cd spark-rocksdb-benchmark/

###### Build the project and Execute Spark Streaming Jobs on both clusters (Open multiple windows to execute them in parallel, if needed)
        sbt assembly

###### Use RocksDB State Storage
        spark-submit --conf spark.sql.streaming.stateStore.providerClass=org.apache.spark.sql.execution.streaming.state.RocksDBStateStoreProvider \
        --class com.qubole.spark.benchmark.streaming.states.StateStoreBenchmarkRunner \
        --driver-memory 2g \
        --executor-memory 7g \
        --num-executors 1 \
        --max-executors 1 \
        --executor-cores 4 \
        --conf spark.executor.memoryOverhead=3g \
        ./build/spark-benchmark.jar \
        --query-status-file "/tmp/queryStatus-rocksdb" \
        --rate-row-per-second "20000" \
        --output-mode "append" \
        --run-time-in-sec 1800 \
        --shuffle-partition 8

###### Use Memory State Storage
        spark-submit \
        --class com.qubole.spark.benchmark.streaming.states.StateStoreBenchmarkRunner \
        --driver-memory 2g \
        --executor-memory 7g \
        --num-executors 1 \
        --executor-cores 4 \
        --conf spark.executor.memoryOverhead=3g \
        ./build/spark-benchmark.jar \
        --query-status-file "/tmp/queryStatus-memory" \
        --rate-row-per-second "20000" \
        --output-mode "append" \
        --run-time-in-sec 1800 \
        --shuffle-partition 8

###### Analyze the progress
        spark-submit \
        --class com.qubole.spark.benchmark.streaming.states.AnalyzeProgress \
        --driver-memory 2g \
        --executor-memory 5g \
        --num-executors 2 \
        --max-executors 2 \
        --conf spark.executor.memoryOverhead=1g \
        ./build/spark-benchmark.jar \
         "hdfs:///tmp/queryStatus-rocksdb"

###### Sample Output 

        |runId                |maxBatchId|TotalProcessedRecordsInMillion|TotalExecutionTimeInSec|AvgExecutionTimeInSec|MaxStateRowsInMillion|maxStateSizeInGB|
        | 3fc89a39-1cc3-46b...|        33|                         30.62|                1638.42|                48.19|                 8.57|            0.68| 


###### Clean Up

If you terminate Spark Jobs you had executed in previous steps, cluster would auto-terminate in 60 mins.
If not, manually force terminate it from console and execute below CLI:

        aws emr terminate-clusters --cluster-ids j-3KVXXXXXXX7UG j-WJ2XXXXXX8EU


## Acknowledgement

[Jungtaek Lim](https://github.com/HeartSaVioR) and [Vikram](https://github.com/itsvikramagr) and for the original work for the structured streaming benchmarking.
