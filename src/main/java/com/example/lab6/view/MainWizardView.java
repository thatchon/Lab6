package com.example.lab6.view;

import com.example.lab6.pojo.Wizard;
import com.example.lab6.pojo.Wizards;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Route("mainPage.it")
public class MainWizardView extends VerticalLayout {
    private TextField fullName;
    private NumberField dollars;
    private RadioButtonGroup genderGroup;
    private ComboBox positionSelection, schoolSelection, houseSelection;
    private HorizontalLayout buttonLayout;
    private Button b_back, b_create, b_update, b_delete, b_next;
    private Wizards wizards;
    private int wizardIndex = 0;

    public MainWizardView() {
        this.wizards = new Wizards();

        this.buttonLayout = new HorizontalLayout();
        this.fullName = new TextField();
        this.genderGroup = new RadioButtonGroup("Gender :");
        this.positionSelection = new ComboBox();
        this.dollars = new NumberField("Dollars");
        this.schoolSelection = new ComboBox();
        this.houseSelection = new ComboBox();
        this.b_back = new Button("<<");
        this.b_create = new Button("Create");
        this.b_update = new Button("Update");
        this.b_delete = new Button("Delete");
        this.b_next = new Button(">>");

        this.fullName.setPlaceholder("Fullname");
        this.genderGroup.setItems("Male", "Female");
        this.positionSelection.setItems("Student", "Teacher");
        this.dollars.setPrefixComponent(new Paragraph("$"));
        this.schoolSelection.setItems("Hogwarts", "Beauxbatons", "Durmstrang");
        this.houseSelection.setItems("Gryffindor", "Ravenclaw", "Hufflepuff", "Slytherin");

        this.buttonLayout.add(b_back, b_create, b_update, b_delete, b_next);
        this.add(fullName, genderGroup, positionSelection, dollars, schoolSelection, houseSelection, buttonLayout);

        this.b_back.addClickListener(e -> {
            this.wizardIndex = Math.max(this.wizardIndex - 1, 0);
            this.updateField();
        });

        this.b_next.addClickListener(e -> {
            this.wizardIndex = Math.min(this.wizardIndex + 1, this.wizards.getWizards().size() - 1);
            this.updateField();
        });

        this.b_create.addClickListener(e -> {
            String fullName = this.fullName.getValue();
            char sex = this.genderGroup.getValue().equals("Male") ? 'm' : 'f';
            String positionSelection = this.positionSelection.getValue().equals("Student") ? "student" : "teacher";
            int money = this.dollars.getValue().intValue();
            String school = (String) this.schoolSelection.getValue();
            String house = (String) this.houseSelection.getValue();

            Wizard wiz = new Wizard(null, sex, fullName, school, house, money, positionSelection);

            WebClient
                    .create()
                    .post()
                    .uri("http://localhost:8080/addWizard")
                    .body(Mono.just(wiz), Wizard.class)
                    .retrieve()
                    .bodyToMono(Wizard.class)
                    .block();
            this.wizardIndex = this.wizards.getWizards().size();
            this.loadWizards();
        });

        this.b_update.addClickListener(e -> {
            String id = this.wizards.getWizards().get(this.wizardIndex).get_id();
            String fullName = this.fullName.getValue();
            char sex = this.genderGroup.getValue().equals("Male") ? 'm' : 'f';
            String positionSelection = this.positionSelection.getValue().equals("Student") ? "student" : "teacher";
            int money = this.dollars.getValue().intValue();
            String school = (String) this.schoolSelection.getValue();
            String house = (String) this.houseSelection.getValue();

            WebClient
                    .create()
                    .post()
                    .uri("http://localhost:8080/updateWizard")
                    .body(Mono.just(new Wizard(id, sex, fullName, school, house, money, positionSelection)), Wizard.class)
                    .retrieve()
                    .bodyToMono(Wizard.class)
                    .block();
            this.loadWizards();
        });

        this.b_delete.addClickListener(e -> {
            WebClient
                    .create()
                    .post()
                    .uri("http://localhost:8080/deleteWizard")
                    .body(Mono.just(this.wizards.getWizards().get(this.wizardIndex)), Wizard.class)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            this.wizardIndex = 0;
            this.loadWizards();
        });

        this.loadWizards();
    }

    private void loadWizards() {
        List<Wizard> w = WebClient
                .create()
                .get()
                .uri("http://localhost:8080/wizards")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Wizard>>() {
                })
                .block();
        this.wizards.setWizards(w);
        this.updateField();
    }

    private void updateField() {
        this.b_back.setEnabled(!(this.wizardIndex == 0));
        this.b_next.setEnabled(!(this.wizardIndex == this.wizards.getWizards().size() - 1));
        this.fullName.setValue(this.wizards.getWizards().get(this.wizardIndex).getName());
        this.genderGroup.setValue(this.wizards.getWizards().get(this.wizardIndex).getSex() == 'm' ? "Male" : "Female");
        this.positionSelection.setValue(this.wizards.getWizards().get(this.wizardIndex).getPosition().equals("student") ? "Student" : "Teacher");
        this.dollars.setValue((double) this.wizards.getWizards().get(this.wizardIndex).getMoney());
        this.schoolSelection.setValue(this.wizards.getWizards().get(this.wizardIndex).getSchool());
        this.houseSelection.setValue(this.wizards.getWizards().get(this.wizardIndex).getHouse());
    }
}