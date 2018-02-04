package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.nio.file.Files.readAllLines;

public class Controller {
    @FXML
    private TableView<Human> table;
    @FXML
    private TableColumn<Human, String> nameColumn;
    @FXML
    private TableColumn<Human, String> phoneColumn;
    @FXML
    private TextField nameInput;
    @FXML
    private TextField phoneInput;
    @FXML
    private Button add;
    @FXML
    private Button update;
    @FXML
    private Button delete;
    @FXML
    private Button menu_otworz;
    @FXML
    private Button menu_zapisz;
    @FXML
    private Button menu_zamknij;
    @FXML
    private Stage stage;


    public void initialize(){

        ObservableList<Human> humans = FXCollections.observableArrayList();

        humans.add (new Human("Czesław Horyń", "503452103"));
        humans.add (new Human("Anna Horyń", "502499182"));

        table.itemsProperty().setValue(humans);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

    }


    public void addButtonClicked() {

        Human human = new Human();

        int s;

        try {
            s = Integer.parseInt(phoneInput.getText());
        } catch (NumberFormatException e) {

            s = -1 ;// jeśli nie da się przekonwertować na int
        }


        if ((nameInput.getText().equals(""))  || (phoneInput.getText().equals("")) || s==-1)

        {
            human.setName("wrong data");
            human.setPhone("wrong data");
            table.getItems().add(human);
            nameInput.clear();
            phoneInput.clear();

        }

        else
        {

            human.setName(nameInput.getText());
            human.setPhone(phoneInput.getText());
            table.getItems().add(human);
            nameInput.clear();
            phoneInput.clear();

        }
    }

    public void updateButtonClicked(ActionEvent actionEvent) {

        deleteButtonClicked();
        addButtonClicked();
    }


    public void deleteButtonClicked() {

        ObservableList<Human> humanSelected, allHumans;
        allHumans = table.getItems();
        humanSelected = table.getSelectionModel().getSelectedItems();

        humanSelected.forEach(allHumans::remove);

    }

    public void otworzPlikAction() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Otwórz Plik");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Plik TSV", "*.tsv"));

        File plik = fileChooser.showOpenDialog(stage);

        if (plik != null) {

            System.out.println("Plik: " +plik.getAbsolutePath());
            ladujDane(plik);

        }

    }

    private void ladujDane(File file) {

        Path sciezkaDoPliku = Paths.get(file.getAbsolutePath());
        ArrayList<String> odczyt = new ArrayList<>();

        try{
            odczyt = (ArrayList) readAllLines(sciezkaDoPliku);

        } catch (IOException ex){

            System.out.println("Brak plik");

        }

        ArrayList<Human> lista = new ArrayList<Human>();
        for (String l : odczyt) {
            String[] dt = l.split("\t");
            if (dt.length > 1) {
                lista.add (new Human (dt[0],dt[1]));
            }
        }

        ObservableList<Human> dane = FXCollections.observableArrayList(lista);
        table.itemsProperty().setValue(dane);
        nameColumn.setCellValueFactory(new PropertyValueFactory<Human,String>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<Human,String>("phone"));

    }


    public void zapiszPlikAction() throws IOException {


        // Tworzymy kontrolkę (okienko)
        FileChooser fileChooser = new FileChooser();
        // Tytuł okienka
        fileChooser.setTitle("Zapisz Plik");
        // Dodajemy filtr rodzaju pliku - tu plików tsv
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Pliki TSV", "*.tsv")
        );
        // Pokaż okno
        File plik = fileChooser.showSaveDialog(stage);

        // Jeśli zamkniemy fileChooser nie wybierając pliku zostanie zwrócony null
        // Jeśli wybierzemy plik, podejmujemy  odpowiednie działania
        if (plik != null) {
            // Wyswietlenie w terminalu ścieżki do pliku.
            System.out.println("Plik: " + plik.getAbsolutePath());

            zapiszDane(plik);

        }
    }


    private void zapiszDane(File plik) throws IOException{

        ObservableList<Human> data = FXCollections.observableArrayList(

                table.getItems()

        );


        Writer writer = null;

        try {
            File file = new File(plik.getAbsolutePath());
            writer = new BufferedWriter(new FileWriter(file));
            for (Human human : data) {

                String text = human.getName() + "\t" + human.getPhone() + "\n";
                writer.write(text);

            }

        } catch (Exception ex) {

            ex.printStackTrace();

        } finally {


            writer.flush();

            writer.close();

        }


    }


    public void zamknijAplikacjeAction(ActionEvent actionEvent) {

        Platform.exit();
    }
}
