package com.cmpt276.project.porject.rank;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

/**
 * Represents a user's ranking profile in the system.
 */
@Entity
@Table(name = "rank_profiles")
public class RankProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int rr;

    @Transient
    private String rankImageName;

    public RankProfile() {
        this.rr = 0;
    }

    // -- Getters and Setters --

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRr() {
        return rr;
    }

    public void setRr(int rr) {
        this.rr = rr;
    }

    public String getRankImageName() {
        return rankImageName;
    }

    public void setRankImageName(String rankImageName) {
        this.rankImageName = rankImageName;
    }

    // -- Helper Methods for Views --

    /**
     * Calculates the tier based on raw rr.
     * 
     * @return The rank string based on the raw rr.
     */
    public String getTierName() {
        // If rr is greater than or equal to 1500, return the rr as a string
        if (rr >= 1500)
            return String.valueOf(rr) + " RR";

        String[] ranks = {
                "Bronze I", "Bronze II", "Bronze III", // 0 - 300rr
                "Silver I", "Silver II", "Silver III", // 301 - 600rr
                "Gold I", "Gold II", "Gold III", // 601 - 900rr
                "Platinum I", "Platinum II", "Platinum III", // 901 - 1200rr
                "Diamond I", "Diamond II", "Diamond III", // 1201 - 1500rr
        };

        // Divide by 100 to get the exact array index
        // Example: 250 / 100 = 2 ("Bronze III")
        // Example: 410 / 100 = 4 ("Silver II")
        int index = rr / 100;
        
        // Safety bound in case calculated mathematically
        if (index >= ranks.length) {
            return ranks[ranks.length - 1]; 
        }

        return ranks[index];
    }

    public boolean isMaxRank() {
        return this.rr >= 1500;
    }

    public int getPointsToNextRank() {
        if (isMaxRank()) {
            return 0;
        }
        return 100 - (this.rr % 100);
    }

    public int getProgressPercentage() {
        if (isMaxRank()) {
            return 100;
        }
        return this.rr % 100;
    }
}
