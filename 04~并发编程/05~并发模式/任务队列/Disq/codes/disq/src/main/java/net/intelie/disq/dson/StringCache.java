package net.intelie.disq.dson;

public class StringCache {
    public static final String EMPTY = "";
    private final int bucketCount;
    private final int maxStringLength;
    private final String[] cache;

    public StringCache() {
        this(8192, 1024);
    }

    public StringCache(int bucketCount, int maxStringLength) {
        if (Integer.bitCount(bucketCount) != 1)
            throw new IllegalArgumentException("bucketCount must be a power of two");
        this.bucketCount = bucketCount;
        this.maxStringLength = maxStringLength;
        this.cache = new String[bucketCount];
    }

    public String get(CharSequence cs) {
        if (cs == null) return null;
        int length = cs.length();
        if (length == 0) return EMPTY;
        if (length > maxStringLength) return cs.toString();

        int hash = hash(cs, length);
        int n = hash & (bucketCount - 1);
        String cached = cache[n];
        if (eq(cached, cs, hash))
            return cached;
        return cache[n] = cs.toString();
    }

    private int hash(CharSequence cs, int length) {
        int hash = 0;
        for (int i = 0; i < length; i++)
            hash = 31 * hash + cs.charAt(i);
        return hash;
    }


    private static boolean eq(String cached, CharSequence cs, int hash) {
        if (cached == null || cached.hashCode() != hash)
            return false;
        return cached.contentEquals(cs);
    }
}