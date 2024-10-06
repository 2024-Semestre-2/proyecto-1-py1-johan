package Model;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProcessStatistics {
    public static class ProcessStats {
        private String processName;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Duration duration;

        public ProcessStats(String processName, LocalDateTime startTime) {
            this.processName = processName;
            this.startTime = startTime;
        }

        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
            this.duration = Duration.between(startTime, endTime);
        }

        public String getFormattedStartTime() {
            return startTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        }

        public String getFormattedEndTime() {
            return endTime != null ? endTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "";
        }

        public String getDurationInSeconds() {
            return duration != null ? String.valueOf(duration.getSeconds()) : "";
        }

        public String[] toTableRow() {
            return new String[]{
                processName,
                getFormattedStartTime(),
                getFormattedEndTime(),
                getDurationInSeconds()
            };
        }
    }

    private List<ProcessStats> statsList = new ArrayList<>();

    public void addProcess(String processName) {
        statsList.add(new ProcessStats(processName, LocalDateTime.now()));
    }

    public void completeProcess(String processName) {
        for (ProcessStats stats : statsList) {
            if (stats.processName.equals(processName) && stats.endTime == null) {
                stats.setEndTime(LocalDateTime.now());
                break;
            }
        }
    }

    public String[][] getStatisticsData() {
        return statsList.stream()
                .map(ProcessStats::toTableRow)
                .toArray(String[][]::new);
    }

    public void clear() {
        statsList.clear();
    }
}
