package com.codecademy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.codecademy.domain.Course;
import com.codecademy.domain.Difficulty;
import com.codecademy.database.DbConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CourseDAOImpl implements CourseDAO{
    private DbConnection dbConnection;

    public CourseDAOImpl(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public ObservableList<Course> getCourses() {
                try(Connection db = dbConnection.getConnection()) {
            PreparedStatement query = db.prepareStatement("SELECT * FROM Course");
            ResultSet result = query.executeQuery();

            ObservableList<Course> list = FXCollections.observableArrayList();

            while (result.next()) {
                Difficulty difficulty;
                if (result.getString("Difficulty") == "Beginner" || result.getString("Difficulty") == "BEGINNER") {
                    difficulty = Difficulty.BEGINNER;
                } else if (result.getString("Difficulty") == "Intermediate" || result.getString("Difficulty") == "INTERMEDIATE") {
                    difficulty = Difficulty.ADVANCED;
                } else {
                    difficulty = Difficulty.EXPERT;
                }
                list.add(new Course(result.getString("CourseName"), result.getString("CourseTopic"), result.getString("CourseIntroText"), result.getString("CourseTag"), difficulty));
            }
            return list;
        } catch (SQLException e) {
            System.err.println("Error in getStudents");
            e.printStackTrace();
        }
        return null;
     }

    @Override
    public ObservableList<String> getAllCourseNames() {
        try(Connection db = dbConnection.getConnection()) {
            PreparedStatement query = db.prepareStatement("SELECT CourseName FROM Course");
            ResultSet result = query.executeQuery();
            ObservableList<String> list = FXCollections.observableArrayList();

            while (result.next()) {
                list.add(result.getString("CourseName"));
            }
            return list;
        } catch (SQLException e) {
            System.out.println("Error in getAllCourseNames");
            e.printStackTrace();
        }
        return null; }

    @Override
    public void addCourse(Course course) {
        try(Connection db = dbConnection.getConnection()) {
            PreparedStatement query = db.prepareStatement("INSERT INTO Course VALUES(?, ?, ?, ?, ?, ?)");
            query.setString(1, course.getCourseName());
            query.setString(2, course.getCourseTopic());
            query.setString(3, course.getCourseIntroText());
            query.setString(4, course.getCourseTag());
            query.setString(5, course.getDifficulty().toString());
            query.executeUpdate();
            System.out.println("Course added");
        } catch (SQLException e) {
            System.out.println("Error in addCourse");
            e.printStackTrace();
        } }

    @Override
    public void updateCourse(Course course) {
        try(Connection db = dbConnection.getConnection()) {
            PreparedStatement query = db.prepareStatement("DELETE FROM Course WHERE CourseName = ?");
            query.setString(1, course.getCourseName());
            query.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error in deleteCourse");
            e.printStackTrace();
        } }

    @Override
    public void deleteCourse(Course course) {
        try(Connection db = dbConnection.getConnection()) {
            PreparedStatement query = db.prepareStatement("UPDATE Course SET ModuleNumber = ?, CourseTopic = ?, CourseIntroText = ?, CourseTag = ?, Difficulty = ? WHERE CourseName = ?"); 
            query.setString(2, course.getCourseTopic());
            query.setString(3, course.getCourseIntroText());
            query.setString(4, course.getCourseTag());
            query.setString(5, course.getDifficulty().toString());
            query.setString(6, course.getCourseName());
            query.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error in updateCourse");
            e.printStackTrace(); 
        } }

        @Override
        public List<String> getTop3CertifiedCourses() {
            List<String> courses = new ArrayList<>();
            try(Connection db = dbConnection.getConnection()) {
                PreparedStatement query = db.prepareStatement("SELECT TOP 3 CourseName FROM (SELECT CourseName, COUNT(*) as numCertificates FROM Enrollment WHERE CertificateID IS NOT NULL GROUP BY CourseName) AS counts ORDER BY numCertificates DESC");
                ResultSet rs = query.executeQuery();
                while (rs.next()) {
                    String courseName = rs.getString("CourseName");
                    courses.add(courseName);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return courses;
        }


        @Override
        public List<String> getRecommendedCourses(String selectedCourse) {
            List<String> recommendedCourses = new ArrayList<>();
            try (Connection conn = dbConnection.getConnection()) {
                String query = "SELECT RecommendedCourseName FROM CourseRecommendation1 WHERE CourseName = ?";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, selectedCourse);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    String recommendedCourse = rs.getString("RecommendedCourseName");
                    recommendedCourses.add(recommendedCourse);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return recommendedCourses;
        }

        @Override
        public int getNumCompletedCourses(String courseName) {
            int numCompleted = 0;
            try (Connection db = dbConnection.getConnection()) {
                PreparedStatement query = db.prepareStatement("SELECT COUNT(DISTINCT StudentEmail) AS num_completed FROM Enrollment WHERE CourseName = ? AND CertificateID IS NOT NULL");
                query.setString(1, courseName);
                ResultSet rs = query.executeQuery();
                if (rs.next()) {
                    numCompleted = rs.getInt("num_completed");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return numCompleted;
        }
        
        
}
