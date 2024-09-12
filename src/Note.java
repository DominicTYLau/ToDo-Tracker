import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.Border;

public class Note extends JPanel{
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static LocalDate today = LocalDate.now();
    private static String todayMonth = convertMonth(today.getMonthValue());;
    private JTextField descriptionField;
    private JTextField monthDueField;
    private JTextField dayDueField;
    private JComboBox priorityDropDown;


    public Note(){
        this("Description of the note", todayMonth, String.valueOf(today.getDayOfMonth()), "High");
    }

    public Note(String description, String dueDateMonth, String dueDateDay, String priority){

        this.setMinimumSize(new Dimension(screenSize.width / 3, 200));
        this.setPreferredSize(new Dimension(screenSize.width / 3, 200));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        descriptionField = new JTextField(description, 10);
        descriptionField.setFont(new Font("Arial", Font.PLAIN, 16));
        descriptionField.setEditable(true);
        descriptionField.setBorder(BorderFactory.createCompoundBorder(descriptionField.getBorder(), BorderFactory.createEmptyBorder(0, 5, 0, 0)));
        descriptionField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Due date fields
        JLabel dueLabel = new JLabel("Due:");
        monthDueField = new JTextField((dueDateMonth), 7);
        dayDueField = new JTextField((dueDateDay), 3);
        JPanel dueDatePanel = new JPanel();
        
        dueDatePanel.add(dueLabel);
        dueDatePanel.add(monthDueField);
        dueDatePanel.add(dayDueField);

        // Drop down for priority
        JLabel priorityLabel = new JLabel("Priority:");
        String priorityList[] = { "Critical", "High", "Normal", "Low"};
        priorityDropDown = new JComboBox(priorityList);
        
        switch(priority){
            case "Critical" -> priorityDropDown.setSelectedIndex(0);
            case "High" -> priorityDropDown.setSelectedIndex(1);
            case "Normal" -> priorityDropDown.setSelectedIndex(2);
            case "Low" -> priorityDropDown.setSelectedIndex(3);
        }
        // Organize the panel
        JPanel priorityPanel = new JPanel();
        priorityPanel.add(priorityLabel);
        priorityPanel.add(priorityDropDown);

        // Add to panel
        this.add(descriptionField);
        this.add(dueDatePanel);
        this.add(priorityPanel);

        // Add border
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        Border margin = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border compoundBorder = BorderFactory.createCompoundBorder(border, margin);
        this.setBorder(compoundBorder);
    }   
    
    
    public static String convertMonth(int monthNum){
        return switch (monthNum) {
            case 1 -> "January";
            case 2 -> "February";
            case 3 -> "March";
            case 4 -> "April";
            case 5 -> "May";
            case 6 -> "June";
            case 7 -> "July";
            case 8 -> "August";
            case 9 -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> "Error";
        };
    }

    public String getDescription(){
        return descriptionField.getText();
    } 

    public String getDueDateMonth(){
        return monthDueField.getText();
    }

    public String getDueDateDay(){
        return dayDueField.getText();
    }

    public String getPriority(){
        return priorityDropDown.getSelectedItem().toString();
    }
}

