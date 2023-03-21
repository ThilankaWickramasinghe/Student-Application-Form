package lk.ijse.dep10;

import javafx.application.Application;
import javafx.stage.Stage;
import lk.ijse.dep10.db.DBConnection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            try {
                if (DBConnection.getInstance().getConnection() != null && !DBConnection.getInstance().getConnection().isClosed()){
                    System.out.println("Database connection is about to close");
                    DBConnection.getInstance().getConnection().close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        generateTables();

    }
    private void generateTables(){
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SHOW TABLES");
            if (!rst.next()){
                InputStream is = getClass().getResourceAsStream("/schema.sql");
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder dbScript = new StringBuilder();
                while ((line = br.readLine()) != null){
                    dbScript.append(line).append("\n");
                }
                br.close();
                stm.execute(dbScript.toString());
                System.out.println(dbScript.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
