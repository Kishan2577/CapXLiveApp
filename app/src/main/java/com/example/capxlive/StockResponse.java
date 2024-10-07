package com.example.capxlive;

import java.util.Map;

public class StockResponse {
    private String id;
    private String type;
    private Map<String, StockData> attributes; // Changed to Map to handle dynamic date keys

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Map<String, StockData> getAttributes() {
        return attributes;
    }

    public static class StockData {
        private double open;
        private double close;
        private double high;
        private double low;
        private double volume;
        private double adj;

        public double getOpen() {
            return open;
        }

        public double getClose() {
            return close;
        }

        public double getHigh() {
            return high;
        }

        public double getLow() {
            return low;
        }

        public double getVolume() {
            return volume;
        }

        public double getAdj() {
            return adj;
        }
    }
}
