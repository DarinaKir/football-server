package com.ashcollege.entities;

public class Match {
    private int id;
    private Team team1;
    private Team team2;
    private int goals_T1;
    private int goals_T2;

    public Match() {

    }

    public Match(int id, Team team1, Team team2, int goals_T1, int goals_T2) {
        this.id = id;
        this.team1 = team1;
        this.team2 = team2;
        this.goals_T1 = goals_T1;
        this.goals_T2 = goals_T2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Team getTeam1() {
        return team1;
    }

    public void setTeam1(Team team1) {
        this.team1 = team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public void setTeam2(Team team2) {
        this.team2 = team2;
    }

    public int getGoals_T1() {
        return goals_T1;
    }

    public void setGoals_T1(int goals_T1) {
        this.goals_T1 = goals_T1;
    }

    public int getGoals_T2() {
        return goals_T2;
    }

    public void setGoals_T2(int goals_T2) {
        this.goals_T2 = goals_T2;
    }

    public Team winner () {
        Team winner = null;
        if (this.goals_T1 > this.goals_T2) {
            winner = this.team1;
        }else if (this.goals_T1 < this.goals_T2){
            winner = this.team2;
        }
        return winner;
    }
}