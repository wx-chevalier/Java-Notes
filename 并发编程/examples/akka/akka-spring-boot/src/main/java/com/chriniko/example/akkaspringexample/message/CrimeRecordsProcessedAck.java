public class CrimeRecordsProcessedAck {

    private final long processedCount;

    public CrimeRecordsProcessedAck(long processedCount) {
        this.processedCount = processedCount;
    }

    public long getProcessedCount() {
        return processedCount;
    }
}
