package com.crio.xpoll.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.crio.xpoll.model.Choice;
import com.crio.xpoll.model.Poll;
import com.crio.xpoll.model.PollSummary;
import com.crio.xpoll.util.DatabaseConnection;

/**
 * Data Access Object (DAO) for managing polls in the XPoll application.
 * Provides methods for creating, retrieving, closing polls, and fetching poll summaries.
 */
public class PollDAO {

    private final DatabaseConnection databaseConnection;

    /**
     * Constructs a PollDAO with the specified DatabaseConnection.
     *
     * @param databaseConnection The DatabaseConnection to be used for database operations.
     */
    public PollDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Creates a new poll with the specified question and choices.
     *
     * @param userId   The ID of the user creating the poll.
     * @param question The question for the poll.
     * @param choices  A list of choices for the poll.
     * @return The created Poll object with its associated choices.
     * @throws SQLException If a database error occurs during poll creation.
     */
    public Poll createPoll(int userId, String question, List<String> choices) throws SQLException {
        String pollQuery="INSERT INTO `polls`(`user_id`,`question`) values(?,?)";
        String choiceQuery="INSERT INTO `choices`(`poll_id`,`choice_text`) values(?,?)";

        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        PreparedStatement choicepstmt=null;
        ResultSet rs=null;
        try{
            con=databaseConnection.getConnection();
            pstmt=con.prepareStatement(pollQuery,PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1,userId);
            pstmt.setString(2,question);
            pstmt.executeUpdate();
            resultSet= pstmt.getGeneratedKeys();
            if(resultSet.next()){
                choicepstmt=con.prepareStatement(choiceQuery,PreparedStatement.RETURN_GENERATED_KEYS);
                int pollId=resultSet.getInt(1);
                List<Choice> res=new ArrayList<>();
                for(String choice:choices){
                    choicepstmt.setInt(1,pollId);
                    choicepstmt.setString(2,choice);
                    choicepstmt.executeUpdate();
                    rs=choicepstmt.getGeneratedKeys();
                    if(rs.next()){
                        int choiceId=rs.getInt(1);
                        Choice ch=new Choice(choiceId,pollId,choice);
                        res.add(ch);
                    }
                }
                return new Poll(pollId,userId,question,res);
            }else{
                throw new SQLException("Failed to create a Poll!");
            }
        }catch(SQLException e){
            throw e;
        }finally{
            if(rs!=null){
                rs.close();
            }if(choicepstmt!=null){
                choicepstmt.close();
            }if(resultSet!=null){
                resultSet.close();
            }if(pstmt!=null){
                pstmt.close();
            }if(con!=null){
                con.close();
            }
        }
    }
    /**
     * Retrieves a poll by its ID.
     *
     * @param pollId The ID of the poll to retrieve.
     * @return The Poll object with its associated choices.
     * @throws SQLException If a database error occurs or the poll is not found.
     */
    public Poll getPoll(int pollId) throws SQLException {
        String query="SELECT * FROM `polls` WHERE `id`=?";
        String choiceQuery="SELECT `id`,`choice_text` FROM `choices` WHERE `poll_id`=?";
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        PreparedStatement choicepstmt=null;
        ResultSet rs=null;
        try{
            con=databaseConnection.getConnection();
            pstmt=con.prepareStatement(query);
            pstmt.setInt(1,pollId);
            resultSet=pstmt.executeQuery();
            if(resultSet.next()){
                int userId=resultSet.getInt("user_id");
                String question=resultSet.getString("question");
                boolean isClosed=resultSet.getBoolean("is_closed");
                choicepstmt=con.prepareStatement(choiceQuery);
                choicepstmt.setInt(1,pollId);
                rs=choicepstmt.executeQuery();
                List<Choice> choices=new ArrayList<>();
                while(rs.next()){
                    int choiceId=rs.getInt("id");
                    String choiceText=rs.getString("choice_text");
                    Choice choice=new Choice(choiceId, pollId, choiceText);
                    choices.add(choice);
                }
                return new Poll(pollId,userId,question,choices,isClosed);
            }else{
                throw new SQLException("Failed to fetch Poll!");
            }
        }catch(SQLException e){
            throw e;
        }finally{
            if(rs!=null){
                rs.close();
            }if(choicepstmt!=null){
                choicepstmt.close();
            }if(resultSet!=null){
                resultSet.close();
            }if(pstmt!=null){
                pstmt.close();
            }if(con!=null){
                con.close();
            }
        }
    }

    /**
     * Closes a poll by updating its status in the database.
     *
     * @param pollId The ID of the poll to close.
     * @throws SQLException If a database error occurs during the update.
     */
    public void closePoll(int pollId) throws SQLException {
        String query="UPDATE `polls` SET `is_closed`=? WHERE `id`=?";
        Connection con=null;
        PreparedStatement pstmt=null;
        try{
            con=databaseConnection.getConnection();
            pstmt=con.prepareStatement(query);
            pstmt.setBoolean(1, true);
            pstmt.setInt(2,pollId);
            int n=pstmt.executeUpdate();
            System.out.println(n);
            if(n==0){
                throw new SQLException();
            }
        }catch(SQLException e){
            throw e;
        }finally{
            if(pstmt!=null){
                pstmt.close();
            }
            if(con!=null){
                con.close();
            }
        }
    }

    /**
     * Retrieves a list of poll summaries for the specified poll.
     *
     * @param pollId The ID of the poll for which to retrieve summaries.
     * @return A list of PollSummary objects containing the poll question, choice text, and response count.
     * @throws SQLException If a database error occurs during the query.
     */
    public List<PollSummary> getPollSummaries(int pollId) throws SQLException {
        String query="SELECT  `question`,`choice_text`,`response_count` FROM `poll_summaries` WHERE `poll_id`=?";
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        try{
            con=databaseConnection.getConnection();
            pstmt=con.prepareStatement(query);
            pstmt.setInt(1,pollId);
            resultSet= pstmt.executeQuery();
            List<PollSummary> summaries=new ArrayList<>();
            while(resultSet.next()){
                String question=resultSet.getString("question");
                String choiceText=resultSet.getString("choice_text");
                int responseCount=resultSet.getInt("response_count");
                summaries.add(new PollSummary(question, choiceText, responseCount));
            }
            return summaries;
        }catch(SQLException e){
            throw e;
        }finally{
            if(resultSet!=null){
                resultSet.close();
            }
            if(pstmt!=null){
                pstmt.close();
            }
            if(con!=null){
                con.close();
            }
        }
    }
}
