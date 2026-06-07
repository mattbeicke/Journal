package mattb;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        LocalDate date = LocalDate.now();
        String dateFileName = "Journals\\" + date.format(DateTimeFormatter.ofPattern("MMddyyyy")) + ".txt";
        File file = new File(dateFileName);
        TextArea text = new TextArea();
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(dateFileName));
                String line;
                StringBuilder data = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    data.append(line).append("\n");
                }
                text.setText(data.toString());
            } catch (IOException ignored) {
            }
        }
        Button done = new Button("Done");
        AtomicBoolean viewing = new AtomicBoolean(false);
        done.setOnAction(_ -> {
            if (!viewing.get()) {
                try {
                    FileWriter toFile = new FileWriter(dateFileName);
                    toFile.write(text.getText());
                    toFile.close();
                } catch (IOException ignored) {
                }
            }
            Platform.exit();
        });
        Button viewer = new Button(" View\nEntries");
        viewer.setOnAction(_ -> {
            viewing.set(true);
            File directory = new File("Journals\\");
            File[] files = directory.listFiles();
            VBox fileNames = new VBox();
            if (files != null) {
                for (File f : files) {
                    String fileName = f.getPath().substring(f.getPath().indexOf("\\") + 1, f.getPath().indexOf('.'));
                    Button b = new Button("    " + fileName + "    ");
                    b.setOnAction(_ -> {
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(f.getPath()));
                            String line;
                            StringBuilder data = new StringBuilder();
                            while ((line = reader.readLine()) != null) {
                                data.append(line).append("\n");
                            }
                            text.setText(data.toString());
                            text.setEditable(false);
                            Label dat = new Label("Viewing entry from: " + fileName.substring(0, 2) + "/" + fileName.substring(2, 4) + "/" + fileName.substring(4));
                            BorderPane rute = new BorderPane(text, dat, done, new Label(), new Label("     "));
                            BorderPane.setAlignment(dat, Pos.CENTER);
                            Scene view = new Scene(rute, 1000, 500);
                            primaryStage.setScene(view);
                            primaryStage.show();
                        } catch (IOException ignored) {
                        }
                    });
                    fileNames.getChildren().add(b);
                }
            }
            Scene past = new Scene(fileNames);
            primaryStage.setScene(past);
            primaryStage.show();
        });
        Label dateLabel = new Label(date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        dateLabel.setFont(new Font("Arial", 24));
        BorderPane root = new BorderPane(text, dateLabel, new VBox(done, viewer), new Label(), new Label("     "));
        BorderPane.setAlignment(dateLabel, Pos.CENTER);
        Scene scene = new Scene(root, 1000, 500);
        primaryStage.setTitle("Journal");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
