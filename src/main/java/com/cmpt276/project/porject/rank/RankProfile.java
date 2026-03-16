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
 * 
 * Class is not explicitly accessed by http, it is only used by other classes
 * through RankService, thus it does not need a controller.
 */
@Entity
@Table(name = "rank_profiles")
public class RankProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Automatically set to 0 if user does not have a rank profile
    @Column(columnDefinition = "integer default 0", nullable = false)
    private int rr;

    // Transient field, not stored in the database
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

}
