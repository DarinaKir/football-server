package com.ashcollege;


import com.ashcollege.entities.Client;
import com.ashcollege.entities.Match;
import com.ashcollege.entities.Team;
import com.ashcollege.entities.User;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import com.github.javafaker.Faker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.ashcollege.utils.Errors.*;


@Transactional
@Component
@SuppressWarnings("unchecked")
public class Persist {

    private static final Logger LOGGER = LoggerFactory.getLogger(Persist.class);

    private final SessionFactory sessionFactory;

    private List<Team> teamList;

    @Autowired
    public Persist(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public Session getQuerySession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(Object object) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(object);
    }

    public <T> T loadObject(Class<T> clazz, int oid) {
        return this.getQuerySession().get(clazz, oid);
    }

    public <T> List<T> loadUserList() {
        return this.sessionFactory.getCurrentSession().createQuery("FROM User").list();
    }

    public <T> List<T> loadTeamList() {
        return this.sessionFactory.getCurrentSession().createQuery("FROM Team").list();
    }

    public <T> List<T> loadMatchList() {
        return this.sessionFactory.getCurrentSession().createQuery("FROM Match").list();
    }

    public void addMatch (int teamId1,int teamId2) {
        List <Team> teamList = loadTeamList();
        Match match = new Match(teamList.get(teamId1 - 1),teamList.get(teamId2 - 1));
        save(match);
    }

    public ArrayList<ArrayList<Match>> getLeague () {
        List <Team> teams = loadTeamList();
        ArrayList<ArrayList<Match>> fixtures = new ArrayList<>();
        int rounds = teams.size() - 1;
        int matchesPerRound = teams.size() / 2;
        for (int i = 0; i < rounds; i++) {
            ArrayList<Match> round = new ArrayList<>();
            for (int j = 0; j < matchesPerRound; j++) {
                Match match = new Match(teams.get(j), teams.get(teams.size() - 1 - j));
                round.add(match);
            }
            fixtures.add(round);

            // Rotate teams
            Collections.rotate(teams.subList(1, teams.size()), 1);
        }
        return fixtures;
    }

    public User login(String email, String password) {
        if (email != null && password != null) {
            return (User) this.sessionFactory.getCurrentSession().createQuery(
                            "FROM User WHERE email = :email AND password = :password")
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .setMaxResults(1)
                    .uniqueResult();
        }
        // check if not null
        return null;
    }


    public boolean isUsernameExist (String username) {
        User userList = (User) this.sessionFactory.getCurrentSession().createQuery(
                        "FROM User WHERE username = :username")
                .setParameter("username", username)
                .uniqueResult();

        return (userList != null);
    }
    public BasicResponse signUp(String username,String email, String password) {
        BasicResponse basicResponse;
        if (!isUsernameExist(username) && isEmailCorrect(email)) {
            Faker faker = new Faker();
            User user = new User(username,email, password, faker.random().hex());
            save(user);
            basicResponse = new LoginResponse(true, null, user);
        } else {
            basicResponse = new BasicResponse(false, ERROR_SIGN_UP_USERNAME_TAKEN);
        }
        return basicResponse;
    }

    public User getUserBySecret(String secret) {
        return (User) this.sessionFactory.getCurrentSession().createQuery(
                        "FROM User WHERE secret = :secret")
                .setParameter("secret", secret)
                .setMaxResults(1)
                .uniqueResult();
    }

    public void createTeams() {
        Faker faker = new Faker();
        teamList = new ArrayList<>();
        List<Team> teams = (List<Team>) this.sessionFactory.getCurrentSession().createQuery(
                        "FROM Team")
                .list();
        if (teams.isEmpty()) {
            for (int i = 0; i < 8; i++) {
                Team team = new Team(faker.country().capital());
//                team.setSkillLevel(faker.random().nextInt(0,100));
                save(team);
                teamList.add(team);
            }
        } else {
            teamList = teams;
        }

        for (Team team : teamList) {
//            team.setSkillLevel(faker.random().nextInt(0, 100));
        }
        System.out.println(teamList.toString());
    }

    public boolean isEmailCorrect (String email) {
        return email.contains("@") && email.contains(".") && (email.lastIndexOf(".") - email.indexOf("@") > 1) && (email.indexOf("@") != 0);
    }

    public boolean isPasswordCorrect (String password) {
        return password.length() >= 8;
    }

    public User changeProfile (String category, String toChange, String secret) {
        User user = getUserBySecret(secret);
        switch (category){
            case "username":
                if (!isUsernameExist(toChange)) {
                    user.setUsername(toChange);
                    save(user);
                }
                break;
            case "email":
                if (isEmailCorrect(toChange)) {
                    user.setEmail(toChange);
                    save(user);
                }
                break;
            case "password":
                if (isPasswordCorrect(toChange)) {
                    user.setPassword(toChange);
                    save(user);
                }
                break;
        }
        return user;
    }
}